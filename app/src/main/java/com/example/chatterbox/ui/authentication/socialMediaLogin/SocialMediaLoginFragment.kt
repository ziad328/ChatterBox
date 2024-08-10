package com.example.chatterbox.ui.authentication.socialMediaLogin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.chatterbox.databinding.FragmentSocialMediaBinding
import com.example.chatterbox.ui.home.HomeActivity
import com.example.chatterbox.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialMediaLoginFragment : Fragment() {
    private lateinit var viewBinding: FragmentSocialMediaBinding
    private val viewModel by viewModels<SocialMediaLoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSocialMediaBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.vm = viewModel
        viewModel.configureGoogleSignIn()
        subscribeToLiveData()
    }


    private fun subscribeToLiveData() {
        viewModel.signInResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->
                if (user != null) {
                    Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT)
                        .show()
                    navigateToHomeActivity()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Sign in failed: User is null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Sign in failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        viewModel.googleSignInClicked.observe(viewLifecycleOwner) {
            startActivityForResult(it, SharedPref.RC_SIGN_IN)
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SharedPref.RC_SIGN_IN) {
            data?.let {
                viewModel.handleSignInResult(it)
            }
        }
    }
}