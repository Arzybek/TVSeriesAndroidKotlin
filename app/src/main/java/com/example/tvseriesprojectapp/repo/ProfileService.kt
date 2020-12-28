package com.example.tvseriesprojectapp.repo


import com.example.tvseriesprojectapp.dto.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileService {

    @GET("/profile")
    suspend fun getProfile(@Header("Cookie") auth: String): User

    @POST("/register/insecure")
    suspend fun insecureRegister(@Header("Cookie") auth: String): String

    @POST("/register")
    suspend fun secureRegister(@Header("Cookie") auth: String): String

    @GET("/register")
    suspend fun getRSAkey(): String


}