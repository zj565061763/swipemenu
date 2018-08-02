package com.fanwe.swipemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fanwe.lib.swipemenu.FSwipeMenu;
import com.fanwe.lib.swipemenu.SwipeMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private FSwipeMenu mSwipeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeMenu = findViewById(R.id.swipemenu);

        // 设置菜单在左边打开，默认右边
        mSwipeMenu.setMenuGravity(SwipeMenu.Gravity.Left);
        // 设置状态变化回调
        mSwipeMenu.setOnStateChangeCallback(new SwipeMenu.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(SwipeMenu.State state, SwipeMenu swipeMenu)
            {
                Log.i(TAG, "onStateChanged:" + state);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_cancel:
                // 关闭菜单
                mSwipeMenu.close();
                Toast.makeText(this, "click cancel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_delete:
                Toast.makeText(this, "click delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fl_content:
                Toast.makeText(this, "click content", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
