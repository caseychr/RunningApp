package com.runningapp.runningapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runningapp.runningapp.model.MyRun;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MyRunsFragment extends Fragment implements MyRunsRecyclerAdapter.OnItemClickListener {

    private List<MyRun> mMyRunList = new ArrayList<>();
    private MyRunsRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    View mView;

    TextView mMyRunsTitle;
    TextView mAchievementsTitle;
    TextView mNoData;

    public static MyRunsFragment newInstance() {
        return new MyRunsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_runs, container, false);
        mMyRunsTitle = mView.findViewById(R.id.tab_my_runs);
        mAchievementsTitle = mView.findViewById(R.id.tab_achievements);
        mNoData = mView.findViewById(R.id.tv_no_data);
        setTitleClick();
        initMyRunsAdapter();
        return mView;
    }

    private void initMyRunsAdapter() {
        populateRunList();
        mAdapter = new MyRunsRecyclerAdapter(getContext(), mMyRunList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView = mView.findViewById(R.id.recycler_view_my_runs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        if(mMyRunList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Populate List from Database
     */
    private void populateRunList() {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play);
        for(int i=0;i<5;i++){
            MyRun myRun = new MyRun();
            myRun.setStaticMap(getActivity().getDrawable(R.drawable.ic_play));
            myRun.setDistance("6.2 miles");
            myRun.setDuration("51:43:00");
            myRun.setMileSplit("8:32");
            myRun.setDate(" Sat 07/13/19 08:40 AM");
            mMyRunList.add(myRun);
        }
    }

    @Override
    public void onClick(MyRun myRun) {

    }

    private void setTitleClick() {
        mMyRunsTitle.setBackgroundColor(Color.CYAN);
        mMyRunsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyRunsTitle.setBackgroundColor(Color.CYAN);
                mAchievementsTitle.setBackgroundColor(Color.WHITE);
                mNoData.setText("No Runs\nLogged Yet");
                if(!mMyRunList.isEmpty()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoData.setVisibility(View.GONE);
                }
            }
        });

        mAchievementsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.GONE);
                mAchievementsTitle.setBackgroundColor(Color.CYAN);
                mMyRunsTitle.setBackgroundColor(Color.WHITE);
                mNoData.setVisibility(View.VISIBLE);
                mNoData.setText("No Achievements\nLogged Yet");
            }
        });
    }
}
