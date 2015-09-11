package com.doemski.displaytiling.statemachine;

import android.util.Log;

import com.doemski.displaytiling.CommunicationService;
import com.doemski.displaytiling.SwipeInfo;

public class StateMachineIdle extends StateMachine {


    public StateMachineIdle(CommunicationService comService) {
        super(comService);
        Log.d("SWIPEHANDLER", "SET TO IDLE " + this.toString());
    }

    @Override
    public void handleSwipeIn(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERIDLE", "SWIPEIN");
        setIn(swipeInfo);
    }

    @Override
    public void handleSwipeOut(SwipeInfo swipeInfo) {
        Log.d("SWIPEHANDLERIDLE", "SWIPEOUT");
        //Send message to client
        String message= "SwipeOut";
        comService.writeOut(message);

        setOut(swipeInfo);
    }

    @Override
    public void handleMessage(Object message) {
        Log.d("SWIPEHANDLERIDLE", "MESSAGERECEIVED");

        if(message instanceof byte[]){
            comService.fireBitmapIntent ((byte[])message);
        } else {
            String messageString = (String) message;
            if (messageString.equals("SwipeOut")) {
                setAwaitingIn();
            } else if (messageString.equals("Stitch")) {
                comService.writeOut(getScreenDimensions());
            }
        }
    }

    @Override
    public void setIdle() {}

    @Override
    public void setIn(SwipeInfo swipeInfo) { comService.stateMachine = new StateMachineIn(comService,swipeInfo);}

    @Override
    public void setOut(SwipeInfo swipeInfo) { comService.stateMachine = new StateMachineOut(comService,swipeInfo);}

    @Override
    public void setAwaitingIn() { comService.stateMachine = new StateMachineAwaitingIn(comService);}

    @Override
    public void setStitching() {

    }
}
