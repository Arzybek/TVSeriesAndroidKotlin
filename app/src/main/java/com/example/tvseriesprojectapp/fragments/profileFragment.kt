package com.example.tvseriesprojectapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tvseriesprojectapp.MainActivity
import com.example.tvseriesprojectapp.R
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.addCookie
import io.ktor.client.request.get
import io.ktor.http.Cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var ip = ""
    private var port = ""
    private var url = "http://${ip}:${port}/register/insecure"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        this.ip = (activity as MainActivity?)?.ip!!
        this.port = (activity as MainActivity?)?.port!!
        this.url = "http://${ip}:${port}/profile"

    }

    override fun onResume(){
        super.onResume()

        var aa = activity.toString()
        Log.i("prof", aa)
        var jwt = (activity as MainActivity?)?.getJWT()!!
        val client = HttpClient(){
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
                GlobalScope.launch(Dispatchers.IO) {
                    storage.addCookie(url, Cookie("auth", jwt))
                }
            }
        }

        var data = ""



        runBlocking(Dispatchers.IO) {
            data = client.get<String>(url)
            Log.i("aaa", data)
        }

        Log.i("prof", "hello")
        Log.i("profaaa", data)
        Log.i("JWT", jwt)

        val textView = view!!.findViewById<TextView>(R.id.profileLabel)
        if (data.equals(""))
            textView.setText("nologin")
        else
            textView.setText(data)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onClick(view:View){
        Log.i("Profile", "login tapped")

        val toast: Toast = Toast.makeText(view!!.context, "Login failed.", Toast.LENGTH_LONG);
        toast.show()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}