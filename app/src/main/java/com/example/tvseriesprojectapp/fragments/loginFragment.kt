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
import com.example.tvseriesprojectapp.repo.ProfileAdapter
import com.example.tvseriesprojectapp.user.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

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
            if (Session.RSAsecure)
            {
                Log.d("coroutine", "coroutine secureRegister")
                secureRegister(logPass)
            }
            else
            {
                Log.d("coroutine", "coroutine insecureRegister")
                insecureRegister(logPass)
            }
        }
    }


    suspend private fun insecureRegister(logPass:String)
    {
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

    suspend private fun secureRegister(logPass: String)
    {
        var key = ProfileAdapter().getRSAkey()
        Log.d("auth", "RSA public key: "+key)
        var arguments = key.split("\n")
        var modulus = ""
        var exponent = ""
        for(i:String in arguments)
        {
            var formatted = i.replace(" ", "")
            if (formatted.startsWith("modulus"))
            {
                modulus = formatted.replace("modulus:", "")
            }
            if (formatted.startsWith("publicexponent"))
            {
                exponent = formatted.replace("publicexponent:","")
            }

        }
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val cipher: Cipher = Cipher.getInstance("RSA")
        val pubSpec = RSAPublicKeySpec(modulus.toBigInteger(), exponent.toBigInteger())
        val pubKey: PublicKey = keyFactory.generatePublic(pubSpec)
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        val encrypted = cipher.doFinal(logPass.toByteArray())
        val toSend = String(encrypted)

        var data = ProfileAdapter().secureRegister(toSend)
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