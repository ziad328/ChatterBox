package com.example.chatterbox.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatterbox.utils.Message
import com.example.chatterbox.utils.SingleLiveEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterViewModel : ViewModel() {
    val messageLiveData = SingleLiveEvent<Message>()
    val isLoading = MutableLiveData<Boolean>()
    val events = SingleLiveEvent<RegisterViewEvents>()
    private val auth = Firebase.auth

    val userName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirmation = MutableLiveData<String>()
    val userNameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmationError = MutableLiveData<String?>()

    fun register() {
        if (!valid()) return
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isLoading.value = false
                    messageLiveData.postValue(Message(task.result?.user?.uid))
                    navigateToHomeActivity()
                    // insertUserToFirestore(task.result.user?.uid)
                } else {
                    isLoading.value = false
                    messageLiveData.postValue(Message(task.exception?.localizedMessage))
                }
            }
    }

    private fun navigateToHomeActivity() {
        events.postValue(RegisterViewEvents.NavigateToHome)
    }

    fun navigateToLogin() {
        events.postValue(RegisterViewEvents.NavigateToLogin)
    }
    
    private fun insertUserToFirestore(uid: String?) {
        TODO("Not yet implemented")
    }


    private fun valid(): Boolean {
        var isValid = true
        if (userName.value.isNullOrBlank()) {
            userNameError.postValue("please enter user name")
            isValid = false
        } else {
            userNameError.postValue(null)
        }
        if (email.value.isNullOrBlank()) {
            emailError.postValue("please enter email")
            isValid = false
        } else {
            emailError.postValue(null)
        }
        if (password.value.isNullOrBlank()) {
            passwordError.postValue("please enter password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }
        if (passwordConfirmation.value.isNullOrBlank() || passwordConfirmation.value != password.value) {
            passwordConfirmationError.postValue("please confirm password")
            isValid = false
        } else {
            passwordConfirmationError.postValue(null)
        }
        return isValid
    }
}