package com.example.you.ui.fragments.splash

import android.animation.Animator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.you.R
import com.example.you.databinding.SplashFragmentBinding
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashFragmentBinding>(SplashFragmentBinding::inflate) {
    private val viewModel: SplashViewModel by viewModels()
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        init()
    }

    private fun init() {
        initAnim()
    }

    private fun initAnim() {
        binding.lotieAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                nextAction()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}

        })
    }

    private fun nextAction() {
        if (viewModel.checkSession())
            findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
        else
            findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
    }
}