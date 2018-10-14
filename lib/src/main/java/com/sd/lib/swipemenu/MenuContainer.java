package com.sd.lib.swipemenu;

import android.content.Context;
import android.view.Gravity;

class MenuContainer extends BaseContainer
{
    private SwipeMenu.Direction mDirection;

    public MenuContainer(Context context)
    {
        super(context);
    }

    /**
     * 设置菜单方向
     *
     * @param direction
     */
    public final void setDirection(SwipeMenu.Direction direction)
    {
        mDirection = direction;

        switch (direction)
        {
            case Left:
                setGravity(Gravity.LEFT);
                break;
            case Top:
                setGravity(Gravity.TOP);
                break;
            case Right:
                setGravity(Gravity.RIGHT);
                break;
            case Bottom:
                setGravity(Gravity.BOTTOM);
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (mDirection == null)
            throw new NullPointerException();
    }
}
