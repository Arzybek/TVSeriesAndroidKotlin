package com.example.tvseriesprojectapp.fragments;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.Episode
import com.example.tvseriesprojectapp.dto.EpisodeSerias
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.repo.RepoListAdapterSerias
import com.example.tvseriesprojectapp.user.Session
import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.toast
import java.net.URL
import java.net.URLEncoder


class showFragment : Fragment(), View.OnClickListener {
    private var url = "http://${Session.ip}:${Session.port}/"
    private var pos = -1;

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
    }

    var currentShow: TvShow? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("showFrag", "oncreatview")

        val mContainer = inflater.inflate(R.layout.fragment_show, null)
        val linearLayout = mContainer.findViewById<LinearLayout>(R.id.testLayout)

        var show = (activity as MainActivity).allFragment.result[pos]
        currentShow = show
        linearLayout.findViewById<TextView>(R.id.show_name).setText(show.name)
        linearLayout.findViewById<TextView>(R.id.show_name).setText(show.name)
        linearLayout.findViewById<TextView>(R.id.show_category).setText(show.category)
        linearLayout.findViewById<TextView>(R.id.show_year).setText(show.year.toString())
        val url = url+"tvshows/image/"+show.id.toString()
        DownLoadImageTask(linearLayout.findViewById<ImageView>(R.id.show_pic))
            .execute(url)

        val isWatching = checkWatchingShow(show.id);

        val addToWatchingButton = Button(this.context)
        addToWatchingButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addToWatchingButton.x = 20.0F;
        addToWatchingButton.y = 0F;
        addToWatchingButton.setTag(R.id.resourceShowID, show.id)
        if (isWatching)
        {
            addToWatchingButton.isActivated = true
            addToWatchingButton.setBackgroundColor(Color.GREEN)
        }
        else
        {
            addToWatchingButton.isActivated = false
            addToWatchingButton.setBackgroundColor(Color.GRAY)
        }

        addToWatchingButton.text = "Watching"

        addToWatchingButton.setOnClickListener{
            onAddToWatchingClick(addToWatchingButton);
        }
        linearLayout.addView(addToWatchingButton, addToWatchingButton.layoutParams);

        val episodeView = linearLayout.findViewById<RecyclerView>(R.id.episodeView)
        episodeView.refreshDrawableState()
        var watchedEpisodes = getWatchedEpisodes(show.id)
        val e = show.episodes.zip(watchedEpisodes).forEach{ pair -> pair.first.isWatched = pair.second }
        episodeView.layoutManager = GridLayoutManager(linearLayout.context, 5)
        episodeView.adapter = RepoListAdapterSerias(EpisodeSerias(show.episodes), ClickListener(episodeView)  )



        return mContainer
    }

    inner class ClickListener(val rv: RecyclerView): RepoListAdapterSerias.OnItemClickListener{
        override fun onItemClick(position: Int) {
            if (currentShow == null) return
            context?.toast("press episode - " + position)
            val postUrl = "http://${Session.ip}:${Session.port}/user/"

            var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(currentShow?.id.toString(), "UTF-8")
            reqParam += "&" + URLEncoder.encode("epID", "UTF-8") + "=" + URLEncoder.encode(position.toString(), "UTF-8")

            var watchURL = postUrl+"watchEpisode?"+reqParam
            var unwatchURL = postUrl+"unwatchEpisode?"+reqParam

            val jwt = (activity as MainActivity?)?.getJWT()!!
            var client = HttpClient(){
                install(HttpCookies) {
                    storage = AcceptAllCookiesStorage()
                    GlobalScope.launch(Dispatchers.IO) {
                        storage.addCookie(watchURL, Cookie("auth", jwt))
                        storage.addCookie(unwatchURL, Cookie("auth", jwt))
                    }
                }
            }

            val episode: Episode = currentShow!!.episodes[position]

            if (episode.isWatched)
            {
                runBlocking(Dispatchers.IO) {
                    Log.d("showButtonPost", postUrl+"unwatchEpisode?"+reqParam)
                    client.post<String>(unwatchURL)
                }
                episode.isWatched = false
            }
            else
            {
                runBlocking(Dispatchers.IO) {
                    Log.d("showButtonPost", postUrl+"watchEpisode?"+reqParam)
                    client.post<String>(watchURL)
                }
                episode.isWatched = true
            }
        }
    }

    private fun onEpisodeClick(dynamicButton:Button, epIndex:Int)
    {
        val postUrl = "http://${Session.ip}:${Session.port}/user/"
        val showID = dynamicButton.getTag(R.id.resourceShowID)


        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("epID", "UTF-8") + "=" + URLEncoder.encode(epIndex.toString(), "UTF-8")

        var watchURL = postUrl+"watchEpisode?"+reqParam
        var unwatchURL = postUrl+"unwatchEpisode?"+reqParam

        val jwt = (activity as MainActivity?)?.getJWT()!!
        var client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(watchURL, Cookie("auth", jwt))
                    storage.addCookie(unwatchURL, Cookie("auth", jwt))
                }
            }
        }

        if (dynamicButton.isActivated)
        {
            runBlocking(Dispatchers.IO) {
                Log.d("showButtonPost", postUrl+"unwatchEpisode?"+reqParam)
                client.post<String>(unwatchURL)
            }
            dynamicButton.isActivated = false
            dynamicButton.setBackgroundColor(Color.GRAY)
        }
        else
        {
            runBlocking(Dispatchers.IO) {
                Log.d("showButtonPost", postUrl+"watchEpisode?"+reqParam)
                client.post<String>(watchURL)
            }
            dynamicButton.isActivated = true
            dynamicButton.setBackgroundColor(Color.GREEN)
        }
    }

    private fun onAddToWatchingClick(dynamicButton:Button)
    {
        val postUrl = "http://${Session.ip}:${Session.port}/user/"
        val showID = dynamicButton.getTag(R.id.resourceShowID)


        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")

        var watchURL = postUrl+"addWatching?"+reqParam
        var unwatchURL = postUrl+"deleteWatching?"+reqParam

        val jwt = (activity as MainActivity?)?.getJWT()!!
        var client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(watchURL, Cookie("auth", jwt))
                    storage.addCookie(unwatchURL, Cookie("auth", jwt))
                }
            }
        }

        if (dynamicButton.isActivated)
        {
            runBlocking(Dispatchers.IO) {
                client.post<String>(unwatchURL)
            }
            dynamicButton.isActivated = false
            dynamicButton.setBackgroundColor(Color.GRAY)
        }
        else
        {
            runBlocking(Dispatchers.IO) {
                client.post<String>(watchURL)
            }
            dynamicButton.isActivated = true
            dynamicButton.setBackgroundColor(Color.GREEN)
        }
    }


    private fun checkWatchingShow(showID:Long):Boolean
    {
        val getUrl = "http://${Session.ip}:${Session.port}/user/"
        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")
        var isWatchingUrl = getUrl+"isWatching?"+reqParam

        val jwt = (activity as MainActivity?)?.getJWT()!!
        var client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(isWatchingUrl, Cookie("auth", jwt))
                }
            }
        }
        var data = ""

        runBlocking(Dispatchers.IO) {
            Log.d("showButtonPost", isWatchingUrl)
            data = client.get<String>(isWatchingUrl)
        }

        if (data=="true")
            return true
        return false
    }


    private fun getWatchedEpisodes(showID:Long):List<Boolean>
    {
        val getUrl = "http://${Session.ip}:${Session.port}/user/"
        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")
        var isWatchingUrl = getUrl+"watchedEpisodes?"+reqParam

        val jwt = (activity as MainActivity?)?.getJWT()!!
        var client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(isWatchingUrl, Cookie("auth", jwt))
                }
            }
        }
        var data = ""

        runBlocking(Dispatchers.IO) {
            Log.d("getWatchedRequest", isWatchingUrl)
            data = client.get<String>(isWatchingUrl)
            Log.d("getWatchedRequestData", data)
            System.out.println(data)
        }

        if (data.equals(""))
        {
            return listOf()
        }

        data = data.removeSuffix("]").removePrefix("[")
        var dataArr = data.split(",")
        var outputArr = BooleanArray(dataArr.size)
        for (i in 0..dataArr.size-1)
        {
            outputArr[i] = dataArr[i] != "false"
        }


        return outputArr.toList();
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