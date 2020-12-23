package com.example.tvseriesprojectapp.fragments;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.example.tvseriesprojectapp.repo.RepoListAdapterSeries
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import com.example.tvseriesprojectapp.user.Session
import kotlinx.android.synthetic.main.fragment_show.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import java.net.URL


class showFragment : Fragment(), View.OnClickListener {
    private var url = "http://${Session.ip}:${Session.port}/"
    private var pos = -1;
    var episodeSeries = EpisodeSerias(mutableListOf())

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
        Log.d("showFragment", "oncreatview")

        val mContainer = inflater.inflate(R.layout.fragment_show, null)

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine launch")
            val resultShow = TvShowsRetriever().getShow(pos.toLong())
            currentShow = resultShow
            drawShow(mContainer, resultShow)
        }

        return mContainer
    }

    private fun drawShow(mContainer: View, show: TvShow): View {
        val linearLayout = mContainer.findViewById<LinearLayout>(R.id.testLayout)
        linearLayout.findViewById<TextView>(R.id.show_name).setText(show.name)
        linearLayout.findViewById<TextView>(R.id.show_category).setText("Жанр: "+show.category)
        linearLayout.findViewById<TextView>(R.id.show_year).setText("Год: "+show.year.toString())
        //ToDO set description
        val url = url + "tvshows/image/" + show.imgLink
        DownLoadImageTask(linearLayout.findViewById<ImageView>(R.id.show_pic))
                .execute(url)

        var mRatingBar = linearLayout.findViewById(R.id.ratingBar) as RatingBar;


       mRatingBar.onRatingBarChangeListener =
           RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
               onRatingChangeAsync(ratingBar, rating, fromUser, show.id)

           }

        (linearLayout.findViewById(R.id.commentsButton) as Button).setOnClickListener({
            onCommentsClick(
                show.id
            )
        })

        var cont = this.context

            val mainActivityJob = Job()

            val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
            coroutineScope.launch {
                Log.d("coroutine", "coroutine drawShow launch")
                var cookie = (activity as MainActivity).getAuthCookie()
                var isWatching = TvShowsRetriever().isWatching(show.id, cookie)
                val addToWatchingButton = linearLayout.findViewById(R.id.showIsWatch) as Button
                var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                var ratingText = (linearLayout.findViewById(R.id.show_rating) as TextView)
                var rating = TvShowsRetriever().getShowRating(show.id)
                var rounded = Math.round(rating * 100).toDouble()/100
                ratingText.setText(rounded.toString())

                //mRatingBar.rating = TvShowsRetriever().getUserRating(show.id, cookie)

//            addToWatchingButton.x = 20.0F;
//            addToWatchingButton.y = 0F;
                addToWatchingButton.setTag(R.id.resourceShowID, show.id)
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

                var watchedEpisodes = TvShowsRetriever().getWatchedEpisodes(show.id, cookie)
                val episodeView = linearLayout.findViewById<RecyclerView>(R.id.episodeView)
                episodeView.refreshDrawableState()
                val e = show.episodes.zip(watchedEpisodes).forEach{ pair -> pair.first.isWatched = pair.second }
                episodeSeries = EpisodeSerias(show.episodes)
                episodeView.layoutManager = GridLayoutManager(linearLayout.context, 5)
                episodeView.adapter = RepoListAdapterSeries(
                    EpisodeSerias(show.episodes), ClickListener(
                        episodeView
                    )
                )

            }

            return mContainer
        }


    fun redraw(){
        var myAdapter = episodeView.adapter
        episodeView.setAdapter(myAdapter);
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged()
        };
    }

    inner class ClickListener(val rv: RecyclerView): RepoListAdapterSeries.OnItemClickListener{
        override fun onItemClick(position: Int) {
            if (currentShow == null) return
            val episode: Episode = currentShow!!.episodes[position]

            if (episode.isWatched)
            {
                onEpisodeClickAsync(false, position)
                episode.isWatched = false
            }
            else
            {
                onEpisodeClickAsync(true, position)
                episode.isWatched = true
            }
            redraw()
        }
    }

    private fun onEpisodeClickAsync(value: Boolean, epIndex: Int) {
        if (currentShow == null) return
        val showID = currentShow!!.id
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onEpisodeClick launch")
            if (!value) {
                TvShowsRetriever().unwatchingEpisode(showID as Long, epIndex.toLong(), cookie)
            } else {
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


    private fun onRatingChangeAsync(
        ratingBar: RatingBar,
        rating: Float,
        fromUser: Boolean,
        showID: Long
    )
    {
        val cookie = (activity as MainActivity).getAuthCookie()
        val mainActivityJob = Job()
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onRatingChangeAsync launch")
            TvShowsRetriever().ratingShow(rating, showID, cookie)
        }
    }


    private fun onCommentsClick(showID:Long)
    {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        var bundle = Bundle()
        bundle.putLong("showID", showID)
        if (transaction != null) {
            val fragment = CommentsFragment()
            fragment.arguments = bundle
            transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment).addToBackStack("tag")
            transaction.commit()
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