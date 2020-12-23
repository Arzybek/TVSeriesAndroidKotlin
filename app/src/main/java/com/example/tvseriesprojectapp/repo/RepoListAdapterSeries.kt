package com.example.tvseriesprojectapp.repo

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.Episode
import com.example.tvseriesprojectapp.dto.EpisodeSerias
import kotlinx.android.synthetic.main.episode.view.*

class RepoListAdapterSeries(private val repoList: EpisodeSerias, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RepoListAdapterSeries.ViewHolder>() {

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
            itemView.seriasName.text = repo.episodeName.toString().orEmpty()
            if (repo.isWatched){
                itemView.setBackgroundColor(Color.GREEN)
                //todo Change color
            } else
            {
                itemView.setBackgroundColor(0)
            }
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
                itemView.refreshDrawableState()
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}