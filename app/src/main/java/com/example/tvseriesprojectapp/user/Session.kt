package com.example.tvseriesprojectapp.user

import android.content.Context

import android.net.NetworkCapabilities
import android.os.Build
import android.net.ConnectivityManager
import android.support.v7.widget.LinearLayoutManager

object Session {

    const val ip = "192.168.0.109"  // val ip = "172.17.98.49" // 109 - laptop, 103 - pc
    const val port = "8080"
    val host = "http://${ip}:${port}/"

}