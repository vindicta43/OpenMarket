package com.alperen.openmarket.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentLoginBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodels.BaseViewModel

class LoginFragment : Fragment() {
    private lateinit var viewModel: BaseViewModel
    val loading by lazy { LoadingFragment() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater)

        with(binding) {
            setListenersForText(this)
            setOnClickListeners(this)
            return root
        }
    }

    private fun setOnClickListeners(binding: FragmentLoginBinding) {
        viewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        with(binding) {
            val email = etEmail.text
            val password = etPassword.text

            btnLogin.setOnClickListener {
                if (checkFields(email, password)) {
                    viewModel.login(email.toString(), password.toString(), viewLifecycleOwner)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PROCESSING -> {
                                    loading.show(childFragmentManager, "loader")
                                }
                                Constants.SUCCESS -> {
                                    AlertDialog.Builder(root.context).setMessage(it).show()
                                    loading.dismissAllowingStateLoss()
                                    root.findNavController()
                                        .navigate(R.id.action_loginFragment_to_mainActivity)
                                    activity?.finish()
                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(root.context).setMessage(it)
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

            btnRegister.setOnClickListener {
                root.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnForgotPassword.setOnClickListener {
                if (!email.isNullOrEmpty()) {
                    viewModel.sendResetEmail(email.toString(), viewLifecycleOwner)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PROCESSING -> {
                                    loading.show(childFragmentManager, "loader")
                                }
                                Constants.SUCCESS -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(root.context).setMessage(it).show()
                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(root.context).setMessage(it)
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

    private fun checkFields(email: Editable?, password: Editable?): Boolean {
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    private fun setListenersForText(binding: FragmentLoginBinding) {
        with(binding) {
            etEmail.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutEmail.isErrorEnabled = false
                }
            }

            etPassword.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutPassword.isErrorEnabled = false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}