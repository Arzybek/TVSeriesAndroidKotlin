package com.example.tvseriesprojectapp.repo

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.Episode
import com.example.tvseriesprojectapp.dto.EpisodeSerias
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.user.Session
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.episode.view.*
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoListAdapterSerias(private val repoList: EpisodeSerias, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RepoListAdapterSerias.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRepo(repoList.items[position])
    }

    override fun getItemCount(): Int = repoList.items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        fun bindRepo(repo: Episode) {
            itemView.seriasName.text = repo.index.toString().orEmpty()
            itemView.seriasDescription.text = repo.description.orEmpty()
            if (repo.isWatched){
                itemView.setBackgroundColor(Color.GREEN)
                //todo Change color
            }
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}