package com.example.tvseriesprojectapp.repo
import com.example.tvseriesprojectapp.dto.TvShow
import retrofit2.http.GET
import retrofit2.http.Header

interface TvSeriesService {
    @GET("/tvshows")
    suspend fun retrieveRepositories(): List<TvShow>

    @GET("/tvshows?q=4")
    suspend fun searchRepositories(): List<TvShow>

    @GET("/user/watching")
    suspend fun searchRepositoriesUser(@Header("Cookie") auth: String): List<TvShow>
}