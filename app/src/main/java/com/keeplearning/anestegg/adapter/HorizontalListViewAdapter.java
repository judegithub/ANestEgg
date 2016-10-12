package com.keeplearning.anestegg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.keeplearning.anestegg.R;

public class HorizontalListViewAdapter extends BaseAdapter {

	private final String TAG = "HorizontalListViewAdapter";

	public HorizontalListViewAdapter(Context con) {
		mInflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		return 15;
	}

	private LayoutInflater mInflater;

	@Override
	public Object getItem(int position) {
		return position;
	}

	private ViewHolder vh;

	private static class ViewHolder {
		private TextView time;
		private TextView title;
		private ImageView im;
		private Button btn;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.d("DBG", TAG + " getView() position: " + position
//		);
		final ViewHolder vh;
//		if (convertView == null) {
//			vh = new ViewHolder();
//			convertView = mInflater.inflate(R.layout.horizontallistview_item, null);
//			vh.im = (ImageView) convertView.findViewById(R.id.iv_pic);
//			vh.im.setFocusable(true);
//			vh.im.setFocusableInTouchMode(true);
//			vh.time = (TextView) convertView.findViewById(R.id.tv_time);
//			vh.title = (TextView) convertView.findViewById(R.id.tv_name);
//			convertView.setTag(vh);
//		} else {
//			vh = (ViewHolder) convertView.getTag();
//		}
//		vh.time.setText("00:00");
//		if (position % 2 == 0) {
//			vh.title.setTextColor(Color.RED);
//		}  else {
//			vh.btn.setTextColor(Color.BLACK);
//		}
//		vh.title.setText(position + "XXXXXX");

		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_horizontal_list_view, parent, false);
			vh.title = (TextView) convertView.findViewById(R.id.item_text_view);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
//		if (position % 10 == 0) {
//			vh.title.setTextColor(Color.RED);
//		} else {
//			vh.title.setTextColor(Color.BLACK);
//		}
		vh.title.setText("第" + (position + 1) + "个");

//		if (convertView == null) {
//			vh = new ViewHolder();
//			convertView = mInflater.inflate(R.layout.item_button, parent, false);
//			vh.time = (TextView) convertView.findViewById(R.id.item_btn);
//			convertView.setTag(vh);
//		} else {
//			vh = (ViewHolder) convertView.getTag();
//		}
//		if (position % 10 == 0) {
//			vh.time.setTextColor(Color.RED);
//		} else {
//			vh.time.setTextColor(Color.BLACK);
//		}
//		vh.time.setText(position + "");
		
		return convertView;
	}
}