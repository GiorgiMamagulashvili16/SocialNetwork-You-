package com.example.you.ui.fragments.auth.logIn

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.you.R
import com.example.you.databinding.LogInFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.extensions.slideUp
import com.example.you.ui.base.BaseFragment
import com.example.you.ui.fragments.dashboard.string
import com.example.you.util.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : BaseFragment<LogInFragmentBinding>(LogInFragmentBinding::inflate) {
    private val viewModel: LogInViewModel by viewModels()

    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        binding.btnSignIn.setOnClickListener {
            logIn()
        }
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_registrationFragment)
        }
        observe()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        slideUp(
            requireContext(),
            binding.imageView,
            binding.etEmail,
            binding.textView1,
            binding.textView2,
            binding.etPassword,
            binding.btnSignIn
        )
    }

    override fun onStart() {
        super.onStart()
        val user = Firebase.auth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.action_logInFragment_to_dashboardFragment)
        }
    }

    private fun logIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            createInfoSnackBar(getString(string.please_fill_all_fields), Color.RED)
        } else {
            viewModel.logIn(email, password)
        }
    }

    private fun observe() {
        viewModel.logInResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    dismissLoadingDialog()
                    findNavController().navigate(R.id.action_logInFragment_to_dashboardFragment)
                }
                is Resource.Error -> {
                    it.errorMessage?.let { message -> showErrorDialog(message) }
                    dismissLoadingDialog()
                }
                is Resource.Loading -> {
                    showLoadingDialog()

                }
            }
        })
    }
}