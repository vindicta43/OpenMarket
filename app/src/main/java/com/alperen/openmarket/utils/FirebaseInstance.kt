package com.alperen.openmarket.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.model.User
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Alperen on 6.11.2021.
 */
object FirebaseInstance {
    val user = FirebaseAuth.getInstance().currentUser
    val profile = MutableLiveData<User>()
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance()
    private val storageRef = FirebaseStorage.getInstance()

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
                        val user =
                            User(
                                userId,
                                username,
                                email,
                                name,
                                surname,
                                password,
                                added_products = arrayListOf()
                            )
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
                profile.value = it.getValue(User::class.java)
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun getHomePage(): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        val itemList = arrayListOf<Product>()
        dbRef.reference
            .child("products")
            .get()
            .addOnSuccessListener {
                it.children.forEach { items ->
                    itemList.add(items.getValue<Product>()!!)
                }
                result.value = itemList
            }
            .addOnFailureListener {

            }
        return result
    }

    fun addProductToMarket(
        productName: String,
        productDescription: String,
        productPrice: String,
        productImage: ShapeableImageView
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        // User id
        val id = auth.currentUser?.uid!!

        // Random id for image
        val randomProductID = UUID.randomUUID().toString()

        // Image database reference
        val uploadRef = storageRef.reference
            .child("product_images")
            .child(randomProductID)

        // Compressing operation
        val bitmap = (productImage.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        val data = stream.toByteArray()

        val newProduct = Product(randomProductID, productName, productPrice.toInt(), productDescription, uploadRef.path)

        // Upload compressed file to firebase storage
        uploadRef.putBytes(data)

            // Add this product to added product list
            .addOnSuccessListener {
                dbRef.reference
                    .child("users")
                    .child(id)
                    .child("added_products")
                    .child(randomProductID)
                    .setValue(newProduct)

                    // Add same product into all products list
                    .addOnSuccessListener {
                        dbRef.reference
                            .child("products")
                            .child(randomProductID)
                            .setValue(newProduct)

                            // Get current users added product count
                            .addOnSuccessListener {
                                dbRef.reference
                                    .child("users")
                                    .child(id)
                                    .child("added_product_count")
                                    .get()
                                    .addOnSuccessListener { profile ->
                                        val currentUser = profile.getValue(Int::class.java)!!

                                        // Increase users added product count
                                        dbRef.reference
                                            .child("users")
                                            .child(id)
                                            .updateChildren(
                                                mapOf(
                                                    "added_product_count" to currentUser + 1
                                                )
                                            )
                                            .addOnSuccessListener {
                                                result.value = Constants.PRODUCT_ADDED
                                            }
                                            .addOnFailureListener {
                                                result.value = it.localizedMessage
                                            }
                                    }
                                    .addOnFailureListener {
                                        result.value = it.localizedMessage
                                    }
                            }
                            .addOnFailureListener {
                                result.value = it.localizedMessage
                            }
                    }
                    .addOnFailureListener {
                        result.value = it.localizedMessage
                    }
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }
}