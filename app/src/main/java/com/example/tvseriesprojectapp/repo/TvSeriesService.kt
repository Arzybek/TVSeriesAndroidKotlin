package com.example.tvseriesprojectapp.repo
import com.example.tvseriesprojectapp.dto.TvShow
import retrofit2.http.*

interface TvSeriesService {
    @GET("/tvshows")
    suspend fun retrieveRepositories(): List<TvShow>

    @GET("/tvshows?q=6")
    suspend fun searchRepositories(): List<TvShow>

    @GET("/user/watching")
    suspend fun searchRepositoriesUser(@Header("Cookie") auth: String): List<TvShow>

    @GET("/tvshows/{showID}")
    suspend fun getShow(@Path("showID") showID:Long) : TvShow

    @POST("/user/addWatching")
    suspend fun watchingShow(@Query("showID") showID:String, @Header("Cookie") auth: String)

    @POST("/user/deleteWatching")
    suspend fun unwatchingShow(@Query("showID") showID:String, @Header("Cookie") auth: String)

    @POST("/user/watchEpisode")
    suspend fun watchingEpisode(@Query("showID") showID:String, @Query("epID") epID:String,@Header("Cookie") auth: String)

    @POST("/user/unwatchEpisode")
    suspend fun unwatchingEpisode(@Query("showID") showID:String,@Query("epID") epID:String, @Header("Cookie") auth: String)

    @GET("/user/isWatching")
    suspend fun isWatchingShow(@Query("showID") showID:String, @Header("Cookie") auth: String):Boolean

    @GET("/user/watchedEpisodes")
    suspend fun getWatchedEpisodes(@Query("showID") showID:String, @Header("Cookie") auth: String):BooleanArray

    @POST("/user/addUserWatchingShow")
    suspend fun addUserShow(@Query("info") info:String, @Header("Cookie") auth: String)
}