package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class TabViewPager extends ViewPager {
    private boolean swipeable = true;
    private MyScroller scroller;

    public TabViewPager(Context context) {
        super(context);
        setMyScroller();
    }
    public TabViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    public void setSwipeable(boolean swipeable){
        this.swipeable = swipeable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeable && super.onInterceptTouchEvent(event);
    }

    //down one is added for smooth scrolling

    private void setMyScroller() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, this.scroller = new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}
