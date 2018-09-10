package com.InfinitySolutions.InfinityFilters.Utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.InfinitySolutions.InfinityFilters.R;

import java.util.ArrayList;

import static com.InfinitySolutions.InfinityFilters.MainActivity.filterType;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<FiltersListItem> mDataSet;
    private Context mContext;
    private int selectedPosition;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mFilterPreviewTextView;
        private ImageView mFilterPreviewImageView;

        public ViewHolder(View v) {
            super(v);
            mFilterPreviewTextView = (TextView)v.findViewById(R.id.filter_preview_text);
            mFilterPreviewImageView = (ImageView)v.findViewById(R.id.filter_preview_image);
        }
    }

    public MyAdapter(Context context, ArrayList<FiltersListItem> DataSet){
        mContext = context;
        mDataSet = DataSet;
        selectedPosition = filterType;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filters_item_preview,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        final int pos = position;
        if(pos == selectedPosition){
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#85b0bec5"));
        }
        FiltersListItem itemData = mDataSet.get(position);
        holder.mFilterPreviewTextView.setText(itemData.getName());
        holder.mFilterPreviewImageView.setImageBitmap(BitmapFactory.decodeResource(
                mContext.getResources(),
                itemData.getFilterPreviewImageId())
        );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType = pos;
                selectedPosition = pos;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
