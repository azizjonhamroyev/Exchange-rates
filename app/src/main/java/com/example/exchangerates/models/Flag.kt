package com.example.exchangerates.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Flag {
    @PrimaryKey
    var code: String = ""
    var image: String = ""


    constructor()
    constructor(code: String, image: String) {
        this.code = code
        this.image = image
    }
}