package com.doyoon.android.bravenewworld.view.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by DOYOON on 7/4/2017.
 */

public abstract class RecyclerFragment<T> extends Fragment{

    private static final String TAG = RecyclerFragment.class.getSimpleName();

    private boolean loading = true;
    private LinearLayoutManager linearLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private RecyclerView.Adapter adapter;
    private List<T> dataList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Layout Inflating */
        int fragmentLayoutResId = throwFragmentLayoutResId();
        View view = inflater.inflate(fragmentLayoutResId, container, false);

        /* 의존성 주입 */
        int recyclerViewResId = throwRecyclerViewResId();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(recyclerViewResId);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        // Log.i(TAG, "loading...");
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            // loading = false;
                            scrollEndCallback();
                            // Log.i(TAG, "Last Item Wow !");
                        }
                    }
                }
            }
        });

        dataList = throwDataList();

        /* Set Recycler View */
        adapter = new CustomRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void notifySetChanged(){
        if (adapter == null) {
            Log.e(getClass().getSimpleName(), "Adapter is Null");
            return;
        }
        adapter.notifyDataSetChanged();
    }

    public  class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int itemLayoutId = throwItemLayoutId();
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false);
            CustomViewHolder customViewHolder = throwCustomViewHolder(view);
            return customViewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            T t = dataList.get(position);
            holder.updateItemView(t);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }

    public abstract class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private T t;

        public CustomViewHolder(View itemView) {
            super(itemView);
            dependencyInjection(this.itemView, this.t);
            /* Add Listener */
            itemView.setOnClickListener(this);
        }

        public void updateItemView(T t){
            this.t = t;
            updateRecyclerItemView(this.itemView, this.t);
        }

        public T getT() {
            return t;
        }

        public abstract void updateRecyclerItemView(View view, T t);
        public abstract void dependencyInjection(View itemView, T t);
    }

    public abstract List<T> throwDataList();
    public abstract CustomViewHolder throwCustomViewHolder(View view);

    public abstract int throwFragmentLayoutResId();
    public abstract int throwRecyclerViewResId();
    public abstract int throwItemLayoutId();

    public abstract void scrollEndCallback();
}