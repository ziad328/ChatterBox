package com.example.chatterbox.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatterbox.R
import com.example.chatterbox.databinding.ActivityLoginBinding
import com.example.chatterbox.utils.showAlertDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
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
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        viewBinding.lifecycleOwner = this
        viewBinding.vm = viewModel
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
        viewModel.events.observe(this, ::handleEvents)
    }

    private fun handleEvents(loginViewEvents: LoginViewEvents?) {
        TODO("Not yet implemented")
    }
}