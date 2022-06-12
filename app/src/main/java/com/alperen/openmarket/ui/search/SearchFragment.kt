package com.alperen.openmarket.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
            val baseProductNames = arrayListOf<Product>()
            var productNames = arrayListOf<Product>()
            val productCategories = mutableSetOf<String>()

            viewModel.getHomePage(viewLifecycleOwner).observe(viewLifecycleOwner) { productsResult ->
                productsResult.forEach {
                    baseProductNames.add(it)
                }
                // First initialization
                productNames = baseProductNames

                productCategories.add("Tüm Kategoriler")
                for (i in baseProductNames) {
                    productCategories.add(i.category)
                }

                val arrayAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productCategories.toList())
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spnSearchCategory.adapter = arrayAdapter

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

            spnSearchCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Log.d("spinner", "selected item pos: $position content: ${productCategories.toList()[position]}")

                    if (position == 0) {
                        productNames = baseProductNames
                    } else {
                        val category = productCategories.toList()[position]

                        productNames = baseProductNames
                        productNames = productNames.filter { s -> s.category == category }.toCollection(ArrayList())

                        recyclerSearch.adapter =
                            SearchRecyclerViewAdapter(productNames, object : FragmentCommunication {
                                override fun respond(product: Product) {
                                    searchView.setQuery(product.name, false)
                                }
                            })
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

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