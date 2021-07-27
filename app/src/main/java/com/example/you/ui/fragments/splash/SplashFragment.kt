package com.example.you.ui.fragments.splash

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.you.R
import com.example.you.databinding.SplashFragmentBinding
import com.example.you.extensions.createInfoSnackBar
import com.example.you.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment: BaseFragment<SplashFragmentBinding>(SplashFragmentBinding::inflate){
    private val viewModel: SplashViewModel by viewModels()
    private fun animation(){
        binding.animation.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }
            override fun onAnimationEnd(animation: Animator?) {
                nextAction()
            }
            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
    }
    private fun nextAction(){
        if(viewModel.checkSession()){
            findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
        }else{
            findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
        }


    }
    override fun start(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        if(!connectionChecker()){
            animation()
        }else{
            createInfoSnackBar("please connect to internet", Color.BLACK)
        }
    }
    private fun connectionChecker(): Boolean {
        val manager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo == null
    }

}