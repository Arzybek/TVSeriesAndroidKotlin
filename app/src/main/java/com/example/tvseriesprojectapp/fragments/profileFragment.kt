package com.example.tvseriesprojectapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.example.tvseriesprojectapp.user.Session
import com.squareup.picasso.Picasso
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.request.get
import io.ktor.http.Cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception


class profileFragment : Fragment(), View.OnClickListener {


    private var ip = Session.ip
    private var port = Session.port
    private var url = "http://${ip}:${port}/register/insecure"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        this.ip = Session.ip!!
        this.port = Session.port!!
        this.url = "http://${ip}:${port}/profile"

    }

    override fun onResume(){
        super.onResume()

        var aa = activity.toString()
        Log.i("prof", aa)
        var jwt = (activity as MainActivity?)?.getJWT()!!
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
        Log.i("Profile", "login tapped")

        val toast: Toast = Toast.makeText(view!!.context, "Login failed.", Toast.LENGTH_LONG);
        toast.show()

    }


}