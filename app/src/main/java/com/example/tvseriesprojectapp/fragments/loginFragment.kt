package com.example.tvseriesprojectapp.fragments

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
import com.example.tvseriesprojectapp.user.Session
import com.example.tvseriesprojectapp.user.User
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.features.cookies.cookies
import io.ktor.client.request.post
import io.ktor.http.Cookie
import kotlinx.coroutines.*

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
    private var cookieJWT:String = ""

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
        if (tryLogin(loginText.text.toString(), passwordText.text.toString())) {
            Log.i("Login", "Login was successful")
            User.name = loginText.text.toString()
            val toast: Toast = Toast.makeText(view!!.context, "Successful login!", Toast.LENGTH_LONG);
            toast.show()
            (activity as MainActivity?)?.makeCurrentFragment("profileFragment")
            //startActivity(Intent(view!!.context, MainActivity::class.java))// здесь возможно стоит как то научиться перебрасывать на фрагмент профиля
        } else {
            Log.i("Login", "Login was failed")
            loginText.setText("")
            passwordText.setText("")
            val toast: Toast = Toast.makeText(view!!.context, "Login failed.", Toast.LENGTH_LONG);
            toast.show()
        }
    }

    private fun tryLogin(login: String, password: String) : Boolean {
        //ToDo implement login with dataBase connect
        //ToDo за одно прокинь сюда роль юзер или админ Session.role = Role.valueOf("user or admin")
        //TODO закодить в RSA

        Log.i("Login", "url: "+url)

        val logPass = login+":"+password

        Log.i("Login", "LogPass: "+logPass)
        //Log.i("Login", rsa)

        val client = HttpClient(){
            install(HttpCookies) {
                // Will keep an in-memory map with all the cookies from previous requests.
                storage = AcceptAllCookiesStorage()

                // Will ignore Set-Cookie and will send the specified cookies.
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(url, Cookie("register", logPass))
                }
                //storage = ConstantCookiesStorage(Cookie("register", logPass))
            }
        }

        var data = ""


        runBlocking (Dispatchers.IO) {
            var aaa = client.cookies(url)
            Log.i("Login", "login cookie: "+aaa[0].toString())
            data = client.post<String>(url)
            Log.i("Login", "data: "+data)
        }




        this.cookieJWT = data


        //val publicRsaKey = RSA.getPublicKey(rsa)

        //val encrypted = RSA.encrypt(text, publicRsaKey)
        //this.cookieJWT = sender.sendPostRequest(text)

        Log.i("Login", "Cookie JWT: "+cookieJWT)

        (activity as MainActivity?)?.setJWT(data)

        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment loginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                loginFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}