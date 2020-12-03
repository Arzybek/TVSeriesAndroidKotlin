package com.example.tvseriesprojectapp
import com.example.tvseriesprojectapp.user.Session
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TvShowsRetriever {
    private val service: TvSeriesService

    companion object {
        const val BASE_URL = "http://${Session.ip}:${Session.port}/"
    }

    init {
        // 2
        val retrofit = Retrofit.Builder()
            // 1
            .baseUrl(BASE_URL)
            //3
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //4
        service = retrofit.create(TvSeriesService::class.java)
    }

    suspend fun getRepositories(): List<TvShow>  {
        return service.searchRepositories()
    }
}