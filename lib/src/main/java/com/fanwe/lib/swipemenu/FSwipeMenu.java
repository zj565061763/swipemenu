package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.fanwe.lib.gesture.FGestureManager;
import com.fanwe.lib.gesture.FScroller;
import com.fanwe.lib.gesture.FTouchHelper;
import com.fanwe.lib.gesture.tag.TagHolder;

public class FSwipeMenu extends BaseSwipeMenu
{
    private FGestureManager mGestureManager;
    private FScroller mScroller;

    private final int mTouchSlop;
    private final int mMinFlingVelocity;

    public FSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    private FScroller getScroller()
    {
        if (mScroller == null)
        {
            mScroller = new FScroller(new Scroller(getContext()));
            mScroller.setCallback(new FScroller.Callback()
            {
                @Override
                public void onScrollStateChanged(boolean isFinished)
                {
                    if (isFinished)
                        dealViewIdle();
                }

                @Override
                public void onScroll(int lastX, int lastY, int currX, int currY)
                {
                    final int deltaX = currX - lastX;
                    moveViews(deltaX);
                }
            });
        }
        return mScroller;
    }

    private FGestureManager getGestureManager()
    {
        if (mGestureManager == null)
        {
            mGestureManager = new FGestureManager(new FGestureManager.Callback()
            {
                @Override
                public boolean shouldInterceptEvent(MotionEvent event)
                {
                    final boolean shouldInterceptEvent = canPull();
                    return shouldInterceptEvent;
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event)
                {
                    final boolean shouldConsumeEvent = canPull();
                    return shouldConsumeEvent;
                }

                @Override
                public boolean onEventConsume(MotionEvent event)
                {
                    final int deltaX = (int) getGestureManager().getTouchHelper().getDeltaX();
                    moveViews(deltaX);
                    return true;
                }

                @Override
                public void onEventFinish(boolean hasConsumeEvent, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (hasConsumeEvent)
                    {
                        final int leftStart = getContentView().getLeft();
                        int leftEnd = 0;

                        velocityTracker.computeCurrentVelocity(1000);
                        final int velocityX = (int) velocityTracker.getXVelocity();
                        if (Math.abs(velocityX) > mMinFlingVelocity)
                        {
                            if (velocityX > 0)
                            {
                                leftEnd = getLeftContentViewMax();
                            } else
                            {
                                leftEnd = getLeftContentViewMin();
                            }
                        } else
                        {
                            final int leftMin = getLeftContentViewMin();
                            final int leftMax = getLeftContentViewMax();
                            if (leftStart >= ((leftMin + leftMax) / 2))
                            {
                                leftEnd = getLeftContentViewMax();
                            } else
                            {
                                leftEnd = getLeftContentViewMin();
                            }
                        }

                        onSmoothSlide(leftStart, leftEnd);
                    }
                }
            });
            mGestureManager.getTagHolder().setCallback(new TagHolder.Callback()
            {
                @Override
                public void onTagInterceptChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwipeMenu.this, tag);
                }

                @Override
                public void onTagConsumeChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwipeMenu.this, tag);
                }
            });
        }
        return mGestureManager;
    }

    @Override
    protected boolean onSmoothSlide(int start, int end)
    {
        getScroller().abortAnimation();
        final boolean scrolled = getScroller().scrollToX(start, end, -1);
        if (scrolled)
            invalidate();
        else
            dealViewIdle();
        return scrolled;
    }

    private boolean canPull()
    {
        // 为了调试方便，让每个条件都执行后把值都列出来

        final boolean checkViewIdle = isViewIdle();
        final boolean checkDegree = getGestureManager().getTouchHelper().getDegreeXFromDown() < 30;

        final int deltaX = (int) getGestureManager().getTouchHelper().getDeltaXFromDown();
        final boolean checkPullDelta = Math.abs(deltaX) > mTouchSlop;
        final boolean checkPull = (deltaX < 0 && canPullRightToLeft()) || (deltaX > 0 && canPullLeftToRight());

        return checkViewIdle && checkDegree && checkPullDelta && checkPull;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return getGestureManager().onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return getGestureManager().onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        getScroller().setMaxScrollDistance(getMenuView().getMeasuredWidth());
    }

    @Override
    public void computeScroll()
    {
        if (getScroller().computeScrollOffset())
            invalidate();
    }

    @Override
    protected boolean isViewIdle()
    {
        final boolean checkScrollerFinished = getScroller().isFinished();
        final boolean checkNotDragging = !getGestureManager().getTagHolder().isTagConsume();

        return checkScrollerFinished && checkNotDragging;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        getScroller().abortAnimation();
    }
}
