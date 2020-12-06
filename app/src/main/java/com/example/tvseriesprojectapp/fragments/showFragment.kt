package com.example.tvseriesprojectapp.fragments;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import com.example.tvseriesprojectapp.user.Session
import android.support.v4.app.Fragment;
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import kotlinx.android.synthetic.main.fragment_all.*
import java.net.URL

class showFragment : Fragment(), View.OnClickListener {
    private var url = "http://${Session.ip}:${Session.port}/"
    private var pos = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("id")?.let {
            pos = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onResume() {
        super.onResume()
        val a = (activity as MainActivity).allFragment.result[pos]
        view!!.findViewById<TextView>(R.id.show_name).setText(a.name)
        view!!.findViewById<TextView>(R.id.show_category).setText(a.category)
        view!!.findViewById<TextView>(R.id.show_year).setText(a.year.toString())
        val url = url+"tvshows/image/"+a.id.toString()
        DownLoadImageTask(view!!.findViewById<ImageView>(R.id.show_pic))
            .execute(url)
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