package com.example.tvseriesprojectapp.repo

import android.support.annotation.Nullable
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.dto.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileService {

    @GET("/profile")
    suspend fun getProfile(@Header("Cookie") auth: String): User

    @POST("/register/insecure")
    suspend fun insecureRegister(@Header("Cookie") auth: String) : String


}