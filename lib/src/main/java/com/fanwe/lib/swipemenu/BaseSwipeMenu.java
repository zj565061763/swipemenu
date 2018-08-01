package com.fanwe.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.gesture.FTouchHelper;

public abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    private View mMenuView;
    private View mContentView;

    private Gravity mMenuGravity = Gravity.Right;
    private State mState = State.Closed;
    private OnStateChangedCallback mOnStateChangedCallback;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setMenuView(null);
        setContentView(null);
    }

    @Override
    public final void setOnStateChangedCallback(OnStateChangedCallback callback)
    {
        mOnStateChangedCallback = callback;
    }

    @Override
    public final void setMenuGravity(Gravity gravity)
    {
        if (gravity == null)
            throw new NullPointerException();

        if (mMenuGravity != gravity)
        {
            mMenuGravity = gravity;
            requestLayout();
        }
    }

    @Override
    public final void open()
    {
        onSmoothSlide(getContentView().getLeft(), getLeftContentViewOpened());
    }

    @Override
    public final void close()
    {
        onSmoothSlide(getContentView().getLeft(), getLeftContentViewClosed());
    }

    protected abstract boolean onSmoothSlide(int start, int end);

    @Override
    public final void setMenuView(View view)
    {
        if (mMenuView != view)
        {
            removeView(mMenuView);
            removeViewFromParent(view);

            addView(view);
            mMenuView = view;
        }
    }

    @Override
    public final void setContentView(View view)
    {
        if (mContentView != view)
        {
            removeView(mContentView);
            removeViewFromParent(view);

            addView(view);
            mContentView = view;
        }
    }

    private static void removeViewFromParent(View view)
    {
        if (view == null)
            return;
        if (view.getParent() == null)
            return;
        try
        {
            ((ViewGroup) view.getParent()).removeView(view);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public final State getState()
    {
        return mState;
    }

    protected final View getMenuView()
    {
        if (mMenuView == null)
        {
            final View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(0, 0));
            setMenuView(view);
        }
        return mMenuView;
    }

    protected final View getContentView()
    {
        if (mContentView == null)
        {
            final View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(0, 0));
            setContentView(view);
        }
        return mContentView;
    }

    private void setState(State state)
    {
        if (state == null)
            throw new NullPointerException();
        if (mState == state)
            return;

        mState = state;

        if (mOnStateChangedCallback != null)
            mOnStateChangedCallback.onStateChanged(state, this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        View menuView = null;
        View contentView = null;

        final int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            if (i == 0)
                contentView = child;
            else if (i == 1)
                menuView = child;
            else
                throw new IllegalArgumentException("SwipeMenu can only has 2 children at most");
        }

        setMenuView(menuView);
        setContentView(contentView);
    }

    /**
     * 是否可以从左向右拖动
     *
     * @return
     */
    protected final boolean canPullLeftToRight()
    {
        switch (mMenuGravity)
        {
            case Right:
                return mState == State.Opened;
            case Left:
                return mState == State.Closed;
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    /**
     * 是否可以从右向左拖动
     *
     * @return
     */
    protected final boolean canPullRightToLeft()
    {
        switch (mMenuGravity)
        {
            case Right:
                return mState == State.Closed;
            case Left:
                return mState == State.Opened;
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    protected final void moveViews(int delta)
    {
        if (delta == 0)
            return;

        final View contentView = getContentView();

        final int left = contentView.getLeft();
        final int minLeft = getLeftContentViewMin();
        final int maxLeft = getLeftContentViewMax();
        delta = FTouchHelper.getLegalDelta(left, minLeft, maxLeft, delta);
        if (delta == 0)
            return;

        ViewCompat.offsetLeftAndRight(contentView, delta);

        if (contentView.getLeft() == getLeftContentViewClosed())
            getMenuView().setVisibility(INVISIBLE);
        else
            getMenuView().setVisibility(VISIBLE);
    }

    protected final void dealViewIdle()
    {
        if (isViewIdle())
        {
            final int left = getContentView().getLeft();
            if (left == getLeftContentViewClosed())
            {
                setState(State.Closed);
            } else if (left == getLeftContentViewOpened())
            {
                setState(State.Opened);
            } else
            {
                requestLayout();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;

        final View contentView = getContentView();
        if (contentView.getVisibility() != GONE)
        {
            contentView.measure(getChildMeasureSpec(widthMeasureSpec, 0, contentView.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, 0, contentView.getLayoutParams().height));

            final View menuView = getMenuView();
            menuView.measure(getChildMeasureSpec(widthMeasureSpec, 0, menuView.getLayoutParams().width),
                    MeasureSpec.makeMeasureSpec(contentView.getMeasuredHeight(), MeasureSpec.EXACTLY));

            width = contentView.getMeasuredWidth();
            height = contentView.getMeasuredHeight();
        }

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        width = getMeasureSize(width, widthMeasureSpec);
        height = getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    private static int getMeasureSize(int size, int measureSpec)
    {
        int result = 0;

        final int modeSpec = MeasureSpec.getMode(measureSpec);
        final int sizeSpec = MeasureSpec.getSize(measureSpec);

        switch (modeSpec)
        {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = sizeSpec;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, sizeSpec);
                break;
        }
        return result;
    }

    /**
     * view是否处于空闲状态（静止且未被拖动状态）
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    protected final int getLeftContentViewMin()
    {
        switch (mMenuGravity)
        {
            case Right:
                return getLeftContentViewOpened();
            case Left:
                return getLeftContentViewClosed();
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    protected final int getLeftContentViewMax()
    {
        switch (mMenuGravity)
        {
            case Right:
                return getLeftContentViewClosed();
            case Left:
                return getLeftContentViewOpened();
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    private int getLeftMenuView()
    {
        switch (mMenuGravity)
        {
            case Right:
                return getMeasuredWidth() - getMenuView().getMeasuredWidth();
            case Left:
                return 0;
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    private int getLeftContentViewClosed()
    {
        return 0;
    }

    private int getLeftContentViewOpened()
    {
        switch (mMenuGravity)
        {
            case Right:
                return -getMenuView().getMeasuredWidth();
            case Left:
                return getMenuView().getMeasuredWidth();
            default:
                throw new AssertionError("unknow gravity:" + mMenuGravity);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final View contentView = getContentView();
        if (contentView.getVisibility() == GONE)
            return;

        // ContentView
        int left = 0;
        int top = 0;
        if (isViewIdle())
        {
            switch (mState)
            {
                case Closed:
                    left = getLeftContentViewClosed();
                    top = 0;
                    break;
                case Opened:
                    left = getLeftContentViewOpened();
                    top = 0;
                    break;
            }
        } else
        {
            left = contentView.getLeft();
            top = contentView.getTop();
        }

        contentView.layout(left, top,
                left + contentView.getMeasuredWidth(),
                top + contentView.getMeasuredHeight());

        final View menuView = getMenuView();

        // MenuView
        if (menuView.getVisibility() != GONE)
        {
            left = getLeftMenuView();
            top = 0;

            menuView.layout(left, top,
                    left + menuView.getMeasuredWidth(),
                    top + menuView.getMeasuredHeight());
        }

        if (ViewCompat.getZ(contentView) <= ViewCompat.getZ(menuView))
            ViewCompat.setZ(contentView, ViewCompat.getZ(menuView) + 1);
    }
}
