package com.sd.lib.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.sd.lib.swipemenu.gesture.FTouchHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class BaseSwipeMenu extends ViewGroup implements SwipeMenu
{
    protected boolean mIsDebug;

    private final ContentContainer mContentContainer;
    private final Map<Direction, MenuContainer> mMapMenuContainer = new HashMap<>();

    private Mode mMode = Mode.Overlay;
    private State mState = State.Close;

    private Direction mMenuDirection;
    private DirectionHandler mDirectionHandler;

    private ScrollState mScrollState = ScrollState.Idle;

    private int mContentContainerLeft;
    private int mContentContainerTop;

    private OnStateChangeCallback mOnStateChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;
    private OnScrollStateChangeCallback mOnScrollStateChangeCallback;

    private List<PullCondition> mListPullCondition;

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

        mDirectionHandler = new NullHandler();
        mDirectionHandler.init();
    }

    private MenuContainer getOrCreateMenuContainer(final Direction direction)
    {
        if (direction == null)
            throw new IllegalArgumentException("direction is null when getOrCreateMenuContainer()");

        MenuContainer container = mMapMenuContainer.get(direction);
        if (container == null)
        {
            container = new MenuContainer(direction, getContext())
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
            addView(container);
        }
        return container;
    }

    private void removeMenuContainer(Direction direction)
    {
        final MenuContainer container = mMapMenuContainer.remove(direction);
        if (container == null)
            throw new RuntimeException("MenuContainer was not found for direction: " + direction + " when removeMenuContainer()");

        if (direction == getMenuDirection())
            setState(State.Close, false);

        removeView(container);
    }

    @Override
    public void setDebug(boolean debug)
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
    public void addPullCondition(PullCondition condition)
    {
        if (condition == null)
            return;

        if (mListPullCondition == null)
            mListPullCondition = new CopyOnWriteArrayList<>();

        if (mListPullCondition.contains(condition))
            return;

        mListPullCondition.add(condition);

        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), " + addPullCondition " + mListPullCondition.size() + " : " + condition);
    }

    @Override
    public void removePullCondition(PullCondition condition)
    {
        if (mListPullCondition != null)
        {
            if (mListPullCondition.remove(condition))
            {
                if (mIsDebug)
                    Log.i(SwipeMenu.class.getSimpleName(), " - removePullCondition " + mListPullCondition.size() + " : " + condition);
            }
        }
    }

    @Override
    public void clearPullCondition()
    {
        if (mListPullCondition != null)
        {
            mListPullCondition.clear();
            mListPullCondition = null;
        }
    }

    protected boolean checkPullCondition(Direction pullDirection, MotionEvent event)
    {
        if (pullDirection == null)
            throw new IllegalArgumentException("pullDirection is null when checkPullCondition()");

        if (mListPullCondition != null)
        {
            for (PullCondition item : mListPullCondition)
            {
                if (!item.canPull(this, pullDirection, event))
                    return false;
            }
        }

        return true;
    }

    @Override
    public final void setContentView(View view)
    {
        mContentContainer.setContentView(view);
    }

    @Override
    public final void setMenuView(Direction direction, View view)
    {
        if (direction == null)
            throw new IllegalArgumentException("direction is null when setMenuView()");

        if (view == null && !mMapMenuContainer.containsKey(direction))
            return;

        getOrCreateMenuContainer(direction).setContentView(view);
    }

    @Override
    public final void setMode(Mode mode)
    {
        if (mode == null)
            throw new IllegalArgumentException("mode is null when setMode()");

        if (mMode != mode)
        {
            mMode = mode;
            setState(getState(), false);
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

    @Override
    public float getScrollPercent()
    {
        return mDirectionHandler.getScrollPercent();
    }

    @Override
    public boolean setState(State state, boolean anim)
    {
        if (state == null)
            throw new IllegalArgumentException("state is null when setState()");

        if (getContentView() == null)
        {
            state = State.Close;
            anim = false;
        }

        final State stateOld = mState;
        final boolean changed = stateOld != state;

        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "setState:" + stateOld + " -> " + state);

        if (changed)
        {
            if (state != State.Close)
            {
                final Direction direction = stateToMenuDirection(state);
                if (direction == null)
                    throw new RuntimeException("direction is null for state: " + state);

                if ((mDirectionHandler instanceof NoneNullHandler) && mDirectionHandler.mDirection != direction)
                    mDirectionHandler.updateView(State.Close, false);

                setMenuDirection(direction);
            }

            mState = state;

            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(this, stateOld, state);
        }

        if (!changed && state == State.Close && mScrollState == ScrollState.Fling)
            anim = false;

        updateView(state, anim);

        return changed;
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
                throw new RuntimeException("Unexpected state: " + state);
        }
    }

    /**
     * 设置菜单显示方向
     *
     * @param direction
     */
    protected final void setMenuDirection(Direction direction)
    {
        if (mMenuDirection != direction)
        {
            mMenuDirection = direction;

            if (mIsDebug)
                Log.e(SwipeMenu.class.getSimpleName(), "setMenuDirection:" + direction);

            if (direction == null)
            {
                if (!isViewIdle())
                    throw new RuntimeException("direction can not be set to null when view is not idle");

                if (mState != State.Close)
                    throw new RuntimeException("direction can not be set to null when state is:" + mState);

                mDirectionHandler = new NullHandler();
            } else if (direction == Direction.Left)
            {
                mDirectionHandler = new LeftHandler(direction);
            } else if (direction == Direction.Top)
            {
                mDirectionHandler = new TopHandler(direction);
            } else if (direction == Direction.Right)
            {
                mDirectionHandler = new RightHandler(direction);
            } else if (direction == Direction.Bottom)
            {
                mDirectionHandler = new BottomHandler(direction);
            } else
            {
                throw new RuntimeException("Unexpected direction: " + direction);
            }
            mDirectionHandler.init();

            onMenuDirectionChanged(direction);
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final List<View> list = new ArrayList<>(5);
        for (int i = 0; i < getChildCount(); i++)
        {
            final View child = getChildAt(i);
            if (child != mContentContainer)
                list.add(child);
        }

        for (View item : list)
        {
            final int childId = item.getId();
            if (childId == R.id.lib_swipemenu_content)
            {
                removeView(item);
                setContentView(item);
            } else if (childId == R.id.lib_swipemenu_menu_left)
            {
                removeView(item);
                setMenuView(Direction.Left, item);
            } else if (childId == R.id.lib_swipemenu_menu_top)
            {
                removeView(item);
                setMenuView(Direction.Top, item);
            } else if (childId == R.id.lib_swipemenu_menu_right)
            {
                removeView(item);
                setMenuView(Direction.Right, item);
            } else if (childId == R.id.lib_swipemenu_menu_bottom)
            {
                removeView(item);
                setMenuView(Direction.Bottom, item);
            } else
            {
                throw new RuntimeException("Illegal child in swipe menu:" + item);
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
     * 根据状态刷新View
     *
     * @param state
     * @param anim
     */
    private void updateView(State state, boolean anim)
    {
        mDirectionHandler.updateView(state, anim);
    }

    /**
     * 移动View
     *
     * @param delta
     * @param isDrag
     */
    protected final void moveView(int delta, boolean isDrag)
    {
        mDirectionHandler.moveView(delta, isDrag);
    }

    /**
     * 拖动结束后需要执行的逻辑
     *
     * @param velocity
     */
    protected final void dealDragFinish(int velocity)
    {
        mDirectionHandler.dealDragFinish(velocity);
    }

    /**
     * 设置滚动状态
     *
     * @param state
     */
    protected final void setScrollState(ScrollState state)
    {
        if (state == null)
            throw new NullPointerException("state is null when setScrollState()");

        final ScrollState old = mScrollState;
        if (old != state)
        {
            mScrollState = state;

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "setScrollState:" + state);

            if (state == ScrollState.Idle && mState == State.Close)
            {
                setMenuDirection(null);

                if (mIsDebug)
                    Log.i(SwipeMenu.class.getSimpleName(), "requestLayout() when Idle and Close");
                requestLayout();
            }

            if (mOnScrollStateChangeCallback != null)
                mOnScrollStateChangeCallback.onScrollStateChanged(this, old, state);
        }
    }

    /**
     * 返回View可以滚动的最大距离
     *
     * @return
     */
    protected final int getMaxScrollDistance()
    {
        return mDirectionHandler.getContentBoundSize();
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
    protected abstract boolean smoothScroll(int start, int end);

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        mContentContainer.measure(getChildMeasureSpec(widthMeasureSpec, 0, LayoutParams.MATCH_PARENT),
                getChildMeasureSpec(heightMeasureSpec, 0, LayoutParams.MATCH_PARENT));

        int width = mContentContainer.getMeasuredWidth();
        int height = mContentContainer.getMeasuredHeight();

        final int widthMenuSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int heightMenuSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        for (MenuContainer item : mMapMenuContainer.values())
        {
            item.measure(widthMenuSpec, heightMenuSpec);
        }

        width = getMeasureSize(width, widthMeasureSpec);
        height = getMeasureSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    private static int getMeasureSize(int size, int measureSpec)
    {
        int result = 0;

        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode)
        {
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case View.MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case View.MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mIsDebug)
            Log.i(SwipeMenu.class.getSimpleName(), "onLayout");

        layoutInternal(mState);
    }

    private void layoutInternal(State state)
    {
        final boolean isViewIdle = isViewIdle();

        int left = 0;
        int top = 0;

        // ---------- Content ----------
        if (isViewIdle)
        {
            if (state == State.Close)
            {
                left = 0;
                top = 0;
            } else if (state == State.OpenLeft || state == State.OpenRight)
            {
                left = mDirectionHandler.getContentBound(state);
                top = 0;
            } else if (state == State.OpenTop || state == State.OpenBottom)
            {
                left = 0;
                top = mDirectionHandler.getContentBound(state);
            } else
            {
                throw new RuntimeException("Unexpected state: " + state);
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

        notifyViewPositionChangeIfNeed(false);
    }

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
                mOnViewPositionChangeCallback.onViewPositionChanged(this, left, top, isDrag);
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

    //---------- DirectionHandler start ----------

    private abstract class DirectionHandler
    {
        protected final Direction mDirection;
        protected final MenuContainer mMenuContainer;

        public DirectionHandler(Direction direction)
        {
            mDirection = direction;
            mMenuContainer = mMapMenuContainer.get(direction);
        }

        public abstract void init();

        public final void updateView(State state, boolean anim)
        {
            final int boundCurrent = getContentBoundCurrent();
            final int boundState = getContentBound(state);

            if (boundCurrent != boundState)
            {
                if (mIsDebug)
                    Log.i(SwipeMenu.class.getSimpleName(), "updateView " + state + ":" + boundCurrent + "," + boundState + " anim:" + anim + " (" + getClass().getSimpleName() + ")");

                abortAnimation();

                if (anim)
                {
                    smoothScroll(boundCurrent, boundState);
                } else
                {
                    if (state == mState)
                    {
                        if (mIsDebug)
                            Log.i(SwipeMenu.class.getSimpleName(), "state == mState requestLayout()");

                        requestLayout();
                    } else
                    {
                        layoutInternal(state);
                    }
                }
            }
        }

        public final void moveView(int delta, boolean isDrag)
        {
            if (delta == 0)
                return;

            final int boundCurrent = getContentBoundCurrent();
            final int boundOpen = getContentBoundOpen();
            final int boundClose = getContentBoundClose();

            delta = FTouchHelper.getLegalDelta(boundCurrent, Math.min(boundOpen, boundClose), Math.max(boundOpen, boundClose), delta);
            if (delta == 0)
                return;

            moveViewImpl(delta, isDrag);
            notifyViewPositionChangeIfNeed(isDrag);
        }

        public final void dealDragFinish(int velocity)
        {
            final int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 10;

            State state = null;
            if (Math.abs(velocity) > minFlingVelocity)
            {
                state = getStateForDragFinishLegalVelocity(velocity);
            } else
            {
                state = getStateForDragFinish();
            }

            if (mIsDebug)
                Log.i(SwipeMenu.class.getSimpleName(), "dealDragFinish try state:" + state);

            setState(state, true);
        }

        public final float getScrollPercent()
        {
            final int size = getContentBoundSize();
            if (size == 0)
                return 0;

            final int delta = Math.abs(getContentBoundCurrent() - getContentBoundClose());
            final float percent = delta / (float) size;

            return percent;
        }

        public final int getContentBoundSize()
        {
            return Math.abs(getContentBoundOpen() - getContentBoundClose());
        }

        public final int getContentBound(State state)
        {
            if (state == State.Close)
                return getContentBoundClose();

            if (state == getStateOpen())
                return getContentBoundOpen();

            throw new RuntimeException("Illegal state when invoke getContentBound() : " + state);
        }

        protected abstract int getContentBoundCurrent();

        protected abstract int getContentBoundOpen();

        protected abstract int getContentBoundClose();

        protected abstract void moveViewImpl(int delta, boolean isDrag);

        protected abstract State getStateOpen();

        protected abstract State getStateForDragFinishLegalVelocity(int velocity);

        protected final State getStateForDragFinish()
        {
            final int boundCurrent = getContentBoundCurrent();
            final int deltaOpen = Math.abs(boundCurrent - getContentBoundOpen());
            final int deltaClose = Math.abs(boundCurrent - getContentBoundClose());

            return deltaOpen < deltaClose ? getStateOpen() : State.Close;
        }
    }

    private abstract class NoneNullHandler extends DirectionHandler
    {
        public NoneNullHandler(Direction direction)
        {
            super(direction);
            if (direction == null)
                throw new IllegalArgumentException("direction is null when create DirectionHandler:" + getClass().getSimpleName());
        }

        @Override
        public void init()
        {
            if (mMenuContainer == null)
                throw new RuntimeException("MenuContainer was not found for direction:" + mDirection);

            if (mMenuContainer.getContentView() == null)
                throw new RuntimeException("MenuContainer contentView was not found for direction:" + mDirection);

            for (MenuContainer item : mMapMenuContainer.values())
            {
                item.setVisibility(item == mMenuContainer ? VISIBLE : INVISIBLE);
            }
        }

        @Override
        protected int getContentBoundOpen()
        {
            return mMenuContainer.getContentBoundOpen();
        }

        @Override
        protected int getContentBoundClose()
        {
            return mMenuContainer.getContentBoundClose();
        }
    }

    private abstract class HorizontalHandler extends NoneNullHandler
    {
        public HorizontalHandler(Direction direction)
        {
            super(direction);
            if (!direction.isHorizontal())
                throw new IllegalArgumentException("direction must be horizontal");
        }

        @Override
        public final int getContentBoundCurrent()
        {
            return mContentContainer.getLeft();
        }

        @Override
        protected final void moveViewImpl(int delta, boolean isDrag)
        {
            ViewCompat.offsetLeftAndRight(mContentContainer, delta);

            if (mMode == Mode.Drawer)
            {
                final View view = mMenuContainer;
                if (view != null)
                    ViewCompat.offsetLeftAndRight(view, delta);
            }
        }
    }

    private abstract class VerticalHandler extends NoneNullHandler
    {
        public VerticalHandler(Direction direction)
        {
            super(direction);
            if (!direction.isVertical())
                throw new IllegalArgumentException("direction must be vertical");
        }

        @Override
        public final int getContentBoundCurrent()
        {
            return mContentContainer.getTop();
        }

        @Override
        protected final void moveViewImpl(int delta, boolean isDrag)
        {
            ViewCompat.offsetTopAndBottom(mContentContainer, delta);

            if (mMode == Mode.Drawer)
            {
                final View view = mMenuContainer;
                if (view != null)
                    ViewCompat.offsetTopAndBottom(view, delta);
            }
        }
    }

    private class LeftHandler extends HorizontalHandler
    {
        public LeftHandler(Direction direction)
        {
            super(direction);
        }

        @Override
        protected State getStateOpen()
        {
            return State.OpenLeft;
        }

        @Override
        protected State getStateForDragFinishLegalVelocity(int velocity)
        {
            return velocity > 0 ? getStateOpen() : State.Close;
        }
    }

    private class TopHandler extends VerticalHandler
    {
        public TopHandler(Direction direction)
        {
            super(direction);
        }

        @Override
        protected State getStateOpen()
        {
            return State.OpenTop;
        }

        @Override
        protected State getStateForDragFinishLegalVelocity(int velocity)
        {
            return velocity > 0 ? getStateOpen() : State.Close;
        }
    }

    private class RightHandler extends HorizontalHandler
    {
        public RightHandler(Direction direction)
        {
            super(direction);
        }

        @Override
        protected State getStateOpen()
        {
            return State.OpenRight;
        }

        @Override
        protected State getStateForDragFinishLegalVelocity(int velocity)
        {
            return velocity < 0 ? getStateOpen() : State.Close;
        }
    }

    private class BottomHandler extends VerticalHandler
    {
        public BottomHandler(Direction direction)
        {
            super(direction);
        }

        @Override
        protected State getStateOpen()
        {
            return State.OpenBottom;
        }

        @Override
        protected State getStateForDragFinishLegalVelocity(int velocity)
        {
            return velocity < 0 ? getStateOpen() : State.Close;
        }
    }

    private class NullHandler extends DirectionHandler
    {
        public NullHandler()
        {
            super(null);
        }

        @Override
        public void init()
        {
            for (MenuContainer item : mMapMenuContainer.values())
            {
                item.setVisibility(INVISIBLE);
            }
        }

        @Override
        public int getContentBoundCurrent()
        {
            return 0;
        }

        @Override
        protected int getContentBoundOpen()
        {
            return 0;
        }

        @Override
        protected int getContentBoundClose()
        {
            return 0;
        }

        @Override
        protected void moveViewImpl(int delta, boolean isDrag)
        {
            throw new RuntimeException();
        }

        @Override
        protected State getStateOpen()
        {
            throw new RuntimeException();
        }

        @Override
        protected State getStateForDragFinishLegalVelocity(int velocity)
        {
            throw new RuntimeException();
        }
    }
    //---------- DirectionHandler end ----------
}
