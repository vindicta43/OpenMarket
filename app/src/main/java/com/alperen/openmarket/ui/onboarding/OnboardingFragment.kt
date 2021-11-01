package com.alperen.openmarket.ui.onboarding

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.alperen.openmarket.R
import com.ramotion.paperonboarding.PaperOnboardingPage
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.alperen.openmarket.databinding.FragmentOnboardingBinding
import com.ramotion.paperonboarding.PaperOnboardingFragment


class OnboardingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnboardingBinding.inflate(inflater)

        with(binding) {
            val pageList = implementPages()

            val paperOnboardingFragment = PaperOnboardingFragment.newInstance(pageList)

            paperOnboardingFragment.setOnRightOutListener {
                root.findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }

            val manager = activity?.supportFragmentManager
            val transaction = manager?.beginTransaction()?.add(R.id.fragmentOnboarding, paperOnboardingFragment)
            transaction?.commit()

            return root
        }
    }

    private fun implementPages(): ArrayList<PaperOnboardingPage> {
        val scr1 = PaperOnboardingPage()
        scr1.descriptionText = "Sayfa 1 description"
        scr1.titleText = "Sayfa 1 title"

        val scr2 = PaperOnboardingPage()
        scr2.descriptionText = "Sayfa 2 description"
        scr2.titleText = "Sayfa 2 title"

        val scr3 = PaperOnboardingPage()
        scr3.descriptionText = "Sayfa 3 description"
        scr3.titleText = "Sayfa 3 title"

        return arrayListOf(scr1, scr2, scr3)
    }
}