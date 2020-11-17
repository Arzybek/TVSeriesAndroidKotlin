package com.example.tvseriesprojectapp

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.tvseriesprojectapp.common.HTTPHandler
import com.example.tvseriesprojectapp.common.RSA
import com.example.tvseriesprojectapp.user.User
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread






class LoginScreen :AppCompatActivity(), View.OnClickListener{

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
        setContentView(R.layout.activity_login_screen)

    }

    fun login(v:View){
        if (v != null) {
            when (v.id) {
                R.id.loginButton -> loginPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        val toast: Toast = Toast.makeText(this, "Trying to login.", Toast.LENGTH_LONG);
        toast.show()
        if (v != null) {
            when (v.id) {
                R.id.loginButton -> loginPressed()
            }
        }
    }

    private fun loginPressed() {
        Log.d("Login", "Try login")

        val loginText: EditText = findViewById<EditText>(R.id.login)
        val passwordText: EditText = findViewById<EditText>(R.id.password)
        if (tryLogin(loginText.text.toString(), passwordText.text.toString())) {
            Log.i("Login", "Login was successful")
            User.name = loginText.text.toString()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Log.i("Login", "Login was failed")
            loginText.setText("")
            passwordText.setText("")
            val toast: Toast = Toast.makeText(this, "Login failed.", Toast.LENGTH_LONG);
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
}