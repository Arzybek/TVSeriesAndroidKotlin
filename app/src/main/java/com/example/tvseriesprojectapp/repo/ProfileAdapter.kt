package com.example.tvseriesprojectapp.repo

import android.util.Log
import com.example.tvseriesprojectapp.dto.User
import com.example.tvseriesprojectapp.user.Session
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProfileAdapter {

    private val service: ProfileService


    companion object {
        const val BASE_URL = "http://${Session.ip}:${Session.port}/"
    }

    init {
        // 2
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(OkHttpClient().newBuilder().cookieJar(SessionCookieJar()).build())
                .build()

        service = retrofit.create(ProfileService::class.java)
    }

    suspend public fun getProfile(cookie:String) : User
    {
        Log.d("retrofit", "getProfile "+cookie)
        var cookieToSend = "auth="+cookie
        return service.getProfile(cookieToSend)
    }

    suspend public fun insecureRegister(logPass:String) : String
    {
        Log.d("retrofit", "insecureRegister  "+logPass)
        var cookieToSend = "register="+logPass
        return service.insecureRegister(cookieToSend)
    }


}