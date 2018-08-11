package com.fanwe.lib.swipemenu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.swipemenu.FSwipeMenu;
import com.fanwe.lib.swipemenu.SwipeMenu;

public class SwipeMenuRecyclerAdapter extends RecyclerView.Adapter<SwipeMenuRecyclerAdapter.SwipeMenuViewHolder>
{
    private final RecyclerView.Adapter mAdapter;

    public SwipeMenuRecyclerAdapter(RecyclerView.Adapter adapter)
    {
        if (adapter instanceof SwipeMenuAdapter)
        {
            mAdapter = adapter;
            adapter.registerAdapterDataObserver(mDataObserver);
        } else
        {
            throw new IllegalArgumentException("adapter must be instance of " + SwipeMenuAdapter.class);
        }
    }

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
        {
            notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            notifyItemMoved(fromPosition, toPosition);
        }
    };

    @Override
    public final SwipeMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final FSwipeMenu swipeMenu = new FSwipeMenu(parent.getContext(), null);

        final View menuView = ((SwipeMenuAdapter) mAdapter).onCreateMenuView(viewType, swipeMenu);
        swipeMenu.setMenuView(menuView);

        final RecyclerView.ViewHolder viewHolder = mAdapter.onCreateViewHolder(parent, viewType);
        swipeMenu.setContentView(viewHolder.itemView);
        final ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
        if (params != null)
            swipeMenu.setLayoutParams(new ViewGroup.LayoutParams(params));

        final SwipeMenuViewHolder swipeMenuViewHolder = new SwipeMenuViewHolder(swipeMenu);
        swipeMenuViewHolder.mViewHolder = viewHolder;

        return swipeMenuViewHolder;
    }

    @Override
    public final void onBindViewHolder(SwipeMenuViewHolder holder, int position)
    {
        mAdapter.onBindViewHolder(holder.mViewHolder, position);

        final SwipeMenu swipeMenu = (SwipeMenu) holder.itemView;
        ((SwipeMenuAdapter) mAdapter).onBindSwipeMenu(position, swipeMenu);
    }

    @Override
    public final int getItemCount()
    {
        return mAdapter.getItemCount();
    }

    @Override
    public final int getItemViewType(int position)
    {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public final long getItemId(int position)
    {
        return mAdapter.getItemId(position);
    }

    public final static class SwipeMenuViewHolder extends RecyclerView.ViewHolder
    {
        private RecyclerView.ViewHolder mViewHolder;

        public SwipeMenuViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
