package com.example.battleship;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatsListAdapter extends RecyclerView.Adapter<StatsListAdapter.StatsItemViewHolder>{
    class StatsItemViewHolder extends RecyclerView.ViewHolder {
//        private final TextView itemTitleView, itemPubDateView, itemDescriptionView;
//        private final ImageView itemImageView;

        private StatsItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final LayoutInflater inflater;


    public List<Stats> getItems() {
        return items;
    }

    private List<Stats> items;
    private View.OnClickListener itemOnClickListener;

    StatsListAdapter(Context context, View.OnClickListener itemOnClickListener) {
        inflater = LayoutInflater.from(context);
        this.itemOnClickListener = itemOnClickListener;

//        final Timer timer =  new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                notifyDataSetChanged();
//            }
//        }, 0, 500);
    }

    @Override
    public StatsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        itemView.setOnClickListener(itemOnClickListener);
        itemView.setTag(this);
        return new StatsItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatsItemViewHolder holder, int position) {
        if (items != null) {
            Stats current = items.get(position);
//            holder.itemTitleView.setText(current.getTitle());
//            holder.itemPubDateView.setText(OffsetDateTimeToStringConverter.getStringFromDate(current.getPubDate()));
//            holder.itemDescriptionView.setText(getDescriptionForItem(current.getDescription()));
//
//            holder.itemImageView.setImageBitmap(ByteBitmapConverter.getBitmapFromBytes(current.getBitmap()));

        } else {
//            holder.itemTitleView.setText("No news");
        }
    }


    void setItems(List<Stats> rssItems) {
        items = rssItems;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        else return 0;
    }
}
