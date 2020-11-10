package com.example.tvseriesprojectapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
//import android.util.Log
//import android.view.ViewGroup
import android.widget.LinearLayout
//import com.google.gson.Gson;
//import android.widget.TextView
//import com.android.volley.Request
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
//import kotlinx.coroutines.experimental.launch
//import java.net.URL

class MainActivity : AppCompatActivity() {
    val ip = "192.168.0.109" // 109 - laptop, 103 - pc
    val port = "8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val layout = findViewById<LinearLayout>(R.id.root)
        root.layoutManager = LinearLayoutManager(this)

//        var listOfShows: MutableList<TvShow> = mutableListOf();
        val url = "http://${ip}:${port}/tvshows"

        var resp: String = "";
//      val str: String = "[{\"id\":1,\"name\":\"Everybody hates Chris\",\"category\":\"comedy\",\"year\":2005},{\"id\":2,\"name\":\"Friends\",\"category\":\"comedy\",\"year\":1994}]";

//        val err = ""
//        val queue = Volley.newRequestQueue(this)
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            Response.Listener<String> { response -> resp = response },
//            Response.ErrorListener { response -> err = "${response.message}" }
//            )
//        queue.add(stringRequest)

        if(isNetworkConnected()) {
            doAsync {
                val result = Request(url).run()
                uiThread {
                    var repoList = RepoResult(result)
                    root.adapter = RepoListAdapter(repoList)
                }
            }
        }
        else {
        AlertDialog.Builder(this).setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again")
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

//        listOfShows = gson.fromJson(resp, Array<TvShow>::class.java).toMutableList()
//        for (one in listOfShows) {
//            val textView = TextView(this)
//            textView.layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            textView.text = "Name: ${one.name} \n Category: ${one.category} \n Year: ${one.year}"
//            layout?.addView(textView)
//        }
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
