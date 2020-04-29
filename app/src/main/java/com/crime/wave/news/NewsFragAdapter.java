package com.crime.wave.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.crime.wave.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewsFragAdapter extends RecyclerView.Adapter<NewsFragAdapter.ViewHolder> {

    private List<NewsItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public NewsFragAdapter(Context context, List<NewsItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_news1, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsItem item = mData.get(position);
        holder.descr.setText(item.getDescription());
        holder.title.setText(item.getTitle());
        holder.dtime.setText(formatDate(item.getPublishedDate(),"yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy/MM/dd HH:mm:ss"));
        Picasso.get()
                .load(item.getImage())
                .placeholder(R.drawable.icon_image_error)
                .error(R.drawable.icon_image_error)
                .into(holder.image);

    }
    public String formatDate(String dateToFormat, String inputFormat, String outputFormat) {
        try {

            String convertedDate = new SimpleDateFormat(outputFormat)
                    .format(new SimpleDateFormat(inputFormat)
                            .parse(dateToFormat));
            return convertedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView dtime;
        TextView descr;


        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.arTitle);
            dtime = itemView.findViewById(R.id.arTime);
            descr = itemView.findViewById(R.id.arDescription);
            itemView.setOnClickListener(v -> {
                if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
            });
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }
}