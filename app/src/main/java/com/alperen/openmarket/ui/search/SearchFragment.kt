package com.alperen.openmarket.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater)

        with(binding) {
            searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->

            }

            val list = arrayListOf("a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a",)
            recyclerSearch.apply {
                adapter = SearchRecyclerViewAdapter(list)
                layoutManager = LinearLayoutManager(root.context)
                addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
            }

            return root
        }
    }
}