package com.example.tvseriesprojectapp.fragments

import android.content.Intent
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
import com.example.tvseriesprojectapp.common.HTTPHandler
import com.example.tvseriesprojectapp.common.RSA
import com.example.tvseriesprojectapp.user.User
import org.jetbrains.anko.doAsync

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


    override fun onCreate(savedInstanceState: Bundle?) {

        val ip = "172.17.98.49" // 109 - laptop, 103 - pc
        val port = "8080"
        val url = "http://${ip}:${port}/tvshows/register"
        this.URL = url

        doAsync {
            val result = java.net.URL(url).readText()
            rsa = result
        }


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
                R.id.loginButton -> loginPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        Log.d("Login", "Try login from loginfrag")
        if (v != null) {
            when (v.id) {
                R.id.loginButton -> Toast.makeText(view!!.context, "Logging in.", Toast.LENGTH_LONG).show()
                //R.id.loginButton -> loginPressed()
            }
        }
    }

    private fun loginPressed() {
        Log.d("Login", "Try login")

        val loginText: EditText = view!!.findViewById<EditText>(R.id.login)
        val passwordText: EditText = view!!.findViewById<EditText>(R.id.password)
        if (tryLogin(loginText.text.toString(), passwordText.text.toString())) {
            Log.i("Login", "Login was successful")
            User.name = loginText.text.toString()
            startActivity(Intent(view!!.context, MainActivity::class.java))
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
        val text = login+":"+password
        val publicRsaKey = RSA.getPublicKey(rsa)
        val encrypted = RSA.encrypt(text, publicRsaKey)
        val sender = HTTPHandler(url = URL);
        this.cookieJWT = sender.sendPostRequest(encrypted)


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