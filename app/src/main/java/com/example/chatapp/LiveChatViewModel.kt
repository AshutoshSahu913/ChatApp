package com.example.chatapp

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.chatapp.Data.Event
import com.example.chatapp.Data.USER_NODE
import com.example.chatapp.Data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

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

    private fun createAndUpdateProfile(
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

        uid?.let {
            inProgress.value =
                true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    //if data is exists so update the data
                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
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
                inProgress.value = false
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.d("LiveChatApp", "Live chat exception : $exception")
        exception?.printStackTrace()
        var errorMsg = exception?.localizedMessage ?: ""
        var message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false


    }
}