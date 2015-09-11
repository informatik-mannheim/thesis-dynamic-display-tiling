package com.doemski.displaytiling.statemachine;

import android.util.Log;

import com.doemski.displaytiling.CommunicationService;
import com.doemski.displaytiling.SwipeInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StateMachineOut extends StateMachine {

    SwipeInfo swipeInfo;

    public StateMachineOut(CommunicationService comService, SwipeInfo swipeInfo) {
        super(comService);
        Log.d("SWIPEHANDLER", "SET TO OUT");
        this.swipeInfo = swipeInfo;

        startWaiting();
    }

    @Override
    public void handleSwipeIn(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLEROUT", "SWIPEIN");
    }

    @Override
    public void handleSwipeOut(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLEROUT", "SWIPEOUT");
    }

    @Override
    public void handleMessage(Object message) {
        stopWaiting();
        Log.d("SWIPEHANDLEROUT", "MESSAGERECEIVED");

        String messageString = (String)message;

        //ONLY POSSIBLE MESSAGES ARE "SwipeOut" AND A SwipeInfo Json
        if(!messageString.equals("SwipeOut")) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            SwipeInfo clientSwipeInfo = gson.fromJson(messageString, SwipeInfo.class);

            if (isSwipeSimilar(clientSwipeInfo)) {
                Log.d("SWIPEHANDLEROUT", "STITCH RECOGNIZED");
                comService.writeOut("Stitch");
                setStitching();
            } else {
                Log.d("SWIPEHANDLEROUT", "STITCH NOT RECOGNIZED");
                setIdle();
            }
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
        comService.stateMachine = new StateMachineStitching(comService,swipeInfo);
    }


    private boolean isSwipeSimilar(SwipeInfo clientSI){
        if(this.swipeInfo.getDir().equals(clientSI.getDir())){
            if(Math.abs(swipeInfo.getAngle()-clientSI.getAngle())<10){
                return true;
            }
        }
        return false;
    }
}