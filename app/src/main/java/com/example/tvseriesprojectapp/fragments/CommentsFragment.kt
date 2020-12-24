package com.example.tvseriesprojectapp.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import com.example.tvseriesprojectapp.repo.TvShowsRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CommentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var showID: Long = -1;
    private var mContext: Context? = null
    private var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onAttach(context: Context?) {
        Log.d("CommentsFragment", "onAttach")
        super.onAttach(context)
        arguments?.getLong("showID")?.let {
            showID = it
        }
        mContext = context;
    }


    fun onClick() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mContainer = inflater.inflate(R.layout.fragment_comments, null)

        var linearLayout = mContainer?.findViewById<LinearLayout>(R.id.commentsLayout)

        var cont = this.context

        (linearLayout?.findViewById(R.id.addReviewButton) as Button).setOnClickListener({
            onSendReviewClickAsync(
                showID
            )
        })

        val mainActivityJob = Job()

        linearLayout = mContainer?.findViewById<LinearLayout>(R.id.reviews)
        this.linearLayout = linearLayout

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            redraw(linearLayout)
        }

        return mContainer;
    }

    suspend fun redraw(linearLayout: LinearLayout?){
        var cookie = (activity as MainActivity).getAuthCookie()
        var review = TvShowsRetriever().getReview(showID, cookie)
        if(review=="no review")
            review = ""
        else view!!.findViewById<EditText>(R.id.review).setText(TvShowsRetriever().getReview(showID, cookie));
        var reviews = TvShowsRetriever().getRandomReviews(5, showID)
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dp = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 10F,
            mContext?.getResources()?.getDisplayMetrics()
        ).toInt();
        params.setMargins(dp, dp, dp, dp)
        linearLayout?.removeAllViews()
        for (i in 0..reviews.size - 1) {
            val dynamicText = TextView(mContext)
            dynamicText.setText(reviews.get(i))
            dynamicText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            linearLayout?.addView(dynamicText, params);
        }
    }

    private fun onSendReviewClickAsync(showID: Long) {
        val cookie = (activity as MainActivity).getAuthCookie()
        val mainActivityJob = Job()
        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onSendReviewClickAsync launch")
            val review = view!!.findViewById<EditText>(R.id.review).text.toString()
            TvShowsRetriever().sendReview(review, showID, cookie)
            redraw(linearLayout)
        }
    }

}