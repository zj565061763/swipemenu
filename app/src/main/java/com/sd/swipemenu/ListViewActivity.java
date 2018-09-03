package com.sd.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ListView;

import com.fanwe.swipemenu.R;

public class ListViewActivity extends AppCompatActivity
{
    private ListView mListView;
    private ListViewAdapter mAdapter = new ListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = findViewById(R.id.listview);

        mAdapter.getDataHolder().setData(DataModel.get(50));
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                mAdapter.mAdapterSwipeMenuHolder.setAllSwipeMenuOpenedExcept(false, true, null);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {

            }
        });
    }
}
