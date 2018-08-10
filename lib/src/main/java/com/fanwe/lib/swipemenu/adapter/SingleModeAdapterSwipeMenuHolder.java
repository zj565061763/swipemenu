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
        {
            for (SwipeMenu item : getAllSwipeMenu())
            {
                if (item != swipeMenu)
                    item.setOpened(false, true);
            }
        }
    }

    @Override
    public void onViewPositionChanged(boolean isDrag, SwipeMenu swipeMenu)
    {
        if (isDrag)
        {
            for (SwipeMenu item : getAllSwipeMenu())
            {
                if (item != swipeMenu)
                    item.setOpened(false, true);
            }
        }
    }
}
