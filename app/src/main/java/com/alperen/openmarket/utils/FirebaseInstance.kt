package com.alperen.openmarket.utils

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Alperen on 6.11.2021.
 */
object FirebaseInstance {
    private val auth = FirebaseAuth.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    fun login(email: String, password: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (user != null && user.isEmailVerified)
                    result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.message
            }
        return result
    }

    fun sendResetEmail(email: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.message
            }
        return result
    }
}