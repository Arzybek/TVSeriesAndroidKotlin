package com.example.tvseriesprojectapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val ip = "192.168.0.109" // 109 - laptop, 103 - pc
    val port = "8080"

    private val repoRetriever = TvShowsRetriever()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root.layoutManager = LinearLayoutManager(this)

        val url = "http://${ip}:${port}/tvshows"
        val search = "?q=2"
        var resp: String = "";
//      val str: String = "[{\"id\":1,\"name\":\"Everybody hates Chris\",\"category\":\"comedy\",\"year\":2005},{\"id\":2,\"name\":\"Friends\",\"category\":\"comedy\",\"year\":1994}]";

        if(isNetworkConnected()) {
            retrieveRepositories()
        }
        else {
        AlertDialog.Builder(this).setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again")
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
        refreshButton.setOnClickListener {
            retrieveRepositories()
        }
    }

    fun retrieveRepositories() {
        //1 Create a Coroutine scope using a job to be able to cancel when needed
        val mainActivityJob = Job()

        //2 Handle exceptions if any
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            AlertDialog.Builder(this).setTitle("Error")
                .setMessage(exception.message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

        //3 the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            //4
            val resultList = TvShowsRetriever().getRepositories()
            val result = RepoResult(resultList)
            root.adapter = RepoListAdapter(result)
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
