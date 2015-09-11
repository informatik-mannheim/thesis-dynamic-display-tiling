/*
package com.doemski.displaytiling;

import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.doemski.displaytiling.swipehandler.SwipeHandler;
import com.doemski.displaytiling.swipehandler.SwipeHandlerIdle;
import com.doemski.displaytiling.swipehandler.SwipeHandlerStitching;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerIntentService extends CommunicationIntentService{

    private static final String ACTION_ESTABLISH_SOCKETS = "com.doemski.displaytiling.action.ESTABLISH_SOCKETS";
    private static final String ACTION_HANDLE_SWIPES = "com.doemski.displaytiling.action.HANDLE_SWIPES";

    private static final String EXTRA_ISSWIPEOUT = "com.doemski.displaytiling.extra.ISSWIPEOUT";
    private static final String EXTRA_DIRECTION = "com.doemski.displaytiling.extra.DIRECTION";
    private static final String EXTRA_ANGLE = "com.doemski.displaytiling.extra.ANGLE";




    private final IBinder mBinder = new ServerIntentServiceBinder();

    private static final int PORT = 8888;
    private List<InetAddress> clientAdressList = new ArrayList<>();
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectInputStream inputstream;
    ObjectOutputStream outputStream;
    Context context;

    SwipeHandler swipeHandler;



    public void startActionEstablishSockets(Context context) {
        //this.context = context;
        this.context=this;


        Intent intent = new Intent(context, ServerIntentService.class);
        intent.setAction(ACTION_ESTABLISH_SOCKETS);
        context.startService(intent);
    }

    public ServerIntentService() {
        super();
        //swipeHandler = new SwipeHandlerIdle(this);
    }

   // @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ESTABLISH_SOCKETS.equals(action)) {
                handleActionEstablishSockets();
            } else if (ACTION_HANDLE_SWIPES.equals(action)) {
                final boolean isSwipeOut = intent.getBooleanExtra(EXTRA_ISSWIPEOUT, true);
                final String direction = intent.getStringExtra(EXTRA_DIRECTION);
                final float angle = intent.getFloatExtra(EXTRA_ANGLE, 0.0f);
                handleActionHandleSwipes(isSwipeOut, direction, angle);
            }
        }
    }

    private void handleActionEstablishSockets() {
        try {
            serverSocket = new ServerSocket(PORT);

            Log.d("SERVERINTENTSERVICE", "Waiting for Client");
            Socket clientSocket = serverSocket.accept();

            this.inputstream = new ObjectInputStream(clientSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            clientAdressList.add(clientSocket.getInetAddress());

            for(InetAddress clientAdress : clientAdressList){
                Log.d("SERVERINTENTSERVICE","Client Adress: " + clientAdress);
            }

            ConnectionState.getInstance().setClients(clientAdressList);

            String handshake = (String)inputstream.readObject();

            //Start listening to the inputstream once handshake received
            if(handshake.equals("Hi")){
                Log.d("SERVERINTENTSERVICE", "Handshake received");


                swipeHandler = new SwipeHandlerIdle(this);

                Runnable socketLoop = new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                Object message = inputstream.readObject();
                                //if(message!=null && swipeHandler instanceof SwipeHandlerStitching == false){
                                    Log.d("SERVERINTENTSERVICE","MESSAGE RECEIVED: " + message);
                                    swipeHandler.handleMessage(message);
                                //}
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                performOnBackgroundThread(socketLoop);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleActionHandleSwipes(boolean isSwipeOut, String direction, float angle) {



        if(swipeHandler!=null){
            swipeHandler.handleSwipe(isSwipeOut, direction, angle);
        } else {
            //swipeHandler = new
        }
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

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServerIntentServiceBinder extends Binder {
        ServerIntentService getService() {
            // Return this instance of ServerIntentService so clients can call public methods
            return ServerIntentService.this;
        }
    }

    @Override
    public void writeOut(Object o){
        try {
            outputStream.writeObject(o);
            Log.d("SERVERINTENTSERVICE","MESSAGE SENT: " + o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
