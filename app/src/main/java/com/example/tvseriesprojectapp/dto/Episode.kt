package com.example.tvseriesprojectapp.dto

data class EpisodeSerias(val items: List<Episode>)

data class Episode(val id: Long, val index: Int, val description: String, var isWatched:Boolean){
}