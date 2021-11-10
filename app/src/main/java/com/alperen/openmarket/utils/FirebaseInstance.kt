package com.alperen.openmarket.utils

import androidx.lifecycle.MutableLiveData
import com.alperen.openmarket.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

/**
 * Created by Alperen on 6.11.2021.
 */
object FirebaseInstance {
    val user = FirebaseAuth.getInstance().currentUser
    val profile = MutableLiveData<User>()
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance()

    fun login(email: String, password: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (auth.currentUser?.isEmailVerified == true)
                    result.value = Constants.SUCCESS
                else {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            result.value = Constants.VERIFICATION_REQUIRED
                        }
                        ?.addOnFailureListener {
                            result.value = it.localizedMessage
                        }
                }
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun sendResetEmail(email: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                result.value = Constants.RESET_MAIL_SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun register(
        username: String,
        email: String,
        name: String,
        surname: String,
        password: String,
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        // Create user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                // Update display name
                val update = UserProfileChangeRequest.Builder().setDisplayName(username).build()
                auth.currentUser?.updateProfile(update)
                    ?.addOnSuccessListener {

                        // Create row in user table
                        val userId = auth.currentUser!!.uid
                        val user = User(username, email, name, surname, password)
                        dbRef.reference
                            .child("users")
                            .child(userId)
                            .setValue(user)
                            .addOnSuccessListener {
                                auth.currentUser?.sendEmailVerification()
                                    ?.addOnSuccessListener {
                                        result.value = Constants.REGISTER_SUCCESS
                                    }
                                    ?.addOnFailureListener {
                                        result.value = it.localizedMessage
                                    }
                            }
                            .addOnFailureListener {
                                result.value = it.localizedMessage
                            }

                    }
                    ?.addOnFailureListener {
                        result.value = it.localizedMessage
                    }

            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun logout() {
        auth.signOut()
    }

    fun getUserProfile(): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val id = auth.currentUser?.uid
        dbRef.reference
            .child("users")
            .child(id!!)
            .get()
            .addOnSuccessListener {
                profile.value = it.getValue<User>()
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }
}