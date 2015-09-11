package com.doemski.displaytiling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class ImageProcessor {

    Bitmap img;
    int myX, myY, otherX, otherY;
    double[] myDims;
    double[] otherDims;
    double myDens, otherDens;
    String dir;
    Bitmap myBitmap=null, otherBitmap=null;
    Context context;

    public ImageProcessor(double[] myDims, double[] otherDims, String dir, Context context){
        this.myDims=myDims;
        this.otherDims=otherDims;
        myDens = myDims[4];
        otherDens = otherDims[4];
        this.dir = dir;
        this.context = context;
    }
    public Bitmap[] processImage(Bitmap bitmap){
        //int[] overallDims = getOverallDims();
        //this.img = getResizedBitmap(bitmap, getOverallDims());
        Bitmap[] cutBitmaps = cutBitmap(bitmap);

        return cutBitmaps;
    }

    private Bitmap[] cutBitmap(Bitmap img) {
        double myScreenSizeXpix = myDims[0];
        double myScreenSizeYpix = myDims[1];
        double myScreenSizeXdp = myDims[2];
        double myScreenSizeYdp = myDims[3];
        double myDPI = myDims[5];
        double myScreenSizeXinch = myScreenSizeXpix/myDPI;
        double myScreenSizeYinch = myScreenSizeYpix/myDPI;
        Log.d("IP myXpix",""+myScreenSizeXpix);
        Log.d("IP myYpix",""+myScreenSizeYpix);
        Log.d("IP myXdp",""+myScreenSizeXdp);
        Log.d("IP myYdp",""+myScreenSizeYdp);
        Log.d("IP myDPI",""+myDPI);
        Log.d("IP myXinch",""+myScreenSizeXinch);
        Log.d("IP myYinch",""+myScreenSizeYinch);

        double otherScreenSizeXpix = otherDims[0];
        double otherScreenSizeYpix = otherDims[1];
        double otherScreenSizeXdp = otherDims[2];
        double otherScreenSizeYdp = otherDims[3];
        double otherDPI = otherDims[5];
        double otherScreenSizeXinch = otherScreenSizeXpix/otherDPI;
        double otherScreenSizeYinch = otherScreenSizeYpix/otherDPI;

        Log.d("IP otherXpix",""+otherScreenSizeXpix);
        Log.d("IP otherYpix",""+otherScreenSizeYpix);
        Log.d("IP otherXdp",""+otherScreenSizeXdp);
        Log.d("IP otherYdp",""+otherScreenSizeYdp);
        Log.d("IP otherDPI",""+otherDPI);
        Log.d("IP otherXinch",""+otherScreenSizeXinch);
        Log.d("IP otherYinch",""+otherScreenSizeYinch);


        double cutPercentage;
        Bitmap myBitmapScaled=null,otherBitmapScaled=null;
        Bitmap[] cutBMs = new Bitmap[2];

        if(dir.equals("RIGHT")||dir.equals("LEFT")){

            cutPercentage = myScreenSizeXinch/(myScreenSizeXinch + otherScreenSizeXinch);
            int imgHeight = img.getHeight(), imgWidth = img.getWidth();

            if(dir.equals("LEFT")) {
                if (myScreenSizeYinch <= otherScreenSizeYinch) {
                    //SMALL --> BIG

                    myBitmap = Bitmap.createBitmap(img, (int) (imgWidth * (1 - cutPercentage)), 0, imgWidth - (int) (imgWidth * (1 - cutPercentage)), imgHeight);
                    myBitmapScaled = Bitmap.createScaledBitmap(myBitmap, (int) myScreenSizeXpix, (int) myScreenSizeYpix, false);
                    Log.d("IP myBitmapScaledX", "" + myBitmapScaled.getWidth());
                    Log.d("IP myBitmapScaledY", "" + myBitmapScaled.getHeight());

                    otherBitmap = Bitmap.createBitmap(img, 0, 0, (int) (imgWidth * (1 - cutPercentage)), imgHeight);
                    otherBitmapScaled = Bitmap.createScaledBitmap(otherBitmap, (int) otherScreenSizeXpix, (int) (myScreenSizeYdp * otherDens), false);
                    Log.d("IP otherBitmapScaledX", "" + otherBitmapScaled.getWidth());
                    Log.d("IP otherBitmapScaledY", "" + otherBitmapScaled.getHeight());
                } else {
                    //BIG --> SMALL

                    myBitmap = Bitmap.createBitmap(img, (int) (imgWidth * (1 - cutPercentage)), 0, imgWidth - (int) (imgWidth * (1 - cutPercentage)), imgHeight);
                    myBitmapScaled = Bitmap.createScaledBitmap(myBitmap, (int) myScreenSizeXpix, (int) (otherScreenSizeYdp * myDens), false);
                    Log.d("IP myBitmapScaledX", "" + myBitmapScaled.getWidth());
                    Log.d("IP myBitmapScaledY", "" + myBitmapScaled.getHeight());

                    otherBitmap = Bitmap.createBitmap(img, 0, 0, (int) (imgWidth * (1 - cutPercentage)), imgHeight);
                    otherBitmapScaled = Bitmap.createScaledBitmap(otherBitmap, (int) otherScreenSizeXpix, (int) otherScreenSizeYpix, false);
                    Log.d("IP otherBitmapScaledX", "" + otherBitmapScaled.getWidth());
                    Log.d("IP otherBitmapScaledY", "" + otherBitmapScaled.getHeight());
                }
            } else if(dir.equals("RIGHT")){
                if (myScreenSizeYinch <= otherScreenSizeYinch) {
                    //SMALL --> BIG

                    myBitmap = Bitmap.createBitmap(img, 0, 0, (int) (imgWidth * cutPercentage), imgHeight);
                    myBitmapScaled = Bitmap.createScaledBitmap(myBitmap, (int) myScreenSizeXpix, (int) myScreenSizeYpix, false);
                    Log.d("IP myBitmapScaledX", "" + myBitmapScaled.getWidth());
                    Log.d("IP myBitmapScaledY", "" + myBitmapScaled.getHeight());

                    otherBitmap = Bitmap.createBitmap(img, (int) (imgWidth * cutPercentage), 0, imgWidth-(int) (imgWidth * cutPercentage), imgHeight);
                    otherBitmapScaled = Bitmap.createScaledBitmap(otherBitmap, (int) otherScreenSizeXpix, (int) (myScreenSizeYdp * otherDens), false);
                    Log.d("IP otherBitmapScaledX", "" + otherBitmapScaled.getWidth());
                    Log.d("IP otherBitmapScaledY", "" + otherBitmapScaled.getHeight());
                } else {
                    //BIG --> SMALL

                    myBitmap = Bitmap.createBitmap(img, 0, 0, (int) (imgWidth * cutPercentage), imgHeight);
                    myBitmapScaled = Bitmap.createScaledBitmap(myBitmap, (int) myScreenSizeXpix, (int) (otherScreenSizeYdp * myDens), false);
                    Log.d("IP myBitmapScaledX", "" + myBitmapScaled.getWidth());
                    Log.d("IP myBitmapScaledY", "" + myBitmapScaled.getHeight());

                    otherBitmap = Bitmap.createBitmap(img, (int) (imgWidth * cutPercentage), 0, imgWidth-(int) (imgWidth * cutPercentage), imgHeight);
                    otherBitmapScaled = Bitmap.createScaledBitmap(otherBitmap, (int) otherScreenSizeXpix, (int) otherScreenSizeYpix, false);
                    Log.d("IP otherBitmapScaledX", "" + otherBitmapScaled.getWidth());
                    Log.d("IP otherBitmapScaledY", "" + otherBitmapScaled.getHeight());
                }
            }
        } else {
            cutPercentage = myScreenSizeYdp/(myScreenSizeYdp + otherScreenSizeYdp);
        }

        cutBMs[0] = myBitmapScaled;
        cutBMs[1] = otherBitmapScaled;
        return cutBMs;
    }

    private double[] getOverallDims(){
        /*int overallWidth=0, overallHeight=0;
        if(dir.equals("RIGHT")||dir.equals("LEFT")){
            overallWidth = myX + otherX;
            if(otherY>=myY){
                overallHeight=otherY;
            } else {
                overallHeight=myY;
            }
        } else {
            overallHeight = myY + otherY;
            if(otherX>=myX){
                overallWidth=otherX;
            } else {
                overallWidth=myX;
            }
        }
        int[] overallDims = new int[2];
        overallDims[0]=overallWidth;
        overallDims[1]=overallHeight;*/
        double[] overallDims = new double[4];

        return overallDims;
    }

    private Bitmap getResizedBitmap(Bitmap bm, double[] overallDims) {

        int currentWidth = bm.getWidth();
        int currentHeight = bm.getHeight();
        double newWidth = overallDims[0];
        double newHeight = overallDims[1];

        float scaleWidth = ((float) newWidth) / currentWidth;
        float scaleHeight = ((float) newHeight) / currentHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, currentWidth, currentHeight, matrix, false);

        return resizedBitmap;
    }

    public double[] getImageViewDims(){
        if(myX>=otherX){
            return otherDims;
        }else{
            return myDims;
        }
    }




}
