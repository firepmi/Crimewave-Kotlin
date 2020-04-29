package com.crime.wave.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crime.wave.App
import com.crime.wave.R
import com.crime.wave.WebViewActivity
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import org.json.JSONObject

/**
 * Created by Mobile World on 4/17/2020.
 */
class NewsFragment(index: Int) : Fragment(),
    NewsFragAdapter.ItemClickListener {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var currentIndex = 0
    private var page = 1
    private var tabString = arrayOf(
        "US News",
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology"
    )

    private var adapter: NewsFragAdapter? = null
    private var data: Array<NewsItem> = arrayOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_news, container, false)

        adapter = NewsFragAdapter(activity, data.toMutableList())
        adapter!!.setClickListener(this)
        view.rvNews.adapter = adapter
        view.rvNews.layoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )

        getNews()
        view.mSwipyRefreshLayout.setOnRefreshListener {
            view.mSwipyRefreshLayout.isRefreshing = true
            page++
            getNews()
        }
        return view
    }

    private fun getNews() {
        var link: String = App.instance!!.newsUrl
        link = link + "page=" + page
        if (currentIndex == 0) {
            link = "$link&country=us"
        } /*
        else if currentIndex == 1 {
            link = link + "&q=crime"
        }*/ else if (currentIndex > 0) {
            link = link + "&country=us&category=" + tabString[currentIndex]
        }
        Log.d("link", link)
        val queue = Volley.newRequestQueue(activity)
        val stringRequest = StringRequest(
            Request.Method.GET, link, Response.Listener { response ->
            val resultObject = JSONObject(response)
            val dataArray = resultObject.getJSONArray("articles")
            data = arrayOf()
            for (i in 0 until dataArray.length()) {
                data += NewsItem(
                    dataArray.getJSONObject(i).getString("title"),
                    dataArray.getJSONObject(i).getString("description"),
                    dataArray.getJSONObject(i).getString("urlToImage"),
                    dataArray.getJSONObject(i).getString("url"),
                    dataArray.getJSONObject(i).getString("publishedAt"))
            }
            adapter?.notifyDataSetChanged()
                mSwipyRefreshLayout.isRefreshing = false
        },
            Response.ErrorListener {
                it?.printStackTrace()
            })
        queue.add(stringRequest)
    }

    override fun onItemClick(position: Int) {
        val url: String = data[position].url
        val i = Intent(context, WebViewActivity::class.java)
        i.putExtra("webLink", url)
        i.putExtra("title", "News Article Detail")
        startActivity(i)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }

    init {
        // Required empty public constructor
        currentIndex = index
    }
}