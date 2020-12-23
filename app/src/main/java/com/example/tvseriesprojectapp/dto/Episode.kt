package com.example.tvseriesprojectapp.dto

data class EpisodeSerias(val items: List<Episode>)

data class Episode(val id: Long, val index: Int, val description: String, val episodeName: String, var isWatched:Boolean){
}