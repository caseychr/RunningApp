package com.runningapp.runningapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.runningapp.runningapp.model.MyRun;

import java.util.List;

public class MyRunsRecyclerAdapter extends RecyclerView.Adapter<MyRunsRecyclerAdapter.ViewHolder> {

    List<MyRun> mMyRunList;
    Context mContext;
    OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(MyRun myRun);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public MyRunsRecyclerAdapter(Context context, List<MyRun> myRuns) {
        mContext = context;
        mMyRunList = myRuns;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_run, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MyRun myRun = mMyRunList.get(i);
        viewHolder.mStaticMap.setImageDrawable(myRun.getStaticMap());
        viewHolder.mDistance.setText(myRun.getDistance());
        viewHolder.mDuration.setText(myRun.getDuration());
        viewHolder.mMileSplit.setText(myRun.getMileSplit());
        viewHolder.mDateTime.setText(myRun.getDate());
        viewHolder.mUseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onClick(myRun);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMyRunList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mStaticMap;
        TextView mDistance;
        TextView mDuration;
        TextView mMileSplit;
        TextView mDateTime;
        TextView mUseTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStaticMap = itemView.findViewById(R.id.img_static_map);
            mDistance = itemView.findViewById(R.id.tv_item_distance);
            mDuration = itemView.findViewById(R.id.tv_item_duration);
            mMileSplit = itemView.findViewById(R.id.tv_item_mile_time);
            mDateTime = itemView.findViewById(R.id.tv_item_date);
            mUseTV = itemView.findViewById(R.id.tv_use);
        }
    }
}
