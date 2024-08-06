package com.example.chatterbox.ui.onboarding

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatterbox.R
import com.example.chatterbox.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityOnboardingBinding
    private lateinit var viewModel: OnboardingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //initViews()

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
    }
}