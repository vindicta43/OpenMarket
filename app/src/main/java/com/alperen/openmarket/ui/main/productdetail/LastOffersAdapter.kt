package com.alperen.openmarket.ui.main.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.OfferModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Alperen on 16.04.2022.
 */
class LastOffersAdapter(val list: ArrayList<OfferModel>): RecyclerView.Adapter<LastOffersAdapter.LastOffersViewHolder>() {

    inner class LastOffersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvOfferDate = itemView.findViewById<TextView>(R.id.tvOfferDate)
        val tvOffererName = itemView.findViewById<TextView>(R.id.tvOffererName)
        val tvOfferPrice = itemView.findViewById<TextView>(R.id.tvOfferPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastOffersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_single_offer, parent, false)
        return LastOffersViewHolder(view)
    }

    override fun onBindViewHolder(holder: LastOffersViewHolder, position: Int) {
        with(holder) {
            val date = getDate(list[position].offer_date.toLong())
            tvOfferDate.text = date
            tvOffererName.text = list[position].name_surname
            tvOfferPrice.text = list[position].increment.toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getDate(milliSeconds: Long): String {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date(milliSeconds)

        return fmt.format(date)
    }
}