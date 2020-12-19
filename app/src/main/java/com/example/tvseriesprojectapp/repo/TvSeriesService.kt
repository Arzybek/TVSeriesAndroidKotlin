package com.example.tvseriesprojectapp.repo
import com.example.tvseriesprojectapp.dto.TvShow
import retrofit2.http.*

interface TvSeriesService {
    @GET("/tvshows")
    suspend fun retrieveRepositories(): List<TvShow>

    @GET("/tvshows")
    suspend fun searchRepositories(@Query("q") perPage: Int): List<TvShow>

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
    suspend fun addUserShow(@Body info:String, @Header("Cookie") auth: String)

    @POST("/user/rateShow")
    suspend fun rateShow(@Query("rating") rating:String, @Query("showID") showID:String, @Header("Cookie") auth: String)

    @GET("/user/rating")
    suspend fun getUserRating(@Query("showID") showID:String, @Header("Cookie") auth: String):Float

    @POST("/user/reviewShow")
    suspend fun reviewShow(@Body review:String, @Query("showID") showID:String, @Header("Cookie") auth: String)

    @GET("/user/review")
    suspend fun getUserReview(@Query("showID") showID:String, @Header("Cookie") auth: String):String

    @GET("/tvshows/rating")
    suspend fun getShowRating(@Query("showID") showID:String):Float

    @GET("/tvshows/randomReviews")
    suspend fun getRandomReviews(@Query("amount") amount:String, @Query("showID") showID:String):ArrayList<String>
}