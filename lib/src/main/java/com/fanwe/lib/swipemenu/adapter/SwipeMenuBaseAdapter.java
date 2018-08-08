package com.fanwe.lib.swipemenu.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fanwe.lib.swipemenu.FSwipeMenu;

public class SwipeMenuBaseAdapter extends BaseAdapter
{
    private final BaseAdapter mAdapter;
    private final SwipeMenuHolder mSwipeMenuHolder;

    public SwipeMenuBaseAdapter(BaseAdapter adapter)
    {
        if (adapter instanceof SwipeMenuAdapter)
        {
            mAdapter = adapter;
            adapter.registerDataSetObserver(mDataSetObserver);
            mSwipeMenuHolder = ((SwipeMenuAdapter) adapter).getSwipeMenuHolder();
        } else
            throw new IllegalArgumentException("adapter must be instance of " + SwipeMenuAdapter.class);
    }

    private final DataSetObserver mDataSetObserver = new DataSetObserver()
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated()
        {
            super.onInvalidated();
            notifyDataSetInvalidated();
        }
    };

    @Override
    public final int getViewTypeCount()
    {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public final int getItemViewType(int position)
    {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public final int getCount()
    {
        return mAdapter.getCount();
    }

    @Override
    public final Object getItem(int position)
    {
        return mAdapter.getItem(position);
    }

    @Override
    public final long getItemId(int position)
    {
        return mAdapter.getItemId(position);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent)
    {
        FSwipeMenu swipeMenu = null;
        if (convertView instanceof FSwipeMenu)
        {
            swipeMenu = (FSwipeMenu) convertView;
        } else
        {
            swipeMenu = new FSwipeMenu(parent.getContext(), null);
            swipeMenu.setMenuView(((SwipeMenuAdapter) mAdapter).onCreateMenuView(position, swipeMenu));
        }

        View contentView = swipeMenu.getContentView();
        contentView = mAdapter.getView(position, contentView, parent);
        swipeMenu.setContentView(contentView);

        ((SwipeMenuAdapter) mAdapter).onBindData(position, contentView, swipeMenu.getMenuView(), swipeMenu);

        if (mSwipeMenuHolder != null)
            mSwipeMenuHolder.put(swipeMenu, position);

        return swipeMenu;
    }
}
