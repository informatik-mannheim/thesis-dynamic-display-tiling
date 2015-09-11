package com.doemski.displaytiling;

import android.app.Service;
import android.content.Intent;

import com.doemski.displaytiling.statemachine.StateMachine;


public abstract class CommunicationService extends Service{

    public final static String EXTRA_BITMAP = "com.doemski.displaytiling.extra.BITMAP";
    public final static String EXTRA_ISMASTER = "com.doemski.displaytiling.extra.ISMASTER";

    public StateMachine stateMachine;

    public CommunicationService() {
        super();
    }
    public abstract void writeOut(Object o);
    public abstract void establishSockets(String hostAddress);
    public void fireBitmapIntent(byte[] bitmap){
        /*Intent i = new Intent("com.doemski.displaytiling.ACTION_SHOW_BITMAP");
        i.putExtra("bitmap", bitmap);
        this.sendBroadcast(i);*/

        Intent intent = new Intent(getApplicationContext(),FullscreenActivity.class);
        intent.putExtra(EXTRA_BITMAP, bitmap);
        intent.putExtra(EXTRA_ISMASTER,false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }


}
