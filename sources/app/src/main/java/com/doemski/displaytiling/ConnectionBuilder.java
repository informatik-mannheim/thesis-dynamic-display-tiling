package com.doemski.displaytiling;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionBuilder extends AsyncTask <String, Void, String>{

    private Context context;
    private TextView statusText;
    private List<InetAddress> clientAdressList = new ArrayList<>();
    private static final int PORT = 8888;

    public ConnectionBuilder(Context context, View statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
    }

    public ConnectionBuilder() {
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */


            ServerSocket serverSocket = new ServerSocket(PORT);
            Log.d("Waiting for client","true");
            Socket clientSocket = serverSocket.accept();
            Log.d("CONNECTIONBUILDER", "1");

            InputStream inputstream = clientSocket.getInputStream();
            Log.d("CONNECTIONBUILDER","2");
            clientAdressList.add(clientSocket.getInetAddress());
            Log.d("CONNECTIONBUILDER", "3");

            for(InetAddress clientAdress : clientAdressList){
                Log.d("Client Address",""+clientAdress);
            }
            Log.d("CONNECTIONBUILDER","4");

            ConnectionState.getInstance().setClients(clientAdressList);
            Log.d("CONNECTIONBUILDER", "5");

            if(inputstream!=null){
                Log.d("CONNECTIONBUILDER","Inputstream received");
                return convertStreamToString(inputstream);
            }

            return "No Handshake Received";
        } catch (IOException e) {
            Log.e("AsyncTask Exception",e+"");
            return null;
        }
    }
    /**
     * Start activity that can handle the JPEG image
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {

            Log.d("Handshake", result);

        }
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}