package personalprojects.seakyluo.randommenu.Views;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ScalableImageView extends android.support.v7.widget.AppCompatImageView {
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector scaler;
    private boolean pinching = false;
    public ScalableImageView(Context context) {
        super(context);
        scaler = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
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
        Log.d("fuck", "MotionEvent: " + event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                pinching = true;
                break;
            case MotionEvent.ACTION_UP:
                if (!pinching)
                    return performClick();
            default:
                pinching = false;
                break;
        }
        return scaler.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
