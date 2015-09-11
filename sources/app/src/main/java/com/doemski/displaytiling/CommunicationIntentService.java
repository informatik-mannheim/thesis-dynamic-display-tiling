package com.doemski.displaytiling;

import android.app.Service;

import com.doemski.displaytiling.statemachine.StateMachine;

/**
 * Created by Dome on 05.08.2015.
 */
public abstract class CommunicationIntentService extends Service{
    public StateMachine stateMachine;


    public CommunicationIntentService() {
        super();
    }
    public abstract void writeOut(Object o);
}
