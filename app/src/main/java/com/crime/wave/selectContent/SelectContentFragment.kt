package com.crime.wave.selectContent

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.crime.wave.MainActivity
import com.crime.wave.R
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.DiscreteScrollView.OnItemChangedListener
import com.yarolegovich.discretescrollview.DiscreteScrollView.ScrollStateChangeListener
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.fragment_select_content.*
import kotlinx.android.synthetic.main.fragment_select_content.view.*
import kotlin.math.abs

/**
 * Created by Mobile World on 4/8/2020.
 */
class SelectContentFragment : Fragment(),
    ScrollStateChangeListener<ContentAdapter.ViewHolder?>,
    OnItemChangedListener<ContentAdapter.ViewHolder?> {
    private var contents: List<ContentItem> = ContentStation.get().getContents()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_select_content, container, false)
        contents = ContentStation.get().getContents()
        if (view.contentPicker != null) {
            view.contentPicker.setSlideOnFling(true)
            view.contentPicker.adapter = ContentAdapter(contents)
            view.contentPicker.addOnItemChangedListener(this)
            view.contentPicker.addScrollStateChangeListener(this)
            view.contentPicker.scrollToPosition(2)
            view.contentPicker.setItemTransitionTimeMillis(500)
            view.contentPicker.setItemTransformer(
                ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build()
            )
        }
        if (view.selectContentBackgroundView != null ) {
            view.selectContentBackgroundView.setForecast(contents[0])
        }
        if (view.btnSelectContent != null ) {
            view.btnSelectContent.setOnClickListener {
                smoothScrollToUserSelectedPosition(
                    view.contentPicker,
                    view.btnSelectContent
                )
            }
            val myCustomFont : Typeface? = ResourcesCompat.getFont(context!!, R.font.nexa_bold)
            view.btnSelectContent.typeface = myCustomFont
        }
        return view
    }

    override fun onCurrentItemChanged(
        holder: ContentAdapter.ViewHolder?,
        position: Int
    ) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (holder != null) {
            selectContentBackgroundView.setForecast(contents[position])
            holder.showText()
            Log.d("Select Content", ContentStation.get().getContents()[position].title)
            (activity as MainActivity).onSelectContent(position)
        }

    }

    override fun onScrollStart(
        holder: ContentAdapter.ViewHolder,
        position: Int
    ) {
        holder.hideText()
    }

    override fun onScroll(
        position: Float,
        currentIndex: Int, newIndex: Int,
        currentHolder: ContentAdapter.ViewHolder?,
        newHolder: ContentAdapter.ViewHolder?
    ) {
        val current: ContentItem = contents[currentIndex]
        if (newIndex >= 0 && newIndex < contentPicker!!.adapter!!.itemCount) {
            val next: ContentItem = contents[newIndex]
            selectContentBackgroundView.onScroll(1f - abs(position), current, next)
        }
    }

    private fun smoothScrollToUserSelectedPosition(
        scrollView: DiscreteScrollView,
        anchor: View?
    ) {
        val popupMenu =
            PopupMenu(scrollView.context, anchor!!)
        val menu = popupMenu.menu
        val adapter =
            scrollView.adapter
        val itemCount =
            if (adapter is InfiniteScrollAdapter<*>) adapter.realItemCount else adapter!!.itemCount
        ContentStation.get().getContents()[0].title
        for (i in 0 until itemCount) {
            menu.add(ContentStation.get().getContents()[i].title)
        }
        popupMenu.setOnMenuItemClickListener { item ->
            var destination = 0
            for (i in 0 until itemCount) {
                if (ContentStation.get().getContents()[i].title == item.title) {
                    destination = i
                }
            }
            if (adapter is InfiniteScrollAdapter<*>) {
                destination =
                    adapter.getClosestPosition(destination)
            }
            scrollView.smoothScrollToPosition(destination)
            true
        }
        popupMenu.show()
    }

    override fun onScrollEnd(
        holder: ContentAdapter.ViewHolder,
        position: Int
    ) {

    }
}