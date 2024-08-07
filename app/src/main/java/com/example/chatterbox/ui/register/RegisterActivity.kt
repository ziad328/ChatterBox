package com.example.chatterbox.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chatterbox.R
import com.example.chatterbox.databinding.ActivityRegisterBinding
import com.example.chatterbox.ui.home.HomeActivity
import com.example.chatterbox.ui.login.LoginActivity
import com.example.chatterbox.ui.login.LoginViewModel
import com.example.chatterbox.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViews()
        subscribeToLiveData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RegisterViewModel.RC_SIGN_IN) {
            data?.let {
                viewModel.handleSignInResult(it)
            }
        }
    }

    private fun initViews() {
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
        viewModel.configureGoogleSignIn()
    }

    private fun subscribeToLiveData() {
        viewModel.messageLiveData.observe(this) { message ->
            showAlertDialog(
                message = message.message ?: "something went wrong",
                posActionName = "Ok",
                posAction = message.onPosActionClick,
                negActionName = message.negActionName,
                negAction = message.onNegActionClick,
                isCancellable = message.isCancellable
            )
        }
        viewModel.signInResult.observe(this, Observer { result ->
            result.onSuccess { user ->
                if (user != null) {
                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                    navigateToHomeActivity()
                    finish()
                } else {
                    Toast.makeText(this, "Sign in failed: User is null", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(this, "Sign in failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.events.observe(this, ::handleEvents)
        viewModel.googleSignInClicked.observe(this) {
            startActivityForResult(it, LoginViewModel.RC_SIGN_IN)
        }
    }

    private fun handleEvents(registerViewEvent: RegisterViewEvents?) {
        when (registerViewEvent) {
            RegisterViewEvents.NavigateToHome -> {
                navigateToHomeActivity()
            }

            RegisterViewEvents.NavigateToLogin -> {
                navigateToLoginActivity()
            }

            else -> {

            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}