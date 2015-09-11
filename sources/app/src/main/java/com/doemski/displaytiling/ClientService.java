package com.doemski.displaytiling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.doemski.displaytiling.statemachine.StateMachineIdle;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientService extends CommunicationService{

    public static String ACTION_SWIPE = "com.doemski.displaytiling.ACTION_SWIPE";
    private static final String EXTRA_ISSWIPEOUT = "com.doemski.displaytiling.extra.ISSWIPEOUT";
    private static final String EXTRA_ANGLE = "com.doemski.displaytiling.extra.ANGLE";
    public final static String EXTRA_BITMAP = "com.doemski.displaytiling.extra.BITMAP";
    public final static String EXTRA_DIRECTION = "com.doemski.displaytiling.extra.DIRECTION";
    public final static String EXTRA_ISMASTER = "com.doemski.displaytiling.extra.ISMASTER";
    BroadcastReceiver broadcastReceiver;
    private final IBinder mBinder = new ClientServiceBinder();

    private static final int PORT = 8888;
    private List<InetAddress> clientAdressList = new ArrayList<>();
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectInputStream inputstream;
    ObjectOutputStream outputStream;
    Handler handler = new Handler();
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CLIENTSERVICE", "onCreate");
        this.stateMachine = new StateMachineIdle(this);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SWIPE);
        this.broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean isSwipeOut = intent.getBooleanExtra(EXTRA_ISSWIPEOUT, true);
                final String direction = intent.getStringExtra(EXTRA_DIRECTION);
                final float angle = intent.getFloatExtra(EXTRA_ANGLE, 0.0f);
                stateMachine.handleSwipe(isSwipeOut, direction, angle);
            }
        };

        this.registerReceiver(this.broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ClientServiceBinder extends Binder {
        ClientService getService() {
            return ClientService.this;
        }
    }

    @Override
    public void writeOut(Object o){
        try {
            outputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void establishSockets(final String hostAddress) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    clientSocket = new Socket();
                    clientSocket.bind(null);
                    Log.d("CLIENTSERVICE", "hostAddress: " + hostAddress);
                    clientSocket.setReuseAddress(true);
                    clientSocket.connect((new InetSocketAddress(hostAddress, PORT)), 5000);


                    outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    String handshake = "Hi";
                    writeOut(handshake);
                    //outputStream.writeObject(handshake);
                    inputstream = new ObjectInputStream(clientSocket.getInputStream());

                    //stateMachine = new StateMachineIdle(ClientService.this);

                    Runnable socketLoop = new Runnable() {
                        public void run() {
                            while (true) {
                                try {
                                    Object message = inputstream.readObject();
                                    if (message != null) {
                                        Log.d("CLIENTSERVICE", "MESSAGE RECEIVED: " + message);
                                        stateMachine.handleMessage(message);
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }catch (EOFException e){

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    performOnBackgroundThread(socketLoop);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    /*try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }

            }
        };
        thread.start();
    }

    /*@Override
    public void fireBitmapIntent(byte[] bitmap){
        *//*Intent i = new Intent("com.doemski.displaytiling.ACTION_SHOW_BITMAP");
        i.putExtra("bitmap", bitmap);
        this.sendBroadcast(i);*//*

        Intent intent = new Intent(getApplicationContext(),FullscreenActivity.class);
        intent.putExtra(EXTRA_BITMAP, bitmap);
        //intent.putExtra("imgViewDims",imgViewDims);
        //intent.putExtra(EXTRA_DIRECTION,dirString);
        intent.putExtra(EXTRA_ISMASTER,false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }*/
}