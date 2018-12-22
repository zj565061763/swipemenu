package com.sd.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.sd.lib.gesture.FTouchHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    protected boolean mIsDebug;

    private final ContentContainer mContentContainer;
    private final Map<Direction, MenuContainer> mMapMenuContainer = new HashMap<>();

    private Mode mMode = Mode.Overlay;
    private State mState = State.Close;
    private Direction mMenuDirection;

    private ScrollState mScrollState = ScrollState.Idle;

    private int mContentContainerLeft;
    private int mContentContainerTop;

    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;
    private OnScrollStateChangeCallback mOnScrollStateChangeCallback;
    private PullCondition mPullCondition;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContentContainer = new ContentContainer(getContext())
        {
            @Override
            protected void onContentViewChanged(View view)
            {
                super.onContentViewChanged(view);
                if (view == null)
                    setState(State.Close, false);
            }
        };
        addView(mContentContainer);
    }

    private MenuContainer getMenuContainer(final Direction direction)
    {
        if (direction == null)
            throw new NullPointerException("direction is null");

        MenuContainer container = mMapMenuContainer.get(direction);
        if (container == null)
        {
            container = new MenuContainer(getContext())
            {
                @Override
                protected void onContentViewChanged(View view)
                {
                    super.onContentViewChanged(view);
                    if (view == null)
                        removeMenuContainer(direction);
                }
            };
            mMapMenuContainer.put(direction, container);
            container.setDirection(direction);
            addView(container);
        }
        return container;
    }

    private void removeMenuContainer(Direction direction)
    {
        final MenuContainer container = mMapMenuContainer.remove(direction);
        if (container == null)
            throw new RuntimeException();

        removeView(container);

        switch (direction)
        {
            case Left:
                if (mState == State.OpenLeft)
                    setState(State.Close, false);
                break;
            case Top:
                if (mState == State.OpenTop)
                    setState(State.Close, false);
                break;
            case Right:
                if (mState == State.OpenRight)
                    setState(State.Close, false);
                break;
            case Bottom:
                if (mState == State.OpenBottom)
                    setState(State.Close, false);
                break;
        }
    }

    @Override
    public final void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    @Override
    public final void setOnStateChangeCallback(OnStateChangeCallback callback)
    {
        mOnStateChangeCallback = callback;
    }

    @Override
    public final void setOnViewPositionChangeCallback(OnViewPositionChangeCallback callback)
    {
        mOnViewPositionChangeCallback = callback;
    }

    @Override
    public final void setOnScrollStateChangeCallback(OnScrollStateChangeCallback callback)
    {
        mOnScrollStateChangeCallback = callback;
    }

    @Override
    public final void setPullCondition(PullCondition pullCondition)
    {
        mPullCondition = pullCondition;
    }

    @Override
    public final void setContentView(View view)
    {
        mContentContainer.setContentView(view);
    }

    @Override
    public final void setMenuView(View view, Direction direction)
    {
        getMenuContainer(direction).setContentView(view);
    }

    @Override
    public final void setMode(Mode mode)
    {
        if (mode == null)
            throw new NullPointerException();

        if (mMode != mode)
        {
            if (!isViewIdle())
                throw new RuntimeException("mode can not be change when view is busy");

            mMode = mode;
            requestLayout();
        }
    }

    @Override
    public final View getContentView()
    {
        return mContentContainer.getContentView();
    }

    @Override
    public final View getMenuView(Direction direction)
    {
        final MenuContainer container = mMapMenuContainer.get(direction);
        if (container == null)
            return null;

        return container.getContentView();
    }

    @Override
    public final Direction getMenuDirection()
    {
        return mMenuDirection;
    }

    @Override
    public final State getState()
    {
        return mState;
    }

    @Override
    public final ScrollState getScrollState()
    {
        return mScrollState;
    }

    /**
     * 设置滚动状态
     *
     * @param state
     */
    protected final void setScrollState(ScrollState state)
    {
        if (state == null)
            throw new NullPointerException();

        if (mScrollState != state)
        {
            mScrollState = state;

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "setScrollState:" + state + " " + getScrollPercent());

            hideMenuContainerIfNeed();

            if (mOnScrollStateChangeCallback != null)
                mOnScrollStateChangeCallback.onScrollStateChanged(state, this);
        }
    }

    private void hideMenuContainerIfNeed()
    {
        if (mScrollState == ScrollState.Idle && mState == State.Close)
        {
            for (MenuContainer item : mMapMenuContainer.values())
            {
                if (item.getVisibility() != INVISIBLE)
                    item.setVisibility(INVISIBLE);
            }

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "hide all menu container");
        }
    }

    @Override
    public float getScrollPercent()
    {
        final float total = getMaxScrollDistance();
        if (total <= 0)
            return 0;

        final int current = Math.abs(getContentBoundCurrent() - getContentBoundState(State.Close));
        return current / total;
    }

    @Override
    public final boolean setState(State state, boolean anim)
    {
        if (state == null)
            throw new NullPointerException();

        if (getContentView() == null)
        {
            state = State.Close;
            anim = false;
        }

        final State stateOld = mState;
        boolean changed = false;
        if (stateOld != state)
        {
            mState = state;
            changed = true;

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "setState:" + state);

            if (state != State.Close)
            {
                final Direction directionOld = stateToMenuDirection(stateOld);
                final Direction direction = stateToMenuDirection(state);

                if (directionOld != null)
                {
                    if (directionOld.isHorizontal() != direction.isHorizontal())
                        anim = false;
                }

                setMenuDirection(direction);
            }

            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(stateOld, state, this);
        }

        if (!changed && state == State.Close)
            anim = false;

        updateViewByState(anim);

        return changed;
    }

    /**
     * 根据状态更新view的位置
     *
     * @param anim
     */
    private void updateViewByState(boolean anim)
    {
        final int boundCurrent = getContentBoundCurrent();
        final int boundState = getContentBoundState(mState);

        if (boundCurrent != boundState)
        {
            abortAnimation();

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "updateViewByState:" + boundCurrent + "," + boundState + " anim:" + anim);

            if (anim)
            {
                checkMenuDirection();
                if (onSmoothScroll(boundCurrent, boundState))
                {
                    setScrollState(ScrollState.Fling);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else
            {
                layoutInternal();
            }
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final int count = getChildCount();
        if (count <= 0)
            return;

        final List<View> list = new ArrayList<>(5);
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            if (child == mContentContainer)
                continue;

            list.add(child);
        }

        for (View item : list)
        {
            final int childId = item.getId();
            if (childId == R.id.lib_swipemenu_content)
            {
                setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_left)
            {
                setMenuView(item, Direction.Left);
            } else if (childId == R.id.lib_swipemenu_menu_top)
            {
                setMenuView(item, Direction.Top);
            } else if (childId == R.id.lib_swipemenu_menu_right)
            {
                setMenuView(item, Direction.Right);
            } else if (childId == R.id.lib_swipemenu_menu_bottom)
            {
                setMenuView(item, Direction.Bottom);
            } else
            {
                throw new RuntimeException("Illegal child:" + item);
            }
        }
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "onViewRemoved:" + child);
    }

    /**
     * 设置菜单显示方向
     *
     * @param direction
     */
    protected final void setMenuDirection(Direction direction)
    {
        if (direction == null)
            throw new NullPointerException();

        final MenuContainer container = mMapMenuContainer.get(direction);
        if (container == null)
            throw new RuntimeException("MenuContainer was not found for direction:" + direction);

        if (container.getContentView() == null)
            throw new RuntimeException("MenuContainer contentView was not found for direction:" + direction);

        for (MenuContainer item : mMapMenuContainer.values())
        {
            final int visibility = item == container ? VISIBLE : INVISIBLE;

            if (item.getVisibility() != visibility)
                item.setVisibility(visibility);
        }

        if (mIsDebug)
            Log.e(SwipeMenu.class.getSimpleName(), "setMenuDirection:" + direction);

        if (mMenuDirection != direction)
        {
            mMenuDirection = direction;
            onMenuDirectionChanged(direction);
        }
    }

    private Direction stateToMenuDirection(State state)
    {
        switch (state)
        {
            case Close:
                return null;
            case OpenLeft:
                return Direction.Left;
            case OpenTop:
                return Direction.Top;
            case OpenRight:
                return Direction.Right;
            case OpenBottom:
                return Direction.Bottom;
            default:
                throw new RuntimeException();
        }
    }

    protected final boolean checkPullCondition()
    {
        return mPullCondition == null ? true : mPullCondition.canPull(this);
    }

    private int getContentBoundState(State state)
    {
        final View view = getMenuView(stateToMenuDirection(state));
        if (view == null)
            return 0;

        switch (state)
        {
            case Close:
                return 0;
            case OpenLeft:
                return view.getWidth();
            case OpenTop:
                return view.getHeight();
            case OpenRight:
                return -view.getWidth();
            case OpenBottom:
                return -view.getHeight();
            default:
                throw new RuntimeException();
        }
    }

    /**
     * 返回内容view当前的边界值
     *
     * @return
     */
    private int getContentBoundCurrent()
    {
        if (mMenuDirection == null)
            return 0;

        if (mMenuDirection.isHorizontal())
            return mContentContainer.getLeft();
        else
            return mContentContainer.getTop();
    }

    /**
     * 返回内容view可移动范围的最小边界值
     *
     * @return
     */
    private int getContentBoundMin()
    {
        if (mMenuDirection == null)
            return 0;

        switch (mMenuDirection)
        {
            case Left:
                return getContentBoundState(State.Close);
            case Right:
                return getContentBoundState(State.OpenRight);
            case Top:
                return getContentBoundState(State.Close);
            case Bottom:
                return getContentBoundState(State.OpenBottom);
            default:
                throw new RuntimeException();
        }
    }

    /**
     * 返回内容view可移动范围的最大边界值
     *
     * @return
     */
    private int getContentBoundMax()
    {
        if (mMenuDirection == null)
            return 0;

        switch (mMenuDirection)
        {
            case Left:
                return getContentBoundState(State.OpenLeft);
            case Right:
                return getContentBoundState(State.Close);
            case Top:
                return getContentBoundState(State.OpenTop);
            case Bottom:
                return getContentBoundState(State.Close);
            default:
                throw new RuntimeException();
        }
    }

    private State getStateForBoundEnd(int bound)
    {
        if (bound == getContentBoundState(State.Close))
            return State.Close;

        if (mMenuDirection.isHorizontal())
        {
            if (bound == getContentBoundState(State.OpenLeft))
                return State.OpenLeft;
            else if (bound == getContentBoundState(State.OpenRight))
                return State.OpenRight;
            else
                throw new RuntimeException("Illegal bound end");
        } else
        {
            if (bound == getContentBoundState(State.OpenTop))
                return State.OpenTop;
            else if (bound == getContentBoundState(State.OpenBottom))
                return State.OpenBottom;
            else
                throw new RuntimeException("Illegal bound end");
        }
    }

    private void checkMenuDirection()
    {
        if (mMenuDirection == null)
            throw new RuntimeException("menu direction required");
    }

    /**
     * 移动View
     *
     * @param delta
     * @param isDrag
     */
    protected final void moveViews(int delta, boolean isDrag)
    {
        if (delta == 0)
            return;

        checkMenuDirection();

        final int boundCurrent = getContentBoundCurrent();
        final int boundMin = getContentBoundMin();
        final int boundMax = getContentBoundMax();

        delta = FTouchHelper.getLegalDelta(boundCurrent, boundMin, boundMax, delta);
        if (delta == 0)
            return;

        if (getMenuDirection().isHorizontal())
        {
            ViewCompat.offsetLeftAndRight(mContentContainer, delta);

            if (mMode == Mode.Drawer)
                ViewCompat.offsetLeftAndRight(mMapMenuContainer.get(mMenuDirection), delta);
        } else
        {
            ViewCompat.offsetTopAndBottom(mContentContainer, delta);

            if (mMode == Mode.Drawer)
                ViewCompat.offsetTopAndBottom(mMapMenuContainer.get(mMenuDirection), delta);
        }

        notifyViewPositionChangeIfNeed(isDrag);
    }

    /**
     * 拖动结束后需要执行的逻辑
     *
     * @param velocity
     */
    protected final void dealDragFinish(int velocity)
    {
        checkMenuDirection();

        final int boundCurrent = getContentBoundCurrent();
        final int boundMin = getContentBoundMin();
        final int boundMax = getContentBoundMax();

        final int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 10;

        int boundEnd = 0;
        if (Math.abs(velocity) > minFlingVelocity)
        {
            boundEnd = velocity > 0 ? boundMax : boundMin;
        } else
        {
            final int boundMiddle = (boundMin + boundMax) / 2;
            boundEnd = boundCurrent >= boundMiddle ? boundMax : boundMin;
        }

        final State state = getStateForBoundEnd(boundEnd);

        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "dealDragFinish should be state:" + state);

        if (setState(state, true))
        {
            // 状态更新成功，会触发刷新view位置
        } else
        {
            updateViewByState(true);
        }

        if (mScrollState == ScrollState.Drag)
            setScrollState(ScrollState.Idle);
    }

    /**
     * view惯性滚动结束需要执行的逻辑
     */
    protected final void dealScrollFinish()
    {
        final boolean isViewIdle = isViewIdle();

        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "dealScrollFinish isViewIdle:" + isViewIdle);

        if (isViewIdle)
        {
            updateViewByState(false);
            setScrollState(ScrollState.Idle);
        }
    }

    /**
     * 返回View可以滚动的最大距离
     *
     * @return
     */
    protected final int getMaxScrollDistance()
    {
        final View view = getMenuView(mMenuDirection);
        if (view == null)
            return 0;

        if (mMenuDirection.isHorizontal())
            return view.getWidth();
        else
            return view.getHeight();
    }

    /**
     * 显示菜单方向发生变化
     *
     * @param direction
     */
    protected abstract void onMenuDirectionChanged(Direction direction);

    /**
     * view是否处于空闲状态（静止且未被拖动状态）
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    /**
     * 停止滑动动画
     */
    protected abstract void abortAnimation();

    /**
     * 滑动
     *
     * @param start
     * @param end
     * @return
     */
    protected abstract boolean onSmoothScroll(int start, int end);

    /**
     * 通知内容view位置变化
     *
     * @param isDrag
     */
    private void notifyViewPositionChangeIfNeed(boolean isDrag)
    {
        final int left = mContentContainer.getLeft();
        final int top = mContentContainer.getTop();

        if (mContentContainerLeft != left || mContentContainerTop != top)
        {
            mContentContainerLeft = left;
            mContentContainerTop = top;

            updateLockEvent();

            if (mOnViewPositionChangeCallback != null)
                mOnViewPositionChangeCallback.onViewPositionChanged(left, top, isDrag, this);
        }
    }

    private void updateLockEvent()
    {
        final float percent = getScrollPercent();

        final MenuContainer container = mMapMenuContainer.get(mMenuDirection);
        if (container != null)
            container.setLockEvent(percent < 1.0f);

        mContentContainer.setLockEvent(percent > 0 && percent < 1.0f);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        final int contentWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, LayoutParams.MATCH_PARENT);
        final int contentHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, LayoutParams.MATCH_PARENT);
        mContentContainer.measure(contentWidthSpec, contentHeightSpec);

        int width = mContentContainer.getMeasuredWidth();
        int height = mContentContainer.getMeasuredHeight();

        final int widthMenuSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int heightMenuSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        final int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            if (child == mContentContainer)
                continue;

            child.measure(widthMenuSpec, heightMenuSpec);
        }

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "onLayout");
        layoutInternal();
    }

    private void layoutInternal()
    {
        final State state = mState;
        final boolean isViewIdle = isViewIdle();

        int left = 0;
        int top = 0;

        // ---------- Content ----------
        if (isViewIdle)
        {
            final int boundState = getContentBoundState(state);
            switch (state)
            {
                case Close:
                    left = 0;
                    top = 0;
                    break;
                case OpenLeft:
                case OpenRight:
                    left = boundState;
                    top = 0;
                    break;
                case OpenTop:
                case OpenBottom:
                    left = 0;
                    top = boundState;
                    break;
                default:
                    throw new RuntimeException();
            }
        } else
        {
            left = mContentContainer.getLeft();
            top = mContentContainer.getTop();
        }
        mContentContainer.layout(left, top,
                left + mContentContainer.getMeasuredWidth(), top + mContentContainer.getMeasuredHeight());

        if (mIsDebug)
        {
            Log.i(SwipeMenu.class.getSimpleName(), "layoutInternal state:" + state + " isViewIdle:" + isViewIdle() + " mode:" + mMode
                    + " [" + mContentContainer.getLeft() + "," + mContentContainer.getTop() + "," + mContentContainer.getRight() + "," + mContentContainer.getBottom() + "]");
        }

        // ---------- Menu ----------
        if (mMode == Mode.Overlay)
        {
            for (MenuContainer item : mMapMenuContainer.values())
            {
                left = 0;
                top = 0;
                item.layout(left, top,
                        left + item.getMeasuredWidth(), top + item.getMeasuredHeight());
            }
        } else
        {
            for (MenuContainer item : mMapMenuContainer.values())
            {
                if (isViewIdle)
                {
                    switch (item.getDirection())
                    {
                        case Left:
                            left = mContentContainer.getLeft() - item.getMeasuredWidth();
                            top = 0;
                            break;
                        case Top:
                            left = 0;
                            top = mContentContainer.getTop() - item.getMeasuredHeight();
                            break;
                        case Right:
                            left = mContentContainer.getRight();
                            top = 0;
                            break;
                        case Bottom:
                            left = 0;
                            top = mContentContainer.getBottom();
                            break;
                    }
                } else
                {
                    left = item.getLeft();
                    top = item.getTop();
                }

                item.layout(left, top,
                        left + item.getMeasuredWidth(), top + item.getMeasuredHeight());
            }
        }

        float maxZ = 0;
        for (MenuContainer item : mMapMenuContainer.values())
        {
            maxZ = Math.max(maxZ, ViewCompat.getZ(item));
        }
        if (ViewCompat.getZ(mContentContainer) <= maxZ)
            ViewCompat.setZ(mContentContainer, maxZ + 1);

        hideMenuContainerIfNeed();
        notifyViewPositionChangeIfNeed(false);
    }
}
