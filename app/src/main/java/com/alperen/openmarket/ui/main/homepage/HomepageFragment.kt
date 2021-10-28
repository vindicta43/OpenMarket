package com.alperen.openmarket.ui.main.homepage

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentHomepageBinding

class HomepageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomepageBinding.inflate(inflater)

        with(binding) {
            setOnClickListeners(binding)
            rootLayout.setOnRefreshListener {
                val timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    rootLayout.isRefreshing = false
                    Toast.makeText(activity, "Refreshed", Toast.LENGTH_SHORT).show()
                }
            }
                timer.start()
            }

            val list = arrayListOf("a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a")

            recyclerRecentlyShown.adapter = HomepageRecyclerViewAdapter(list)
            recyclerRecentlyShown.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)

            recyclerMain.adapter = HomepageRecyclerViewAdapter(list)
            recyclerMain.layoutManager = GridLayoutManager(root.context, 2)

            return root
        }
    }

    private fun setOnClickListeners(binding: FragmentHomepageBinding) {
        with(binding) {
            ivProfile.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_profileFragment)
            }

            ibSearch.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_searchFragment)
            }

            ibNotifications.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_notificationsFragment)
            }
        }
    }
}