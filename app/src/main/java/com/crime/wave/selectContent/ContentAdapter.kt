package com.crime.wave.selectContent

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crime.wave.App
import com.crime.wave.R

/**
 * Created by Mobile World on 4/8/2020.
 */
class ContentAdapter(private val data: List<ContentItem>) :
    RecyclerView.Adapter<ContentAdapter.ViewHolder?>() {
    private var parentRecycler: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentRecycler = recyclerView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.item_content_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val iconTint = ContextCompat.getColor(
            holder.itemView.context,
            R.color.grayIconTint
        )
        val content = data[position]
        Glide.with(holder.itemView.context)
            .load(content.icon)
            .listener(TintOnLoad(holder.imageView, iconTint))
            .into(holder.imageView)
        holder.textView.text = content.title
        holder.textView.typeface = App.instance!!.nexaBoldFont
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.city_image)
        val textView: TextView = itemView.findViewById(R.id.city_name)
        fun showText() {
            val parentHeight = (imageView.parent as View).height
            val scale =
                (parentHeight - textView.height) / imageView.height.toFloat()
            imageView.pivotX = imageView.width * 0.5f
            imageView.pivotY = 0f
            imageView.animate().scaleX(scale)
                .withEndAction {
                    textView.visibility = View.VISIBLE
                    imageView.setColorFilter(Color.BLACK)
                }
                .scaleY(scale).setDuration(200)
                .start()
        }

        fun hideText() {
            imageView.setColorFilter(
                ContextCompat.getColor(
                    imageView.context,
                    R.color.grayIconTint
                )
            )
            textView.visibility = View.INVISIBLE
            imageView.animate().scaleX(1f).scaleY(1f)
                .setDuration(200)
                .start()
        }

        override fun onClick(v: View) {
//            parentRecycler!!.smoothScrollToPosition(adapterPosition)
            parentRecycler!!.smoothScrollToPosition(bindingAdapterPosition)
        }

        init {
            itemView.findViewById<View>(R.id.container).setOnClickListener(this)
        }
    }

    private class TintOnLoad(private val imageView: ImageView, private val tintColor: Int) :
        RequestListener<Drawable?> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable?>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable?>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            imageView.setColorFilter(tintColor)
            return false
        }
    }

}