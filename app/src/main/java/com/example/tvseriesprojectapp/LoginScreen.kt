package com.example.tvseriesprojectapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText

class LoginScreen : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
    }

    override fun onClick(v: View?) {
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
            startActivity(Intent(this, MainActivity::class.java))

        } else {
            Log.i("Login", "Login was failed")
            loginText.setText("")
            passwordText.setText("")
        }
    }

    private fun tryLogin(login: String, password: String) : Boolean {
        //ToDo implement login with dataBase connect
        return "PesGospoda" == login && "228" == password
    }
}