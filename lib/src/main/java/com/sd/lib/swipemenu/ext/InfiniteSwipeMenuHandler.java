package com.sd.lib.swipemenu.ext;

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
     * @param viewDirection view方向
     * @param dataDirection 数据方向
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
        Direction directionOther = null;

        switch (state)
        {
            case OpenLeft:
                direction = Direction.Left;
                directionOther = Direction.Right;
                break;
            case OpenTop:
                direction = Direction.Top;
                directionOther = Direction.Bottom;
                break;
            case OpenRight:
                direction = Direction.Right;
                directionOther = Direction.Left;
                break;
            case OpenBottom:
                direction = Direction.Bottom;
                directionOther = Direction.Top;
                break;
            default:
                return;
        }

        onBindData(Direction.Center, direction);

        swipeMenu.setState(SwipeMenu.State.Close, false);
        onMoveIndex(direction);

        onBindData(direction, direction);
        onBindData(directionOther, directionOther);
    }

    /**
     * 绑定数据
     *
     * @param viewDirection view方向
     * @param dataDirection 数据方向
     */
    protected abstract void onBindData(Direction viewDirection, Direction dataDirection);

    /**
     * 移动索引
     *
     * @param direction 索引需要移动的方向
     */
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
