package com.example.tvseriesprojectapp

data class RepoResult(val items: List<TvShow>)

data class TvShow(val id: Int, val name: String, val category: String, val year: Int){
}