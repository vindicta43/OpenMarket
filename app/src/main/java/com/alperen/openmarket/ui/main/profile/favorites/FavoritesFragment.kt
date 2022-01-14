package com.alperen.openmarket.ui.main.profile.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentFavoritesBinding
import com.alperen.openmarket.utils.ProductRecyclerViewAdapter
import com.alperen.openmarket.utils.BaseViewModel

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)
        with(binding) {
            startRefresh()
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            btnBack.setOnClickListener { navController.popBackStack() }
        }
    }

    private fun startRefresh() {
        with(binding) {
            shimmerRecentlyShown.apply {
                visibility = View.VISIBLE
                startShimmer()
            }
            viewModel.getUserFavorites(viewLifecycleOwner).observe(viewLifecycleOwner) {
                shimmerRecentlyShown.apply {
                    visibility = View.INVISIBLE
                    stopShimmer()
                }

                recyclerRecentlyShown.apply {
                    adapter = ProductRecyclerViewAdapter(it, "FavoritesFragment")
                    layoutManager = GridLayoutManager(context, 2)
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentFavoritesBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}