package com.alperen.openmarket.viewmodels

import androidx.lifecycle.*
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import kotlin.random.Random

/**
 * Created by Alperen on 6.11.2021.
 */
class BaseViewModel(private val state: SavedStateHandle): ViewModel() {
    private val liveDate = state.getLiveData("liveData", Random.nextInt().toString())

    fun saveState() {
        state.set("liveData", liveDate.value)
    }

    fun login(email: String, password: String, viewLifecycleOwner: LifecycleOwner): MutableLiveData<String> {
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
}