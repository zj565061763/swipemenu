package com.fanwe.swipemenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanwe.lib.adapter.FSimpleRecyclerAdapter;
import com.fanwe.lib.adapter.viewholder.FRecyclerViewHolder;
import com.fanwe.lib.swipemenu.SwipeMenu;
import com.fanwe.lib.swipemenu.adapter.AdapterSwipeMenuHolder;
import com.fanwe.lib.swipemenu.adapter.SingleModeAdapterSwipeMenuHolder;
import com.fanwe.lib.swipemenu.adapter.SwipeMenuAdapter;

public class RecyclerViewAdapter extends FSimpleRecyclerAdapter<DataModel> implements SwipeMenuAdapter
{
    public final AdapterSwipeMenuHolder mAdapterSwipeMenuHolder = new SingleModeAdapterSwipeMenuHolder(this);

    @Override
    public int getLayoutId(ViewGroup parent, int viewType)
    {
        return R.layout.item_list;
    }

    @Override
    public void onBindData(FRecyclerViewHolder<DataModel> holder, int position, DataModel model)
    {
        final TextView textView = holder.get(R.id.textview);
        textView.setText(model.name);
    }

    @Override
    public View onCreateMenuView(int position, ViewGroup parent)
    {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_menu_view, parent, false);
    }

    @Override
    public void onBindSwipeMenu(int position, final SwipeMenu swipeMenu)
    {
        mAdapterSwipeMenuHolder.bind(swipeMenu, position);

        final DataModel model = getDataHolder().get(position);

        swipeMenu.getContentView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (swipeMenu.getScrollPercent() != 0)
                    swipeMenu.setOpened(false, true);
                else
                    Toast.makeText(getContext(), "click " + model, Toast.LENGTH_SHORT).show();
            }
        });

        final Button btn_delete = swipeMenu.getMenuView().findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAdapterSwipeMenuHolder.remove(model);
                getDataHolder().removeData(model);
            }
        });
    }

    @Override
    public Object getTag(int position)
    {
        return getDataHolder().get(position);
    }
}
