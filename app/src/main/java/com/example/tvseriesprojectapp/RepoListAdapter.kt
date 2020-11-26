package com.example.tvseriesprojectapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoListAdapter(private val repoList: RepoResult) : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRepo(repoList.items[position])
    }

    override fun getItemCount(): Int = repoList.items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ip = "192.168.0.109" // 109 - laptop, 103 - pc
        val port = "8080"
        private val url = "http://${ip}:${port}/tvshows/image/";

        fun bindRepo(repo: TvShow) {
            itemView.name.text = repo.name.orEmpty()
            itemView.category.text = repo.category.orEmpty()
            itemView.year.text = repo.year.toString().orEmpty()
            Picasso.get().load(url+repo.id.toString()).into(itemView.icon)
        }
    }
}