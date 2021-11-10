package com.alperen.openmarket.ui.main.homepage

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentHomepageBinding
import com.alperen.openmarket.viewmodel.BaseViewModel

class HomepageFragment : Fragment() {
    private lateinit var binding: FragmentHomepageBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            setOnClickListeners(binding)
            rootLayout.setOnRefreshListener {
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

                val timer = object : CountDownTimer(4000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
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
            ibSearch.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_searchFragment)
            }

            ibNotifications.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_notificationsFragment)
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentHomepageBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}