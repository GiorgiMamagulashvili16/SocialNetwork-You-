package com.example.you.util

import android.util.Log.d
import java.net.InetSocketAddress
import java.net.Socket

object HasInternetConnection {
    fun hasInternet(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: Exception) {
            d("socketerror", "$e")
            false
        }
    }
}