package com.keeplearning.anestegg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keeplearning.anestegg.R;

import java.util.List;

public class TestRecyclerViewAdapter extends Adapter<TestRecyclerViewAdapter.TestViewHolder> {

    private Context mContext;

    private List<String> mData;

    public TestRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<String> data) {
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TestViewHolder holder = new TestViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_test_recycler_view,
                        parent,
                        false));
        return holder;
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.mTV.setText(mData.get(position));
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView mTV;

        public TestViewHolder(View view) {
            super(view);
            mTV = (TextView) view.findViewById(R.id.item_recycler_view_text_view);
        }
    }
}
