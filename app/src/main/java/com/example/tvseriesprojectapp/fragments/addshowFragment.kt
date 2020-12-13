package com.example.tvseriesprojectapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import com.example.tvseriesprojectapp.user.User
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.features.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Cookie
import kotlinx.android.synthetic.main.fragment_addshow.*
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addshowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addshowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val url = "http://${Session.ip}:${Session.port}/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addshow, container, false)
    }


    fun onClick(v: View?) {
        Log.d("addShow", "Try add show")
        if (v != null) {
            when (v.id) {
                R.id.addShowButton -> addShowButtonPressedAsync()
            }
        }
    }

    private fun addShowButtonPressedAsync(){
        val showName: EditText = view!!.findViewById<EditText>(R.id.showName)
        val epCount: EditText = view!!.findViewById<EditText>(R.id.episodesCount)

        val product = HashMap<String, String>();
        product.put("showName", showName.text.toString());
        product.put("episodesCount", epCount.text.toString())
        val result = Klaxon().toJsonString(product)
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine addShowButtonPressedAsync launch")
            TvShowsRetriever().addUserShow(result, cookie)
            (activity as MainActivity?)?.makeCurrentFragment("profileFragment")
        }
    }

}