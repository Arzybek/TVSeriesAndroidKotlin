package com.example.tvseriesprojectapp.fragments

import android.R
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class allFragment : Fragment(), View.OnClickListener {
    public var result: List<TvShow> = listOf()

    inner class ClickListener(val cookie: String): RepoListAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            var bundle = Bundle()
            bundle.putInt("id", position)
            if (transaction != null) {
                val fragment = showFragment()
                fragment.arguments = bundle
                transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
    }

    var cookie = "";
    var clickHandler = ClickListener(cookie)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.cookie = (activity as MainActivity).getJWT()
        clickHandler = ClickListener(cookie)
        return inflater.inflate(com.example.tvseriesprojectapp.R.layout.fragment_all, container, false)
    }

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume(){
        root.layoutManager = LinearLayoutManager(mContext)
//        this.cookie = (activity as MainActivity).getJWT()
        retrieveRepositories()
        refreshButton.setOnClickListener {
            retrieveRepositories()
        }
        super.onResume()
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
            val result = RepoResult(resultList)
            root.adapter = RepoListAdapter(result, clickHandler)
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