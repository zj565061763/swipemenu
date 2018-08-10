package com.fanwe.lib.swipemenu.adapter;

import com.fanwe.lib.swipemenu.SwipeMenu;

public class SingleModeAdapterSwipeMenuHolder extends AdapterSwipeMenuHolder implements SwipeMenu.OnViewPositionChangeCallback
{
    public SingleModeAdapterSwipeMenuHolder(SwipeMenuAdapter adapter)
    {
        super(adapter);
    }

    @Override
    public void bind(SwipeMenu swipeMenu, int position)
    {
        super.bind(swipeMenu, position);
        swipeMenu.setOnViewPositionChangeCallback(this);
    }

    @Override
    public void onStateChanged(boolean isOpened, SwipeMenu swipeMenu)
    {
        super.onStateChanged(isOpened, swipeMenu);
        if (isOpened)
            setAllSwipeMenuOpenedExcept(false, true, swipeMenu);
    }

    @Override
    public void onViewPositionChanged(boolean isDrag, SwipeMenu swipeMenu)
    {
        if (isDrag)
            setAllSwipeMenuOpenedExcept(false, true, swipeMenu);
    }
}
