package com.sd.swipemenu;

import com.sd.lib.swipemenu.SwipeMenu;

public abstract class InfiniteSwipeMenuHandler implements SwipeMenu.OnScrollStateChangeCallback
{
    private SwipeMenu mSwipeMenu;

    public void setSwipeMenu(SwipeMenu swipeMenu)
    {
        final SwipeMenu old = mSwipeMenu;
        if (old != swipeMenu)
        {
            if (old != null)
            {
                old.setOnScrollStateChangeCallback(null);
            }

            mSwipeMenu = swipeMenu;

            if (mSwipeMenu != null)
            {
                mSwipeMenu.setOnScrollStateChangeCallback(this);
            }
        }
    }

    @Override
    public void onScrollStateChanged(SwipeMenu swipeMenu, SwipeMenu.ScrollState oldState, SwipeMenu.ScrollState newState)
    {
        bindDataWhenOpenIdle(swipeMenu);
    }

    /**
     * 绑定数据
     *
     * @param viewDirection
     * @param dataDirection
     */
    public void bindData(Direction viewDirection, Direction dataDirection)
    {
        onBindData(viewDirection, dataDirection);
    }

    private void bindDataWhenOpenIdle(SwipeMenu swipeMenu)
    {
        final SwipeMenu.ScrollState scrollState = swipeMenu.getScrollState();
        if (scrollState != SwipeMenu.ScrollState.Idle)
            return;

        final SwipeMenu.State state = swipeMenu.getState();
        if (state == SwipeMenu.State.Close)
            return;

        Direction direction = null;
        switch (state)
        {
            case OpenLeft:
                direction = Direction.Left;
                break;
            case OpenTop:
                direction = Direction.Top;
                break;
            case OpenRight:
                direction = Direction.Right;
                break;
            case OpenBottom:
                direction = Direction.Bottom;
                break;
            default:
                return;
        }

        onBindData(Direction.Center, direction);
        swipeMenu.setState(SwipeMenu.State.Close, false);

        onMoveIndex(direction);
        onBindData(direction, direction);
    }

    protected abstract void onBindData(Direction viewDirection, Direction dataDirection);

    protected abstract void onMoveIndex(Direction direction);

    public enum Direction
    {
        Left,
        Top,
        Right,
        Bottom,
        Center
    }
}
