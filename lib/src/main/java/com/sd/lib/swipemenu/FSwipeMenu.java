package com.sd.lib.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.sd.lib.gesture.FGestureManager;
import com.sd.lib.gesture.FScroller;
import com.sd.lib.gesture.FTouchHelper;
import com.sd.lib.gesture.tag.TagHolder;

public class FSwipeMenu extends BaseSwipeMenu
{
    private FGestureManager mGestureManager;
    private FScroller mScroller;
    private final int mTouchSlop;

    public FSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
                        dealScrollFinish();
                }

                @Override
                public void onScroll(int lastX, int lastY, int currX, int currY)
                {
                    final int deltaX = currX - lastX;
                    moveViews(deltaX, false);
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
                    moveViews(deltaX, true);
                    return true;
                }

                @Override
                public void onEventFinish(boolean hasConsumeEvent, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (hasConsumeEvent)
                    {
                        velocityTracker.computeCurrentVelocity(1000);
                        final int velocityX = (int) velocityTracker.getXVelocity();
                        dealDragFinish(velocityX);
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
                    if (tag)
                        setScrollState(ScrollState.Drag);
                }
            });
        }
        return mGestureManager;
    }

    @Override
    protected void abortAnimation()
    {
        getScroller().abortAnimation();
    }

    @Override
    protected boolean onSmoothScroll(int start, int end)
    {
        return getScroller().scrollToX(start, end, -1);
    }

    private boolean canPull()
    {
        final boolean checkViewIdle = isViewIdle();
        if (!checkViewIdle)
            return false;

        final boolean checkPullCondition = checkPullCondition();
        if (!checkPullCondition)
            return false;

        final double degreeX = getGestureManager().getTouchHelper().getDegreeXFromDown();
        if (degreeX < 45)
        {
            // horizontal
            final State state = getState();
            if (state == State.OpenTop || state == State.OpenBottom)
                return false;

            final int deltaX = (int) getGestureManager().getTouchHelper().getDeltaXFromDown();
            if (Math.abs(deltaX) < mTouchSlop)
                return false;

            if (deltaX < 0)
            {
                // drag left
                if (state == State.OpenRight)
                    return false;

                if (!FTouchHelper.isScrollToRight(getContentView()))
                    return false;

                if (state == State.Close)
                    setOpenDirection(Direction.Right);
                else if (state == State.OpenLeft)
                    setOpenDirection(Direction.Left);

                return true;
            } else
            {
                // drag right
                if (state == State.OpenLeft)
                    return false;

                if (!FTouchHelper.isScrollToLeft(getContentView()))
                    return false;

                return true;
            }

        } else
        {
            // vertical
        }


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
        getScroller().setMaxScrollDistance(getMaxScrollDistance());
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