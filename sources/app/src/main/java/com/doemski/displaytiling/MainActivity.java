package com.doemski.displaytiling;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.software.shell.fab.FloatingActionButton;



public class MainActivity extends ActionBarActivity implements View.OnTouchListener{

    //TODO: ActionBarActivity Deprecated...

    ImageView mainImageView;
    FloatingActionButton fabAddImage;
    FloatingActionButton crossIcon;
    boolean isFullScreen;
    float crossIconX,crossIconY=0.0f;
    float crossIconXCentered,crossIconYCentered;//TODO:calculate instead of global var
    boolean crossIconMoving;//TODO:make local somehow
    boolean togglingFullscreenFirstTime=true;//TODO:change this! needed to determine crossIconXCentered,crossIconYCentered

    boolean isSwiping=false;

    private Swipe swipe;

    private WifiDirectManager wifiDirectManager;
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mainImageView = (ImageView) findViewById(R.id.mainImageView);

        buildAddButton();
        buildCrossIcon();

        mainImageView.setOnTouchListener(this);

        wifiDirectManager = new WifiDirectManager((WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE), this);


        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        swipe=new Swipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode == 1) {
                mainImageView.setImageURI(data.getData());
            }
        }
    }

    //TODO:change name to something more fitting. thjis mehtod does more than fullscreen
    private void toggleFullScreen(){
        if(!isFullScreen){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            if(getSupportActionBar()!=null) {
                getSupportActionBar().hide();
            }
            fabAddImage.hide();
            crossIcon.show();

            if(togglingFullscreenFirstTime) {//save centered data. TODO:change this!
                crossIconXCentered = crossIcon.getX();
                crossIconYCentered = crossIcon.getY();
                togglingFullscreenFirstTime=false;
            }else{//move crossIcon back to center and make it grabbable again
                ObjectAnimator animationX = ObjectAnimator.ofFloat(crossIcon, "x", crossIcon.getX(), crossIconXCentered);
                ObjectAnimator animationY = ObjectAnimator.ofFloat(crossIcon, "y", crossIcon.getY(), crossIconYCentered);
                AnimatorSet animSetXY = new AnimatorSet();
                animSetXY.playTogether(animationX, animationY);
                animSetXY.setDuration(1000);
                animSetXY.setInterpolator(new OvershootInterpolator());
                animSetXY.start();
            }
            isFullScreen = true;

        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if(getSupportActionBar()!=null) {
                getSupportActionBar().show();
            }
            fabAddImage.show();
            crossIcon.hide();
            isFullScreen = false;
            swipe = new Swipe();
        }

        mainImageView.requestLayout();
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            toggleFullScreen();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(v.equals(crossIcon)) {//Movement of the crossIcon

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    crossIconMoving = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (crossIconMoving && !swipe.isOffScreen()) {
                        //this prevents the crossicon from jumping when dragged
                        crossIconX = event.getRawX() - crossIcon.getWidth() / 2;
                        crossIconY = event.getRawY() - crossIcon.getHeight() / 2;
                        crossIcon.setX(crossIconX);
                        crossIcon.setY(crossIconY);

                        //dragging the crossIcon off the screen TODO: use  if(event.getEdgeFlags()==MotionEvent.EDGE_RIGHT) instead?
                        if (event.getRawX() >= mainImageView.getWidth() - 30) {
                            startConnectionProcess(event.getRawX(), event.getRawY(), Direction.RIGHT);
                        }
                        if (event.getRawX() <= 30) {
                            startConnectionProcess(event.getRawX(), event.getRawY(), Direction.LEFT);
                        }
                        if (event.getRawY() <= 30) {
                            startConnectionProcess(event.getRawX(), event.getRawY(), Direction.UP);
                        }
                        if (event.getRawY() >= mainImageView.getHeight() - 30) {
                            startConnectionProcess(event.getRawX(), event.getRawY(), Direction.DOWN);
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    //animation stuff
                    if (!swipe.isOffScreen()) {
                        ObjectAnimator animationX = ObjectAnimator.ofFloat(crossIcon, "x", crossIcon.getX(), crossIconXCentered);
                        ObjectAnimator animationY = ObjectAnimator.ofFloat(crossIcon, "y", crossIcon.getY(), crossIconYCentered);
                        AnimatorSet animSetXY = new AnimatorSet();
                        animSetXY.playTogether(animationX, animationY);
                        animSetXY.setDuration(1000);
                        animSetXY.setInterpolator(new OvershootInterpolator());
                        animSetXY.start();
                    }

                    crossIconMoving = false;
                    break;
            }
        } else if(v.equals(mainImageView)){//Swipe onto screen

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Vector2d swipeStartPoint;
                    if (event.getRawX() <= 100) {
                    //touch on left side
                        swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        swipe = new Swipe(false, Direction.RIGHT, swipeStartPoint);
                    } else if(event.getRawX() >= mainImageView.getWidth()-100){
                    //touch on right side
                        swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        swipe = new Swipe(false, Direction.LEFT, swipeStartPoint);
                    } else if(event.getRawY() <= 100){
                    //touch on top of screen
                        swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        swipe = new Swipe(false, Direction.DOWN, swipeStartPoint);
                    } else if(event.getRawY() >= mainImageView.getHeight()-100){
                    //touch on bottom of screen
                        swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        swipe = new Swipe(false, Direction.UP, swipeStartPoint);
                    } else {
                    //touch on center of screen
                        toggleFullScreen();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(isSwiping){

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(swipe != null){
                        if(swipe.inProgress()) {
                            swipe.setSwipeEndPoint( new Vector2d(event.getRawX(), event.getRawY()));
                            Log.d("Swipe", swipe.toString());
                        }
                    }
                    break;
            }
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiDirectManager.getReceiver(), intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiDirectManager.getReceiver());
    }

    private void buildAddButton(){
        fabAddImage = (FloatingActionButton) findViewById(R.id.action_button);
        fabAddImage.setButtonColor(getResources().getColor(R.color.accent));
        fabAddImage.setButtonColorPressed(getResources().getColor(R.color.accent_dark));
        fabAddImage.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));//TODO: Deprecated
        fabAddImage.setAnimationOnShow(FloatingActionButton.Animations.FADE_IN);
        fabAddImage.setAnimationOnHide(FloatingActionButton.Animations.FADE_OUT);
        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Select an Image.",
                        Toast.LENGTH_SHORT).show();

                //select image from phone
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                toggleFullScreen();
            }
        });
    }

    private void buildCrossIcon(){
        crossIcon = (FloatingActionButton) findViewById(R.id.cross_icon);
        crossIcon.setButtonColor(getResources().getColor(R.color.accent));
        crossIcon.setButtonColorPressed(getResources().getColor(R.color.accent_dark));
        crossIcon.setImageDrawable(getResources().getDrawable(R.drawable.crossarrowbuttonorange));//TODO: Deprecated
        crossIcon.setAnimationOnShow(FloatingActionButton.Animations.FADE_IN);
        crossIcon.setAnimationOnHide(FloatingActionButton.Animations.FADE_OUT);

        //TODO:Ugly!

        crossIcon.setOnTouchListener(this);
        crossIcon.hide();
    }

    private void startConnectionProcess(float x, float y, Direction dir){
        Vector2d centerPoint = new Vector2d(mainImageView.getWidth() / 2, mainImageView.getHeight() / 2);
        Vector2d edgePoint = new Vector2d(x, y);
        swipe = new Swipe(true, dir, centerPoint, edgePoint);
        Log.d("Swipe", swipe.toString());

       wifiDirectManager.discoverPeers();
    }
}
