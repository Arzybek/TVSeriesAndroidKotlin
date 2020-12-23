package com.example.tvseriesprojectapp.fragments

import android.R
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.tvseriesprojectapp.*
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.repo.RepoListAdapter
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

class allFragment : Fragment(), View.OnClickListener {
    public var result: List<List<TvShow>> = listOf()

    inner class ClickListener(val cookie: String): RepoListAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            var bundle = Bundle()
            bundle.putInt("id", position)
            if (transaction != null) {
                val fragment = showFragment()
                fragment.arguments = bundle
                transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment).addToBackStack("tag")
                transaction.commit()
            }
        }
    }

    //var cookie = "";
    var clickHandler = ClickListener("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var cookie = (activity as MainActivity).getAuthCookie()
        clickHandler = ClickListener(cookie)  // возможно стоит сделать чтобы clicklistener сам дергал куку
        val mContainer = inflater.inflate(com.example.tvseriesprojectapp.R.layout.fragment_all, container, false)
        var editText = mContainer.findViewById<EditText>(com.example.tvseriesprojectapp.R.id.search_input)
        editText.setOnKeyListener( object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // if the event is a key down event on the enter button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    doSearch(editText.text.toString())
                    hideKeyboard()
                    editText.clearFocus()
                    editText.isCursorVisible = false
                    return true
                }
                return false
            }
        }
        )
        return mContainer
    }

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume(){
        super.onResume()
        root.layoutManager = LinearLayoutManager(mContext)
//        this.cookie = (activity as MainActivity).getJWT()
        retrieveRepositories()
//        refreshButton.setOnClickListener {
//            retrieveRepositories()
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (isNetworkConnected()) {
//            retrieveRepositories()
//        } else {
//            mContext?.let {
//                AlertDialog.Builder(it).setTitle("No Internet Connection")
//                    .setMessage("Please check your internet connection and try again")
//                    .setPositiveButton(android.R.string.ok) { _, _ -> }
//                    .setIcon(android.R.drawable.ic_dialog_alert).show()
//            }
//        }
//        retrieveRepositories()
//        refreshButton.setOnClickListener {
//            retrieveRepositories()
//        }
    }

    fun refreshPage(i: Int) {
        root.layoutManager = LinearLayoutManager(mContext)
        val mainActivityJob = Job()
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            mContext?.let {
                AlertDialog.Builder(it).setTitle("Error")
                    .setMessage(exception.message)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_dialog_alert).show()
            }
        }
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            root.adapter = RepoListAdapter(RepoResult(result[i]), clickHandler)
        }
    }

    fun retrieveRepositories() {
        //1 Create a Coroutine scope using a job to be able to cancel when needed
        root.layoutManager = LinearLayoutManager(mContext)
        val mainActivityJob = Job()

        //2 Handle exceptions if any
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            mContext?.let {
                AlertDialog.Builder(it).setTitle("Error")
                    .setMessage(exception.message)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_dialog_alert).show()
            }
        }

        //3 the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            //4
            val resultList = TvShowsRetriever().getRepositories()
            result = resultList
            val result = RepoResult(resultList[0])
            root.adapter = RepoListAdapter(result, clickHandler)
            addPageButtons()
        }
    }

    fun addPageButtons(){
            buttons.removeAllViews()
        for(j in 1..result.size){
            var button = Button(mContext)
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            button.text = (j).toString()
            button.setOnClickListener { refreshPage(j-1) }
            buttons.addView(button);
        }
    }

    fun doSearch(text: String) {
        root.layoutManager = LinearLayoutManager(mContext)
        val mainActivityJob = Job()
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            mContext?.let {
                AlertDialog.Builder(it).setTitle("Error")
                    .setMessage(exception.message)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_dialog_alert).show()
            }
        }
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            var resultList = mutableListOf<List<TvShow>>();
            if(text.isBlank())
             resultList = TvShowsRetriever().getRepositories() as MutableList<List<TvShow>>
            else resultList = TvShowsRetriever().searchRepositoriesByName(text) as MutableList<List<TvShow>>
            result = resultList
            val result = RepoResult(resultList[0])
            root.adapter = RepoListAdapter(result, clickHandler)
            addPageButtons()
        }
    }

    override fun onClick(v: View?) {
//        if (v != null) {
//            when (v.id) {
//                com.example.tvseriesprojectapp.R.id.refreshButton -> retrieveRepositories()
//            }
//        }
    }

//    private fun isNetworkConnected(): Boolean {
//        val connectivityManager = getSystemService(mContext.CONNEC) as ConnectivityManager
//        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            connectivityManager.activeNetwork
//        } else {
//            TODO("VERSION.SDK_INT < M")
//        }
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
//        return networkCapabilities != null &&
//                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//    }
}