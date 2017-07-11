package com.doyoon.android.bravenewworld.presenter.fragment.abst;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by DOYOON on 7/4/2017.
 */

public abstract class RecyclerFragment<T> extends Fragment{

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

        this.dataList = throwDataList();

        /* Set Recycler View */
        RecyclerView.Adapter adapter = new CustomRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
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

    public abstract CustomViewHolder throwCustomViewHolder(View view);
    public abstract int throwFragmentLayoutResId();
    public abstract int throwRecyclerViewResId();
    public abstract List<T> throwDataList();
    public abstract int throwItemLayoutId();
}