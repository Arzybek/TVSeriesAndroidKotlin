package com.example.tvseriesprojectapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.example.tvseriesprojectapp.fragments.addshowFragment
import com.example.tvseriesprojectapp.fragments.allFragment
import com.example.tvseriesprojectapp.fragments.loginFragment
import com.example.tvseriesprojectapp.fragments.profileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    val loginFrag = loginFragment()
    val profileFrag = profileFragment()
    val allFragment  = allFragment()
    val addshowFragment = addshowFragment()
    var jwtCookie = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_login-> makeCurrentFragment(loginFrag)
                R.id.action_profile-> makeCurrentFragment(profileFrag)
                R.id.action_all -> makeCurrentFragment(allFragment)
                R.id.action_addshow -> makeCurrentFragment(addshowFragment)
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
