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
                    final int delta = getMenuDirection().isHorizontal() ? (currX - lastX) : (currY - lastY);
                    moveViews(delta, false);
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
                    final int delta = getMenuDirection().isHorizontal()
                            ? (int) getGestureManager().getTouchHelper().getDeltaX()
                            : (int) getGestureManager().getTouchHelper().getDeltaY();

                    moveViews(delta, true);
                    return true;
                }

                @Override
                public void onEventFinish(boolean hasConsumeEvent, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (hasConsumeEvent)
                    {
                        velocityTracker.computeCurrentVelocity(1000);
                        final int velocity = getMenuDirection().isHorizontal()
                                ? (int) velocityTracker.getXVelocity()
                                : (int) velocityTracker.getYVelocity();

                        dealDragFinish(velocity);
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
    protected void onMenuDirectionChanged(Direction direction)
    {
        getScroller().setMaxScrollDistance(getMaxScrollDistance());
    }

    @Override
    protected void abortAnimation()
    {
        getScroller().abortAnimation();
    }

    @Override
    protected boolean onSmoothScroll(int start, int end)
    {
        if (getMenuDirection().isHorizontal())
            return getScroller().scrollToX(start, end, -1);
        else
            return getScroller().scrollToY(start, end, -1);
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
    public void computeScroll()
    {
        if (getScroller().computeScrollOffset())
            invalidate();
    }

    @Override
    protected boolean isViewIdle()
    {
        final boolean checkScrollerFinished = getScroller().isFinished();
        if (!checkScrollerFinished)
            return false;

        final boolean checkNotDragging = !getGestureManager().getTagHolder().isTagConsume();
        if (!checkNotDragging)
            return false;

        return true;
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
            final View contentView = getContentView();
            if (contentView == null)
                return false;

            if (Math.abs(delta) < mTouchSlop)
                return false;

            final boolean pullToStart = delta < 0;
            if (!isLegalState(state, pullToStart))
                return false;

            if (!isScrollToBound(pullToStart, contentView))
                return false;

            Direction direction = null;
            if (state == State.Close)
                direction = getMenuDirectionForCloseState(pullToStart);
            else if (state == getOpenStateCanPull(pullToStart))
                direction = getMenuDirectionForOpenState(state);

            final View view = getMenuView(direction);
            if (view == null)
                return false;

            setMenuDirection(direction);
            return true;
        }

        protected abstract boolean isLegalState(State state, boolean pullToStart);

        protected abstract State getOpenStateCanPull(boolean pullToStart);

        protected abstract boolean isScrollToBound(boolean pullToStart, View view);

        protected abstract Direction getMenuDirectionForCloseState(boolean pullToStart);

        protected abstract Direction getMenuDirectionForOpenState(State state);
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
        protected Direction getMenuDirectionForCloseState(boolean pullToStart)
        {
            return pullToStart ? Direction.Right : Direction.Left;
        }

        @Override
        protected State getOpenStateCanPull(boolean pullToStart)
        {
            return pullToStart ? State.OpenLeft : State.OpenRight;
        }

        @Override
        protected Direction getMenuDirectionForOpenState(State state)
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
        protected Direction getMenuDirectionForCloseState(boolean pullToStart)
        {
            return pullToStart ? Direction.Bottom : Direction.Top;
        }

        @Override
        protected State getOpenStateCanPull(boolean pullToStart)
        {
            return pullToStart ? State.OpenTop : State.OpenBottom;
        }

        @Override
        protected Direction getMenuDirectionForOpenState(State state)
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
