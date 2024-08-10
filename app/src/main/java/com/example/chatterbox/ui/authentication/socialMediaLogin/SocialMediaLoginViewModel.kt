package com.example.chatterbox.ui.authentication.socialMediaLogin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatterbox.R
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
class SocialMediaLoginViewModel @Inject constructor(private val appContext: Context) : ViewModel() {

    private val _signInResult = MutableLiveData<Result<FirebaseUser?>>()
    val signInResult: LiveData<Result<FirebaseUser?>> get() = _signInResult
    val googleSignInClicked = MutableLiveData<Intent>()
    private val auth = Firebase.auth

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
}