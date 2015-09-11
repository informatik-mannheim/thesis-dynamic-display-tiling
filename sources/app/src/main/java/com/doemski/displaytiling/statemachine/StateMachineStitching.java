package com.doemski.displaytiling.statemachine;

import android.content.Intent;
import android.util.Log;

import com.doemski.displaytiling.CommunicationService;
import com.doemski.displaytiling.Direction;
import com.doemski.displaytiling.SwipeInfo;

public class StateMachineStitching extends StateMachine {

    private SwipeInfo swipeInfo;

    public StateMachineStitching(CommunicationService comService, SwipeInfo swipeInfo){
        super(comService);
        Log.d("SWIPEHANDLER", "SET TO STITCHING");
        this.swipeInfo = swipeInfo;
        startWaiting();
    }

    @Override
    public void handleSwipeIn(SwipeInfo swipeInfo) {}

    @Override
    public void handleSwipeOut(SwipeInfo swipeInfo) {}

    @Override
    public void handleMessage(Object message) {
        stopWaiting();
        double[] otherDims = (double[]) message;
        double[] myDims = getScreenDimensions();

        //fire stitch event
        Intent intent = new Intent("com.doemski.displaytiling.ACTION_STITCH");
        intent.putExtra("MY_DIMS",myDims);
        intent.putExtra("OTHER_DIMS",otherDims);
        intent.putExtra("DIRECTION", swipeInfo.getDirectionAsString());
        comService.sendBroadcast(intent);
    }

    @Override
    public void setIdle() {}

    @Override
    public void setIn(SwipeInfo swipeInfo) {}

    @Override
    public void setOut(SwipeInfo swipeInfo) {}

    @Override
    public void setAwaitingIn() {}

    @Override
    public void setStitching() {}
}
