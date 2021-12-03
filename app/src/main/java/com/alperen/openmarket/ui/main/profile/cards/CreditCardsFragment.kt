package com.alperen.openmarket.ui.main.profile.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.databinding.FragmentCreditCardsBinding
import com.alperen.openmarket.model.CreditCard

class CreditCardsFragment : Fragment() {
    private lateinit var binding: FragmentCreditCardsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            val ccArray =
                arrayListOf(
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                    CreditCard("", "Kredi kartım", "1111 2222 3333 4444", "05/25", "789"),
                )

            recyclerUserCards.adapter = CreditCardsRecyclerViewAdapter(ccArray)
            recyclerUserCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentCreditCardsBinding.inflate(inflater)
    }
}