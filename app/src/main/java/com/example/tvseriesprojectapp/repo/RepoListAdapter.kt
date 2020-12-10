package com.example.tvseriesprojectapp.repo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.user.Session
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoListAdapter(private val repoList: RepoResult, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRepo(repoList.items[position])
    }

    override fun getItemCount(): Int = repoList.items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val url = "http://${Session.ip}:${Session.port}/tvshows/image/";
        init {
            itemView.setOnClickListener(this)
        }
        fun bindRepo(repo: TvShow) {
            itemView.name.text = repo.name.orEmpty()
            itemView.category.text = repo.category.orEmpty()
            itemView.year.text = repo.year.toString().orEmpty()
            if (repo.imgLink=="")
                Picasso.get().load(url+repo.id.toString()).into(itemView.icon) // здесь можно просто грузить какую нибудь заглушку
            else
                Picasso.get().load(url+repo.imgLink).into(itemView.icon)
               //Picasso.get().load(url+repo.id.toString()).into(itemView.icon)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            val id = repoList.items[position].id
            if(position != RecyclerView.NO_POSITION)
                listener.onItemClick(id.toInt())
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}