package com.alperen.openmarket.ui.main.productdetail

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
import com.alperen.openmarket.utils.AuctionAlarmService
import com.alperen.openmarket.utils.BaseViewModel
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import java.util.*
import java.util.concurrent.TimeUnit

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    lateinit var handler: Handler
    private val loading by lazy { LoadingFragment() }
    private val args: ProductDetailFragmentArgs by navArgs()
    val pickerValues = mutableListOf<String>()

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
            } else {
                // Picker values
                for (i in 1..10) {
                    val singleValue = (args.product.increment_multiplier!! * i).toString()
                    pickerValues.add(singleValue)
                }
                pickerIncrement.apply {
                    minValue = 1
                    maxValue = 10
                    displayedValues = pickerValues.toTypedArray()
                }
            }

            if (args.product.purchased) {
                btnPurchase.isEnabled = false
                tvProductIsPurchased.text = "Bu ürün satıldı"
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        if (args.product.productType == PRODUCT_TYPE.AUCTION) {
            handler.post(updateTask)
            binding.layoutAuction.visibility = View.VISIBLE
            binding.recyclerLastOffers.visibility = View.VISIBLE
        }

        with(binding) {
            // TODO: açık artırma ve satın alma için düzenle
            btnPurchase.setOnClickListener {
                when (args.product.productType) {
                    PRODUCT_TYPE.SELL -> {
                        loading.show(childFragmentManager, "loaderSell")
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
                        if (etIncrement.text?.isNotEmpty() == true) {
                            // TODO: input error null geçmemeli
                            val incrementValue = etIncrement.text.toString().toInt()
                            if (args.product.price < incrementValue) {
                                if (incrementValue % args.product.increment_multiplier!! == 0) {
                                    loading.show(childFragmentManager, "loaderAuction")
                                    viewModel.makeOffer(args.product, incrementValue, viewLifecycleOwner)
                                        .observe(viewLifecycleOwner) {
                                            when (it) {
                                                Constants.OFFER_MADE -> {
                                                    loading.dismissAllowingStateLoss()
                                                    setOffer(incrementValue)
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
                                } else {
                                    textInputLayoutIncrement.apply {
                                        isErrorEnabled = true
                                        error = "Artırma miktarı ${args.product.increment_multiplier} katlarında olmalıdır"
                                    }
                                }
                            } else {
                                textInputLayoutIncrement.apply {
                                    isErrorEnabled = true
                                    error = Constants.SMALL_OFFER
                                }
                            }

                        }
                    }
                }
            }

            pickerIncrement.setOnValueChangedListener { picker, oldVal, newVal ->
                val offer = pickerValues[newVal - 1].toInt() + args.product.price
                etIncrement.setText(offer.toString())
            }

            etIncrement.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutIncrement.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutIncrement.isErrorEnabled = false
                }
            }

            viewModel.observeOffers(args.product).observeForever {
                recyclerLastOffers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerLastOffers.adapter = LastOffersAdapter(it)
                if (it.isNotEmpty()) {
                    tvProductPrice.text = it[0].increment.toString()
                    args.product.price = it[0].increment
                }
                notifyChange()
            }
        }
    }

    private fun setOffer(incrementValue: Int) {
        // TODO: background service test
        val alarmIntent =
            Intent(requireContext(), AuctionAlarmService::class.java)

        alarmIntent.putExtra("productId", args.product.id)
        alarmIntent.putExtra("expDate", args.product.expiration_date.toString())
        alarmIntent.putExtra("incrementValue", incrementValue)

        val pendingIntent =
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                alarmIntent,
                0
            )

        val alarmManager: AlarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val remainingTime = args.product.expiration_date!!.toLong() - System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + remainingTime
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("auctionService", "Alarm invoked first time. remaining: ${calendar.timeInMillis}")
        Log.d("auctionService", "Alarm intent ${alarmIntent.extras.toString()}")
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
            remainingString = getDate(remainingTime)
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