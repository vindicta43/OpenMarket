package com.alperen.openmarket.ui.main.homepage

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.viewpager2.widget.ViewPager2
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentHomepageBinding
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.BaseViewModel
import com.alperen.openmarket.utils.ProductRecyclerViewAdapter
import java.util.*

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
            startRefresh()
            return root
        }
    }

    override fun onResume() {
        var currentPage = 0
        val NUM_PAGES = 3
        super.onResume()
        with(binding) {




            val handler = Handler()

            val update = Runnable {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0
                }
                productSponsoredPager.setCurrentItem(currentPage++, true)
            }

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            }, 1000, 2000)

            productSponsoredPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPage = position
                }
            })

            ibSearch.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_searchFragment)
            }

            ibNotifications.setOnClickListener {
                root.findNavController().navigate(R.id.action_homepageFragment_to_notificationsFragment)
            }

            rootLayout.setOnRefreshListener {
                startRefresh()
            }
        }
    }

    private fun startRefresh() {
        with(binding) {
            startAnim(binding)
            viewModel.getHomePage(viewLifecycleOwner).observe(viewLifecycleOwner) {
                stopAnim(this)

                recyclerMain.apply {
                    adapter = ProductRecyclerViewAdapter(it, "HomepageFragment")
                    layoutManager = GridLayoutManager(context, 2)
                }

                val sponsoredProducts = arrayListOf<Product>()
                if (it.size >= 3) {
                    sponsoredProducts.ensureCapacity(3)
                    for (i in 0..3) {
                        sponsoredProducts.add(it.random())
                    }
                } else {
                    if(it.isNotEmpty()) {
                        sponsoredProducts.ensureCapacity(it.size)
                        for (i in 0..it.size) {
                            sponsoredProducts.add(it.random())
                        }
                    }
                }
                productSponsoredPager.adapter = HomepageViewPagerAdapter(sponsoredProducts)

//                if (!it["recently"].isNullOrEmpty()) {
//                    productSponsoredPager.apply {
//                        adapter = HomepageViewPagerAdapter(it["recently"]!!)
//                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                    }
//                } else {
//
//                }
//
//                if (!it["products"].isNullOrEmpty()) {
//
//                }
            }
        }
    }

    private fun startAnim(binding: FragmentHomepageBinding) {
        with(binding) {
            recyclerMain.visibility = View.VISIBLE
//            recyclerRecentlyShown.visibility = View.VISIBLE

            shimmerMain.apply {
                visibility = View.VISIBLE
                startShimmer()
            }

//            shimmerRecentlyShown.apply {
//                visibility = View.VISIBLE
//                startShimmer()
//            }
        }
    }

    private fun stopAnim(binding: FragmentHomepageBinding) {
        with(binding) {
            rootLayout.isRefreshing = false

            recyclerMain.visibility = View.VISIBLE
//            recyclerRecentlyShown.visibility = View.VISIBLE

            shimmerMain.apply {
                visibility = View.INVISIBLE
                stopShimmer()
            }

//            shimmerRecentlyShown.apply {
//                visibility = View.INVISIBLE
//                stopShimmer()
//            }
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