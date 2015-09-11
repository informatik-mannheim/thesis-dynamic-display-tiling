package com.doemski.displaytiling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.doemski.displaytiling.statemachine.StateMachineIdle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerService extends CommunicationService{

    public static String ACTION_START = "com.doemski.displaytiling.ACTION_START";
    public static String ACTION_SWIPE = "com.doemski.displaytiling.ACTION_SWIPE";
    private static final String EXTRA_ISSWIPEOUT = "com.doemski.displaytiling.extra.ISSWIPEOUT";
    private static final String EXTRA_DIRECTION = "com.doemski.displaytiling.extra.DIRECTION";
    private static final String EXTRA_ANGLE = "com.doemski.displaytiling.extra.ANGLE";
    private static final int PORT = 8888;
    private final IBinder mBinder = new ServerServiceBinder();

    private List<InetAddress> clientAdressList = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectInputStream inputstream;
    ObjectOutputStream outputStream;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVERSERVICE", "onCreate");
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
    public void establishSockets(String hostAddress){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if(serverSocket==null) {
                        serverSocket = new ServerSocket();
                        serverSocket.setReuseAddress(true);
                        serverSocket.bind(new InetSocketAddress(PORT));
                        Log.d("SERVERSERVICE", "New serverSocket built");
                    }

                    Log.d("SERVERSERVICE", "Waiting for Client");
                    Socket clientSocket = serverSocket.accept();
                    Log.d("SERVERSERVICE", "clientSocket established");
                    inputstream = new ObjectInputStream(clientSocket.getInputStream());
                    Log.d("SERVERSERVICE", "inputstream built");
                    outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    Log.d("SERVERSERVICE", "outputstream built");

                    clientAdressList.add(clientSocket.getInetAddress());

                    for(InetAddress clientAdress : clientAdressList){
                        Log.d("SERVERSERVICE","Client Adress: " + clientAdress);
                    }
                    ConnectionState.getInstance().setClients(clientAdressList);

                    String handshake = (String)inputstream.readObject();
                    //Start listening to the inputstream once handshake received
                    if(handshake.equals("Hi")){
                        Log.d("SERVERSERVICE", "Handshake received");

                        //stateMachine = new StateMachineIdle(ServerService.this);

                        Runnable socketLoop = new Runnable() {
                            public void run() {
                                while (true) {
                                    try {
                                        Object message = inputstream.readObject();
                                        //if(message!=null && swipeHandler instanceof SwipeHandlerStitching == false){
                                        Log.d("SERVERSERVICE","MESSAGE RECEIVED: " + message);
                                        stateMachine.handleMessage(message);
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
                finally {
                    /*if(serverSocket != null &&!serverSocket.isClosed()) {
                        try {
                            serverSocket.close();
                        } catch (IOException e){
                            e.printStackTrace(System.err);
                        }

                    }*/
                }

            }
        };
        thread.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public void writeOut(Object o) {
        try {
            Log.d("SERVERSERVICE", "WRITEOUT " + this.toString());
            outputStream.writeObject(o);
            Log.d("SERVERSERVICE", "MESSAGE SENT: " + o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /*public void writeOutBitmap(Bitmap bmp) {
        try {
            byte buf[] = new byte[1024];
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.write(buf, 0, len);
            //outputStream.writeObject(o);
            //Log.d("SERVERINTENTSERVICE", "MESSAGE SENT: " + o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServerServiceBinder extends Binder {
        ServerService getService() {
            // Return this instance of ServerService so clients can call public methods
            return ServerService.this;
        }
    }

   /*@Override
    public void fireBitmapIntent(byte[] bitmap){
        Intent i = new Intent("com.doemski.displaytiling.ACTION_SHOW_BITMAP");
        i.putExtra("bitmap", bitmap);
        this.sendBroadcast(i);
    }*/
}