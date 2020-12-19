package com.example.tvseriesprojectapp.fragments;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import android.widget.RatingBar.OnRatingBarChangeListener
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.dto.TvShow
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL


class showFragment : Fragment(), View.OnClickListener {
    private var url = "http://${Session.ip}:${Session.port}/"
    private var pos = -1;
    //private var cookie = "";

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
        //cookie = (activity as MainActivity).getAuthCookie()
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


    private fun drawShow(mContainer: View, a: TvShow): View {
        val linearLayout = mContainer.findViewById<LinearLayout>(R.id.testLayout)
        linearLayout.findViewById<TextView>(R.id.show_name).setText(a.name)
        linearLayout.findViewById<TextView>(R.id.show_category).setText(a.category)
        linearLayout.findViewById<TextView>(R.id.show_year).setText(a.year.toString())
        val url = url + "tvshows/image/" + a.imgLink
        DownLoadImageTask(linearLayout.findViewById<ImageView>(R.id.show_pic))
                .execute(url)

        var mRatingBar = linearLayout.findViewById(R.id.ratingBar) as RatingBar;
        //var mRatingText = linearLayout.findViewById(R.id.ResText) as TextView;

        mRatingBar.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            onRatingChangeAsync(ratingBar, rating, fromUser, a.id)
            //mRatingText.text = "Выбрано звезд: $rating"
        }

        (linearLayout.findViewById(R.id.addReviewButton) as Button).setOnClickListener({ onSendReviewClickAsync(a.id)})



            var cont = this.context

            val mainActivityJob = Job()

            val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
            coroutineScope.launch {
                Log.d("coroutine", "coroutine drawShow launch")
                var cookie = (activity as MainActivity).getAuthCookie()
                var isWatching = TvShowsRetriever().isWatching(a.id, cookie)
                val addToWatchingButton = Button(cont)
                var params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                var ratingText = (linearLayout.findViewById(R.id.show_rating) as TextView)
                var rating = TvShowsRetriever().getShowRating(a.id)
                var rounded = Math.round(rating*100).toDouble()/100
                ratingText.setText(rounded.toString())


                mRatingBar.rating = TvShowsRetriever().getUserRating(a.id, cookie)

                var review = TvShowsRetriever().getReview(a.id, cookie)

                (linearLayout.findViewById(R.id.review) as EditText).setText(review, TextView.BufferType.EDITABLE)

                var reviews = TvShowsRetriever().getRandomReviews(5, a.id)

                for (i in 0..reviews.size-1) {
                    val dynamicText = TextView(cont)
                    dynamicText.setText(reviews.get(i))
                    linearLayout.addView(dynamicText, params);
                }

//            addToWatchingButton.x = 20.0F;
//            addToWatchingButton.y = 0F;
                addToWatchingButton.setTag(R.id.resourceShowID, a.id)
                if (isWatching) {
                    addToWatchingButton.isActivated = true
                    addToWatchingButton.setBackgroundColor(Color.GREEN)
                } else {
                    addToWatchingButton.isActivated = false
                    addToWatchingButton.setBackgroundColor(Color.GRAY)
                }
                addToWatchingButton.text = "Watching"

                val scale = resources.displayMetrics.density
                val dpAsPixels = (10 * scale + 0.5f)
                Log.d("aa", dpAsPixels.toString())
                params.setMargins(dpAsPixels.toInt(), dpAsPixels.toInt(), 0, 0)
                addToWatchingButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER

                addToWatchingButton.setOnClickListener {
                    onAddToWatchingClickAsync(addToWatchingButton);
                }
                linearLayout.addView(addToWatchingButton, params);

                var watchedEpisodes = TvShowsRetriever().getWatchedEpisodes(a.id, cookie)
                for (i in 0..a.episodes.size - 1) {
                    val dynamicButton = Button(cont)
                    dynamicButton.id = i;
//                dynamicButton.layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                dynamicButton.x = 20.0F;
//                dynamicButton.y = i * 100 + 120.0F;
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
                        dynamicButton.text = "episode ${i+1}"
                    else
                        dynamicButton.text = "episode $i: " + a.episodes.get(i).description

                    dynamicButton.setOnClickListener {
                        onEpisodeClickAsync(dynamicButton, i);
                    }
                    dynamicButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER

                    linearLayout.addView(dynamicButton, params);
                }
            }

            return mContainer
        }



    private fun onEpisodeClickAsync(dynamicButton: Button, epIndex: Int) {
        val showID = dynamicButton.getTag(R.id.resourceShowID)
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onEpisodeClick launch")
            if (dynamicButton.isActivated) {
                dynamicButton.isActivated = false
                dynamicButton.setBackgroundColor(Color.GRAY)
                TvShowsRetriever().unwatchingEpisode(showID as Long, epIndex.toLong(), cookie)
            } else {
                dynamicButton.isActivated = true
                dynamicButton.setBackgroundColor(Color.GREEN)
                TvShowsRetriever().watchingEpisode(showID as Long, epIndex.toLong(), cookie)
            }
        }
    }

    private fun onAddToWatchingClickAsync(dynamicButton: Button) {
        val showID = dynamicButton.getTag(R.id.resourceShowID)

        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine ononAddToWatching launch")
            if (dynamicButton.isActivated) {
                TvShowsRetriever().unwatchingShow(showID as Long, cookie)
                dynamicButton.isActivated = false
                dynamicButton.setBackgroundColor(Color.GRAY)
            } else {
                TvShowsRetriever().watchingShow(showID as Long, cookie)
                dynamicButton.isActivated = true
                dynamicButton.setBackgroundColor(Color.GREEN)
            }
        }

    }


    private fun onRatingChangeAsync(ratingBar:RatingBar, rating:Float, fromUser:Boolean, showID:Long)
    {
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onRatingChangeAsync launch")
            TvShowsRetriever().ratingShow(rating, showID, cookie)
        }
    }


    private fun onSendReviewClickAsync(showID:Long)
    {
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onRatingChangeAsync launch")
            val review =view!!.findViewById<EditText>(R.id.review).text.toString()
            TvShowsRetriever().sendReview(review, showID, cookie)
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
            if (result != null) {
                imageView!!.setImageBitmap(result)
            } else {
                Toast.makeText(imageView.context, "Error downloading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {

    }
}