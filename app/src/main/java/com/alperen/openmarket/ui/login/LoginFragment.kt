package com.alperen.openmarket.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentLoginBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class LoginFragment : Fragment() {
    private lateinit var viewModel: BaseViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    val loading by lazy { LoadingFragment() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            setListenersForText(this)
            setOnClickListeners(this)
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            setListenersForText(this)
            setOnClickListeners(this)
        }
    }

    private fun setOnClickListeners(binding: FragmentLoginBinding) {
        with(binding) {
            val email = etEmail.text
            val password = etPassword.text

            btnRegister.setOnClickListener {
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnLogin.setOnClickListener {
                if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                    viewModel.login(email.toString(), password.toString(), viewLifecycleOwner)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PROCESSING -> {
                                    loading.show(childFragmentManager, "loaderLogin")
                                }
                                Constants.SUCCESS -> {
                                    if (btnForgetMe.isChecked) {
                                        val sharedPref = requireActivity().getSharedPreferences(
                                            Constants.FORGET_ME,
                                            Context.MODE_PRIVATE
                                        )
                                        sharedPref?.edit()?.putBoolean(Constants.FORGET_ME, true)?.apply()
                                    }
                                    loading.dismissAllowingStateLoss()
                                    navController.navigate(R.id.action_loginFragment_to_mainActivity)
                                    activity?.finish()

                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context).setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ ->

                                        }.show()
                                }
                            }
                        }

                } else {
                    if (email.isNullOrEmpty())
                        textInputLayoutEmail.error = Constants.FIELD_REQUIRED

                    if (password.isNullOrEmpty())
                        textInputLayoutPassword.error = Constants.FIELD_REQUIRED
                }
            }

            btnForgotPassword.setOnClickListener {
                if (!email.isNullOrEmpty()) {
                    viewModel.sendResetEmail(email.toString(), viewLifecycleOwner)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PROCESSING -> {
                                    loading.show(childFragmentManager, "loaderLogin")
                                }
                                Constants.RESET_MAIL_SUCCESS -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(root.context).setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ ->

                                        }.show()
                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context).setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ ->

                                        }.show()
                                }
                            }
                        }
                } else {
                    textInputLayoutEmail.error = Constants.EMAIL_REQUIRED
                }
            }
        }
    }

    private fun setListenersForText(binding: FragmentLoginBinding) {
        with(binding) {
            etEmail.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutEmail.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutEmail.isErrorEnabled = false
                }
            }

            etPassword.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutPassword.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutPassword.isErrorEnabled = false
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentLoginBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerLogin) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}