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
        bindDataDefault();

        initSwipeMenu();
    }

    private void initSwipeMenu()
    {
        mSwipeMenu.setMode(SwipeMenu.Mode.Drawer);
        mSwipeMenu.setOnScrollStateChangeCallback(new SwipeMenu.OnScrollStateChangeCallback()
        {
            @Override
            public void onScrollStateChanged(SwipeMenu swipeMenu, SwipeMenu.ScrollState oldState, SwipeMenu.ScrollState newState)
            {
                if (newState == SwipeMenu.ScrollState.Idle)
                {
                    switch (swipeMenu.getState())
                    {
                        case OpenTop:
                            tv_content.setText(mLoopList.previous(1));
                            mLoopList.movePrevious(1);
                            break;
                        case OpenBottom:
                            tv_content.setText(mLoopList.next(1));
                            mLoopList.moveNext(1);
                            break;
                        default:
                            return;
                    }

                    swipeMenu.setState(SwipeMenu.State.Close, false);
                    bindDataDefault();
                }
            }
        });
    }

    private void bindDataDefault()
    {
        tv_menu_top.setText(mLoopList.previous(1));
        tv_menu_bottom.setText(mLoopList.next(1));
        tv_content.setText(mLoopList.current());
    }

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
