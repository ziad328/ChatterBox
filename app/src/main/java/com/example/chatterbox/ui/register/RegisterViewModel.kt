package com.example.chatterbox.ui.register

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatterbox.R
import com.example.chatterbox.firestore.dao.UsersDao
import com.example.chatterbox.firestore.model.User
import com.example.chatterbox.sessionProvider.SessionProvider
import com.example.chatterbox.utils.Message
import com.example.chatterbox.utils.SingleLiveEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val appContext: Context) : ViewModel() {
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

    private val _signInResult = MutableLiveData<Result<FirebaseUser?>>()
    val signInResult: LiveData<Result<FirebaseUser?>> get() = _signInResult
    val googleSignInClicked = MutableLiveData<Intent>()

    private lateinit var googleSignInClient: GoogleSignInClient

    fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(appContext, gso)
    }

    fun onGoogleSignInClick() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClicked.postValue(signInIntent)
    }

    fun handleSignInResult(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            _signInResult.value = Result.failure(e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInResult.value = Result.success(auth.currentUser)
                } else {
                    _signInResult.value =
                        Result.failure(task.exception ?: Exception("Authentication failed"))
                }
            }
    }

    fun register() {
        if (!valid()) return
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    insertUserToFirestore(task.result.user?.uid)
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
        val user = User(id = uid, username = userName.value, email = email.value)
        UsersDao.createUser(user) { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                //saving user
                SessionProvider.user = user
                //navigate to home screen
                navigateToHomeActivity()
            } else {
                messageLiveData.postValue(Message(task.exception?.localizedMessage))
            }

        }
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

    companion object {
        const val RC_SIGN_IN = 9001
    }

}