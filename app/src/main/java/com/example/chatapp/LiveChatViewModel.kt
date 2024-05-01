package com.example.chatapp

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.Data.CHATS
import com.example.chatapp.Data.ChatData
import com.example.chatapp.Data.ChatUser
import com.example.chatapp.Data.Event
import com.example.chatapp.Data.MESSAGES
import com.example.chatapp.Data.Message
import com.example.chatapp.Data.STATUS
import com.example.chatapp.Data.Status
import com.example.chatapp.Data.USER_NODE
import com.example.chatapp.Data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AndroidViewModel(application = Application()) {

    var inProgress = mutableStateOf(false)
    var inProgressChats = mutableStateOf(false)
    private var eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    var chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    private val inProgressChatMessage = mutableStateOf(false)
    private var currentChatMessageListener: ListenerRegistration? = null

    var status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }


    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        viewModelScope.launch {
            currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGES)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(exception = error)
                    }
                    if (value != null) {
                        chatMessages.value = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.timeStamp }
                        inProgressChatMessage.value = false
                    }
                }
        }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    //send message to firebase
    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()

        val msg = Message(sendBy = userData.value?.userId, message = message, timeStamp = time)
        db.collection(CHATS).document(chatID).collection(MESSAGES).document().set(msg)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    handleException(customMessage = "Error Found : Message Not Send to Firebase")
                }
            }
    }

    fun signUp(
        name: String,
        email: String,
        phoneNumber: String,
        password: String,
        context: Context
    ) {
        inProgress.value = true

        // Validate input fields
        if (name.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank()) {
            handleException(customMessage = "Please fill all fields")
            return
        }

        // Check if the phone number is already registered
        viewModelScope.launch {
            db.collection(USER_NODE)
                .whereEqualTo("number", phoneNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // Phone number is not registered, proceed with sign-up
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { signUpTask ->
                                if (signUpTask.isSuccessful) {
                                    // Sign-up successful
                                    signIn.value = true
                                    Log.d("SignUp", "Sign-up successful: $email")
                                    createAndUpdateProfile(name, phoneNumber)

                                    Toast.makeText(
                                        context,
                                        "Sign up Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Sign-up failed
                                    Log.e("SignUp", "Sign-up failed: $email", signUpTask.exception)
                                    handleException(signUpTask.exception, "Sign-up failed")
                                    Toast.makeText(
                                        context,
                                        "Sign-up failed, ${signUpTask.exception?.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                    } else {
                        // Phone number already exists
                        handleException(customMessage = "Phone number already exists")

                        Toast.makeText(
                            context,
                            "Phone number already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                        inProgress.value = false
                    }
                }
                .addOnFailureListener { exception ->
                    // Error fetching data
                    Log.e("SignUp", "Error fetching data", exception)
                    handleException(exception, "Error fetching data")
                }
        }
    }

    fun createAndUpdateProfile(
        name: String? = null,
        phoneNumber: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.uid
        val userData =
            UserData(
                userId = uid,
                userName = name ?: userData.value?.userName,
                userNumber = phoneNumber ?: userData.value?.userNumber,
                userImageUrl = imageUrl ?: userData.value?.userImageUrl
            )
        Log.d("IMG", "ProfileContent: $imageUrl")
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    //if data is exists so update the data
                    inProgress.value = false
                    db.collection(USER_NODE).document(uid).set(userData)
                    getUserData(uid)
                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    getUserData(uid)
                    inProgress.value = false
                }
            }
                .addOnFailureListener {
                    handleException(it, "Can't Retrieve Data")
                }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        viewModelScope.launch {
            db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Cannot Retrieve User")
                }
                if (value != null) {
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false
                    populateChats()
                    populateStatuses()
                    Log.d("IMG", "ProfileContent: $user")
                }
            }
        }
    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill the all fields")
            return
        } else {
            inProgress.value = true
            viewModelScope.launch {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        signIn.value = true
                        inProgress.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    } else {
                        handleException(exception = it.exception, customMessage = "Login Failed")
                    }
                }
            }
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.d("LiveChatApp", "Live chat exception : $exception")
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false

    }

    fun uploadProfileImg(uri: Uri) {
        uploadImg(uri) {
            Log.d("IMG", "ProfileContent upload: $it")
            createAndUpdateProfile(imageUrl = it.toString())
        }
    }

    private fun uploadImg(uri: Uri, onSuccess: (Uri) -> Unit) {
        Log.d("IMG", "ProfileContent: $uri")
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        viewModelScope.launch {
            uploadTask.addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                inProgress.value = false
                Log.d("RESULT", "uploadImg: $result")
                Log.d(
                    "STATUS",
                    "upload Img server-------------------------------------------------------------------------------- "
                )
                result?.addOnSuccessListener(onSuccess)
            }
                .addOnFailureListener {
                    handleException(exception = it)
                    inProgress.value = false
                }
        }
    }

    fun logout(context: Context) {
        auth.signOut()
        signIn.value=false
        userData.value=null
        eventMutableState.value = Event("Logout")
        depopulateMessages()
        currentChatMessageListener = null

        Toast.makeText(
            context,
            "Logout Successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onAddChat(number: String, context: Context) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digit only")

        } else {
            //here check the number is already available
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.userNumber),
                        Filter.equalTo("user2.number",number),
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number",number ),
                        Filter.equalTo("user2.number", userData.value?.userNumber)
                    )
                )
            ).get().addOnSuccessListener { it ->
                //agar empty h toh user available nai h
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("userNumber", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                //ye bala number humare app ke database m available ni h
                                handleException(customMessage = "Number is Not Found from database")

                                Toast.makeText(
                                    context,
                                    "User is not register in this app!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.userName,
                                        userData.value?.userNumber,
                                        userData.value?.userImageUrl
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.userName,
                                        chatPartner.userNumber,
                                        chatPartner.userImageUrl,
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)
                            }
                        }.addOnFailureListener {
                            handleException(exception = it)
                        }

                }
            }
        }

    }

    fun populateChats() {
        inProgressChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )


        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(exception = error)
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    Log.d("DATA", "populateChats: ${it.data}")
                    it.toObject<ChatData>()
                }
                inProgressChats.value = false
            }

        }
    }

    fun uploadStatus(uri: Uri) {
        inProgressStatus.value = true
        uploadImg(uri = uri) {
            createStatus(it.toString())
            Log.d(
                "STATUS",
                "uploadStatus-------------------------------------------------------------------------------- "
            )
        }
    }

    private fun createStatus(imageUrl: String) {
        Log.d(
            "STATUS",
            "creating -------------------------------------------------------------------------------- "

        )
        val newStatus = Status(
            ChatUser(
                userId = userData.value?.userId,
                name = userData.value?.userName,
                number = userData.value?.userNumber,
                imageUrl = userData.value?.userImageUrl,
            ),
            imageUrl = imageUrl,
            timeStamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            db.collection(STATUS).document().set(newStatus)
        }
        Log.d(
            "STATUS",
            "create Node-------------------------------------------------------------------------------- "
        )
    }

    private fun populateStatuses() {
        Log.d("STATUS", "get Status-------------------------------------------------------------------------------- ")
        val timeDelta = 24L * 60 * 60 * 1000
        val currentTime = System.currentTimeMillis()
        val cutOff = currentTime - timeDelta
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId),
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(exception = error)
            }
            if (value != null) {
                //jinse m chat m connect hoon unke data ko store karne ke liye
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    //ye mere status h isko ni lana h
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnections.add(chat.user2.userId)
//                        inProgressStatus.value=false
                    } else {
                        currentConnections.add(chat.user1.userId)
//                        inProgressStatus.value = false
                    }
                }
                viewModelScope.launch {
                    db.collection(STATUS).whereGreaterThan("timeStamp", cutOff)
                        .whereIn("user.userId", currentConnections)
                        .addSnapshotListener { value1, error1 ->
                            if (error1 != null) {
                                handleException(exception = error1)
                                inProgressStatus.value = false
                            }
                            if (value1 != null) {
                                status.value = value1.toObjects()
                                Log.d(
                                    "STATUS",
                                    "set Status in value-------------------------------------------------------------------------------- "
                                )
                                inProgressStatus.value = false
                            }
                        }
                }
                //jinke sath baatcheet karta hoon unke status lana h
            }
        }
    }
}