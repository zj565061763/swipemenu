package com.fanwe.lib.swipemenu.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.fanwe.lib.swipemenu.SwipeMenu;

public interface SwipeMenuAdapter
{
    View onCreateMenuView(int position, ViewGroup parent);

    void onBindData(int position, View contentView, View menuView, SwipeMenu swipeMenu);

    SwipeMenuHolder getSwipeMenuHolder();
}
