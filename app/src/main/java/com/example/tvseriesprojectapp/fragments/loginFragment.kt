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
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.repo.ProfileAdapter
import com.example.tvseriesprojectapp.repo.ProfileService
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.features.cookies.cookies
import io.ktor.client.request.post
import io.ktor.http.Cookie
import kotlinx.coroutines.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginFragment : Fragment(), View.OnClickListener{

    private var rsa:String = ""
    private var URL:String = ""
    //private var cookieJWT:String = ""

    private var ip = ""
    private var port = ""
    private var url = "http://${ip}:${port}/register/insecure"


    override fun onCreate(savedInstanceState: Bundle?) {


        this.ip = Session.ip!!
        this.port = Session.port
        this.url = "http://${ip}:${port}/register/insecure"

        //пока что RSA не работает, шлем в открытую логин пароль

        /*doAsync {
            val result = java.net.URL(url).readText()
            Log.i("Login_rsa_request", result)
            rsa = result
        }*/


        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    fun login(v:View){
        if (v != null) {
            when (v.id) {
                R.id.loginButton1 -> loginPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        Log.d("Login", "Try login from loginfrag")
        if (v != null) {
            when (v.id) {
                R.id.loginButton1 -> loginPressed()
            }
        }
    }

    private fun loginPressed() {
        val loginText: EditText = view!!.findViewById<EditText>(R.id.login)
        val passwordText: EditText = view!!.findViewById<EditText>(R.id.password)
        Log.i("login", "data: "+loginText.text.toString()+passwordText.text.toString())
        Log.i("Login", "url: "+url)

        val logPass = loginText.text.toString()+":"+passwordText.text.toString()

        Log.i("Login", "LogPass: "+logPass)


        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine tryLogin launch")
            var data = ProfileAdapter().insecureRegister(logPass)
            if (data=="ERROR")
            {
                val toast: Toast = Toast.makeText(view!!.context, "Wrong login or password", Toast.LENGTH_LONG);
                toast.show()
            }
            else
            {
                (activity as MainActivity).saveAuthCookie(data)
                val toast: Toast = Toast.makeText(view!!.context, "Successful login!", Toast.LENGTH_LONG);
                toast.show()
                (activity as MainActivity?)?.makeCurrentFragment("profileFragment")
            }
        }
    }

}