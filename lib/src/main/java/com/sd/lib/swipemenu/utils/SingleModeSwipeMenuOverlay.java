/*
 * Copyright (C) 2017 Sunday (https://github.com/zj565061763)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sd.lib.swipemenu.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sd.lib.swipemenu.SwipeMenu;

public class SingleModeSwipeMenuOverlay extends SwipeMenuOverlay
{
    private final int[] mLocation = new int[2];
    private final Rect mRect = new Rect();

    public SingleModeSwipeMenuOverlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private void closeIfNeed(MotionEvent event)
    {
        final int eventX = (int) event.getRawX();
        final int eventY = (int) event.getRawY();

        foreach(new ForeachCallback()
        {
            @Override
            public boolean onNext(SwipeMenu swipeMenu)
            {
                if (swipeMenu.getState() != SwipeMenu.State.Close)
                {
                    final View view = (View) swipeMenu;
                    view.getLocationOnScreen(mLocation);

                    mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());

                    if (!mRect.contains(eventX, eventY))
                        swipeMenu.setState(SwipeMenu.State.Close, true);
                }

                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        closeIfNeed(ev);
        return super.onInterceptTouchEvent(ev);
    }
}
