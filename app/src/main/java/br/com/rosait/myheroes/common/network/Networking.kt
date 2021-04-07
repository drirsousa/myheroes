package br.com.rosait.myheroes.common.network

import android.content.Context
import android.net.ConnectivityManager

class Networking {
    companion object {
        fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }
}