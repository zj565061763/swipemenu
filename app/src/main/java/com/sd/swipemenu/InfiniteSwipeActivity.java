package com.sd.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sd.lib.swipemenu.SwipeMenu;
import com.sd.swipemenu.utils.FLoopList;

import java.util.ArrayList;
import java.util.List;

public class InfiniteSwipeActivity extends AppCompatActivity
{
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
                    data = mLoopList.previous(1);
                    break;
                case Bottom:
                    data = mLoopList.next(1);
                    break;
                case Center:
                    data = mLoopList.current();
                    break;
            }

            if (textView != null)
                textView.setText(data);
        }

        @Override
        protected void onMoveIndex(Direction direction)
        {
            if (direction == Direction.Top)
                mLoopList.movePrevious(1);
            else if (direction == Direction.Bottom)
                mLoopList.moveNext(1);
        }
    };

    private final FLoopList<String> mLoopList = new FLoopList<String>()
    {
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
