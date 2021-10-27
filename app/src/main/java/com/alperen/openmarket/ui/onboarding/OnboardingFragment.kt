package com.alperen.openmarket.ui.onboarding

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentOnboardingBinding
import com.ramotion.paperonboarding.PaperOnboardingPage
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
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
        val scr1 = PaperOnboardingPage(
            "Sayfa 1",
            "Sayfa 1 açıklama",
            Color.RED,
            android.R.drawable.ic_dialog_dialer,
            android.R.drawable.ic_lock_power_off
        )

        val scr2 = PaperOnboardingPage(
            "Sayfa 2",
            "Sayfa 2 açıklama",
            Color.GREEN,
            android.R.drawable.ic_menu_week,
            android.R.drawable.dialog_frame
        )

        val scr3 = PaperOnboardingPage(
            "Sayfa 3",
            "Sayfa 3 açıklama",
            Color.BLUE,
            android.R.drawable.presence_online,
            android.R.drawable.zoom_plate
        )
        return arrayListOf(scr1, scr2, scr3)
    }
}