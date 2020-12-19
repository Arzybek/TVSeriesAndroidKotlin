package com.example.tvseriesprojectapp.dto

data class RepoResult(val items: List<TvShow>)

data class TvShow(val id: Long, val name: String, val category: String, val year: Int, val episodes:List<Episode>, val imgLink:String){
}