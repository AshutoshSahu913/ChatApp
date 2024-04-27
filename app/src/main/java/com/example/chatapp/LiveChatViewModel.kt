package com.example.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.Data.CHATS
import com.example.chatapp.Data.ChatData
import com.example.chatapp.Data.ChatUser
import com.example.chatapp.Data.Event
import com.example.chatapp.Data.USER_NODE
import com.example.chatapp.Data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var inProgressChats = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    var chats = mutableStateOf<List<ChatData>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name: String, email: String, phoneNumber: String, password: String) {
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
                                } else {
                                    // Sign-up failed
                                    Log.e("SignUp", "Sign-up failed: $email", signUpTask.exception)
                                    handleException(signUpTask.exception, "Sign-up failed")
                                }
                            }
                    } else {
                        // Phone number already exists
                        handleException(customMessage = "Phone number already exists")
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
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot Retrieve User")
            }
            if (value != null) {
                val user = value.toObject<UserData>()
                userData.value = user
                Log.d("IMG", "ProfileContent: $user")
                populateChats()
                inProgress.value = false
            }
        }
    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill the all fields")
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
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

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.d("LiveChatApp", "Live chat exception : $exception")
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

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
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            inProgress.value = false
            Log.d("RESULT", "uploadImg: $result")
            result?.addOnSuccessListener(onSuccess)
        }
            .addOnFailureListener {
                handleException(exception = it)
                inProgress.value = false
            }
    }

    fun logout() {
        auth.signOut()
        signIn.value=false
        userData.value=null
        eventMutableState.value=Event("Logout")
    }

    fun onAddChat(number: String) {
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
            ).get().addOnSuccessListener {
                //agar empty h toh user available nai h
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("userNumber", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                //ye bala number humare app ke database m available ni h
                                handleException(customMessage = "Number is Not Found from database")
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
}