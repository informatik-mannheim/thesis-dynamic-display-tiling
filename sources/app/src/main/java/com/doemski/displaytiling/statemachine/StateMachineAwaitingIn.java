package com.doemski.displaytiling.statemachine;

import android.util.Log;

import com.doemski.displaytiling.CommunicationService;
import com.doemski.displaytiling.SwipeInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StateMachineAwaitingIn extends StateMachine {
    public StateMachineAwaitingIn(CommunicationService comService) {
        super(comService);
        Log.d("SWIPEHANDLER", "SET TO AWAITINGIN");
        startWaiting();
    }

    @Override
    public void handleSwipeIn(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERAWAITINGIN", "SWIPEIN");
        stopWaiting();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        comService.writeOut(gson.toJson(swipeInfo));
    }

    @Override
    public void handleSwipeOut(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERAWAITINGIN", "SWIPEOUT");
    }

    @Override
    public void handleMessage(Object message) {
        Log.d("SWIPEHANDLERAWAITINGIN", "MESSAGERECEIVED");
        stopWaiting();
        String messageString = (String)message;
        if(messageString.equals("Stitch")){
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
