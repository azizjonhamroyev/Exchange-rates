package com.example.exchangerates.ui.home

import android.content.Context
import androidx.lifecycle.*
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.RCurrency
import com.example.exchangerates.retrofit.Common
import com.example.exchangerates.room.AppDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    val liveData = MutableLiveData<Boolean>()
    private val currencies = MutableLiveData<List<Currency>>()

    fun getCurrencies(context: Context): LiveData<List<Currency>> {
        currencies.value = AppDatabase.getInstance(context).currencyDao().getLast()
            .sortedBy { it.nbu_buy_price == null }
        return currencies
    }

    fun addLiveData(context: Context): LiveData<Boolean> {
        Common.retrofitService.getCurrencies().enqueue(
            object : Callback<List<RCurrency>> {
                override fun onResponse(
                    call: Call<List<RCurrency>>,
                    response: Response<List<RCurrency>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        val database = AppDatabase.getInstance(context).currencyDao()
                        val last = database.last()
                        if (last == null || res?.first()?.date != last.date) {
                            for (rCurrency in res!!) {
                                val currency = Currency(
                                    rCurrency.code,
                                    rCurrency.cb_price?.toDouble(),
                                    rCurrency.date,
                                    if (rCurrency.nbu_buy_price == "") null else rCurrency.nbu_buy_price?.toDouble(),
                                    if (rCurrency.nbu_cell_price == "") null else rCurrency.nbu_cell_price?.toDouble(),
                                    rCurrency.title
                                )
                                database.insertCurrency(currency)
                            }
                        }
                        liveData.value = true
                    }
                }

                override fun onFailure(call: Call<List<RCurrency>>, t: Throwable) {
                    liveData.value = AppDatabase.getInstance(context).currencyDao().last() != null
                }

            }
        )
        return liveData
    }


}