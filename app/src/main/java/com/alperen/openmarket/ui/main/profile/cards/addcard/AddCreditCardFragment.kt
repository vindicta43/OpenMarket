package com.alperen.openmarket.ui.main.profile.cards.addcard

import android.app.AlertDialog
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
import com.alperen.openmarket.databinding.FragmentAddCreditCardBinding
import com.alperen.openmarket.model.CreditCard
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.utils.BaseViewModel
import java.util.*

class AddCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCreditCardBinding
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
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            setListenersForText(this)

            btnAddCreditCard.setOnClickListener {
                val cardName = etCardName.text
                val cardNumber = etCardNumber.text
                val cardDate = etCardDate.text
                val cardCvv = etCardCvv.text

                if (checkFields(cardName, cardNumber, cardDate, cardCvv)) {
                    val cardId = UUID.randomUUID().toString()
                    val creditCard = CreditCard(
                        cardId,
                        cardName.toString(),
                        cardNumber.toString(),
                        cardDate.toString(),
                        cardCvv.toString()
                    )
                    loading.show(childFragmentManager, "loaderCard")
                    viewModel.addCreditCard(creditCard, viewLifecycleOwner).observe(viewLifecycleOwner) {
                        when (it) {
                            Constants.SUCCESS -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage("$cardName ${Constants.CARD_ADDED}")
                                    .setPositiveButton(Constants.OK) { _, _ ->
                                        navController.popBackStack()
                                    }
                                    .show()
                            }
                            else -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(it)
                                    .setPositiveButton(Constants.OK) { _, _ ->

                                    }
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkFields(
        cardName: Editable?,
        cardNumber: Editable?,
        cardDate: Editable?,
        cardCvv: Editable?
    ): Boolean {
        if (cardName.isNullOrEmpty() ||
            cardNumber.isNullOrEmpty() ||
            cardDate.isNullOrEmpty() ||
            cardCvv.isNullOrEmpty()
        ) {
            if (cardName.isNullOrEmpty()) {
                binding.cardNameLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_REQUIRED
                }
            }
            if (cardNumber.isNullOrEmpty()) {
                binding.cardNumberLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_REQUIRED
                }
            }
            if (cardDate.isNullOrEmpty()) {
                binding.cardDateLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_REQUIRED
                }
            }
            if (cardCvv.isNullOrEmpty()) {
                binding.cardCvvLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_REQUIRED
                }
            }
            return false
        }
        if (cardNumber.length != 19 || cardDate.length != 5 || cardCvv.length != 3) {
            if (cardNumber.length != 19) {
                binding.cardNumberLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_MISSING
                }
            }
            if (cardDate.length != 5) {
                binding.cardDateLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_MISSING
                }
            }
            if (cardCvv.length != 3) {
                binding.cardCvvLayout.apply {
                    isErrorEnabled = true
                    error = Constants.FIELD_MISSING
                }
            }
            return false
        }
        return true
    }

    private fun setListenersForText(binding: FragmentAddCreditCardBinding) {
        with(binding) {
            etCardName.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    cardNameLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    tvCardName.text = it
                    cardNameLayout.isErrorEnabled = false
                }
            }

            etCardNumber.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    cardNumberLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    tvCardNumber.text = it
                    cardNumberLayout.isErrorEnabled = false
                }
            }

            etCardDate.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    cardDateLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    tvCardDate.text = it
                    cardDateLayout.isErrorEnabled = false
                }
            }

            etCardCvv.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    cardCvvLayout.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    tvCardCvv.text = it
                    cardCvvLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentAddCreditCardBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}