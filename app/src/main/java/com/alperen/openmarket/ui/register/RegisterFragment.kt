package com.alperen.openmarket.ui.register

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
import com.alperen.openmarket.databinding.FragmentRegisterBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class RegisterFragment : Fragment() {
    private lateinit var viewModel: BaseViewModel
    private lateinit var binding: FragmentRegisterBinding
    private val loading by lazy { LoadingFragment() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)

        with(binding) {
            addListenerForText(this)
            addSetOnClickListeners(this)
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            addListenerForText(this)
            addSetOnClickListeners(this)
        }
    }

    private fun addSetOnClickListeners(binding: FragmentRegisterBinding) {
        viewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        with(binding) {
            val username = etUsername.text
            val email = etEmail.text
            val name = etName.text
            val surname = etSurname.text
            val password = etPassword.text
            val passwordApply = etPasswordApply.text

            btnBack.setOnClickListener {
                root.findNavController().popBackStack()
            }

            btnRegister.setOnClickListener {
                if (checkFields(username, email, name, surname, password, passwordApply)) {
                    if (password.toString() == passwordApply.toString()) {
                        viewModel.register(
                            username.toString(),
                            email.toString(),
                            name.toString(),
                            surname.toString(),
                            password.toString(),
                            viewLifecycleOwner
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PROCESSING -> {
                                    loading.show(childFragmentManager, "loaderRegister")
                                }
                                Constants.REGISTER_SUCCESS -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(root.context)
                                        .setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ ->
                                            root.findNavController().popBackStack()
                                        }
                                        .show()
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
                        textInputLayoutPassword.error = Constants.FIELDS_BE_SAME
                        textInputLayoutPasswordApply.error = Constants.FIELDS_BE_SAME
                    }
                } else {
                    setError(this)
                }
            }
        }
    }

    private fun setError(binding: FragmentRegisterBinding) {
        with(binding) {
            val username = etUsername.text
            val email = etEmail.text
            val name = etName.text
            val surname = etSurname.text
            val password = etPassword.text
            val passwordApply = etPasswordApply.text

            if (username.isNullOrEmpty())
                textInputLayoutUsername.error = Constants.FIELD_REQUIRED
            if (email.isNullOrEmpty())
                textInputLayoutEmail.error = Constants.FIELD_REQUIRED
            if (name.isNullOrEmpty())
                textInputLayoutName.error = Constants.FIELD_REQUIRED
            if (surname.isNullOrEmpty())
                textInputLayoutSurname.error = Constants.FIELD_REQUIRED
            if (password.isNullOrEmpty())
                textInputLayoutPassword.error = Constants.FIELD_REQUIRED
            if (passwordApply.isNullOrEmpty())
                textInputLayoutPasswordApply.error = Constants.FIELD_REQUIRED
        }
    }

    private fun addListenerForText(binding: FragmentRegisterBinding) {
        with(binding) {
            etUsername.addTextChangedListener{
                if (!it.isNullOrEmpty())
                    textInputLayoutUsername.isErrorEnabled = false
            }

            etEmail.addTextChangedListener {
                if (!it.isNullOrEmpty())
                    textInputLayoutEmail.isErrorEnabled = false
            }

            etName.addTextChangedListener {
                if (!it.isNullOrEmpty())
                    textInputLayoutName.isErrorEnabled = false
            }

            etSurname.addTextChangedListener {
                if (!it.isNullOrEmpty())
                    textInputLayoutSurname.isErrorEnabled = false
            }

            etPassword.addTextChangedListener {
                if (!it.isNullOrEmpty())
                    textInputLayoutPassword.isErrorEnabled = false
            }

            etPasswordApply.addTextChangedListener {
                if (!it.isNullOrEmpty())
                    textInputLayoutPasswordApply.isErrorEnabled = false
            }
        }
    }

    private fun checkFields(
        username: Editable?,
        email: Editable?,
        name: Editable?,
        surname: Editable?,
        password: Editable?,
        passwordApply: Editable?
    ): Boolean {
        return !username.isNullOrEmpty() &&
                !email.isNullOrEmpty() &&
                !name.isNullOrEmpty() &&
                !surname.isNullOrEmpty() &&
                !password.isNullOrEmpty() &&
                !passwordApply.isNullOrEmpty()
    }
}