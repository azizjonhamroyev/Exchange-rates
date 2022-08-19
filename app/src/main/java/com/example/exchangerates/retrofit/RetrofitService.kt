package com.example.exchangerates.retrofit

import com.example.exchangerates.models.RCurrency
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {
    @GET("json/")
    fun getCurrencies(): Call<List<RCurrency>>
}