package com.doemski.displaytiling;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;


public class ImageFileSender extends AsyncTask<String,Void,String> {

    String ip, filepath;
    private static final int PORT=8888;
    int len;
    Socket socket = new Socket();
    byte buf[] = new byte[1024];
    Context context;
    Bitmap img;

    public ImageFileSender(Context context, Bitmap img){            //TODO: bitmap is sent. get path from bitmap it can be sent to the client
        this.context = context;
        this.img = img;
    }

    @Override
    protected String doInBackground(String... params) {
        /*
        if(android.os.Debug.isDebuggerConnected())
        {
            android.os.Debug.waitForDebugger();
        }
        */
        Log.d("GROUP OWNER","File sending initialized");
        try {
            ip=params[0].replaceAll("/","");


            socket.bind(null);
            Log.d("GROUP OWNER", "ip:port " + ip + ":" + PORT);
            socket.connect((new InetSocketAddress(ip, PORT)), 5000);
            Log.d("GROUP OWNER", "socket.connect() executed");
            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */


            OutputStream outputStream = socket.getOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //ContentResolver cr = context.getContentResolver();
            //InputStream inputStream = null;
            //inputStream = cr.openInputStream(Uri.parse(filepath));
            //while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            //}
            outputStream.close();
            //inputStream.close();
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
            Log.e("GROUP OWNER","IOException",e);
        }

/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri, Context inContext) {
        Cursor cursor = inContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
