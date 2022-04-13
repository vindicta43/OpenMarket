package com.alperen.openmarket.ui.main.productdetail

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProductDetailBinding
import com.alperen.openmarket.model.PRODUCT_TYPE
import com.alperen.openmarket.utils.BaseViewModel
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
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

            val time = System.currentTimeMillis()

            Log.e("dateDetail", "Sysyem date: $time")
            Log.e("dateDetail", "Product date: ${args.product.expiration_date}")

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



            return root
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTask)
        with(binding) {
            // TODO: açık artırma ve satın alma için düzenle
            btnPurchase.setOnClickListener {
                loading.show(childFragmentManager, "loaderPurchase")
                when(args.product.productType) {
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
                        // TODO: AuctionFragment işleri bitince buna başla
                        // viewModel.giveOffer(args.product, viewLifecycleOwner).observe(viewLifecycleOwner) {}
                    }
                }
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