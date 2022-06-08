package com.alperen.openmarket.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentSearchBinding
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.BaseViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            val productNames = arrayListOf<Product>()
            viewModel.getHomePage(viewLifecycleOwner).observe(viewLifecycleOwner) { productsResult ->
                productsResult.forEach {
                    productNames.add(it)
                }
                recyclerSearch.apply {
                    adapter = SearchRecyclerViewAdapter(productNames, object : FragmentCommunication {
                        override fun respond(product: Product) {
                            searchView.setQuery(product.name, false)
                        }
                    })
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val filter = productNames.filter { s -> s.name.contains(newText.toString()) }
                    recyclerSearch.adapter =
                        SearchRecyclerViewAdapter(filter.toCollection(ArrayList()), object : FragmentCommunication {
                            override fun respond(product: Product) {
                                searchView.setQuery(product.name, false)
                            }
                        })
                    return false
                }
            })

            recyclerSearch.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentSearchBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}