package com.sd.swipemenu;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sd.lib.swipemenu.FSwipeMenu;
import com.sd.lib.swipemenu.SwipeMenu;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = SimpleActivity.class.getSimpleName();

    private FSwipeMenu mSwipeMenu;
    private View btn_follow_content;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mSwipeMenu = findViewById(R.id.swipemenu);
        btn_follow_content = findViewById(R.id.btn_follow_content);

        mSwipeMenu.setDebug(true);

        // 设置抽屉拉开模式
        mSwipeMenu.setMode(SwipeMenu.Mode.Drawer);

        // 设置状态变化回调
        mSwipeMenu.setOnStateChangeCallback(new SwipeMenu.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(SwipeMenu.State oldState, SwipeMenu.State newState, SwipeMenu swipeMenu)
            {
                Log.e(TAG, "onStateChanged:" + oldState + "----->" + newState);
            }
        });
        // 设置view位置变化回调
        mSwipeMenu.setOnViewPositionChangeCallback(new SwipeMenu.OnViewPositionChangeCallback()
        {
            private int mLastLeft;
            private int mLastTop;

            @Override
            public void onViewPositionChanged(int left, int top, boolean isDrag, SwipeMenu swipeMenu)
            {
                final int deltaLeft = left - mLastLeft;
                final int deltaTop = top - mLastTop;

                mLastLeft = left;
                mLastTop = top;

                ViewCompat.offsetLeftAndRight(btn_follow_content, deltaLeft);
                ViewCompat.offsetTopAndBottom(btn_follow_content, deltaTop);
            }
        });
        // 设置滚动状态变化回调
        mSwipeMenu.setOnScrollStateChangeCallback(new SwipeMenu.OnScrollStateChangeCallback()
        {
            @Override
            public void onScrollStateChanged(SwipeMenu.ScrollState state, SwipeMenu swipeMenu)
            {
                Log.i(TAG, "onScrollStateChanged:" + state);
            }
        });

        btn_follow_content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(SimpleActivity.this, "btn_follow_content", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        Log.i(TAG, "onClick:" + v);
        Toast.makeText(this, "onClick:" + v, Toast.LENGTH_SHORT).show();
    }
}
