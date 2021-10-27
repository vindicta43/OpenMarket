package com.alperen.openmarket.ui.splash

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSplashBinding.inflate(inflater)

        with(binding) {
            splashAnimationView.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationEnd(animation: Animator?) {
                    root.findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
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
}