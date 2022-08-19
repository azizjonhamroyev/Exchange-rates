package com.example.exchangerates.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerates.R
import com.example.exchangerates.databinding.CardItemBinding
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.Flag
import com.example.exchangerates.models.ImageCodeDecode
import com.example.exchangerates.room.AppDatabase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class CardItemAdapter(val context: Context) :
    ListAdapter<Currency, CardItemAdapter.Vh>(DiffUtils()) {

    inner class Vh(var itemViewBinding: CardItemBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun onBind(currency: Currency) {
            itemViewBinding.currency = currency
            val database = AppDatabase.getInstance(context).currencyDao()
            val flag = database.getFlagById(currency.code)
            if (flag != null) {
                itemViewBinding.icon.setImageBitmap(ImageCodeDecode.decode(flag.image))
            } else {
                Picasso.get()
                    .load("https://nbu.uz/local/templates/nbu/images/flags/${currency.code}.png")
                    .into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            try {
                                database.insertFlag(
                                    Flag(
                                        currency.code,
                                        ImageCodeDecode.code(bitmap!!)
                                    )
                                )
                                itemViewBinding.icon.setImageBitmap(bitmap)
                            } catch (e: Exception) {

                            }
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                        }

                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            CardItemBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    class DiffUtils : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.equals(newItem);
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.id == newItem.id
        }

    }


}