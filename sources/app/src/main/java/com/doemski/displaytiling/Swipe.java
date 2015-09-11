package com.doemski.displaytiling;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class Swipe{
    private boolean isSwipeOut;
    private Direction direction;
    private Vector2d swipeStartPoint, swipeEndPoint;
    boolean swipeInProgress;
    Context context;

    private static final String EXTRA_ISSWIPEOUT = "com.doemski.displaytiling.extra.ISSWIPEOUT";
    private static final String EXTRA_DIRECTION = "com.doemski.displaytiling.extra.DIRECTION";
    private static final String EXTRA_ANGLE = "com.doemski.displaytiling.extra.ANGLE";


    public Swipe(){
        isSwipeOut=false;
    }

    public Swipe(Context context, boolean isSwipeOut, Direction direction, Vector2d swipeStartPoint, Vector2d swipeEndPoint){
        this.context = context;
        this.isSwipeOut = isSwipeOut;
        this.direction = direction;
        this.swipeStartPoint = swipeStartPoint;
        this.swipeEndPoint = swipeEndPoint;
        swipeInProgress = false;
        fireIntent();
    }

    public Swipe(Context context, boolean isSwipeOut, Direction direction, Vector2d swipeStartPoint){
        this.context = context;
        this.isSwipeOut = isSwipeOut;
        this.direction = direction;
        this.swipeStartPoint = swipeStartPoint;
        swipeInProgress = true;
    }

    public float getAngle(){
        if(!swipeInProgress) {
            return swipeStartPoint.angle(swipeEndPoint, direction);
        } else {
            return 0.0f;//TODO:Add Exception
        }
    }

    public void setSwipeEndPoint(Vector2d swipeEndPoint){
        this.swipeEndPoint = swipeEndPoint;
        swipeInProgress = false;
        fireIntent();
    }

    public boolean inProgress(){
        return swipeInProgress;
    }

    public Direction getDirection(){
        return direction;
    }

    private String getDirectionAsString(){
        if(direction == Direction.LEFT){
            return "LEFT";
        }
        if(direction == Direction.RIGHT){
            return "RIGHT";
        }
        if(direction == Direction.UP){
            return "UP";
        }
        if(direction == Direction.DOWN){
            return "DOWN";
        }
        return "ERRORROROROROR";
    }

    public boolean isSwipeSimilar(Swipe swipe){
        if(swipe.getDirection() == this.getDirection()){
            if(Math.abs(swipe.getAngle()-this.getAngle())<10){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        if(isSwipeOut) {
            return "Off Screen in direction: " + direction + " with angle of " + getAngle();
        } else {
            return "Onto screen in direction: " + direction + " with angle of " + getAngle();
        }
    }

    public boolean isOffScreen(){
        return isSwipeOut;
    }

    private void fireIntent(){
        /*Intent i = new Intent(context, ServerIntentService.class);
        i.setAction("com.doemski.displaytiling.action.HANDLE_SWIPES");
        i.putExtra("com.doemski.displaytiling.extra.ISSWIPEOUT", isSwipeOut);
        i.putExtra("com.doemski.displaytiling.extra.DIRECTION", getDirectionAsString());
        i.putExtra("com.doemski.displaytiling.extra.ANGLE", getAngle());
        context.startService(i);*/

        Intent intent = new Intent("com.doemski.displaytiling.ACTION_SWIPE");
        intent.putExtra(EXTRA_ISSWIPEOUT, isSwipeOut);
        intent.putExtra(EXTRA_DIRECTION, getDirectionAsString());
        intent.putExtra(EXTRA_ANGLE, getAngle());
        context.sendBroadcast(intent);
    }
}
