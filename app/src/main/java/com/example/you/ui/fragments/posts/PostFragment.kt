package com.example.you.ui.fragments.posts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.you.R
import com.example.you.databinding.PostFragmentBinding
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<PostFragmentBinding>(PostFragmentBinding::inflate){
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }
    private fun init(){

    }

}