package com.alperen.openmarket.ui.main.profile.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.CreditCard

/**
 * Created by Alperen on 3.12.2021.
 */
class CreditCardsRecyclerViewAdapter(private val list: ArrayList<CreditCard>) :
    RecyclerView.Adapter<CreditCardsRecyclerViewAdapter.CreditCardsRecyclerViewHolder>() {

    inner class CreditCardsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardName = itemView.findViewById<TextView>(R.id.tvCardName)
        val tvCardNumber = itemView.findViewById<TextView>(R.id.tvCardNumber)
        val tvCardDate = itemView.findViewById<TextView>(R.id.tvCardDate)
        val tvCardCvv = itemView.findViewById<TextView>(R.id.tvCardCvv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_credit_card, parent, false)
        return CreditCardsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardsRecyclerViewHolder, position: Int) {
        with(holder) {
            tvCardCvv.text = list[position].cvv
            tvCardDate.text = list[position].date
            tvCardName.text = list[position].name
            tvCardNumber.text = list[position].number
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}