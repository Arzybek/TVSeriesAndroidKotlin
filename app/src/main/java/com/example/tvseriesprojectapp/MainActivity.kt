package com.example.tvseriesprojectapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.example.tvseriesprojectapp.fragments.*
import io.ktor.util.cio.NoopContinuation.context
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File

//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    val loginFrag = loginFragment()
    var profileFrag = profileFragment()
    val allFragment  = allFragment()
    val addshowFragment = addshowFragment()
    private var jwtCookie = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.jwtCookie = loadAuthCookie()

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_login-> makeCurrentFragment(loginFrag)
                R.id.action_profile-> makeCurrentFragment(profileFrag)
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
                R.id.refreshButton -> allFragment.onClick(v)
                R.id.addShowButton -> addshowFragment.onClick(v)
                R.id.addShowProfileButton -> makeCurrentFragment(addshowFragment)
                R.id.logoutProfileButton -> {profileFrag.onClick(v)}
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
            "addshowFragment" -> replace(R.id.fl_wrapper, addshowFragment)
        }
        //replace(R.id.fl_wrapper, fragment)
        commit()
    }

    fun logout()
    {
        deleteAuthCookie()
        profileFrag = profileFragment()
        makeCurrentFragment(profileFrag)
    }

    fun loadAuthCookie() : String
    {
        val file = File(filesDir, "cookie")
        if (file.exists())
            return file.readText()
        else
            return ""
    }

    fun saveAuthCookie(cookie: String)
    {
        val file = File(filesDir, "cookie")
        if (file.exists())
            file.delete()
        file.writeText(cookie)
        this.jwtCookie = cookie
    }

    fun deleteAuthCookie()
    {
        val file = File(filesDir, "cookie")
        if (file.exists())
            file.delete()
        this.jwtCookie = ""
    }

    fun getAuthCookie():String
    {
        return this.jwtCookie
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

    /*fun setJWT(jwt:String)
    {
        this.jwtCookie = jwt;
    }

    fun getJWT():String
    {
        return this.jwtCookie;
    }*/
}
