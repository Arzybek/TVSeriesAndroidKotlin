package com.example.tvseriesprojectapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.beust.klaxon.Klaxon
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import kotlinx.coroutines.*

class addshowFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private fun addShowButtonPressedAsync() {
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