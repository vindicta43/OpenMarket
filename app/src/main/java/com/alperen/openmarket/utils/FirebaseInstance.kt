package com.alperen.openmarket.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.alperen.openmarket.model.CreditCard
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.model.User
import com.alperen.openmarket.model.UserSnapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Alperen on 6.11.2021.
 */
object FirebaseInstance {
    val profile = MutableLiveData<UserSnapshot>()
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
                // Create row in user table
                val userId = auth.currentUser!!.uid
                val user =
                    User(
                        userId,
                        username,
                        email,
                        name,
                        surname,
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
                profile.value = it.getValue(UserSnapshot::class.java)
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }

        return result
    }

    fun getUserProducts(): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        val itemList = arrayListOf<Product>()
        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("added_products")
            .get()
            .addOnSuccessListener {
                it.children.forEach { items ->
                    itemList.add(items.getValue(Product::class.java)!!)
                }
                result.value = itemList
            }
            .addOnFailureListener {

            }

        return result
    }

    fun getHomePage(): MutableLiveData<MutableMap<String, ArrayList<Product>>> {
        val result = MutableLiveData<MutableMap<String, ArrayList<Product>>>()
        // val itemList = arrayListOf<Product>()
        val dataList = mutableMapOf<String, ArrayList<Product>>()
        dbRef.reference
            .child("products")
            .get()
            .addOnSuccessListener { products ->
//                it.children.forEach { items ->
//                    itemList.add(items.getValue(Product::class.java)!!)
//                }
//                result.value = itemList


                    // Get all products
                    val productList = arrayListOf<Product>()
                    products.children.forEach { items ->
                        productList.add(items.getValue(Product::class.java)!!)
                    }
                    dataList["products"] = productList


                dbRef.reference
                    .child("users")
                    .child(auth.currentUser?.uid!!)
                    .child("user_recently_shown")
                    .get()
                    .addOnSuccessListener { recentlyShown ->

                            val recentlyShownList = arrayListOf<Product>()
                            recentlyShown.children.forEach { items ->
                                recentlyShownList.add(items.getValue(Product::class.java)!!)
                            }
                            dataList["recently"] = recentlyShownList
                            result.value = dataList

                    }
                    .addOnFailureListener { }
            }
            .addOnFailureListener { }
        return result
    }

    // Coroutine function
    fun addProductToMarket(
        productName: String,
        productPrice: String,
        productDescription: String,
        productCategory: String,
        productSize: String,
        productCondition: String,
        productGender: String,
        imageList: ArrayList<Bitmap>
    ): MutableLiveData<String> = runBlocking {
        // Get compressed stream array
        val streamList = compressImages(imageList)

        // Upload images and get database path list
        val imagePathList = uploadImages(streamList)

        // Add this product to added product list
        val randomProductID = UUID.randomUUID().toString()
        val newProduct =
            Product(
                randomProductID,
                productName,
                productPrice.toInt(),
                productDescription,
                productCategory,
                productSize,
                productCondition,
                productGender,
                imagePathList
            )

        // Update table and return result
        return@runBlocking writeOnTable(newProduct)
    }

    fun deleteProductFromMarket(id: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        // Delete prduct from user
        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("added_products")
            .child(id)
            .removeValue()
            .addOnSuccessListener {
                // Delete product from all products
                dbRef.reference
                    .child("products")
                    .child(id)
                    .removeValue()
                    .addOnSuccessListener {
                        result.value = Constants.SUCCESS
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

    private fun compressImages(imageList: ArrayList<Bitmap>): ArrayList<ByteArray> {
        val streamList = arrayListOf<ByteArray>()
        imageList.forEach {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 75, stream)
            streamList.add(stream.toByteArray())
        }
        return streamList
    }

    private fun uploadImages(streamList: ArrayList<ByteArray>): MutableList<String> {
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

    private fun writeOnTable(newProduct: Product): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val id = auth.currentUser?.uid!!

        dbRef.reference
            .child("users")
            .child(id)
            .child("added_products")
            .child(newProduct.id)
            .setValue(newProduct)

            // Add same product into all products list
            .addOnSuccessListener {
                dbRef.reference
                    .child("products")
                    .child(newProduct.id)
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

    fun setProfilePicture(imageUri: Uri): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        val update = UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build()

        auth.currentUser?.updateProfile(update)
            ?.addOnSuccessListener {
                profile.value?.profile_image = update.photoUri
                result.value = Constants.SUCCESS
            }
            ?.addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun updateUser(update: Map<String, String>): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .updateChildren(
                mapOf(
                    "username" to update["username"],
                    "name" to update["name"],
                    "surname" to update["surname"],
                )
            )
            .addOnSuccessListener { result.value = Constants.SUCCESS }
            .addOnFailureListener { result.value = it.localizedMessage!! }

        return result
    }

    fun updateAccount(update: Map<String, String>): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        auth.signInWithEmailAndPassword(update["oldEmail"]!!, update["oldPassword"]!!)
            .addOnSuccessListener {
                // If have new email, update it and then check for new password
                if (update.containsKey("newEmail")) {
                    auth.currentUser!!.updateEmail(update["newEmail"]!!)
                        .addOnSuccessListener {
                            // Update email on realtime database
                            dbRef.reference
                                .child("users")
                                .child(auth.currentUser?.uid!!)
                                .updateChildren(mapOf("email" to update["newEmail"]))
                                .addOnSuccessListener {
                                    // Check for password change
                                    if (update.containsKey("newPassword")) {
                                        auth.currentUser!!.updatePassword(update["newPassword"]!!)
                                            .addOnSuccessListener { result.value = Constants.SUCCESS_WITH_LOGOUT }
                                            .addOnFailureListener { result.value = it.localizedMessage }
                                    } else {
                                        result.value = Constants.SUCCESS_WITH_LOGOUT
                                        auth.signOut()
                                    }
                                }
                                .addOnFailureListener { result.value = it.localizedMessage }
                        }
                }
                // If have old email and new password only change password
                else {
                    auth.currentUser!!.updatePassword(update["newPassword"]!!)
                        .addOnSuccessListener {
                            result.value = Constants.SUCCESS_WITH_LOGOUT
                            auth.signOut()
                        }
                        .addOnFailureListener { result.value = it.localizedMessage }
                }
            }
            .addOnFailureListener { result.value = it.localizedMessage }

        return result
    }

    fun addCreditCard(creditCard: CreditCard): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("cards")
            .child(creditCard.id)
            .setValue(creditCard)
            .addOnSuccessListener {
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun getUserCreditCards(): MutableLiveData<ArrayList<CreditCard>> {
        val result = MutableLiveData<ArrayList<CreditCard>>()
        val itemList = arrayListOf<CreditCard>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("cards")
            .get()
            .addOnSuccessListener {
                it.children.forEach { items ->
                    itemList.add(items.getValue(CreditCard::class.java)!!)
                }
                result.value = itemList
            }
        return result
    }

    fun deleteCreditCard(id: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("cards")
            .child(id)
            .removeValue()
            .addOnSuccessListener { result.value = Constants.SUCCESS }
            .addOnFailureListener { result.value = it.localizedMessage }
        return result
    }

    fun editCreditCard(creditCard: Map<String, String>): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("cards")
            .child(creditCard["id"]!!)
            .updateChildren(creditCard)
            .addOnSuccessListener {
                result.value = Constants.SUCCESS
            }
            .addOnFailureListener {
                result.value = it.localizedMessage
            }
        return result
    }

    fun clearRecentlyShown(): MutableLiveData<String> {
        val result = MutableLiveData<String>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("user_recently_shown")
            .removeValue()
            .addOnSuccessListener { result.value = Constants.SUCCESS }
            .addOnFailureListener { result.value = it.localizedMessage }
        return result
    }

    fun updateProduct(update: MutableMap<String, Any>): MutableLiveData<String> = runBlocking {
        val result = MutableLiveData<String>()

        if (update.containsKey("image")) {
            val bitmapList = update["image"] as ArrayList<Bitmap>
            // Get compressed stream array
            val streamList = compressImages(bitmapList)

            // Upload images and get database path list
            val imagePathList = uploadImages(streamList)
            update["image"] = imagePathList

            // Update product of user
            dbRef.reference
                .child("users")
                .child(auth.currentUser?.uid!!)
                .child("added_products")
                .child(update["id"] as String)
                .updateChildren(update)
                .addOnSuccessListener {
                    // Update from all products
                    dbRef.reference
                        .child("products")
                        .child(update["id"] as String)
                        .updateChildren(update)
                        .addOnSuccessListener { result.value = Constants.PRODUCT_UPDATED }
                        .addOnFailureListener { result.value = it.localizedMessage }
                }
                .addOnFailureListener { result.value = it.localizedMessage }
        } else {
            // Update product of user
            dbRef.reference
                .child("users")
                .child(auth.currentUser?.uid!!)
                .child("added_products")
                .child(update["id"] as String)
                .updateChildren(update)
                .addOnSuccessListener {
                    // Update from all products
                    dbRef.reference
                        .child("products")
                        .child(update["id"] as String)
                        .updateChildren(update)
                        .addOnSuccessListener { result.value = Constants.PRODUCT_UPDATED }
                        .addOnFailureListener { result.value = it.localizedMessage }
                }
                .addOnFailureListener { result.value = it.localizedMessage }
        }

        return@runBlocking result
    }

    fun getUserFavorites(): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        val itemList = arrayListOf<Product>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("user_favorites")
            .get()
            .addOnSuccessListener {
                it.children.forEach { items ->
                    itemList.add(items.getValue(Product::class.java)!!)
                }
                result.value = itemList
            }
        return result
    }

    fun getUserRecentlyShown(): MutableLiveData<ArrayList<Product>> {
        val result = MutableLiveData<ArrayList<Product>>()
        val itemList = arrayListOf<Product>()

        dbRef.reference
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("user_recently_shown")
            .get()
            .addOnSuccessListener {
                it.children.forEach { items ->
                    itemList.add(items.getValue(Product::class.java)!!)
                }
                result.value = itemList
            }
        return result
    }
}