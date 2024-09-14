package com.example.everynote.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class Utilities
{
    companion object
    {
        @JvmStatic fun checkForInternet(context: Context): Boolean
        {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when
            {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }
}