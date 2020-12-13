package com.example.tvseriesprojectapp.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.dto.User
import com.example.tvseriesprojectapp.repo.ProfileAdapter
import com.example.tvseriesprojectapp.repo.RepoListAdapter
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import com.squareup.picasso.Picasso
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.request.get
import io.ktor.http.Cookie
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import kotlin.math.log


class profileFragment : Fragment(), View.OnClickListener {
    public var result: List<TvShow> = listOf()

    private var ip = Session.ip
    private var port = Session.port
    private var url = "http://${ip}:${port}/profile"
    private var cookie = ""
    var clickHandler = ClickListener(cookie)

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    inner class ClickListener(val cookie: String): RepoListAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            var bundle = Bundle()
            bundle.putInt("id", position)
            if (transaction != null) {
                val fragment = showFragment()
                fragment.arguments = bundle
                transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.cookie = (activity as MainActivity).getAuthCookie()
        clickHandler = ClickListener(cookie)
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()

        var cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onResumeProfile launch")
            var user = ProfileAdapter().getProfile(cookie)
            if (user.name==null)
                drawNoUser()
            else
            {
                Log.i("profile", user.name)
                drawUser(user)
            }

            if(cookie!="") {
                retrieveShows()
                refreshButtonProfile.setOnClickListener {
                    retrieveShows()
                }
            }
        }

        /*val client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(url, Cookie("auth", cookie))
                }
            }
        }

        var data = ""

        runBlocking(Dispatchers.IO) {
            data = client.get<String>(url)
            Log.i("aaa", data)
        }

        Log.i("profile", data)
        Log.i("JWT", cookie)

        if (data.equals(""))
            drawNoUser()
        else
            drawUser(data)

        if(cookie!="") {
            retrieveShows()
            refreshButtonProfile.setOnClickListener {
                retrieveShows()
            }
        }*/
    }

    fun retrieveShows() {
        //1 Create a Coroutine scope using a job to be able to cancel when needed
        recyclerProfile.layoutManager = LinearLayoutManager(mContext)
        val mainActivityJob = Job()
        //2 Handle exceptions if any
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            mContext?.let {
                AlertDialog.Builder(it).setTitle("Error")
                    .setMessage(exception.message)
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert).show()
            }
        }

        //3 the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            //4
            val resultList = TvShowsRetriever().getRepositoriesUser("auth="+cookie)
            result = resultList
            val result = RepoResult(resultList)
            recyclerProfile.adapter = RepoListAdapter(result, clickHandler)
        }
    }

    private fun drawNoUser()
    {
        view!!.findViewById<TextView>(R.id.profileName).setText("Anonymous")
        view!!.findViewById<TextView>(R.id.profilAge).setText("0")
        view!!.findViewById<ImageView>(R.id.profilePicture).setImageResource(R.drawable.default_profile)
    }


    private fun drawUser(user: User)
    {
        view!!.findViewById<TextView>(R.id.profileName).setText(user.name)
        view!!.findViewById<TextView>(R.id.profilAge).setText(user.age.toString())
        try{
            val imageView = view!!.findViewById<ImageView>(R.id.profilePicture)
            Picasso.get().load(user.photoLink).into(imageView)
        }
        catch (e:Exception)
        {
            view!!.findViewById<ImageView>(R.id.profilePicture).setImageResource(R.drawable.default_profile)
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

    override fun onClick(view:View){

        if (view != null) {
            when (view.id) {
                R.id.logoutProfileButton -> logout()
            }
        }

        //val toast: Toast = Toast.makeText(view!!.context, "Login failed.", Toast.LENGTH_LONG);
        //toast.show()

    }


    private fun logout()
    {
        (activity as MainActivity).logout()
    }


}