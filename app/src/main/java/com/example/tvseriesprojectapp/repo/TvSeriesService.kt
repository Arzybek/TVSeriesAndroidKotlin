package com.example.tvseriesprojectapp.repo
import com.example.tvseriesprojectapp.dto.TvShow
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TvSeriesService {
    @GET("/tvshows")
    suspend fun retrieveRepositories(): List<TvShow>

    @GET("/tvshows?q=6")
    suspend fun searchRepositories(): List<TvShow>

    @GET("/user/watching")
    suspend fun searchRepositoriesUser(@Header("Cookie") auth: String): List<TvShow>

    @GET("/tvshows/{showID}")
    suspend fun getShow(@Path("showID") showID:String) : TvShow
}