package com.alperen.openmarket.ui.splash

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentSplashBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            val sharedPref = activity?.getSharedPreferences(Constants.APP_INIT, Context.MODE_PRIVATE)
            val isInit = sharedPref?.getBoolean(Constants.APP_INIT, false)!!
            if (isInit) {
                navController.navigate(R.id.action_splashFragment_to_loginFragment)
            }

            splashAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (FirebaseInstance.user?.isEmailVerified == true) {
                        navController.navigate(R.id.action_splashFragment_to_mainActivity)
                        activity?.finish()
                    } else {
                        navController.navigate(R.id.action_splashFragment_to_onboardingFragment)
                    }
                }

                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentSplashBinding.inflate(inflater)
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerLogin) as NavHostFragment
        navController = navHostFragment.navController
    }
}