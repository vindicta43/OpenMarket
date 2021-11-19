package com.alperen.openmarket.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import androidx.lifecycle.*
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.google.android.material.imageview.ShapeableImageView
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

    fun getUserProfile(viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        FirebaseInstance.getUserProfile().observe(viewLifecycleOwner) {
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

    fun addProductToMarket(
        productName: String,
        productDescription: String,
        productPrice: String,
        imageList: ArrayList<Bitmap>,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<String> {
        val result = MutableLiveData(Constants.PROCESSING)
        FirebaseInstance.addProductToMarket(productName, productDescription, productPrice, imageList)
            .observe(viewLifecycleOwner) {
                result.value = it
            }
        return result
    }
}