package com.example.exchangerates.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.exchangerates.R
import com.example.exchangerates.databinding.CountrySpinnerItemBinding
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.Flag
import com.example.exchangerates.models.ImageCodeDecode
import com.example.exchangerates.room.AppDatabase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class CountrySpinnerItemAdapter(var l: ArrayList<Currency>, val context: Context) : BaseAdapter() {
    override fun getCount(): Int = l.size

    override fun getItem(p0: Int): Any = l[p0]

    override fun getItemId(p0: Int): Long = l[p0].id?.toLong()!!

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val itemView: View = p1 ?: LayoutInflater.from(p2?.context)
            .inflate(R.layout.country_spinner_item, p2, false)
        val binding = CountrySpinnerItemBinding.bind(itemView)
        binding.code.text = l[p0].code
        val database = AppDatabase.getInstance(context).currencyDao()
        val flag = database.getFlagById(l[p0].code)
        if (flag != null) {
            binding.countryFlag.setImageBitmap(ImageCodeDecode.decode(flag.image))
        } else {
            Picasso.get()
                .load("https://nbu.uz/local/templates/nbu/images/flags/${l[p0].code}.png")
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        try {
                            database.insertFlag(Flag(l[p0].code, ImageCodeDecode.code(bitmap!!)))
                            binding.countryFlag.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                        }
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                })
        }
        return binding.root
    }
}