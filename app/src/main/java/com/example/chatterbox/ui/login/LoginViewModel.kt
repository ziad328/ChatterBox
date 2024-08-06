package com.example.chatterbox.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatterbox.firestore.dao.UsersDao
import com.example.chatterbox.firestore.model.User
import com.example.chatterbox.sessionProvider.SessionProvider
import com.example.chatterbox.utils.Message
import com.example.chatterbox.utils.SingleLiveEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginViewModel : ViewModel() {
    val messageLiveData = SingleLiveEvent<Message>()
    val isLoading = MutableLiveData<Boolean>()
    val events = SingleLiveEvent<LoginViewEvents>()
    private val auth = Firebase.auth

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    fun login() {
        if (!valid()) return
        isLoading.value = true
        auth.signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getUserFromFirestore(task.result.user?.uid)
                } else {
                    isLoading.value = false
                    messageLiveData.postValue(Message(task.exception?.localizedMessage))
                }
            }
    }

    private fun getUserFromFirestore(uid: String?) {
        UsersDao.getUser(uid) { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                val user = task.result.toObject(User::class.java)
                SessionProvider.user = user
                navigateToHomeActivity()
            } else {
                messageLiveData.postValue(Message(task.exception?.localizedMessage))
            }
        }
    }

    private fun navigateToHomeActivity() {
        events.postValue(LoginViewEvents.NavigateToHome)
    }

    fun navigateToRegisterActivity() {
        events.postValue(LoginViewEvents.NavigateToRegister)
    }


    private fun valid(): Boolean {
        var isValid = true
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
        return isValid
    }
}