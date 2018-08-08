package com.fanwe.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.fanwe.lib.swipemenu.adapter.SwipeMenuAdapterView;
import com.fanwe.lib.swipemenu.adapter.SwipeMenuBaseAdapter;

public class ListViewActivity extends AppCompatActivity
{
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private SwipeMenuAdapterView mSwipeMenuAdapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = findViewById(R.id.listview);
        mSwipeMenuAdapterView = findViewById(R.id.swipe_adapter_view);

        mAdapter = new ListViewAdapter(mSwipeMenuAdapterView);
        mAdapter.getDataHolder().setData(DataModel.get(50));

        mListView.setAdapter(new SwipeMenuBaseAdapter(mAdapter));
    }
}
