package com.example.you.ui.fragments.auth.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.you.databinding.RegistrationFragmentBinding
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<RegistrationFragmentBinding>(RegistrationFragmentBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {

    }

}