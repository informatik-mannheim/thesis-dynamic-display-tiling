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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class ImageFileSender extends AsyncTask<String,Void,String> {

    String ip, filepath;
    private static final int PORT=8888;
    int len;
    Socket socket = new Socket();
    byte buf[] = new byte[1024];
    Context context;
    Bitmap img;
    Swipe swipe;

    public ImageFileSender(Context context, Bitmap img, Swipe swipe){
        this.context = context;
        this.img = img;
        this.swipe = swipe;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            ip=params[0].replaceAll("/","");


            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, PORT)), 5000);
            Log.d("IMAGEFILESENDER", "socket.connect() executed");



            OutputStream outputStream = socket.getOutputStream();

            //Send Swipe for comparison as JSON
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Log.d("IMAGEFILESENDER","Swipe = " + gson.toJson(swipe));

            //outputStream.write(gson.toJson(swipe).getBytes(Charset.forName("UTF-8")));

            try (OutputStreamWriter out = new OutputStreamWriter(
                    outputStream, StandardCharsets.UTF_8)) {
                out.write(gson.toJson(swipe).toString());
            }

            //Send image
            //img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //outputStream.write(buf, 0, len);

            outputStream.close();

        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
            Log.e("GROUP OWNER","IOException",e);
        }
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
