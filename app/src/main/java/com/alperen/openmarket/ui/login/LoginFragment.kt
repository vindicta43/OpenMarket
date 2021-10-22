package com.alperen.openmarket.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentLoginBinding
import com.alperen.openmarket.utils.Constants
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater)

        with(binding) {
            addListenerForText(this)
            addSetOnClickListeners(this)
            return root
        }
    }

    private fun addSetOnClickListeners(binding: FragmentLoginBinding) {
        with(binding) {
            val email = etEmail.text
            val password = etPassword.text
            btnLogin.setOnClickListener {
                if (email.isNullOrEmpty()) {
                    textInputLayoutEmail.error = Constants.FIELD_REQUIRED
                }
                if (password.isNullOrEmpty()) {
                    textInputLayoutPassword.error = Constants.FIELD_REQUIRED
                }
            }

            btnRegister.setOnClickListener {
                root.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnForgotPassword.setOnClickListener {
                // TODO: database baglantisi ardindan servis olusturulacak
            }
        }
    }

    private fun addListenerForText(binding: FragmentLoginBinding) {
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
}