package com.example.tvseriesprojectapp
import retrofit2.Call
import retrofit2.http.GET

interface TvSeriesService {
    @GET("/tvshows")
    suspend fun retrieveRepositories(): List<TvShow>

    @GET("/tvshows?q=2")
    suspend fun searchRepositories(): List<TvShow>
}