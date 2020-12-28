package com.example.tvseriesprojectapp.user

object Session {
    const val ip =
        "192.168.0.104" //val ip = "192.168.0.104" // const val ip = "192.168.1.213"  // 109 - laptop, 103 - pc
    const val port = "8080"
    val host = "http://${ip}:${port}/"
    const val RSASecure = false
}