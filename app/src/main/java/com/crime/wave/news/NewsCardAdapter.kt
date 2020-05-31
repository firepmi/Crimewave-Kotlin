package com.crime.wave.news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.crime.wave.App
import com.crime.wave.MainActivity
import com.crime.wave.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_ads.view.*
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

//const val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
const val ADMOB_AD_UNIT_ID = "ca-app-pub-2795479226639345/7324088220"

var currentNativeAd: UnifiedNativeAd? = null
/**
 * Created by Mobile World on 4/10/2020.
 */
class NewsCardAdapter(
    private var context: Context?,
    private var mData: Array<NewsItem>,
    private var mClickListener: ItemClickListener,
    private var type: Int = 0
) :
    RecyclerView.Adapter<NewsCardAdapter.ViewHolder?>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == 0) {
            val view: View = if (type == 0) {
                mInflater.inflate(R.layout.item_news, parent, false)
            } else {
                mInflater.inflate(R.layout.item_news_list, parent, false)
            }
            return ViewHolder(view)
        } else {
            ViewHolder(mInflater.inflate(R.layout.item_ads, parent, false))
        }
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        if (holder.itemViewType == 0 ) {
            val item: NewsItem = mData[position]
            holder.bind(item, position)
            if (position == itemCount - 1) {
                mClickListener.onLoadMore()
            }
        }
        else {
            val builder = AdLoader.Builder(MainActivity.instance, ADMOB_AD_UNIT_ID)

            builder.forUnifiedNativeAd { unifiedNativeAd ->
                // OnUnifiedNativeAdLoadedListener implementation.
                // If this callback occurs after the activity is destroyed, you must call
                // destroy and return or you may get a memory leak.
                if (MainActivity.instance!!.isDestroyed) {
                    unifiedNativeAd.destroy()
                    return@forUnifiedNativeAd
                }
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                currentNativeAd?.destroy()
                currentNativeAd = unifiedNativeAd
                val adView = MainActivity.instance!!.layoutInflater
                    .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
                populateUnifiedNativeAdView(unifiedNativeAd, adView)
                holder.itemView.ad_frame.removeAllViews()
                holder.itemView.ad_frame.addView(adView)
            }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
//                    refresh_button.isEnabled = true
//                    Toast.makeText(this@MainActivity, "Failed to load native ad: $errorCode", Toast.LENGTH_SHORT).show()
                    Log.e("Ads", "Failed to load native ad: $errorCode")
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }
    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
//            videostatus_text.text = String.format(Locale.getDefault(),
//                "Video status: Ad contains a %.2f:1 video asset.",
//                vc.aspectRatio)

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//                    refresh_button.isEnabled = true
//                    videostatus_text.text = "Video status: Video playback has ended."
                    super.onVideoEnd()
                }
            }
        } else {
//            videostatus_text.text = "Video status: Ad does not contain a video asset."
//            refresh_button.isEnabled = true
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (position % 10 == 9) {
            1;
        } else {
            0;
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