package com.crime.wave.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crime.wave.App
import com.crime.wave.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news.view.*


/**
 * Created by Mobile World on 4/10/2020.
 */
class NewsCardAdapter(
    context: Context?,
    private var mData: Array<NewsItem>,
    private var mClickListener: ItemClickListener,
    private var type: Int = 0
) :
    RecyclerView.Adapter<NewsCardAdapter.ViewHolder?>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (type == 0) {
            mInflater.inflate(R.layout.item_news, parent, false)
        } else {
            mInflater.inflate(R.layout.item_news_list, parent, false)
        }
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: NewsItem = mData[position]
        holder.bind(item, position)
        if(position == itemCount - 1) {
            mClickListener.onLoadMore()
        }
    }

    fun setData(data: Array<NewsItem>, t:Int){
        mData = data
        type = t
        notifyDataSetChanged()
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        fun bind(item: NewsItem, position: Int) {
            itemView.title.text = item.title
            itemView.title.typeface = App.instance!!.nexaBoldFont
            itemView.description.text = item.description
            itemView.description.typeface = App.instance!!.nexaLightFont

            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.img_bg)
                .error(R.drawable.icon_image_error)
                .into(itemView.image)
            itemView.setOnClickListener {
                mClickListener.onItemClick(position)
            }
            itemView.setOnTouchListener(OnSwipeTouchListener())
        }
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onLoadMore()
    }

}