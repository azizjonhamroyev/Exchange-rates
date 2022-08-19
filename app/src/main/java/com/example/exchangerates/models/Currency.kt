package com.example.exchangerates.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Currency : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var code: String = ""
    var cb_price: Double? = null
    var date: String? = null
    var nbu_buy_price: Double? = null
    var nbu_cell_price: Double? = null
    var title: String? = null

    constructor()
    constructor(
        code: String,
        cb_price: Double?,
        date: String?,
        nbu_buy_price: Double?,
        nbu_cell_price: Double?,
        title: String?
    ) {
        this.code = code
        this.cb_price = cb_price
        this.date = date
        this.nbu_buy_price = nbu_buy_price
        this.nbu_cell_price = nbu_cell_price
        this.title = title
    }

}
