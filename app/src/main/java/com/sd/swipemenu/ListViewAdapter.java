package com.sd.swipemenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sd.lib.adapter.FSimpleAdapter;
import com.sd.lib.swipemenu.SwipeMenu;
import com.sd.lib.swipemenu.utils.SingleModeSwipeMenuHolder;
import com.sd.lib.swipemenu.utils.SwipeMenuHolder;

public class ListViewAdapter extends FSimpleAdapter<DataModel>
{
    public final SwipeMenuHolder mAdapterSwipeMenuHolder = new SingleModeSwipeMenuHolder();

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent)
    {
        return R.layout.item_list;
    }

    @Override
    public void onBindData(int position, View convertView, ViewGroup parent, final DataModel model)
    {
        final TextView textView = get(R.id.textview, convertView);
        textView.setText(model.name);

        final SwipeMenu swipeMenu = get(R.id.swipemenu, convertView);
        swipeMenu.setDebug(true);
        mAdapterSwipeMenuHolder.bind(swipeMenu, model);

        swipeMenu.getContentView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (swipeMenu.getScrollPercent() != 0)
                    swipeMenu.setState(SwipeMenu.State.Close, true);
                else
                    Toast.makeText(getContext(), "click " + model, Toast.LENGTH_SHORT).show();
            }
        });

        final Button btn_delete = swipeMenu.getMenuView(SwipeMenu.Direction.Right).findViewById(R.id.btn_delete);
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
