package com.example.tvseriesprojectapp.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
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
    private var showID:Long = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onAttach(context: Context?) {
        Log.d("CommentsFragment", "onAttach")
        super.onAttach(context)
        arguments?.getLong("showID")?.let {
            showID = it
        }
    }


    fun onClick()
    {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val mContainer = inflater.inflate(R.layout.fragment_comments, null)

        val linearLayout = mContainer?.findViewById<LinearLayout>(R.id.commentsLayout)

        var cont = this.context

        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        (linearLayout?.findViewById(R.id.commentsReturnButton) as Button).setOnClickListener({
            onReturnClick(
                showID
            )
        })


        (linearLayout.findViewById(R.id.addReviewButton) as Button).setOnClickListener({
            onSendReviewClickAsync(
                showID
            )
        })

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            var reviews = TvShowsRetriever().getRandomReviews(5, showID)

            for (i in 0..reviews.size-1) {
                val dynamicText = TextView(cont)
                dynamicText.setText(reviews.get(i))
                linearLayout?.addView(dynamicText, params);
            }

            var cookie = (activity as MainActivity).getAuthCookie()

            var review = TvShowsRetriever().getReview(showID, cookie)

            (linearLayout.findViewById(R.id.review) as EditText).setText(
                review,
                TextView.BufferType.EDITABLE
            )
        }


        // Inflate the layout for this fragment
        return mContainer;
    }


    private fun onReturnClick(showID:Long)
    {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        var bundle = Bundle()
        bundle.putInt("id", showID.toInt())
        if (transaction != null) {
            val fragment = showFragment()
            fragment.arguments = bundle
            transaction.replace(com.example.tvseriesprojectapp.R.id.fl_wrapper, fragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }


    private fun onSendReviewClickAsync(showID: Long)
    {
        val cookie = (activity as MainActivity).getAuthCookie()

        val mainActivityJob = Job()

        val coroutineScope = CoroutineScope(mainActivityJob + Dispatchers.Main)
        coroutineScope.launch {
            Log.d("coroutine", "coroutine onSendReviewClickAsync launch")
            val review =view!!.findViewById<EditText>(R.id.review).text.toString()
            TvShowsRetriever().sendReview(review, showID, cookie)
        }
    }

}