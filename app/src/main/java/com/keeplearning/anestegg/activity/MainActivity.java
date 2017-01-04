package com.keeplearning.anestegg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.widget.LikeToastView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    private ListView mListView;

    private ListItemProvider mListItemProvider;

    private boolean mIsReadyToFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareData();

        setupViews();

    }

    private void prepareData() {
        mListItemProvider = new ListItemProvider();
    }

    private void setupViews() {
        mListView = (ListView) findViewById(R.id.activity_main_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "您点击了第" + (position + 1) + "个", Toast.LENGTH_SHORT).show();
                startActivity(mListItemProvider.getIntent(position));
            }
        });

        MainAdapter adapter = new MainAdapter();
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if (mIsReadyToFinish) {
                    finish();
                } else {
                    mIsReadyToFinish = true;

                    LikeToastView.makeText(this, "再按一次返回退出", LikeToastView.LENGTH_SHORT).show();

                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mIsReadyToFinish = false;
                        }
                    }, 2000);
                }
            }
                return  true;
        }

        return super.onKeyDown(keyCode, event);
    }

    class MainAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListItemProvider.getCount();
        }

        @Override
        public String getItem(int position) {
            return mListItemProvider.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_main_list_view,
                        parent, false);
                holder = new ViewHolder();
                holder.mTextView = (TextView) convertView.findViewById(R.id.item_main_list_view_text_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTextView.setText(getItem(position));
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mTextView;
    }

    class ListItemProvider {
        private final String[] LIST_ITEM = new String[]{
            "Device Info",
            "HorizontalListView Test",
            "CustomHorizontalListView Test",
            "Glide Test",
            "Scroll Text View Test",
            "Animation Test",
            "Recycler View Test",
            "Scroll View Test",
        };

        public int getCount() {
            return LIST_ITEM.length;
        }

        public String getItem(int position) {
            return LIST_ITEM[position];
        }

        public Intent getIntent(int position) {
            Intent intent = null;
            switch (position) {
                case 0:
                    intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                    break;

                case 1:
                    intent = new Intent(MainActivity.this, TestHorizontalListViewActivity.class);
                    break;

                case 2:
                    intent = new Intent(MainActivity.this, TestCustomHorizontalListViewActivity.class);
                    break;

                case 3:
                    intent = new Intent(MainActivity.this, TestGlideActivity.class);
                    break;

                case 4:
                    intent = new Intent(MainActivity.this, TestScrollTextViewActivity.class);
                    break;

                case 5:
                    intent = new Intent(MainActivity.this, TestAnimationActivity.class);
                    break;

                case 6:
                    intent = new Intent(MainActivity.this, TestRecyclerViewActivity.class);
                    break;

                case 7:
                    intent = new Intent(MainActivity.this, TestScrollViewActivity.class);
                    break;
            }

            return intent;
        }
    }
}
