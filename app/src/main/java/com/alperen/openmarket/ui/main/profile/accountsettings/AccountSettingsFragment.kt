package com.alperen.openmarket.ui.main.profile.accountsettings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentAccountSettingsBinding
import com.alperen.openmarket.ui.login.LoginActivity
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class AccountSettingsFragment : Fragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
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

        with(binding) {
            btnSaveUserChanges.setOnClickListener {
                val update = mutableMapOf<String, String>()
                viewModel.getUserProfile(viewLifecycleOwner).observe(viewLifecycleOwner) {
                    when (it) {
                        Constants.SUCCESS -> {
                            user = FirebaseInstance.profile.value!!
                            val username = etUsernameUpdate.text
                            val name = etNameUpdate.text
                            val surname = etSurnameUpdate.text

                            if (checkFieldIsEmpty(username, name, surname)) {
                                // If any field has changed
                                if (user.username != username.toString() ||
                                    user.name != name.toString() ||
                                    user.surname != surname.toString()
                                ) {
                                    update["username"] = username.toString()
                                    update["name"] = name.toString()
                                    update["surname"] = surname.toString()
                                    updateUser(update)
                                }
                                // Nothing changed
                                else {
                                    AlertDialog.Builder(context)
                                        .setMessage(Constants.NOTHING_CHANGED)
                                        .setPositiveButton(Constants.OK) { _, _ -> }
                                        .show()
                                }
                            }
                        }
                        else -> {
                            AlertDialog.Builder(context)
                                .setMessage(it)
                                .setPositiveButton(Constants.OK) { _, _ -> }
                                .show()
                        }
                    }
                }
            }

            btnSaveAccountChanges.setOnClickListener {
                val update = mutableMapOf<String, String>()
                viewModel.getUserProfile(viewLifecycleOwner).observe(viewLifecycleOwner) {
                    when (it) {
                        Constants.SUCCESS -> {
                            user = FirebaseInstance.profile.value!!
                            val email = etEmailUpdate.text
                            val oldPassword = etPasswordUpdate.text
                            val newPassword = etNewPasswordUpdate.text

                            if (checkFieldIsEmpty(email, oldPassword)) {
                                // Password change
                                if (!newPassword.isNullOrEmpty())
                                    update["newPassword"] = newPassword.toString()

                                if (user.email != email.toString())
                                    update["newEmail"] = email.toString()

                                update["oldEmail"] = user.email
                                update["oldPassword"] = oldPassword.toString()

                                updateAccount(update)
                            } else {
                                passwordUpdateLayout.apply {
                                    isErrorEnabled = true
                                    error = Constants.PASSWORD_REQUIRED
                                }
                            }
                        }
                        else -> {
                            AlertDialog.Builder(context)
                                .setMessage(it)
                                .setPositiveButton(Constants.OK) { _, _ -> }
                                .show()
                        }
                    }
                }
            }

            btnBack.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    private fun updateUser(update: Map<String, String>) {
        loading.show(childFragmentManager, "loaderUserSetting")
        viewModel.updateUser(update, viewLifecycleOwner).observe(viewLifecycleOwner) {
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
    }

    private fun updateAccount(update: Map<String, String>) {
        loading.show(childFragmentManager, "loaderUserSetting")
        viewModel.updateAccount(update, viewLifecycleOwner).observe(viewLifecycleOwner) {
            when (it) {
                Constants.SUCCESS_WITH_LOGOUT -> {
                    loading.dismissAllowingStateLoss()
                    AlertDialog.Builder(context)
                        .setMessage(Constants.UPDATE_SUCCESS_WITH_LOGOUT)
                        .setCancelable(false)
                        .setPositiveButton(Constants.OK) { _, _ ->
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
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
    }

    private fun checkFieldIsEmpty(vararg textList: Editable?): Boolean {
        var result = true

        textList.forEach {
            if (it.isNullOrEmpty())
                result = false
        }

        return result
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

            etEmailUpdate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    emailUpdateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    emailUpdateLayout.isErrorEnabled = false
                }
            }

            etPasswordUpdate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    passwordUpdateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    passwordUpdateLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentAccountSettingsBinding.inflate(inflater)
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