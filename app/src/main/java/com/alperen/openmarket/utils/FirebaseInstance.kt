package com.alperen.openmarket.utils

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Alperen on 6.11.2021.
 */
object FirebaseInstance {
    val user = FirebaseAuth.getInstance().currentUser
    val profile = MutableLiveData<User>()
    val auth = FirebaseAuth.getInstance()
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

    // Coroutine function
    fun addProductToMarket(
        productName: String,
        productDescription: String,
        productPrice: String,
        imageList: ArrayList<Bitmap>
    ): MutableLiveData<String> = runBlocking {
        // Get compressed stream array
        val streamList = compressImages(imageList)

        // Upload images and get database path list
        val imagePathList = uploadImages(streamList)

        // Update table and return result
        return@runBlocking writeOnTable(productName, productDescription, productPrice, imagePathList)
    }

    private suspend fun compressImages(imageList: ArrayList<Bitmap>): ArrayList<ByteArray> {
        val streamList = arrayListOf<ByteArray>()
        imageList.forEach {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 75, stream)
            streamList.add(stream.toByteArray())
        }
        return streamList
    }

    private suspend fun uploadImages(streamList: ArrayList<ByteArray>): MutableList<String> {
        val pathList = mutableListOf<String>()
        streamList.forEach {
            // Random id for image
            val randomImageID = UUID.randomUUID().toString()

            // Image database reference
            val uploadRef = storageRef.reference
                .child("product_images")
                .child(randomImageID)

            pathList.add(uploadRef.path)

            uploadRef.putBytes(it)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }

        return pathList
    }

    private suspend fun writeOnTable(
        productName: String,
        productDescription: String,
        productPrice: String,
        imagePathList: MutableList<String>
    ): MutableLiveData<String> {
        val result = MutableLiveData(Constants.PROCESSING)
        val id = auth.currentUser?.uid!!
        // Add this product to added product list
        val randomProductID = UUID.randomUUID().toString()
        val newProduct = Product(randomProductID, productName, productPrice.toInt(), productDescription, imagePathList)

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
        return result
    }
}