package com.keeplearning.anestegg.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.keeplearning.anestegg.widget.CustomHorizontalListViewItem;

public class CustomHorizontalListViewAdapter extends BaseAdapter {

    private final String TAG = "PlayRecommendViewAdapter";

    private Context mContext;

    public CustomHorizontalListViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 1000;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = new CustomHorizontalListViewItem(mContext);

            vh = new ViewHolder();
            vh.item = (CustomHorizontalListViewItem) convertView;

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.item.setName("第" + (position + 1) + "集");

        return convertView;
    }

    private static class ViewHolder {
        private CustomHorizontalListViewItem item;
    }

}