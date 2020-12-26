package com.example.tvseriesprojectapp.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.dto.User
import com.example.tvseriesprojectapp.repo.ProfileAdapter
import com.example.tvseriesprojectapp.repo.RepoListAdapter
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*
import java.lang.Exception


class profileFragment : Fragment(), View.OnClickListener {
    var result: List<TvShow> = listOf()

    var clickHandler = ClickListener()

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    inner class ClickListener() : RepoListAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            var bundle = Bundle()
            bundle.putInt("id", position)
            if (transaction != null) {
                val fragment = showFragment()
                fragment.arguments = bundle
                transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment)
                    .addToBackStack("")
                transaction.commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        clickHandler = ClickListener()
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        var viewExtracted = view
        var recProfile = recyclerProfile
        var refreshButton =
            refreshButtonProfile  // это нужно чтобы прилажуха при повороте не падала (проблема в асинхронщине мне кажется)

        var cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onResumeProfile launch")
            var user = ProfileAdapter().getProfile(cookie)
            if (user == null || user.name == null || user.name == "")
                drawNoUser()
            else {
                Log.i("profile", user.name)
                drawUser(user, viewExtracted)
                if (cookie != "") {
                    retrieveShows(recProfile)
                    refreshButton.setOnClickListener {
                        retrieveShows(recProfile)
                    }
                }
            }
        }
    }

    fun retrieveShows(recProfile: RecyclerView?) {
        recProfile!!.layoutManager = LinearLayoutManager(mContext)
        val mainActivityJob = Job()
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            mContext?.let {
                AlertDialog.Builder(it).setTitle("Error")
                    .setMessage(exception.message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert).show()
            }
        }

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            var cookie = (activity as MainActivity).getAuthCookie()
            val resultList = TvShowsRetriever().getRepositoriesUser("auth=" + cookie)
            result = resultList
            val result = RepoResult(resultList)
            recyclerProfile.adapter = RepoListAdapter(result, clickHandler)
        }
    }

    private fun drawNoUser() {
        view!!.findViewById<TextView>(R.id.profileName).setText("Anonymous")
        view!!.findViewById<TextView>(R.id.profileAge).setText("0")
        view!!.findViewById<ImageView>(R.id.profilePicture)
            .setImageResource(R.drawable.default_profile)
    }


    private fun drawUser(user: User, viewExtracted: View?) {
        viewExtracted!!.findViewById<TextView>(R.id.profileName).setText(user.name)
        viewExtracted!!.findViewById<TextView>(R.id.profileAge).setText(user.age.toString())
        try {
            val imageView = viewExtracted!!.findViewById<ImageView>(R.id.profilePicture)
            Picasso.get().load(user.photoLink).into(imageView)
        } catch (e: Exception) {
            view!!.findViewById<ImageView>(R.id.profilePicture)
                .setImageResource(R.drawable.default_profile)
            Log.e("drawUSerException", e.toString())
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onClick(view: View) {

        if (view != null) {
            when (view.id) {
                R.id.logoutProfileButton -> logout()
            }
        }


    }


    private fun logout() {
        (activity as MainActivity).logout()
    }


}