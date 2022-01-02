package com.alperen.openmarket.ui.main.profile.useraddedproducts

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.GlideApp
import com.alperen.openmarket.utils.LoadingFragment
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 21.11.2021.
 */
class AddedProductsRecyclerViewAdapter(private val activity: FragmentActivity, private val list: ArrayList<Product>) :
    RecyclerView.Adapter<AddedProductsRecyclerViewAdapter.UserAddedProductsRecyclerViewHolder>() {
    private val loading by lazy { LoadingFragment() }

    inner class UserAddedProductsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAddedProductImage: ImageView = itemView.findViewById(R.id.ivUserAddedProductImage)
        val tvUserAddedProductName: TextView = itemView.findViewById(R.id.tvUserAddedProductName)
        val tvUserAddedProductPrice: TextView = itemView.findViewById(R.id.tvUserAddedProductPrice)
        val ibDelete: ImageButton = itemView.findViewById(R.id.ibDelete)
        val ibEdit: ImageButton = itemView.findViewById(R.id.ibEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAddedProductsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_added_products, parent, false)
        return UserAddedProductsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAddedProductsRecyclerViewHolder, position: Int) {
        with(holder) {
            val storageRef = FirebaseStorage.getInstance().reference.child(list[position].image[0])
            GlideApp.with(itemView.context).load(storageRef).into(ivUserAddedProductImage)
            tvUserAddedProductName.text = list[position].name
            tvUserAddedProductPrice.text = list[position].price.toString()

            ibDelete.setOnClickListener {
                loading.show(activity.supportFragmentManager, "loaderDelete")
                FirebaseInstance.deleteProductFromMarket(list[position].id).observeForever { result ->
                    when (result) {
                        Constants.SUCCESS -> {
                            loading.dismissAllowingStateLoss()
                            AlertDialog.Builder(activity)
                                .setMessage(Constants.PRODUCT_REMOVED)
                                .setPositiveButton(Constants.OK) { _, _ -> }
                                .show()

                            list.removeAt(position)
                            notifyDataSetChanged()
                        }
                        else -> {
                            loading.dismissAllowingStateLoss()
                            AlertDialog.Builder(activity)
                                .setMessage(result)
                                .setPositiveButton(Constants.OK) { _, _ -> }
                                .show()
                        }
                    }
                }
            }

            ibEdit.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}