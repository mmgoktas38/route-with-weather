package com.kogo.iroad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kogo.iroad.Info;
import com.kogo.iroad.R;

import java.io.Serializable;
import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.CardViewHolder> implements Serializable {

    private Context mContext;
    private ArrayList<Info> infoArrayList;

    public TimelineAdapter() {
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_design_timeline, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        Info info = infoArrayList.get(position);
        if (position == 0){
            holder.view3.setVisibility(View.GONE);
            holder.viewTop.setVisibility(View.GONE);
            holder.imageViewCircle.setImageResource(R.drawable.circle);
        }else {
            holder.view3.setVisibility(View.VISIBLE);
            holder.viewTop.setVisibility(View.VISIBLE);
        }
        if (position == infoArrayList.size()-1){
            holder.view4.setVisibility(View.GONE);
            holder.viewBottom.setVisibility(View.GONE);
            holder.imageViewCircle.setImageResource(R.drawable.circle);
        }else {
            holder.view4.setVisibility(View.VISIBLE);
            holder.viewBottom.setVisibility(View.VISIBLE);
        }
        holder.imageViewIcon.setImageResource(mContext.getResources().getIdentifier(info.getIconName(),"drawable", mContext.getPackageName()));
        holder.textViewTime.setText(info.getTime());
        holder.textViewCelcius.setText(info.getCelcius() + "°C");
        holder.textViewDistance.setText(info.getDistance());

    }

    @Override
    public int getItemCount() {
        return infoArrayList.size();
    }

    public TimelineAdapter(Context mContext, ArrayList<Info> infoArrayList) {
        this.mContext = mContext;
        this.infoArrayList = infoArrayList;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageViewIcon;
        private TextView  textViewCelcius, textViewTime, textViewDistance;
        private View view3, view4, viewTop, viewBottom;
        private ImageView imageViewCircle;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            view3 = itemView.findViewById(R.id.view3);
            view4 = itemView.findViewById(R.id.view4);
            viewTop = itemView.findViewById(R.id.viewTop);
            viewBottom = itemView.findViewById(R.id.viewBottom);
            textViewCelcius = itemView.findViewById(R.id.textViewCelcius);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
            imageViewCircle = itemView.findViewById(R.id.imageViewCircle);
        }
    }
}
