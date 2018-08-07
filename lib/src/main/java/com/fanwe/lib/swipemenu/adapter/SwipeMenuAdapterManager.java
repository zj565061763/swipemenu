package com.fanwe.lib.swipemenu.adapter;

import java.util.HashMap;
import java.util.Map;

public class SwipeMenuAdapterManager
{
    private final Map<Integer, SwipeMenuInfo> mMapInfo = new HashMap<>();



    private static class SwipeMenuInfo
    {
        public boolean isOpened;
    }
}
