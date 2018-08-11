package com.fanwe.swipemenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanwe.lib.adapter.FSimpleRecyclerAdapter;
import com.fanwe.lib.adapter.viewholder.FRecyclerViewHolder;
import com.fanwe.lib.swipemenu.SwipeMenu;
import com.fanwe.lib.swipemenu.utils.SingleModeSwipeMenuHolder;
import com.fanwe.lib.swipemenu.utils.SwipeMenuHolder;

public class RecyclerViewAdapter extends FSimpleRecyclerAdapter<DataModel>
{
    public final SwipeMenuHolder mAdapterSwipeMenuHolder = new SingleModeSwipeMenuHolder();

    @Override
    public int getLayoutId(ViewGroup parent, int viewType)
    {
        return R.layout.item_list;
    }

    @Override
    public void onBindData(FRecyclerViewHolder<DataModel> holder, int position, final DataModel model)
    {
        final TextView textView = holder.get(R.id.textview);
        textView.setText(model.name);

        final SwipeMenu swipeMenu = holder.get(R.id.swipemenu);
        mAdapterSwipeMenuHolder.bind(swipeMenu, position);

        swipeMenu.setMenuGravity(SwipeMenu.Gravity.Left);
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
}
