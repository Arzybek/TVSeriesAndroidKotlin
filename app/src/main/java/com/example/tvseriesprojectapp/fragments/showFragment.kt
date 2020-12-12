package com.example.tvseriesprojectapp.fragments;

import android.app.ActionBar
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.Episode
import com.example.tvseriesprojectapp.dto.RepoResult
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.repo.RepoListAdapter
import com.example.tvseriesprojectapp.repo.TvSeriesService
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import com.example.tvseriesprojectapp.user.Session.port
import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import java.net.URL
import java.net.URLEncoder


class showFragment : Fragment(), View.OnClickListener {
    private var url = "http://${Session.ip}:${Session.port}/"
    private var pos = -1;
    private var cookie = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("showFrag", "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        Log.d("showFrag", "onAttach")
        super.onAttach(context)
        arguments?.getInt("id")?.let {
            pos = it
        }
        cookie = (activity as MainActivity).getJWT()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("showFragment", "oncreatview")

        val mContainer = inflater.inflate(R.layout.fragment_show, null)

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine launch")
            val resultShow = TvShowsRetriever().getShow(pos.toLong())
            drawShow(mContainer, resultShow)
        }

        return mContainer
    }


    private fun drawShow(mContainer:View, a:TvShow): View
    {
        val linearLayout = mContainer.findViewById<LinearLayout>(R.id.testLayout)
        linearLayout.findViewById<TextView>(R.id.show_name).setText(a.name)
        linearLayout.findViewById<TextView>(R.id.show_category).setText(a.category)
        linearLayout.findViewById<TextView>(R.id.show_year).setText(a.year.toString())
        val url = url+"tvshows/image/"+a.id.toString()
        DownLoadImageTask(linearLayout.findViewById<ImageView>(R.id.show_pic))
                .execute(url)


        var cont = this.context

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine drawShow launch")
            var isWatching = TvShowsRetriever().isWatching(a.id, cookie)
            val addToWatchingButton = Button(cont)
            addToWatchingButton.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            addToWatchingButton.x = 20.0F;
            addToWatchingButton.y = 0F;
            addToWatchingButton.setTag(R.id.resourceShowID, a.id)
            if (isWatching) {
                addToWatchingButton.isActivated = true
                addToWatchingButton.setBackgroundColor(Color.GREEN)
            } else {
                addToWatchingButton.isActivated = false
                addToWatchingButton.setBackgroundColor(Color.GRAY)
            }
            addToWatchingButton.text = "Watching"

            addToWatchingButton.setOnClickListener {
                onAddToWatchingClickAsync(addToWatchingButton);
            }
            linearLayout.addView(addToWatchingButton, addToWatchingButton.layoutParams);



            var watchedEpisodes = TvShowsRetriever().getWatchedEpisodes(a.id, cookie)
            for (i in 0..a.episodes.size - 1) {
                val dynamicButton = Button(cont)
                dynamicButton.id = i;
                dynamicButton.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                dynamicButton.x = 20.0F;
                dynamicButton.y = i * 100 + 120.0F;
                dynamicButton.setTag(R.id.resourceShowID, a.id)
                dynamicButton.setTag(R.id.resourceEpisodeID, a.episodes.get(i).id)
                if (watchedEpisodes.size < i + 1) {
                    dynamicButton.isActivated = false
                    dynamicButton.setBackgroundColor(Color.GRAY)
                } else {
                    if (watchedEpisodes[i] == false) {
                        dynamicButton.isActivated = false
                        dynamicButton.setBackgroundColor(Color.GRAY)
                    } else {
                        dynamicButton.isActivated = true
                        dynamicButton.setBackgroundColor(Color.GREEN)
                    }
                }
                if (a.episodes.get(i).description == "NULL" || a.episodes.get(i).description == null)
                    dynamicButton.text = "episode $i"
                else
                    dynamicButton.text = "episode $i: " + a.episodes.get(i).description

                dynamicButton.setOnClickListener {
                    onEpisodeClickAsync(dynamicButton, i);
                }
                linearLayout.addView(dynamicButton, dynamicButton.layoutParams);
            }

        }

        return mContainer

    }


    private fun onEpisodeClickAsync(dynamicButton:Button, epIndex:Int)
    {
        val showID = dynamicButton.getTag(R.id.resourceShowID)

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onEpisodeClick launch")
            if (dynamicButton.isActivated)
            {
                dynamicButton.isActivated = false
                dynamicButton.setBackgroundColor(Color.GRAY)
                TvShowsRetriever().unwatchingEpisode(showID as Long, epIndex.toLong(), cookie)
            }
            else
            {
                dynamicButton.isActivated = true
                dynamicButton.setBackgroundColor(Color.GREEN)
                TvShowsRetriever().watchingEpisode(showID as Long, epIndex.toLong(), cookie)
            }
        }
    }

    private fun onAddToWatchingClickAsync(dynamicButton:Button)
    {
        val showID = dynamicButton.getTag(R.id.resourceShowID)

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine ononAddToWatching launch")
            if (dynamicButton.isActivated) {
                TvShowsRetriever().unwatchingShow(showID as Long, cookie)
                dynamicButton.isActivated=false
                dynamicButton.setBackgroundColor(Color.GRAY)
            }
            else {
                TvShowsRetriever().watchingShow(showID as Long, cookie)
                dynamicButton.isActivated = true
                dynamicButton.setBackgroundColor(Color.GREEN)
            }
        }

    }


    override fun onResume() {
        Log.d("showFrag", "onResume")
        super.onResume()

    }



    private class DownLoadImageTask(internal val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlOfImage = urls[0]
            return try {
                val inputStream = URL(urlOfImage).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) { // Catch the download exception
                e.printStackTrace()
                null
            }
        }
        override fun onPostExecute(result: Bitmap?) {
            if(result!=null){
                imageView!!.setImageBitmap(result)
            }else{
                Toast.makeText(imageView.context,"Error downloading",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {

    }
}