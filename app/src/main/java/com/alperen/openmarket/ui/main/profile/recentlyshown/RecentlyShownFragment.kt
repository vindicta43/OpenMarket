package com.alperen.openmarket.ui.main.profile.recentlyshown

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentRecentlyShownBinding
import com.alperen.openmarket.utils.ProductRecyclerViewAdapter
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.BaseViewModel

class RecentlyShownFragment : Fragment() {
    private lateinit var binding: FragmentRecentlyShownBinding
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

            btnRecentlyShownClear.setOnClickListener { clear() }

            tvRecentlyShownClear.setOnClickListener { clear() }
        }
    }

    private fun clear() {
        with(binding) {
            viewModel.clearRecentlyShown(viewLifecycleOwner).observe(viewLifecycleOwner) {
                when (it) {
                    Constants.SUCCESS -> {
                        recyclerRecentlyShown.apply {
                            adapter = ProductRecyclerViewAdapter(arrayListOf(), "RecentlyShownFragment")
                            adapter?.notifyDataSetChanged()

                            // tvRecentlyShownEmpty.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        AlertDialog.Builder(context)
                            .setMessage(it)
                            .setPositiveButton(Constants.OK) { _, _ -> }
                            .show()
                    }
                }
            }

        }
    }

    private fun startRefresh() {
        with(binding) {
            shimmerRecentlyShown.apply {
                visibility = View.VISIBLE
                startShimmer()
            }
            viewModel.getUserRecentlyShown(viewLifecycleOwner).observe(viewLifecycleOwner) {
                shimmerRecentlyShown.apply {
                    visibility = View.INVISIBLE
                    stopShimmer()
                }

                recyclerRecentlyShown.apply {
                    adapter = ProductRecyclerViewAdapter(it, "RecentlyShownFragment")
                    layoutManager = GridLayoutManager(context, 2)
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentRecentlyShownBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}