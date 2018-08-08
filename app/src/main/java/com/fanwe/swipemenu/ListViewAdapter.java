package com.fanwe.swipemenu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanwe.lib.adapter.FSimpleAdapter;
import com.fanwe.lib.swipemenu.SwipeMenu;
import com.fanwe.lib.swipemenu.adapter.SwipeMenuAdapter;
import com.fanwe.lib.swipemenu.adapter.SwipeMenuHolder;

public class ListViewAdapter extends FSimpleAdapter<DataModel> implements SwipeMenuAdapter
{
    public static final String TAG = ListViewAdapter.class.getSimpleName();

    private final SwipeMenuHolder mSwipeMenuHolder = new SwipeMenuHolder();

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent)
    {
        /**
         * 返回item布局
         */
        return R.layout.item_list;
    }

    @Override
    public void onBindData(int position, View convertView, ViewGroup parent, final DataModel model)
    {
        final TextView textView = get(R.id.textview, convertView);
        textView.setText(model.name);
    }

    @Override
    public int getItemViewType(int position)
    {
        final DataModel model = getDataHolder().get(position);
        return model.type;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public View onCreateMenuView(int position, ViewGroup parent)
    {
        final int type = getItemViewType(position);
        switch (type)
        {
            case DataModel.TYPE_ZERO:
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_menu_zero, parent, false);
            case DataModel.TYPE_ONE:
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_menu_one, parent, false);
            default:
                return null;
        }
    }

    @Override
    public void onBindData(final int position, View contentView, View menuView, final SwipeMenu swipeMenu)
    {
        final DataModel model = getDataHolder().get(position);

        swipeMenu.setOnStateChangeCallback(new SwipeMenu.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(boolean isOpened, SwipeMenu swipeMenu)
            {
                model.isOpened = isOpened;
                Log.i(TAG, "onStateChanged:" + isOpened + " " + position);
            }
        });

        if (model.isOpened)
            swipeMenu.open(false);
        else
            swipeMenu.close(false);

        contentView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (swipeMenu.getScrollPercent() != 0)
                    swipeMenu.close(true);
                else
                    Toast.makeText(getContext(), "click " + model, Toast.LENGTH_SHORT).show();
            }
        });

        final Button btn_delete = menuView.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getDataHolder().removeData(model);
            }
        });
    }

    @Override
    public SwipeMenuHolder getSwipeMenuHolder()
    {
        return mSwipeMenuHolder;
    }
}
