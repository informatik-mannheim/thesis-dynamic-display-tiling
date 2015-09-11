package com.doemski.displaytiling.statemachine;

import android.util.Log;

import com.doemski.displaytiling.CommunicationService;
import com.doemski.displaytiling.SwipeInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StateMachineIn extends StateMachine {

    SwipeInfo swipeInfo;

    public StateMachineIn(CommunicationService comService, SwipeInfo swipeInfo) {
        super(comService);
        Log.d("SWIPEHANDLER", "SET TO IN");
        this.swipeInfo=swipeInfo;
        startWaiting();
    }

    @Override
    public void handleSwipeIn(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERIN", "SWIPEIN");
    }

    @Override
    public void handleSwipeOut(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERIN", "SWIPEOUT");
    }

    @Override
    public void handleMessage(Object message) {
        Log.d("SWIPEHANDLERIN", "MESSAGERECEIVED");

        stopWaiting();
        String messageString = (String)message;
        if(messageString.equals("SwipeOut")) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            comService.writeOut(gson.toJson(swipeInfo));
        } else if(messageString.equals("Stitch")){
            comService.writeOut(getScreenDimensions());
        } else {
            setIdle();
        }
    }

    @Override
    public void setIdle() {
        comService.stateMachine = new StateMachineIdle(comService);
    }

    @Override
    public void setIn(SwipeInfo swipeInfo) {

    }

    @Override
    public void setOut(SwipeInfo swipeInfo) {

    }

    @Override
    public void setAwaitingIn() {

    }

    @Override
    public void setStitching() {

    }
}