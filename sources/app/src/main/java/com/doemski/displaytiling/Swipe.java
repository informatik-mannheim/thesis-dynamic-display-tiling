package com.doemski.displaytiling;

public class Swipe {
    private boolean isSwipeOut;
    private Direction direction;
    private Vector2d swipeStartPoint, swipeEndPoint;
    boolean swipeInProgress;


    public Swipe(){
        isSwipeOut=false;
    }

    public Swipe(boolean isSwipeOut, Direction direction, Vector2d swipeStartPoint, Vector2d swipeEndPoint){
        this.isSwipeOut = isSwipeOut;
        this.direction = direction;
        this.swipeStartPoint = swipeStartPoint;
        this.swipeEndPoint = swipeEndPoint;
        swipeInProgress = false;
    }

    public Swipe(boolean isSwipeOut, Direction direction, Vector2d swipeStartPoint){
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

    //TODO: Swipe Comparison (angles +-10deg?)

    public void setSwipeEndPoint(Vector2d swipeEndPoint){
        this.swipeEndPoint = swipeEndPoint;
        swipeInProgress = false;
    }

    public boolean inProgress(){
        return swipeInProgress;
    }

    public Direction getDirection(){
        return direction;
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
}
