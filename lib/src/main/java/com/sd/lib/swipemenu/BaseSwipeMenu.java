package com.sd.lib.swipemenu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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

    private final ContentContainer mContainerContent;
    private MenuContainer mContainerMenuLeft;
    private MenuContainer mContainerMenuTop;
    private MenuContainer mContainerMenuRight;
    private MenuContainer mContainerMenuBottom;

    private final Map<Direction, MenuContainer> mMapContainerMenu = new HashMap<>();

    private State mState = State.Close;
    private Direction mMenuDirection;

    private ScrollState mScrollState = ScrollState.Idle;

    private final int mMinFlingVelocity;

    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;
    private OnScrollStateChangeCallback mOnScrollStateChangeCallback;
    private PullCondition mPullCondition;

    public BaseSwipeMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContainerContent = new ContentContainer(getContext());
        addView(mContainerContent);

        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    private MenuContainer getContainerMenuLeft()
    {
        if (mContainerMenuLeft == null)
        {
            mContainerMenuLeft = new MenuContainer(getContext())
            {
                @Override
                protected void onContentViewChanged(View view)
                {
                    super.onContentViewChanged(view);
                    if (view == null)
                    {
                        if (mState == State.OpenLeft)
                            setState(State.Close, false);

                        mMapContainerMenu.remove(Direction.Left);
                        mContainerMenuLeft = null;
                    }
                }
            };
            mMapContainerMenu.put(Direction.Left, mContainerMenuLeft);
            mContainerMenuLeft.setGravity(Gravity.LEFT);
            addView(mContainerMenuLeft);
        }
        return mContainerMenuLeft;
    }

    private MenuContainer getContainerMenuTop()
    {
        if (mContainerMenuTop == null)
        {
            mContainerMenuTop = new MenuContainer(getContext())
            {
                @Override
                protected void onContentViewChanged(View view)
                {
                    super.onContentViewChanged(view);
                    if (view == null)
                    {
                        if (mState == State.OpenTop)
                            setState(State.Close, false);

                        mMapContainerMenu.remove(Direction.Top);
                        mContainerMenuTop = null;
                    }
                }
            };
            mMapContainerMenu.put(Direction.Top, mContainerMenuTop);
            mContainerMenuTop.setGravity(Gravity.TOP);
            addView(mContainerMenuTop);
        }
        return mContainerMenuTop;
    }

    private MenuContainer getContainerMenuRight()
    {
        if (mContainerMenuRight == null)
        {
            mContainerMenuRight = new MenuContainer(getContext())
            {
                @Override
                protected void onContentViewChanged(View view)
                {
                    super.onContentViewChanged(view);
                    if (view == null)
                    {
                        if (mState == State.OpenRight)
                            setState(State.Close, false);

                        mMapContainerMenu.remove(Direction.Right);
                        mContainerMenuRight = null;
                    }
                }
            };
            mMapContainerMenu.put(Direction.Right, mContainerMenuRight);
            mContainerMenuRight.setGravity(Gravity.RIGHT);
            addView(mContainerMenuRight);
        }
        return mContainerMenuRight;
    }

    private MenuContainer getContainerMenuBottom()
    {
        if (mContainerMenuBottom == null)
        {
            mContainerMenuBottom = new MenuContainer(getContext())
            {
                @Override
                protected void onContentViewChanged(View view)
                {
                    super.onContentViewChanged(view);
                    if (view == null)
                    {
                        if (mState == State.OpenBottom)
                            setState(State.Close, false);

                        mMapContainerMenu.remove(Direction.Bottom);
                        mContainerMenuBottom = null;
                    }
                }
            };
            mMapContainerMenu.put(Direction.Bottom, mContainerMenuBottom);
            mContainerMenuBottom.setGravity(Gravity.BOTTOM);
            addView(mContainerMenuBottom);
        }
        return mContainerMenuBottom;
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
    public final View getContentView()
    {
        return mContainerContent.getContentView();
    }

    @Override
    public final View getMenuView(Direction direction)
    {
        final MenuContainer container = mMapContainerMenu.get(direction);
        if (container == null)
            return null;

        return container.getContentView();
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
                Log.i(SwipeMenu.class.getSimpleName(), "setScrollState:" + state);

            hideContainerMenuIfNeed();

            if (mOnScrollStateChangeCallback != null)
                mOnScrollStateChangeCallback.onScrollStateChanged(state, this);
        }
    }

    private void hideContainerMenuIfNeed()
    {
        if (mScrollState == ScrollState.Idle)
        {
            if (mState == State.Close && getScrollPercent() == 0)
            {
                for (MenuContainer item : mMapContainerMenu.values())
                {
                    item.setVisibility(INVISIBLE);
                }

                if (mIsDebug)
                    Log.i(SwipeMenu.class.getSimpleName(), "hide all menu container");
            }
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
            return false;

        final State oldState = mState;
        boolean changed = false;
        if (oldState != state)
        {
            Direction direction = null;
            if (state == State.Close)
                direction = stateToMenuDirection(oldState);
            else
                direction = stateToMenuDirection(state);

            final View view = getMenuView(direction);
            if (view == null)
                throw new IllegalArgumentException("Illegal state:" + state);

            setMenuDirection(direction);
            mState = state;

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "setState:" + state);

            updateLockEvent();
            updateViewByState(anim);

            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(state, this);

            changed = true;
        }

        if (!anim && !changed)
            updateViewByState(false);

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
                if (onSmoothScroll(boundCurrent, boundState))
                {
                    setScrollState(ScrollState.Fling);
                    invalidate();
                }
            } else
            {
                requestLayout();
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
            list.add(getChildAt(i));
        }

        for (View item : list)
        {
            if (item == mContainerContent)
                continue;

            final int childId = item.getId();
            if (childId == R.id.lib_swipemenu_content)
            {
                mContainerContent.setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_left)
            {
                getContainerMenuLeft().setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_top)
            {
                getContainerMenuTop().setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_right)
            {
                getContainerMenuRight().setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_bottom)
            {
                getContainerMenuBottom().setContentView(item);
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
        for (MenuContainer item : mMapContainerMenu.values())
        {
            if (item == child)
                throw new RuntimeException("you can not remove view this way");
        }
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

        final MenuContainer container = mMapContainerMenu.get(direction);
        if (container == null)
            throw new RuntimeException("MenuContainer not found for direction:" + direction);
        container.setVisibility(VISIBLE);

        if (mMenuDirection != direction)
        {
            mMenuDirection = direction;
            if (mIsDebug)
                Log.e(SwipeMenu.class.getSimpleName(), "setMenuDirection:" + direction);

            onMenuDirectionChanged(direction);
        }
    }

    /**
     * 返回菜显示方向
     *
     * @return
     */
    protected final Direction getMenuDirection()
    {
        return mMenuDirection;
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

    private void updateLockEvent()
    {
        final State state = mState;
        final boolean totalState = getContentBoundCurrent() == getContentBoundState(state);

        switch (state)
        {
            case Close:
                for (MenuContainer item : mMapContainerMenu.values())
                {
                    item.setLockEvent(true);
                }
                break;
            case OpenLeft:
                if (mContainerMenuLeft != null)
                    mContainerMenuLeft.setLockEvent(!totalState);
                break;
            case OpenTop:
                if (mContainerMenuTop != null)
                    mContainerMenuTop.setLockEvent(!totalState);
                break;
            case OpenRight:
                if (mContainerMenuRight != null)
                    mContainerMenuRight.setLockEvent(!totalState);
                break;
            case OpenBottom:
                if (mContainerMenuBottom != null)
                    mContainerMenuBottom.setLockEvent(!totalState);
                break;
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

    private int getContentBoundCurrent()
    {
        if (mMenuDirection == null)
            return 0;

        switch (mMenuDirection)
        {
            case Left:
            case Right:
                return mContainerContent.getLeft();
            case Top:
            case Bottom:
                return mContainerContent.getTop();
            default:
                throw new RuntimeException();
        }
    }

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
            ViewCompat.offsetLeftAndRight(mContainerContent, delta);
        else
            ViewCompat.offsetTopAndBottom(mContainerContent, delta);

        updateLockEvent();

        if (mOnViewPositionChangeCallback != null)
            mOnViewPositionChangeCallback.onViewPositionChanged(isDrag, this);
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

        int boundEnd = 0;
        if (Math.abs(velocity) > mMinFlingVelocity)
        {
            boundEnd = velocity > 0 ? boundMax : boundMin;
        } else
        {
            final int boundMiddle = (boundMin + boundMax) / 2;
            boundEnd = boundCurrent >= boundMiddle ? boundMax : boundMin;
        }

        final State state = getStateForBoundEnd(boundEnd);

        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "dealDragFinish:" + boundCurrent + "," + boundEnd + " " + state);

        if (!setState(state, true))
            updateViewByState(true);

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

        switch (mMenuDirection)
        {
            case Left:
            case Right:
                return view.getWidth();
            case Top:
            case Bottom:
                return view.getHeight();
            default:
                throw new RuntimeException();
        }
    }

    /**
     * 显示菜单方向发生变化
     *
     * @param direction
     */
    protected abstract void onMenuDirectionChanged(Direction direction);

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        measureChild(mContainerContent, widthMeasureSpec, heightMeasureSpec);

        int width = mContainerContent.getMeasuredWidth();
        int height = mContainerContent.getMeasuredHeight();

        final int widthMenuSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int heightMenuSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        final int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            if (child == mContainerContent)
                continue;

            child.measure(widthMenuSpec, heightMenuSpec);
        }

        width = Utils.getMeasureSize(width, widthMeasureSpec);
        height = Utils.getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    /**
     * view是否处于空闲状态（静止且未被拖动状态）
     *
     * @return
     */
    protected abstract boolean isViewIdle();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final State state = mState;

        // content
        int left = 0;
        int top = 0;

        if (isViewIdle())
        {
            left = getContentBoundState(state);
            top = 0;
        } else
        {
            left = mContainerContent.getLeft();
            top = mContainerContent.getTop();
        }
        mContainerContent.layout(left, top,
                left + mContainerContent.getMeasuredWidth(), top + mContainerContent.getMeasuredHeight());

        float maxZ = 0;

        // left
        if (mContainerMenuLeft != null)
        {
            mContainerMenuLeft.layout(0, 0,
                    mContainerMenuLeft.getMeasuredWidth(), mContainerMenuLeft.getMeasuredHeight());

            final float menuZ = ViewCompat.getZ(mContainerMenuLeft);
            if (menuZ > maxZ)
                maxZ = menuZ;
        }

        // top
        if (mContainerMenuTop != null)
        {
            mContainerMenuTop.layout(0, 0,
                    mContainerMenuTop.getMeasuredWidth(), mContainerMenuTop.getMeasuredHeight());

            final float menuZ = ViewCompat.getZ(mContainerMenuTop);
            if (menuZ > maxZ)
                maxZ = menuZ;
        }

        // right
        if (mContainerMenuRight != null)
        {
            mContainerMenuRight.layout(0, 0,
                    mContainerMenuRight.getMeasuredWidth(), mContainerMenuRight.getMeasuredHeight());

            final float menuZ = ViewCompat.getZ(mContainerMenuRight);
            if (menuZ > maxZ)
                maxZ = menuZ;
        }

        // bottom
        if (mContainerMenuBottom != null)
        {
            mContainerMenuBottom.layout(0, 0,
                    mContainerMenuBottom.getMeasuredWidth(), mContainerMenuBottom.getMeasuredHeight());

            final float menuZ = ViewCompat.getZ(mContainerMenuBottom);
            if (menuZ > maxZ)
                maxZ = menuZ;
        }

        if (ViewCompat.getZ(mContainerContent) <= maxZ)
            ViewCompat.setZ(mContainerContent, maxZ + 1);

        updateLockEvent();
        hideContainerMenuIfNeed();
    }
}
