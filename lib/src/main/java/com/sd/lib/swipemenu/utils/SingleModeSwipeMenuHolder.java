package com.sd.lib.swipemenu.utils;

import com.sd.lib.swipemenu.SwipeMenu;

public class SingleModeSwipeMenuHolder extends SwipeMenuHolder implements SwipeMenu.OnViewPositionChangeCallback
{
    @Override
    public void bind(SwipeMenu swipeMenu, Object tag)
    {
        super.bind(swipeMenu, tag);
        swipeMenu.setOnViewPositionChangeCallback(this);
    }

    @Override
    public void onStateChanged(SwipeMenu.State oldState, SwipeMenu.State newState, SwipeMenu swipeMenu)
    {
        super.onStateChanged(oldState, newState, swipeMenu);
        if (newState != SwipeMenu.State.Close)
            setAllSwipeMenuStateExcept(SwipeMenu.State.Close, true, swipeMenu);
    }

    @Override
    public void onViewPositionChanged(int left, int top, boolean isDrag, SwipeMenu swipeMenu)
    {
        if (isDrag)
            setAllSwipeMenuStateExcept(SwipeMenu.State.Close, true, swipeMenu);
    }
}
