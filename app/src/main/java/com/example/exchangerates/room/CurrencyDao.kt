package com.example.exchangerates.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.Flag
import java.util.*

@Dao
interface CurrencyDao {

    @Insert
    fun insertCurrency(currency: Currency)

    @Query("SELECT * FROM Currency")
    fun getAll(): List<Currency>


    @Query("select * from currency where code=:code")
    fun getCurrenciesByCode(code: String): List<Currency>

    @Query("select * from currency order by id desc limit 25")
    fun getLast(): List<Currency>

    @Query("select * from currency order by id desc limit 1")
    fun last(): Currency?

    @Query("select * from currency where code=:code order by id desc")
    fun getByCode(code: String): List<Currency>

    @Insert
    fun insertFlag(flag: Flag)

    @Query("select * from flag where code = :id")
    fun getFlagById(id: String): Flag?

    @Query("SELECT EXISTS(SELECT * FROM flag WHERE code = :id)")
    fun checkIsHaveFlag(id: String): Boolean

}