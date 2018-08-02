package com.fanwe.lib.swipemenu.adapter;

import android.content.Context;
import android.view.View;

import com.fanwe.lib.swipemenu.SwipeMenu;

public interface SwipeMenuAdapter
{
    View onCreateMenuView(Context context);

    void onBindData(int position, View convertView, SwipeMenu swipeMenu);
}
