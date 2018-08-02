package com.fanwe.lib.swipemenu.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fanwe.lib.swipemenu.FSwipeMenu;

public class SwipeMenuAdapterWrapper extends BaseAdapter
{
    private final BaseAdapter mAdapter;

    public SwipeMenuAdapterWrapper(BaseAdapter adapter)
    {
        if (adapter instanceof SwipeMenuAdapter)
        {
            mAdapter = adapter;
            adapter.registerDataSetObserver(mDataSetObserver);
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
            swipeMenu.setMenuView(((SwipeMenuAdapter) mAdapter).onCreateMenuView(position, parent.getContext()));
        }

        final View contentView = mAdapter.getView(position, swipeMenu.getContentView(), parent);
        swipeMenu.setContentView(contentView);

        ((SwipeMenuAdapter) mAdapter).onBindData(position, convertView, swipeMenu);

        return swipeMenu;
    }
}
