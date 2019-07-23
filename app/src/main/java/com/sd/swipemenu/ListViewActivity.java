package com.sd.swipemenu;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.swipemenu.ext.SingleModeSwipeMenuOverlay;

public class ListViewActivity extends AppCompatActivity
{
    private ListView mListView;
    private ListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = findViewById(R.id.listview);

        mAdapter = new ListViewAdapter((SingleModeSwipeMenuOverlay) findViewById(R.id.swipemenu_overlay));
        mAdapter.getDataHolder().setData(DataModel.get(50));
        mListView.setAdapter(mAdapter);
    }
}
