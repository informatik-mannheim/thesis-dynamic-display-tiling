package com.doemski.displaytiling;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;


public class ClientAsyncTask extends AsyncTask<String, Void, String>{

    Context context;
    String host;
    int port=8888;//TODO: hardcoded for now
    int len;
    Socket socket = new Socket();
    byte buf[] = new byte[1024];//TODO: hardcoded for now

    public ClientAsyncTask(){


    }


    @Override
    protected String doInBackground(String... params) {
        try {
            this.host=params[0];
            Log.d("host address", host);
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            OutputStream outputStream = socket.getOutputStream();

            String testString = "THIS IS A TEST MESSAGE";
            outputStream.write(testString.getBytes(Charset.forName("UTF-8")));

            //outputStream.write(buf, 0, len);


        } catch (IOException e){
            Log.e("ClientAsyncTask", e.toString());
        }
        finally{
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                        Log.e("SOCKET",e.toString());
                    }
                }
            }
        }
        return null;
    }
}
