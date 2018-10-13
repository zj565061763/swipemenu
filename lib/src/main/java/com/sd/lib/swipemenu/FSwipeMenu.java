package com.sd.lib.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
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
                    return canPull();
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event)
                {
                    return canPull();
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
            final int delta = (int) getGestureManager().getTouchHelper().getDeltaXFromDown();
            return mHorizontalPullHelper.canPull(delta, getState());

        } else
        {
            // vertical
            final int delta = (int) getGestureManager().getTouchHelper().getDeltaYFromDown();
            return mVerticalPullHelper.canPull(delta, getState());
        }
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

    //---------- PullHelper Start ----------

    public abstract class PullHelper
    {
        public final boolean canPull(int delta, State state)
        {
            if (Math.abs(delta) < mTouchSlop)
                return false;

            final boolean pullToStart = delta < 0;
            if (!isLegalState(state, pullToStart))
                return false;

            if (!isScrollToBound(pullToStart, getContentView()))
                return false;

            if (state == State.Close)
                setOpenDirection(getDirectionForCloseState(pullToStart));
            else if (state == getOpenStateCanPull(pullToStart))
                setOpenDirection(getDirectionForOpenState(state);

            return true;
        }

        protected abstract boolean isLegalState(State state, boolean pullToStart);

        protected abstract State getOpenStateCanPull(boolean pullToStart);

        protected abstract boolean isScrollToBound(boolean pullToStart, View view);

        protected abstract Direction getDirectionForCloseState(boolean pullToStart);

        protected abstract Direction getDirectionForOpenState(State state);
    }

    private final PullHelper mHorizontalPullHelper = new PullHelper()
    {
        @Override
        protected boolean isLegalState(State state, boolean pullToStart)
        {
            if (state == State.Close)
                return true;

            if (state == State.OpenLeft && pullToStart)
                return true;

            if (state == State.OpenRight && !pullToStart)
                return true;

            return false;
        }

        @Override
        protected boolean isScrollToBound(boolean pullToStart, View view)
        {
            return pullToStart ? FTouchHelper.isScrollToRight(view) : FTouchHelper.isScrollToLeft(view);
        }

        @Override
        protected Direction getDirectionForCloseState(boolean pullToStart)
        {
            return pullToStart ? Direction.Right : Direction.Left;
        }

        @Override
        protected State getOpenStateCanPull(boolean pullToStart)
        {
            return pullToStart ? State.OpenLeft : State.OpenRight;
        }

        @Override
        protected Direction getDirectionForOpenState(State state)
        {
            if (state == State.OpenLeft)
                return Direction.Left;
            else if (state == State.OpenRight)
                return Direction.Right;
            else
                throw new RuntimeException();
        }
    };

    private final PullHelper mVerticalPullHelper = new PullHelper()
    {
        @Override
        protected boolean isLegalState(State state, boolean pullToStart)
        {
            if (state == State.Close)
                return true;

            if (state == State.OpenTop && pullToStart)
                return true;

            if (state == State.OpenBottom && !pullToStart)
                return true;

            return false;
        }

        @Override
        protected boolean isScrollToBound(boolean pullToStart, View view)
        {
            return pullToStart ? FTouchHelper.isScrollToBottom(view) : FTouchHelper.isScrollToTop(view);
        }

        @Override
        protected Direction getDirectionForCloseState(boolean pullToStart)
        {
            return pullToStart ? Direction.Bottom : Direction.Top;
        }

        @Override
        protected State getOpenStateCanPull(boolean pullToStart)
        {
            return pullToStart ? State.OpenTop : State.OpenBottom;
        }

        @Override
        protected Direction getDirectionForOpenState(State state)
        {
            if (state == State.OpenTop)
                return Direction.Top;
            else if (state == State.OpenBottom)
                return Direction.Bottom;
            else
                throw new RuntimeException();
        }
    };

    //---------- PullHelper end ----------
}
