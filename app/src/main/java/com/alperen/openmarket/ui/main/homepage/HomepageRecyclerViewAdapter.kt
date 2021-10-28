package com.alperen.openmarket.ui.main.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R

/**
 * Created by Alperen on 28.10.2021.
 */
const val RECENTLY_SHOWN = 0
const val RECENTLY_SHOWN_ITEMS = 1
const val HOMEPAGE_ITEMS = 2
class HomepageRecyclerViewAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderRecentlyShownItems(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    inner class ViewHolderHomepageItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> RECENTLY_SHOWN
            1 -> HOMEPAGE_ITEMS
            else -> HOMEPAGE_ITEMS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECENTLY_SHOWN -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_product, parent, false)
                ViewHolderRecentlyShownItems(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_product, parent, false)
                ViewHolderHomepageItems(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return items.size
    }
}