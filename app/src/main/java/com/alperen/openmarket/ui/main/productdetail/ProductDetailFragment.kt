package com.alperen.openmarket.ui.main.productdetail

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProductDetailBinding
import com.alperen.openmarket.model.PRODUCT_TYPE
import com.alperen.openmarket.utils.BaseViewModel
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import java.util.concurrent.TimeUnit

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    lateinit var handler: Handler
    private val loading by lazy { LoadingFragment() }
    private val args: ProductDetailFragmentArgs by navArgs()

    val updateTask = object : Runnable {
        override fun run() {
            updateText()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)
        handler = Handler(Looper.getMainLooper())
        with(binding) {
            productDetailPager.adapter = ProductDetailViewPagerAdapter(args.product.image)

            tvProductName.text = args.product.name
            tvProductCondition.text = args.product.condition
            tvProductGender.text = args.product.gender
            tvProductSize.text = args.product.size
            tvProductCategory.text = args.product.category
            tvProductDescription.text = args.product.description
            tvProductPrice.text = args.product.price.toString()

            if (args.product.productType == PRODUCT_TYPE.SELL) {
                layoutAuction.visibility = View.GONE
                btnPurchase.text = "Satın al"
            }

            if (args.product.purchased) {
                btnPurchase.isEnabled = false
                tvProductIsPurchased.text = "Bu ürün satıldı"
            }

            // Picker values
            pickerIncrement.apply {
                minValue = 1
                maxValue = 10
                displayedValues = when (args.product.starting_price) {
                    in 0..99 -> {
                        // Small
                        Constants.smallIncrement
                    }
                    in 100..999 -> {
                        // Medium
                        Constants.mediumIncrement
                    }
                    else -> {
                        // Large
                        Constants.largeIncrement
                    }
                }
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTask)
        with(binding) {
            // TODO: açık artırma ve satın alma için düzenle
            btnPurchase.setOnClickListener {
                loading.show(childFragmentManager, "loaderDetail")
                when (args.product.productType) {
                    PRODUCT_TYPE.SELL -> {
                        viewModel.purchaseProduct(args.product, viewLifecycleOwner).observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PRODUCT_PURCHASED -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context)
                                        .setMessage(it)
                                        .setCancelable(false)
                                        .setPositiveButton(Constants.OK) { _, _ ->
                                            navController.popBackStack()
                                        }.show()
                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context)
                                        .setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ -> }.show()
                                }
                            }
                        }
                    }
                    else -> {
                        val incrementValue = etIncrement.text.toString().toInt()
                        viewModel.makeOffer(args.product, incrementValue, viewLifecycleOwner)
                            .observe(viewLifecycleOwner) {
                                when (it) {
                                    Constants.OFFER_MADE -> {
                                        loading.dismissAllowingStateLoss()
                                        AlertDialog.Builder(context)
                                            .setMessage(it)
                                            .setPositiveButton(Constants.OK) { _, _ -> }.show()
                                    }
                                    else -> {
                                        loading.dismissAllowingStateLoss()
                                        AlertDialog.Builder(context)
                                            .setMessage(it)
                                            .setPositiveButton(Constants.OK) { _, _ -> }.show()
                                    }
                                }
                            }
                    }
                }
            }

            pickerIncrement.setOnValueChangedListener { picker, oldVal, newVal ->
                val incrementValues = when (args.product.starting_price) {
                    in 0..99 -> {
                        // Small
                        Constants.smallIncrement
                    }
                    in 100..999 -> {
                        // Medium
                        Constants.mediumIncrement
                    }
                    else -> {
                        // Large
                        Constants.largeIncrement
                    }
                }
                val offer = incrementValues[newVal - 1].toInt() + args.product.price
                etIncrement.setText(offer.toString())
            }

            viewModel.observeOffers(args.product).observeForever {
                recyclerLastOffers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerLastOffers.adapter = LastOffersAdapter(it)
                if (it.isNotEmpty())
                    tvProductPrice.text = it[0].increment.toString()
                notifyChange()
            }
        }
    }

    fun updateText() {
        val time = System.currentTimeMillis()

        val remainingTime = (args.product.expiration_date?.toLong() ?: 0) - time
        var remainingString = ""
        if (remainingTime <= 0) {
            binding.btnPurchase.isEnabled = false
            binding.tvRemainingTime.text = "Süre doldu"
            binding.tvProductIsPurchased.text = "Açık artırmanın süresi doldu"
        } else {
            remainingString = getDate(remainingTime.toLong())
            binding.tvRemainingTime.text = remainingString
        }
    }

    private fun getDate(milliSeconds: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(milliSeconds)
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60

        return "$days gün $hours saat $minutes dakika $seconds saniye"
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentProductDetailBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}