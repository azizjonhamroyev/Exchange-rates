package com.example.exchangerates.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerates.R
import com.example.exchangerates.databinding.CurrencyHistoryItemBinding
import com.example.exchangerates.databinding.WarningItemBinding
import com.example.exchangerates.models.Currency

class CurrencyHistoryItemAdapter :
    ListAdapter<Currency, CurrencyHistoryItemAdapter.Vh>(CardItemAdapter.DiffUtils()) {

    inner class Vh(var itemViewBinding: CurrencyHistoryItemBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun onBind(currency: Currency) {
            itemViewBinding.currency = currency
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {

        return Vh(
            CurrencyHistoryItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.currency_history_item, parent, false)
            )
        )
    }


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }
}