package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ScalableImageView extends android.support.v7.widget.AppCompatImageView {
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;
    public ScalableImageView(Context context) {
        super(context);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor * detector.getScaleFactor(), 5.0f));
                setScaleX(mScaleFactor);
                setScaleY(mScaleFactor);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) return performClick();
        return mScaleGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
