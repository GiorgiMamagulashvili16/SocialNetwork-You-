package com.example.you.ui.fragments.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.you.databinding.ProfileFragmentBinding
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileFragmentBinding>(ProfileFragmentBinding::inflate) {
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }
    private fun init(){
    }

}