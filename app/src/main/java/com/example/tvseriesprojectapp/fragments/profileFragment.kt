package com.example.tvseriesprojectapp.fragments

import android.content.Context
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

        var aa = activity.toString()
        Log.i("prof", aa)
        val file = File(context?.filesDir, "cookie")
        var jwt = ""
        if (file.exists())
            jwt = file.readText()
        else
            jwt = ""
        //var jwt = (activity as MainActivity?)?.getJWT()!!
        val client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(url, Cookie("auth", jwt))
                }
            }
        }

        var data = ""

        runBlocking(Dispatchers.IO) {
            data = client.get<String>(url)
            Log.i("aaa", data)
        }

        Log.i("profile", data)
        Log.i("JWT", jwt)

        if (data.equals(""))
            drawNoUser()
        else
            drawUser(data)

        if(cookie!="") {
            retrieveShows()
            refreshButtonProfile.setOnClickListener {
                retrieveShows()
            }
        }
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


    private fun drawUser(userStr:String)
    {
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(userStr)
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
        try{
            view!!.findViewById<TextView>(R.id.profileName).setText(json.string("name"))
            view!!.findViewById<TextView>(R.id.profilAge).setText(json.int("age").toString())



            val imageView = view!!.findViewById<ImageView>(R.id.profilePicture)
            Picasso.get().load(json.string("photoLink")).into(imageView)
        }
        catch (e:Exception)
        {
            Log.e("drawUSerException", e.toString())
            drawNoUser()
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