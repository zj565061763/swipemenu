package com.fanwe.lib.swipemenu.adapter;

import android.content.Context;
import android.view.View;

import com.fanwe.lib.swipemenu.SwipeMenu;

public interface SwipeMenuAdapter
{
    View onCreateMenuView(int position, Context context);

    void onBindData(int position, View contentView, View menuView, SwipeMenu swipeMenu);
}
