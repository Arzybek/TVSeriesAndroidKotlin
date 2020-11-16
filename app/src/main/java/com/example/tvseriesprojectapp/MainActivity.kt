package com.example.tvseriesprojectapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.tvseriesprojectapp.fragments.loginFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    val ip = "172.17.98.49" // 109 - laptop, 103 - pc
    val port = "8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root.layoutManager = LinearLayoutManager(this)

        val loginFragment = loginFragment()

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_login->makeCurrentFragment(loginFragment)
                R.id.action_mail->makeCurrentFragment(loginFragment)
                else -> 1==1
            }
            true
        }

        val url = "http://${ip}:${port}/tvshows"
        var resp: String = "";
//      val str: String = "[{\"id\":1,\"name\":\"Everybody hates Chris\",\"category\":\"comedy\",\"year\":2005},{\"id\":2,\"name\":\"Friends\",\"category\":\"comedy\",\"year\":1994}]";

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
    }

    fun onClick(v: View?) {// этот метод в логине вызывается здесь а должен из логинскрина
        if (v != null) {
            when (v.id) {
                R.id.loginButton -> 1==1
            }
        }
    }


    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fl_wrapper, fragment)
        commit()
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
