package com.alperen.openmarket.ui.main.profile

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProfileBinding
import com.alperen.openmarket.utils.LoadingFragment

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val loading by lazy { LoadingFragment() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        with(binding) {
            loading.show(childFragmentManager, "loaderProfile")
            rootLayout.visibility = View.INVISIBLE

            val timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    loading.dismissAllowingStateLoss()
                    rootLayout.visibility = View.VISIBLE
                }
            }
            timer.start()
            return root
        }
    }
}