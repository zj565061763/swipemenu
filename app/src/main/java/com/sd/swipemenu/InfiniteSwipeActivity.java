package com.sd.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.sd.lib.swipemenu.SwipeMenu;
import com.sd.lib.swipemenu.ext.InfiniteSwipeMenuHandler;
import com.sd.swipemenu.utils.FLoopList;

import java.util.ArrayList;
import java.util.List;

public class InfiniteSwipeActivity extends AppCompatActivity
{
    public static final String TAG = InfiniteSwipeActivity.class.getSimpleName();

    private SwipeMenu mSwipeMenu;
    private TextView tv_menu_top, tv_menu_bottom, tv_content;

    private final List<String> mListModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinite_swipe);
        mSwipeMenu = findViewById(R.id.swipemenu);
        tv_menu_top = findViewById(R.id.tv_menu_top);
        tv_menu_bottom = findViewById(R.id.tv_menu_bottom);
        tv_content = findViewById(R.id.tv_content);

        initData();
        mLoopList.setIndex(0);

        mSwipeMenu.setDebug(true);
        mSwipeMenu.setMode(SwipeMenu.Mode.Drawer);

        mInfiniteSwipeMenuHandler.setSwipeMenu(mSwipeMenu);
        mInfiniteSwipeMenuHandler.bindData(InfiniteSwipeMenuHandler.Direction.Center, InfiniteSwipeMenuHandler.Direction.Center);
        mInfiniteSwipeMenuHandler.bindData(InfiniteSwipeMenuHandler.Direction.Top, InfiniteSwipeMenuHandler.Direction.Top);
        mInfiniteSwipeMenuHandler.bindData(InfiniteSwipeMenuHandler.Direction.Bottom, InfiniteSwipeMenuHandler.Direction.Bottom);
    }

    private final InfiniteSwipeMenuHandler mInfiniteSwipeMenuHandler = new InfiniteSwipeMenuHandler()
    {
        @Override
        protected void onBindData(Direction viewDirection, Direction dataDirection)
        {
            Log.i(TAG, "onBindData:" + viewDirection + " " + dataDirection);

            TextView textView = null;
            switch (viewDirection)
            {
                case Top:
                    textView = tv_menu_top;
                    break;
                case Bottom:
                    textView = tv_menu_bottom;
                    break;
                case Center:
                    textView = tv_content;
                    break;
            }

            String data = null;
            switch (dataDirection)
            {
                case Top:
                    data = mLoopList.getPrevious(1);
                    break;
                case Bottom:
                    data = mLoopList.getNext(1);
                    break;
                case Center:
                    data = mLoopList.getCurrent();
                    break;
            }

            if (textView != null)
                textView.setText(data);
        }

        @Override
        protected void onMoveIndex(Direction direction)
        {
            if (direction == Direction.Top)
                mLoopList.moveIndexPrevious(1);
            else if (direction == Direction.Bottom)
                mLoopList.moveIndexNext(1);
        }

        @Override
        protected void onPageChanged(Direction direction)
        {
            Log.i(TAG, "onPageChanged:" + direction);
        }
    };

    private final FLoopList<String> mLoopList = new FLoopList<String>()
    {
        @Override
        protected void onIndexChanged(int oldIndex, int newIndex)
        {
            super.onIndexChanged(oldIndex, newIndex);
            Log.i(TAG, "onIndexChanged:" + newIndex);
        }

        @Override
        protected int size()
        {
            return mListModel.size();
        }

        @Override
        protected String get(int index)
        {
            return mListModel.get(index);
        }
    };

    private void initData()
    {
        mListModel.clear();
        for (int i = 0; i < 5; i++)
        {
            mListModel.add(String.valueOf(i));
        }
    }
}
