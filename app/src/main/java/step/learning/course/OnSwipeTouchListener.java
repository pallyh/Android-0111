package step.learning.course;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        this.gestureDetector = new GestureDetector(context,new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
    public void onSwipeRight()  {}
    public void onSwipeLeft()   {}
    public void onSwipeTop()    {}
    public void onSwipeBottom() {}

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener{

        private static final int MIN_DISTANCE = 100;
        private static final int MIN_VELOCITY = 100;
        private static final float COEFFICIENT = 1.5f;
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            float deltaX = e2.getX()-e1.getX();
            float deltaY = e2.getY()-e1.getY();
            if (Math.abs(deltaX) > COEFFICIENT * Math.abs(deltaY) ){    //Horizontal
                //in horizontal mod analyze X and ignore Y
                if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY ){
                    if (deltaX > 0 ){
                        onSwipeRight();
                    }else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }else if(Math.abs(deltaY) > COEFFICIENT * Math.abs(deltaX) ){   //Vertical
                if (Math.abs(deltaY) > MIN_DISTANCE && Math.abs(velocityY) > MIN_VELOCITY ){
                    if (deltaX > 0 ){
                        onSwipeBottom();
                    }else {
                        onSwipeTop();
                    }
                    result = true;
                }
            }
            return result;
        }
    }
}
