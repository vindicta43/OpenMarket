package com.alperen.openmarket.ui.main.profile.accountsettings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.alperen.openmarket.databinding.FragmentAccountSettingsBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class AccountSettingsFragment : Fragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private lateinit var viewModel: BaseViewModel
    private val loading by lazy { LoadingFragment() }
    private var user = FirebaseInstance.profile.value!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            etUsernameUpdate.setText(user.username)
            etNameUpdate.setText(user.name)
            etSurnameUpdate.setText(user.surname)
            etEmailUpdate.setText(user.email)

            return root
        }
    }

    override fun onResume() {
        super.onResume()

        setListenersForText()

        binding.btnSaveChanges.setOnClickListener {
            viewModel.getUserProfile(viewLifecycleOwner).observe(viewLifecycleOwner) {
                if (it == Constants.SUCCESS) {
                    user = FirebaseInstance.profile.value!!
                    Log.e("OpenMarket", "${user.username} ${user.name} ${user.surname}")

                    if (checkFieldChange()) {
                        val username = binding.etUsernameUpdate.text.toString()
                        val name = binding.etNameUpdate.text.toString()
                        val surname = binding.etSurnameUpdate.text.toString()

                        if (user.username != username || user.name != name || user.surname != surname) {
                            val update =
                                mapOf(
                                    "username" to username,
                                    "name" to name,
                                    "surname" to surname
                                )
                            loading.show(childFragmentManager, "loaderSetting")
                            viewModel.updateProfile(update, viewLifecycleOwner).observe(viewLifecycleOwner) {
                                when (it) {
                                    Constants.SUCCESS -> {
                                        loading.dismissAllowingStateLoss()
                                        AlertDialog.Builder(context)
                                            .setMessage(Constants.UPDATE_SUCCESS)
                                            .setPositiveButton(Constants.OK) { _, _ -> }
                                            .show()
                                    }
                                    else -> {
                                        loading.dismissAllowingStateLoss()
                                        AlertDialog.Builder(context)
                                            .setMessage(it)
                                            .setPositiveButton(Constants.OK) { _, _ -> }
                                            .show()
                                    }
                                }
                            }
                        } else {
                            AlertDialog.Builder(context)
                                .setMessage(Constants.NOTHING_CHANGED)
                                .setPositiveButton(Constants.OK) { _, _ -> }
                                .show()
                        }
                    } else {
                        with(binding) {
                            usernameUpdateLayout.apply {
                                isErrorEnabled = true
                                error = Constants.FIELD_REQUIRED
                            }
                            nameUpdateLayout.apply {
                                isErrorEnabled = true
                                error = Constants.FIELD_REQUIRED
                            }
                            surnameUpdateLayout.apply {
                                isErrorEnabled = true
                                error = Constants.FIELD_REQUIRED
                            }
                        }
                    }
                }

            }
        }
    }

    private fun checkFieldChange(): Boolean {
        with(binding) {
            val username = !etUsernameUpdate.text.isNullOrEmpty()
            val name = !etNameUpdate.text.isNullOrEmpty()
            val surname = !etSurnameUpdate.text.isNullOrEmpty()

            return username && name && surname
        }
    }

    private fun setListenersForText() {
        with(binding) {
            etUsernameUpdate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    usernameUpdateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    usernameUpdateLayout.isErrorEnabled = false
                }
            }

            etNameUpdate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    nameUpdateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    nameUpdateLayout.isErrorEnabled = false
                }
            }

            etSurnameUpdate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    surnameUpdateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    surnameUpdateLayout.isErrorEnabled = false
                }
            }

//            etEmailUpdate.addTextChangedListener {
//                isFieldChanged = true
//                if (it.isNullOrEmpty()) {
//                    emailUpdateLayout.apply {
//                        isErrorEnabled = true
//                        error = Constants.FIELD_REQUIRED
//                    }
//                } else {
//                    emailUpdateLayout.isErrorEnabled = false
//                }
//            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentAccountSettingsBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}