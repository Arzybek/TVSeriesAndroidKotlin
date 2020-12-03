package com.example.tvseriesprojectapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tvseriesprojectapp.fragments.allFragment
import com.example.tvseriesprojectapp.fragments.loginFragment
import com.example.tvseriesprojectapp.fragments.profileFragment
import com.example.tvseriesprojectapp.user.Session
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val loginFrag = loginFragment()
    val profileFrag = profileFragment()
    val allFragment  = allFragment()
    var jwtCookie = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_login-> makeCurrentFragment(loginFrag)
                R.id.action_mail-> makeCurrentFragment(profileFrag)
                R.id.action_all -> makeCurrentFragment(allFragment)
                else -> 1==1
            }
            true
        }

//        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.loginButton1 -> loginFrag.onClick(v)
                com.example.tvseriesprojectapp.R.id.refreshButton -> allFragment.onClick(v)
            }
        }
    }


    fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fl_wrapper, fragment)
        commit()
    }

    fun makeCurrentFragment(fragmentTag: String) = supportFragmentManager.beginTransaction().apply {
        when (fragmentTag){
            "loginFragment"-> replace(R.id.fl_wrapper, loginFrag)
            "profileFragment"-> replace(R.id.fl_wrapper, profileFrag)
            "allFragment"-> replace(R.id.fl_wrapper, allFragment)
        }
        //replace(R.id.fl_wrapper, fragment)
        commit()
    }

//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.action_map -> {
//                openFragment(profileFrag)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.action_login -> {
//                openFragment(loginFrag)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.action_all -> {
//                openFragment(allFragment)
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//        false
//    }
//
//
//    private fun openFragment(fragment: Fragment) {
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }

    fun setJWT(jwt:String)
    {
        this.jwtCookie = jwt;
    }

    fun getJWT():String
    {
        return this.jwtCookie;
    }
}
