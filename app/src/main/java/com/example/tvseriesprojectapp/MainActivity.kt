package com.example.tvseriesprojectapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.gson.Gson;
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.experimental.launch
import java.net.URL

class MainActivity : AppCompatActivity() {
    val ip = "192.168.0.103"
    val port = "8080"
    val gson = Gson();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = findViewById<LinearLayout>(R.id.root)

        var listOfShows: MutableList<TvShow> = mutableListOf();
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

        launch {
            resp = URL(url).readText()
        }
        Thread.sleep(1000)

        listOfShows = gson.fromJson(resp, Array<TvShow>::class.java).toMutableList()
        for (one in listOfShows) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.text = "Name: ${one.name} \n Category: ${one.category} \n Year: ${one.year}"
            layout?.addView(textView)
        }
    }
}
