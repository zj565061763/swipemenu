package com.sd.swipemenu;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mSwipeMenu = findViewById(R.id.swipemenu);

        mSwipeMenu.setDebug(true);

        // 设置状态变化回调
        mSwipeMenu.setOnStateChangeCallback(new SwipeMenu.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(SwipeMenu.State state, SwipeMenu swipeMenu)
            {
                Log.e(TAG, "onStateChanged:" + state);
            }
        });
        // 设置view位置变化回调
        mSwipeMenu.setOnViewPositionChangeCallback(new SwipeMenu.OnViewPositionChangeCallback()
        {
            @Override
            public void onViewPositionChanged(int delta, boolean isDrag, SwipeMenu swipeMenu)
            {

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
    }

    @Override
    public void onClick(View v)
    {
        Log.i(TAG, "onClick:" + v);
        Toast.makeText(this, "onClick:" + v, Toast.LENGTH_SHORT).show();
    }
}
