package com.example.exchangerates.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageCodeDecode {

    fun code(b: Bitmap): String {
        val baos = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val byte = baos.toByteArray()
        return Base64.encodeToString(byte, Base64.DEFAULT)
    }

    fun decode(s: String): Bitmap? {
        return try {
            val byte = Base64.decode(s, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byte, 0, byte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }
}