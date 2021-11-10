package com.alperen.openmarket.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            btnBack.setOnClickListener {
                navController.popBackStack()
            }

            val list = arrayListOf("a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a",)
            recyclerNotifications.adapter = NotificationsRecyclerViewAdapter(list)
            recyclerNotifications.layoutManager = LinearLayoutManager(root.context)

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentNotificationsBinding.inflate(inflater)
        navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}