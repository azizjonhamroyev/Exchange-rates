package com.example.exchangerates.retrofit

object Common {

    const val BASE_URL = "https://nbu.uz/uz/exchange-rates/"

    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)


}