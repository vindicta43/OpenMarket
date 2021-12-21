package com.alperen.openmarket.ui.main.profile.cards.usercards

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.CreditCard
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.LoadingFragment

/**
 * Created by Alperen on 3.12.2021.
 */
class CreditCardsRecyclerViewAdapter(private val activity: FragmentActivity, private val list: ArrayList<CreditCard>) :
    RecyclerView.Adapter<CreditCardsRecyclerViewAdapter.CreditCardsRecyclerViewHolder>() {
    private val loading by lazy { LoadingFragment() }

    inner class CreditCardsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        val tvCardDate: TextView = itemView.findViewById(R.id.tvCardDate)
        val tvCardCvv: TextView = itemView.findViewById(R.id.tvCardCvv)
        val ibOptions: ImageButton = itemView.findViewById(R.id.ibOptions)
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

            ibOptions.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, ibOptions)
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.btnEdit -> {
                            val singleCard = list[position]
                            val action =
                                CreditCardsFragmentDirections.actionCreditCardsFragmentToEditCreditCardFragment(
                                    singleCard
                                )
                            itemView.findNavController().navigate(action)
                        }
                        R.id.btnDelete -> {
                            loading.show(activity.supportFragmentManager, "loaderDelete")
                            FirebaseInstance.deleteCreditCard(list[position].id).observeForever { result ->
                                when (result) {
                                    Constants.SUCCESS -> {
                                        loading.dismissAllowingStateLoss()

                                        AlertDialog.Builder(activity)
                                            .setMessage("${list[position].name} ${Constants.CARD_REMOVED}")
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
                    }

                    return@setOnMenuItemClickListener true
                }

                popupMenu.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}