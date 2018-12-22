package com.sd.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sd.lib.swipemenu.SwipeMenu;
import com.sd.lib.swipemenu.utils.SingleModeSwipeMenuOverlay;

public class ListViewActivity extends AppCompatActivity
{
    private ListView mListView;
    private SingleModeSwipeMenuOverlay mSingleModeSwipeMenuOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = findViewById(R.id.listview);
        mSingleModeSwipeMenuOverlay = findViewById(R.id.swipemenu_overlay);

        mAdapter.getDataHolder().setData(DataModel.get(50));
        mListView.setAdapter(mAdapter);
    }


    private ListViewAdapter mAdapter = new ListViewAdapter()
    {
        @Override
        public void onBindData(int position, View convertView, ViewGroup parent, DataModel model)
        {
            super.onBindData(position, convertView, parent, model);
            final SwipeMenu swipeMenu = get(R.id.swipemenu, convertView);
            mSingleModeSwipeMenuOverlay.bind(swipeMenu, model);
        }
    };
}
