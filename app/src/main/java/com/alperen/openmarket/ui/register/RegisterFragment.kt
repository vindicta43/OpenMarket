package com.alperen.openmarket.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentRegisterBinding
import com.alperen.openmarket.utils.Constants

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(inflater)

        with(binding) {
            addListenerForText(this)
            addSetOnClickListeners(this)
            return root
        }
    }

    private fun addSetOnClickListeners(binding: FragmentRegisterBinding) {
        with(binding) {
            val email = etEmail.text
            val name = etName.text
            val surname = etSurname.text
            val password = etPassword.text
            val passwordApply = etPasswordApply.text

            btnBack.setOnClickListener {
                root.findNavController().popBackStack()
            }

            btnRegister.setOnClickListener {
                if (email.isNullOrEmpty()) {
                    textInputLayoutEmail.error = Constants.FIELD_REQUIRED
                }
                if (name.isNullOrEmpty()) {
                    textInputLayoutName.error = Constants.FIELD_REQUIRED
                }
                if (surname.isNullOrEmpty()) {
                    textInputLayoutSurname.error = Constants.FIELD_REQUIRED
                }
                if (password.isNullOrEmpty()) {
                    textInputLayoutPassword.error = Constants.FIELD_REQUIRED
                }
                if (passwordApply.isNullOrEmpty()) {
                    textInputLayoutPasswordApply.error = Constants.FIELD_REQUIRED
                }
            }
        }
    }

    private fun addListenerForText(binding: FragmentRegisterBinding) {
        with(binding) {

            etEmail.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutEmail.isErrorEnabled = false
                }
            }

            etName.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutName.isErrorEnabled = false
                }
            }

            etSurname.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutSurname.isErrorEnabled = false
                }
            }

            etPassword.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutPassword.isErrorEnabled = false
                }
            }

            etPasswordApply.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    textInputLayoutPasswordApply.isErrorEnabled = false
                }
            }
        }
    }
}