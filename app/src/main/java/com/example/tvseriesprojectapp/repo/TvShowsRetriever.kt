package com.example.tvseriesprojectapp.repo

import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.user.Session
import io.ktor.http.*
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class TvShowsRetriever {
    private val service: TvSeriesService

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
        // 2
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .client(OkHttpClient().newBuilder().cookieJar(SessionCookieJar()).build())
            .build()

        service = retrofit.create(TvSeriesService::class.java)
    }

    suspend fun getRepositories(): List<TvShow>  {
        return service.searchRepositories()
    }

    suspend fun getRepositoriesUser(str: String): List<TvShow>  {
        return service.searchRepositoriesUser(str)
    }

    suspend fun getShow(showID:Long) : TvShow
    {
        return service.getShow(showID.toString())
    }
}