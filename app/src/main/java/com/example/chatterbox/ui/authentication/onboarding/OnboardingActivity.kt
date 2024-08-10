package com.example.chatterbox.ui.authentication.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatterbox.R
import com.example.chatterbox.databinding.ActivityOnboardingBinding
import com.example.chatterbox.ui.authentication.login.LoginActivity
import com.example.chatterbox.ui.authentication.register.RegisterActivity
import com.example.chatterbox.ui.authentication.socialMediaLogin.SocialMediaLoginFragment
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

    private fun initViews() {
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        viewModel = ViewModelProvider(this)[OnboardingViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
        val socialMediaLoginFragment = SocialMediaLoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.social_media_onboarding_fr, socialMediaLoginFragment)
            .commit()
    }

    private fun subscribeToLiveData() {
        viewModel.events.observe(this, ::handleEvents)
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