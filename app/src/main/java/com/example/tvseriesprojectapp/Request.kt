package com.example.tvseriesprojectapp

import android.util.Log
import com.google.gson.Gson
import java.net.URL

class Request(private val url: String) {
    fun run(): List<TvShow> {
        val resp = URL(url).readText()
        return Gson().fromJson(resp, Array<TvShow>::class.java).toList()
    }
}