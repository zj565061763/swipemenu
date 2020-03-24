package com.sd.swipemenu;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.lib.adapter.FSimpleRecyclerAdapter;
import com.sd.lib.adapter.viewholder.FRecyclerViewHolder;
import com.sd.lib.swipemenu.FSwipeMenu;

public class NestedActivity extends AppCompatActivity implements View.OnClickListener
{
    private FSwipeMenu mSwipeMenu;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested);
        mSwipeMenu = findViewById(R.id.swipemenu);
        mRecyclerView = findViewById(R.id.rv_content);

        mSwipeMenu.setDebug(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.getDataHolder().setData(DataModel.get(100));
    }

    private final FSimpleRecyclerAdapter<DataModel> mAdapter = new FSimpleRecyclerAdapter<DataModel>()
    {
        @Override
        public int getLayoutId(ViewGroup parent, int viewType)
        {
            return R.layout.item_recyclerview;
        }

        @Override
        public void onBindData(FRecyclerViewHolder<DataModel> holder, int position, DataModel model)
        {
            final TextView textView = holder.findViewById(R.id.tv_content);
            textView.setText(model.name);
        }
    };

    @Override
    public void onClick(View view)
    {

    }
}
