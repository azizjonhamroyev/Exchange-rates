package com.example.exchangerates.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.RCurrency
import com.example.exchangerates.retrofit.Common
import com.example.exchangerates.room.AppDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadWorker(var appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        uploadToRoom()
        return Result.success()
    }

    private fun uploadToRoom() {

        Common.retrofitService.getCurrencies().enqueue(
            object : Callback<List<RCurrency>> {
                override fun onResponse(
                    call: Call<List<RCurrency>>,
                    response: Response<List<RCurrency>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        val database = AppDatabase.getInstance(appContext).currencyDao()
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
                    }
                }

                override fun onFailure(call: Call<List<RCurrency>>, t: Throwable) {

                }

            }
        )
    }
}