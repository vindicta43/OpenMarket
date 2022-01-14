package com.alperen.openmarket.ui.main.profile.cards.usercards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentCreditCardsBinding
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.utils.BaseViewModel


class CreditCardsFragment : Fragment() {
    private lateinit var binding: FragmentCreditCardsBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            loading.show(childFragmentManager, "loaderCards")
            viewModel.getUserCreditCards(viewLifecycleOwner).observe(viewLifecycleOwner) {
                loading.dismissAllowingStateLoss()
                recyclerUserCards.adapter = CreditCardsRecyclerViewAdapter(requireActivity(), it)
                recyclerUserCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            btnBack.setOnClickListener {
                navController.popBackStack()
            }

            btnAddCreditCard.setOnClickListener {
                navController.navigate(R.id.action_creditCardsFragment_to_addCreditCardFragment)
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentCreditCardsBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}