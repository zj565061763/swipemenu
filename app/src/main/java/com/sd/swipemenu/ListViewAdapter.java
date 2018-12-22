package com.sd.swipemenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sd.lib.adapter.FSimpleAdapter;
import com.sd.lib.swipemenu.SwipeMenu;
import com.sd.lib.swipemenu.utils.SingleModeSwipeMenuOverlay;

public class ListViewAdapter extends FSimpleAdapter<DataModel>
{
    private SingleModeSwipeMenuOverlay mSingleModeSwipeMenuOverlay;

    public ListViewAdapter(SingleModeSwipeMenuOverlay singleModeSwipeMenuOverlay)
    {
        mSingleModeSwipeMenuOverlay = singleModeSwipeMenuOverlay;
    }

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

        mSingleModeSwipeMenuOverlay.bind(swipeMenu, model);

        swipeMenu.getContentView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (swipeMenu.getScrollPercent() == 0)
                    Toast.makeText(getContext(), "click " + model, Toast.LENGTH_SHORT).show();
                else
                    swipeMenu.setState(SwipeMenu.State.Close, true);
            }
        });

        final Button btn_delete = swipeMenu.getMenuView(SwipeMenu.Direction.Right).findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getDataHolder().removeData(model);
                mSingleModeSwipeMenuOverlay.remove(model);
            }
        });

        final Button btn_cancel = swipeMenu.getMenuView(SwipeMenu.Direction.Left).findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                swipeMenu.setState(SwipeMenu.State.Close, true);
            }
        });
    }
}
