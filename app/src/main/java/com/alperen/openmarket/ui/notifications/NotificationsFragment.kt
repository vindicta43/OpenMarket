package com.alperen.openmarket.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNotificationsBinding.inflate(inflater)

        with(binding) {
            btnBack.setOnClickListener {
                root.findNavController().popBackStack()
            }

            val list = arrayListOf("a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a",)
            recyclerNotifications.adapter = NotificationsRecyclerViewAdapter(list)
            recyclerNotifications.layoutManager = LinearLayoutManager(root.context)

            return root
        }
    }
}