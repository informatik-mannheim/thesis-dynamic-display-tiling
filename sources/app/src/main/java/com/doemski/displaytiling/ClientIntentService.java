/*
package com.doemski.displaytiling;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.doemski.displaytiling.swipehandler.SwipeHandler;
import com.doemski.displaytiling.swipehandler.SwipeHandlerIdle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientIntentService extends CommunicationIntentService{

    */
/*private static final String ACTION_ESTABLISH_SOCKETS = "com.doemski.displaytiling.action.ESTABLISH_SOCKETS";
    private static final String ACTION_HANDLE_SWIPES = "com.doemski.displaytiling.action.EXCHANGE_SWIPES";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.doemski.displaytiling.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.doemski.displaytiling.extra.PARAM2";




    private final IBinder mBinder = new ServerIntentServiceBinder();

    private static final int PORT = 8888;
    private List<InetAddress> clientAdressList = new ArrayList<>();
    ServerSocket serverSocket;
    Socket clientSocket;
    InputStream inputstream;
    OutputStream outputStream;
    Context context;

    SwipeHandler swipeHandler=null;


    public void startActionEstablishSockets(Context context) {
        this.context = context;


        Intent intent = new Intent(context, ClientIntentService.class);
        intent.setAction(ACTION_ESTABLISH_SOCKETS);
        context.startService(intent);
    }

    public static void startActionHandleSwipes(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ClientIntentService.class);
        intent.setAction(ACTION_HANDLE_SWIPES);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public ClientIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ESTABLISH_SOCKETS.equals(action)) {
                handleActionEstablishSockets();
            } else if (ACTION_HANDLE_SWIPES.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionHandleSwipes(param1, param2);
            }
        }
    }

    private void handleActionEstablishSockets() {
        try {
            serverSocket = new ServerSocket(PORT);

            Log.d("SERVERINTENTSERVICE", "Waiting for Client");
            Socket clientSocket = serverSocket.accept();

            inputstream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

            clientAdressList.add(clientSocket.getInetAddress());

            for(InetAddress clientAdress : clientAdressList){
                Log.d("SERVERINTENTSERVICE","Client Adress: " + clientAdress);
            }

            ConnectionState.getInstance().setClients(clientAdressList);

            if(inputstream != null){
                Log.d("SERVERINTENTSERVICE","Handshake received");
                convertStreamToString(inputstream);

                swipeHandler = new SwipeHandlerIdle(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionHandleSwipes(String param1, String param2) {
        if(swipeHandler!=null){

        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void writeOut(Object o) {

    }


    public class ServerIntentServiceBinder extends Binder {
        ClientIntentService getService() {
            // Return this instance of ServerIntentService so clients can call public methods
            return ClientIntentService.this;
        }
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }*//*

    private static final String ACTION_ESTABLISH_SOCKETS = "com.doemski.displaytiling.action.ESTABLISH_SOCKETS";
    private static final String ACTION_HANDLE_SWIPES = "com.doemski.displaytiling.action.HANDLE_SWIPES";

    private static final String EXTRA_ISSWIPEOUT = "com.doemski.displaytiling.extra.ISSWIPEOUT";
    private static final String EXTRA_DIRECTION = "com.doemski.displaytiling.extra.DIRECTION";
    private static final String EXTRA_ANGLE = "com.doemski.displaytiling.extra.ANGLE";
    private static final String EXTRA_HOSTADDRESS = "com.doemski.displaytiling.extra.HOSTADDRESS";




    private final IBinder mBinder = new ClientIntentServiceBinder();

    private static final int PORT = 8888;
    private List<InetAddress> clientAdressList = new ArrayList<>();
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectInputStream inputstream;
    ObjectOutputStream outputStream;
    String hostAddress;
    Context context;

    SwipeHandler swipeHandler=null;


    public void startActionEstablishSockets(Context context, String hostAddress) {
        this.context = context;
        this.hostAddress=hostAddress;
        Log.d("CLIENTINTENTSERVICE", "hostAddress: " + hostAddress);

        Intent intent = new Intent(context, ClientIntentService.class);
        intent.setAction(ACTION_ESTABLISH_SOCKETS);
        intent.putExtra(EXTRA_HOSTADDRESS,hostAddress);
        context.startService(intent);
    }

    public ClientIntentService() {
        super("ClientIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ESTABLISH_SOCKETS.equals(action)) {
                final String hostAddress = intent.getStringExtra(EXTRA_HOSTADDRESS);
                handleActionEstablishSockets(hostAddress);

            } else if (ACTION_HANDLE_SWIPES.equals(action)) {
                final boolean isSwipeOut = Boolean.parseBoolean(intent.getStringExtra(EXTRA_ISSWIPEOUT));
                final String direction = intent.getStringExtra(EXTRA_DIRECTION);
                final float angle = Float.parseFloat(intent.getStringExtra(EXTRA_ANGLE));
                handleActionHandleSwipes(isSwipeOut, direction, angle);
            }
        }
    }

    private void handleActionEstablishSockets(String hAddress) {
        try {

            clientSocket = new Socket();
            clientSocket.bind(null);
            Log.d("CLIENTINTENTSERVICE", "hAddress: " + hAddress);
            clientSocket.connect((new InetSocketAddress(hAddress, PORT)), 1000);


            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            String handshake= "Hi";
            outputStream.writeObject(handshake);
            this.inputstream = new ObjectInputStream(clientSocket.getInputStream());

            //Start listening to the inputstream once handshake received
            swipeHandler = new SwipeHandlerIdle(this);

            Runnable socketLoop = new Runnable() {
                public void run() {
                    while (true) {

                        try {
                            //if(inputstream.available()>0) {
                                Object message = inputstream.readObject();
                                if (message != null) {
                                    Log.d("CLIENTINTENTSERVICE","MESSAGE RECEIVED: " + message);
                                    swipeHandler.handleMessage(message);
                                }
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionHandleSwipes(boolean isSwipeOut, String direction, float angle) {
        if(swipeHandler!=null){
            swipeHandler.handleSwipe(isSwipeOut, direction, angle);
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

    public class ClientIntentServiceBinder extends Binder {
        ClientIntentService getService() {
            // Return this instance of ServerIntentService so clients can call public methods
            return ClientIntentService.this;
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
}
*/
