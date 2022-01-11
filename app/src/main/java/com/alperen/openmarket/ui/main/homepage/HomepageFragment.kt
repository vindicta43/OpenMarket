package com.alperen.openmarket.ui.main.homepage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentHomepageBinding
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.viewmodel.BaseViewModel

class HomepageFragment : Fragment() {
    private lateinit var binding: FragmentHomepageBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    var recyclerData = mutableMapOf<String, ArrayList<Product>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            startRefresh(this)
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            ibSearch.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_searchFragment)
            }

            ibNotifications.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_notificationsFragment)
            }

            tvRecentlyShownClear.setOnClickListener {
                viewModel.clearRecentlyShown(viewLifecycleOwner).observe(viewLifecycleOwner) {
                    when (it) {
                        Constants.SUCCESS -> {
                            recyclerRecentlyShown.apply {
                                adapter = HomepageRecyclerViewAdapter(arrayListOf())
                                adapter?.notifyDataSetChanged()
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

            rootLayout.setOnRefreshListener {
                startRefresh(this)
            }
        }
    }

    private fun startRefresh(binding: FragmentHomepageBinding) {
        with(binding) {
            startAnim(binding)
            viewModel.getHomePage(viewLifecycleOwner).observe(viewLifecycleOwner) {
                stopAnim(this)
                recyclerData = it
                if (!it["recently"].isNullOrEmpty()) {
                    recyclerRecentlyShown.apply {
                        adapter = HomepageRecyclerViewAdapter(it["recently"]!!)
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    }
                }

                if (!it["products"].isNullOrEmpty()) {
                    recyclerMain.apply {
                        adapter = HomepageRecyclerViewAdapter(it["products"]!!)
                        layoutManager = GridLayoutManager(context, 2)
                    }
                }
            }
        }
    }

    private fun startAnim(binding: FragmentHomepageBinding) {
        with(binding) {
            recyclerMain.visibility = View.INVISIBLE
            recyclerRecentlyShown.visibility = View.INVISIBLE

            shimmerMain.apply {
                visibility = View.VISIBLE
                startShimmer()
            }

            shimmerRecentlyShown.apply {
                visibility = View.VISIBLE
                startShimmer()
            }
        }
    }

    private fun stopAnim(binding: FragmentHomepageBinding) {
        with(binding) {
            rootLayout.isRefreshing = false

            recyclerMain.visibility = View.VISIBLE
            recyclerRecentlyShown.visibility = View.VISIBLE

            shimmerMain.apply {
                visibility = View.INVISIBLE
                stopShimmer()
            }

            shimmerRecentlyShown.apply {
                visibility = View.INVISIBLE
                stopShimmer()
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentHomepageBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}