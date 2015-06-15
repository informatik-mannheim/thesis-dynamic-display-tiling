package com.doemski.displaytiling;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

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

    public ClientAsyncTask(){


    }


    @Override
    protected String doInBackground(String... params) {
        try {
            this.host=params[0];
            Log.d("CLIENT", "host address: " + host);

            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), 500);

            OutputStream outputStream = socket.getOutputStream();

            String handshakeString = "Hi";
            outputStream.write(handshakeString.getBytes(Charset.forName("UTF-8")));

            //wait for image data from group owner
            ServerSocket clientSocket = new ServerSocket(PORT);
            Log.d("CLIENT","Waiting for File");

            //Socket from group owner's ImageFileSender class
            Socket groupOwnerSocket = clientSocket.accept();

            Log.d("CLIENT","Socket accepted");

            final File f = new File(Environment.getExternalStorageDirectory() + "/stitch2tile-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = groupOwnerSocket.getInputStream();

            byte[] buffer = new byte[inputstream.available()];
            inputstream.read(buffer);

            Log.d("CLIENT", "inputstream buffer read");
            OutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(buffer);


            clientSocket.close();
            return f.getAbsolutePath();

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
            Log.d("FILE RECEIVED",result);
            /*Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image*//*");
            context.startActivity(intent);*/
        }
    }
}
