package com.example.chatterbox.ui.onboarding

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
import com.example.chatterbox.databinding.ActivityOnboardingBinding
import com.example.chatterbox.ui.home.HomeActivity
import com.example.chatterbox.ui.login.LoginActivity
import com.example.chatterbox.ui.register.RegisterActivity
import com.example.chatterbox.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityOnboardingBinding
    private lateinit var viewModel: OnboardingViewModel
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
        if (requestCode == SharedPref.RC_SIGN_IN) {
            data?.let {
                viewModel.handleSignInResult(it)
            }
        }
    }

    private fun initViews() {
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        viewModel = ViewModelProvider(this)[OnboardingViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
        viewModel.configureGoogleSignIn()
    }

    private fun subscribeToLiveData() {
        viewModel.events.observe(this, ::handleEvents)
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
        viewModel.googleSignInClicked.observe(this) {
            startActivityForResult(it, SharedPref.RC_SIGN_IN)
        }
    }

    private fun handleEvents(onboardingViewEvents: OnboardingViewEvents?) {
        when (onboardingViewEvents) {
            OnboardingViewEvents.NavigateToLogin -> {
                navigateToLoginActivity()
            }

            OnboardingViewEvents.NavigateToRegister -> {
                navigateToRegisterActivity()
            }

            else -> {}
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}