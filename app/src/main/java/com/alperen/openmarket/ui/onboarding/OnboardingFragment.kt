package com.alperen.openmarket.ui.onboarding

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.alperen.openmarket.R
import com.ramotion.paperonboarding.PaperOnboardingPage
import androidx.navigation.fragment.NavHostFragment
import com.alperen.openmarket.databinding.FragmentOnboardingBinding
import com.alperen.openmarket.utils.Constants
import com.ramotion.paperonboarding.PaperOnboardingFragment


class OnboardingFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            val pageList = implementPages()
            val paperOnboardingFragment = PaperOnboardingFragment.newInstance(pageList)

            paperOnboardingFragment.setOnRightOutListener {
                val sharedPref = activity?.getSharedPreferences(Constants.ONBOARDING_LAUNCHED, Context.MODE_PRIVATE)
                sharedPref?.edit()?.putBoolean(Constants.ONBOARDING_LAUNCHED, true)?.apply()
                navController.navigate(R.id.action_onboardingFragment_to_loginFragment)
            }

            val manager = activity?.supportFragmentManager
            val transaction = manager?.beginTransaction()?.add(R.id.fragmentOnboarding, paperOnboardingFragment)
            transaction?.commit()

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentOnboardingBinding.inflate(inflater)
        navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerLogin) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun implementPages(): ArrayList<PaperOnboardingPage> {
        val scr1 = PaperOnboardingPage()
        scr1.titleText = "Merhaba"
        scr1.descriptionText = Constants.ONBOARDING_1
        scr1.bgColor = Color.WHITE
        scr1.contentIconRes = R.drawable.ic_onboarding_1

        val scr2 = PaperOnboardingPage()
        scr2.titleText = ""
        scr2.descriptionText = Constants.ONBOARDING_2
        scr2.bgColor = Color.WHITE
        scr2.contentIconRes = R.drawable.ic_onboarding_2

        val scr3 = PaperOnboardingPage()
        scr3.titleText = "Haydi başlayalım"
        scr3.descriptionText = Constants.ONBOARDING_3
        scr3.bgColor = Color.WHITE
        scr3.contentIconRes = R.drawable.ic_onboarding_3

        return arrayListOf(scr1, scr2, scr3)
    }
}