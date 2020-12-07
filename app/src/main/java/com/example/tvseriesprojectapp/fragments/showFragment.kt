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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.Episode
import com.example.tvseriesprojectapp.user.Session
import com.example.tvseriesprojectapp.user.Session.port
import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("showFrag", "oncreatview")

        val mContainer = inflater.inflate(R.layout.fragment_show, null)
        val linearLayout = mContainer.findViewById<LinearLayout>(R.id.testLayout)


        val a = (activity as MainActivity).allFragment.result[pos]
        linearLayout.findViewById<TextView>(R.id.show_name).setText(a.name)
        linearLayout.findViewById<TextView>(R.id.show_name).setText(a.name)
        linearLayout.findViewById<TextView>(R.id.show_category).setText(a.category)
        linearLayout.findViewById<TextView>(R.id.show_year).setText(a.year.toString())
        val url = url+"tvshows/image/"+a.id.toString()
        DownLoadImageTask(linearLayout.findViewById<ImageView>(R.id.show_pic))
            .execute(url)



        val isWatching = checkWatchingShow(a.id);

        val addToWatchingButton = Button(this.context)
        addToWatchingButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addToWatchingButton.x = 20.0F;
        addToWatchingButton.y = 0F;
        addToWatchingButton.setTag(R.id.resourceShowID, a.id)
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

        for (i in 1..a.episodes.size)
        {
            val dynamicButton = Button(this.context)
            dynamicButton.id = i;
            dynamicButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dynamicButton.x = 20.0F;
            dynamicButton.y = i*100+20.0F;
            dynamicButton.setTag(R.id.resourceShowID, a.id)
            dynamicButton.setTag(R.id.resourceEpisodeID, a.episodes.get(i-1).id)
            if (checkWatchedEpisode(a.id, a.episodes.get(i-1).id))
            {
                dynamicButton.isActivated = true
                dynamicButton.setBackgroundColor(Color.GREEN)
            }
            else
            {
                dynamicButton.isActivated = false
                dynamicButton.setBackgroundColor(Color.GRAY)
            }
            if (a.episodes.get(i-1).description=="NULL" || a.episodes.get(i-1).description==null)
                dynamicButton.text = "episode $i"
            else
                dynamicButton.text = "episode $i: "+a.episodes.get(i-1).description

            dynamicButton.setOnClickListener{
                onEpisodeClick(dynamicButton);
            }
            linearLayout.addView(dynamicButton, dynamicButton.layoutParams);
        }



        return mContainer



        //return inflater.inflate(R.layout.fragment_show, container, false)
    }


    private fun onEpisodeClick(dynamicButton:Button)
    {
        val postUrl = "http://${Session.ip}:${Session.port}/user/"
        val showID = dynamicButton.getTag(R.id.resourceShowID)
        val epID = dynamicButton.getTag(R.id.resourceEpisodeID)


        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("epID", "UTF-8") + "=" + URLEncoder.encode(epID.toString(), "UTF-8")

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
                Log.d("showButtonPost", unwatchURL)
                client.post<String>(unwatchURL)
            }
            dynamicButton.isActivated = false
            dynamicButton.setBackgroundColor(Color.GRAY)
        }
        else
        {
            runBlocking(Dispatchers.IO) {
                Log.d("showButtonPost", watchURL)
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


    private fun checkWatchedEpisode(showID:Long, epID:Long):Boolean
    {
        val getUrl = "http://${Session.ip}:${Session.port}/user/"
        var reqParam = URLEncoder.encode("showID", "UTF-8") + "=" + URLEncoder.encode(showID.toString(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("episodeID", "UTF-8") + "=" + URLEncoder.encode(epID.toString(), "UTF-8")
        var isWatchingUrl = getUrl+"isWatched?"+reqParam

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