package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

final class MenuContainer extends LockContainer
{
    private SwipeMenu.Gravity mMenuGravity;

    public MenuContainer(Context context)
    {
        super(context);
    }

    /**
     * 设置菜单位置
     *
     * @param gravity
     */
    public void setMenuGravity(SwipeMenu.Gravity gravity)
    {
        if (gravity == null)
            throw new NullPointerException();

        if (mMenuGravity != gravity)
        {
            mMenuGravity = gravity;
            requestLayout();
        }
    }

    /**
     * 返回菜单位置
     *
     * @return
     */
    public SwipeMenu.Gravity getMenuGravity()
    {
        return mMenuGravity;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final View contentView = getContentView();
        if (contentView != null && contentView.getVisibility() != GONE)
        {
            final int left = mMenuGravity == SwipeMenu.Gravity.Left ? 0 : (getMeasuredWidth() - contentView.getMeasuredWidth());
            contentView.layout(left, 0, left + contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
        }
    }
}
