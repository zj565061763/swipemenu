package com.sd.lib.swipemenu;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

abstract class LockContainer extends ViewGroup
{
    private boolean mLockEvent;
    private View mContentView;

    public LockContainer(Context context)
    {
        super(context);
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置内容view
     *
     * @param view
     */
    public void setContentView(View view)
    {
        if (mContentView != view)
        {
            mContentView = null;
            removeAllViews();

            if (view != null)
            {
                mContentView = view;
                Utils.removeViewFromParent(view);
                addView(view);
            }
        }
    }

    /**
     * 返回内容view
     *
     * @return
     */
    public View getContentView()
    {
        return mContentView;
    }

    /**
     * 设置是否锁定触摸事件，锁定后内容view接收不到触摸事件
     *
     * @param lockEvent
     */
    public void setLockEvent(boolean lockEvent)
    {
        mLockEvent = lockEvent;
    }

    @Override
    public final void setLayoutParams(LayoutParams params)
    {
        if (params.width != LayoutParams.MATCH_PARENT)
            params.width = LayoutParams.MATCH_PARENT;

        if (params.height != LayoutParams.MATCH_PARENT)
            params.height = LayoutParams.MATCH_PARENT;

        super.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mLockEvent)
            return false;
        else
            return super.onTouchEvent(event);
    }

    @Override
    public void onViewAdded(View child)
    {
        super.onViewAdded(child);
        if (mContentView != child)
            throw new RuntimeException("you can not add view this way");
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (mContentView != null)
            throw new RuntimeException("you can not remove view this way");
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom)
    {
        super.setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;

        if (mContentView != null && mContentView.getVisibility() != GONE)
        {
            measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
            width = mContentView.getMeasuredWidth();
            height = mContentView.getMeasuredHeight();
        }

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }
}
