package com.alperen.openmarket.utils

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.alperen.openmarket.model.CreditCard
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.model.UserSnapshot
import kotlin.random.Random

/**
 * Created by Alperen on 6.11.2021.
 */
class BaseViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val liveDate = state.getLiveData("liveData", Random.nextInt().toString())

    fun saveState() {
        state.set("liveData", liveDate.value)
    }

    fun login(
        email: String,
        password: String,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<String> {
        val result = MutableLiveData(Constants.PROCESSING)
        FirebaseInstance.login(email, password).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun sendResetEmail(email: String, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData(Constants.PROCESSING)
        FirebaseInstance.sendResetEmail(email).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun register(
        username: String,
        email: String,
        name: String,
        surname: String,
        password: String,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<String> {
        val result = MutableLiveData(Constants.PROCESSING)
        FirebaseInstance.register(username, email, name, surname, password)
            .observe(viewLifecycleOwner) {
                result.value = it
            }
        return result
    }

    fun logout() {
        FirebaseInstance.logout()
    }

    fun getUserProfile(viewLifecycleOwner: LifecycleOwner): MutableLiveData<UserSnapshot> {
        val result = MutableLiveData<UserSnapshot>()
        FirebaseInstance.getUserProfile().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getUserProducts(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        FirebaseInstance.getUserProducts().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getHomePage(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        FirebaseInstance.getHomePage().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun addProductToMarketSell(
        productName: String,
        productPrice: String,
        productDescription: String,
        productCategory: String,
        productSize: String,
        productCondition: String,
        productGender: String,
        imageList: ArrayList<Bitmap>,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.addProductToMarketSell(
            productName,
            productPrice,
            productDescription,
            productCategory,
            productSize,
            productCondition,
            productGender,
            imageList
        )
            .observe(viewLifecycleOwner) {
                result.value = it
            }
        return result
    }

    fun addProductToMarketAuction(
        productName: String,
        productPrice: String,
        productDescription: String,
        productCategory: String,
        productSize: String,
        productCondition: String,
        productGender: String,
        imageList: ArrayList<Bitmap>,
        expirationDate: String,
        startingPrice: String,
        increment: String,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.addProductToMarketAuction(
            productName,
            productPrice,
            productDescription,
            productCategory,
            productSize,
            productCondition,
            productGender,
            imageList,
            expirationDate,
            startingPrice,
            increment,
        ).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun setProfilePicture(bitmap: Bitmap, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.setProfilePicture(bitmap).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun updateUser(update: Map<String, String>, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.updateUser(update).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun updateAccount(update: Map<String, String>, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.updateAccount(update).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun addCreditCard(creditCard: CreditCard, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.addCreditCard(creditCard).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getUserCreditCards(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<CreditCard>> {
        val result = MutableLiveData<ArrayList<CreditCard>>()
        FirebaseInstance.getUserCreditCards().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun editCreditCard(creditCard: Map<String, String>, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.editCreditCard(creditCard).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun clearRecentlyShown(viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.clearRecentlyShown().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun updateProduct(update: MutableMap<String, Any>, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.updateProduct(update).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getUserFavorites(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        FirebaseInstance.getUserFavorites().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getUserRecentlyShown(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        FirebaseInstance.getUserRecentlyShown().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun purchaseProduct(product: Product, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.purchaseProduct(product).observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }

    fun getUserPurchasedProducts(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        FirebaseInstance.getUserPurchasedProducts().observe(viewLifecycleOwner) {
            result.value = it
        }
        return result
    }
}