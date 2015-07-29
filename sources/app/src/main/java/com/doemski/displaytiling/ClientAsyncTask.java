package com.doemski.displaytiling;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;


public class ClientAsyncTask extends AsyncTask<String, Void, String>{

    Context context;
    String host;
    private static final int PORT=8888;
    int len;
    Socket socket = new Socket();
    byte buf[] = new byte[1024];

    public ClientAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            this.host=params[0];
                                                                    Log.d("CLIENTASYNCTASK", "host address: " + host);

            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), 500);

            OutputStream outputStream = socket.getOutputStream();

            String handshake= "Hi";
            outputStream.write(handshake.getBytes(Charset.forName("UTF-8")));

            //wait for image data from group owner
            ServerSocket clientSocket = new ServerSocket(PORT);
                                                                    Log.d("CLIENTASYNCTASK","Waiting for Swipe");

            //Socket from group owner's ImageFileSender class
            Socket groupOwnerSocket = clientSocket.accept();
                                                                    Log.d("CLIENTASYNCTASK","ImageFileSender Socket accepted");
            OutputStream out = groupOwnerSocket.getOutputStream();
            InputStream in = groupOwnerSocket.getInputStream();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            if(in != null){
                Swipe groupOwnerSwipe = gson.fromJson(convertStreamToString(in), Swipe.class);
                //Log.d("CLIENTASYNCTASK","SWIPE: " + groupOwnerSwipe.toString());
                Log.d("CLIENTASYNCTASK",groupOwnerSwipe.toString());
            }




/*

            //File handling code
            final File f = new File(Environment.getExternalStorageDirectory() + "/stitch2tile-" + System.currentTimeMillis()
                    + ".jpg");
            Log.d("CLIENTASYNCTASK","9");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            InputStream inputStream = groupOwnerSocket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(f);

            int read = 0;

            while ((read = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, read);
            }

            inputStream.close();
            outputStream.close();
            fileOutputStream.close();

            clientSocket.close();
            return f.getAbsolutePath();
*/

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

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("FILE RECEIVED", result);

            //Intent intent = new Intent();
            //intent.setAction(android.content.Intent.ACTION_VIEW);
            //intent.setDataAndType(Uri.parse("file://" + result), "image*//*");
            //context.startActivity(intent);

        }
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}