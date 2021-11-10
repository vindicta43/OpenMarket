package com.alperen.openmarket.ui.main.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProfileBinding
import com.alperen.openmarket.ui.login.LoginActivity
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            loading.show(childFragmentManager, "loaderProfile")
            rootLayout.visibility = View.INVISIBLE

            viewModel.getUserProfile(viewLifecycleOwner).observe(viewLifecycleOwner) {
                when (it) {
                    Constants.SUCCESS -> {
                        loading.dismissAllowingStateLoss()
                        rootLayout.visibility = View.VISIBLE
                        val profile = FirebaseInstance.profile

                        tvUsername.text = profile.value?.username
                        tvNameSurname.text = "${profile.value?.name} ${profile.value?.surname}"
                        tvAddedProduct.text = profile.value?.added_product.toString()
                        tvCommentCount.text = profile.value?.comment_count.toString()
                        tvPurchasedProduct.text = profile.value?.purchased_product.toString()
                    }
                    else -> {

                    }
                }
            }

            btnLogout.setOnClickListener {
                val sharedPref =
                    activity?.getSharedPreferences(Constants.APP_INIT, Context.MODE_PRIVATE)
                sharedPref?.edit()?.putBoolean(Constants.APP_INIT, true)?.apply()
                viewModel.logout()

                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentProfileBinding.inflate(inflater)
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