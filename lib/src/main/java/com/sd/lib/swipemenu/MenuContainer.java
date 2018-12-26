package com.sd.lib.swipemenu;

import android.content.Context;
import android.view.Gravity;

class MenuContainer extends BaseContainer
{
    private final SwipeMenu.Direction mDirection;

    public MenuContainer(SwipeMenu.Direction direction, Context context)
    {
        super(context);
        if (direction == null)
            throw new NullPointerException();

        mDirection = direction;

        switch (direction)
        {
            case Left:
                super.setGravity(Gravity.LEFT);
                break;
            case Top:
                super.setGravity(Gravity.TOP);
                break;
            case Right:
                super.setGravity(Gravity.RIGHT);
                break;
            case Bottom:
                super.setGravity(Gravity.BOTTOM);
                break;
            default:
                throw new RuntimeException();
        }
    }

    public final SwipeMenu.Direction getDirection()
    {
        return mDirection;
    }

    public int getContentBoundState()
    {
        if (getContentView() == null)
            return 0;

        switch (mDirection)
        {
            case Left:
                return getContentView().getWidth();
            case Top:
                return getContentView().getHeight();
            case Right:
                return -getContentView().getWidth();
            case Bottom:
                return -getContentView().getHeight();
            default:
                throw new RuntimeException();
        }
    }

    public int getContentBoundClose()
    {
        return 0;
    }

    @Override
    public void setGravity(int gravity)
    {
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + mDirection;
    }
}
