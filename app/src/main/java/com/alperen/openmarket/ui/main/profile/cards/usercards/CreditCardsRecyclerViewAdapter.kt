package com.alperen.openmarket.ui.main.profile.cards.usercards

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
        val tvCardName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        val tvCardDate: TextView = itemView.findViewById(R.id.tvCardDate)
        val tvCardCvv: TextView = itemView.findViewById(R.id.tvCardCvv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_credit_card, parent, false)
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