package com.example.tvseriesprojectapp.repo

import android.util.Log
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.user.Session
import com.google.gson.GsonBuilder
import io.ktor.http.*
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*


class TvShowsRetriever {
    private val service: TvSeriesService
    private val perPage = 4;

    private class SessionCookieJar : CookieJar {
        private var cookies: List<okhttp3.Cookie>? = null

        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<okhttp3.Cookie>) {
                this.cookies = cookies
        }

        override fun loadForRequest(url: HttpUrl): MutableList<okhttp3.Cookie> {
            return if (cookies != null) {
                cookies as MutableList<okhttp3.Cookie>
            } else mutableListOf()
        }
    }

    companion object {
        const val BASE_URL = "http://${Session.ip}:${Session.port}/"
    }

    init {

        val gson = GsonBuilder()
                .setLenient()
                .create()
        // 2
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) //important
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .client(OkHttpClient().newBuilder().cookieJar(SessionCookieJar()).build())
            .build()


        service = retrofit.create(TvSeriesService::class.java)
    }

    suspend fun getRepositories(): List<TvShow>  {
        return service.searchRepositories(perPage)
    }

    suspend fun getRepositoriesUser(str: String): List<TvShow>  {
        return service.searchRepositoriesUser(str)
    }

    suspend fun getShow(showID:Long) : TvShow
    {
        Log.d("retrofit", "getShow "+showID.toString())
        return service.getShow(showID)
    }


    suspend fun watchingShow(showID: Long, cookie:String)
    {
        Log.d("retrofit", "watchingShow "+showID.toString()+" "+cookie)
        var cookieToSend = "auth="+cookie
        service.watchingShow(showID.toString(), cookieToSend)
    }

    suspend fun unwatchingShow(showID: Long, cookie:String)
    {
        Log.d("retrofit", "unwatchingShow "+showID.toString()+" "+cookie)
        var cookieToSend = "auth="+cookie
        service.unwatchingShow(showID.toString(), cookieToSend)
    }


    suspend fun watchingEpisode(showID: Long, episodeID:Long, cookie:String)
    {
        Log.d("retrofit", "watchingEpisode "+showID.toString()+" "+episodeID.toString()+" "+cookie)
        var cookieToSend = "auth="+cookie
        service.watchingEpisode(showID.toString(), episodeID.toString(), cookieToSend)
    }

    suspend fun unwatchingEpisode(showID: Long, episodeID:Long, cookie:String)
    {
        Log.d("retrofit", "unwatchingEpisode "+showID.toString()+" "+episodeID.toString()+" "+cookie)
        var cookieToSend = "auth="+cookie
        service.unwatchingEpisode(showID.toString(), episodeID.toString(), cookieToSend)
    }

    suspend fun isWatching(showID: Long, cookie:String):Boolean
    {
        Log.d("retrofit", "isWatching "+showID.toString()+" "+cookie)
        var cookieToSend = "auth="+cookie
        return service.isWatchingShow(showID.toString(), cookieToSend)
    }

    suspend fun getWatchedEpisodes(showID: Long, cookie:String):BooleanArray
    {
        var cookieToSend = "auth="+cookie
        Log.d("retrofit", "getWatchedEpisodes "+showID.toString()+" "+cookieToSend)
        return service.getWatchedEpisodes(showID.toString(), cookieToSend)
    }

    suspend fun addUserShow(showData:String, cookie:String)
    {
        Log.d("retrofit", "addUserShow "+showData+" "+cookie)
        var cookieToSend = "auth="+cookie
        service.addUserShow(showData, cookieToSend)
    }

    suspend public fun ratingShow(rating:Float, showID: Long, cookie:String)
    {
        var cookieToSend = "auth="+cookie
        service.rateShow(rating.toString(), showID.toString(), cookieToSend)
    }

    suspend public fun getUserRating(showID: Long, cookie:String):Float
    {
        var cookieToSend = "auth="+cookie
        return service.getUserRating(showID.toString(), cookieToSend)
    }

    suspend public fun sendReview(review:String, showID: Long, cookie:String)
    {
        var cookieToSend = "auth="+cookie
        service.reviewShow(review, showID.toString(), cookieToSend)
    }

    suspend public fun getReview(showID: Long, cookie:String):String
    {
        var cookieToSend = "auth="+cookie
        return service.getUserReview(showID.toString(), cookieToSend)
    }

    suspend public fun getShowRating(showID: Long):Float
    {
        return service.getShowRating(showID.toString());
    }

    suspend public fun getRandomReviews(amount:Int, showID: Long):ArrayList<String>
    {
        return service.getRandomReviews(amount.toString(), showID.toString());
    }
}