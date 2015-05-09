package com.doemski.displaytiling;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.software.shell.fab.FloatingActionButton;



public class MainActivity extends ActionBarActivity implements View.OnTouchListener{

    ImageView mainImageView;
    FloatingActionButton fabAddImage;
    FloatingActionButton crossIcon;
    boolean isFullScreen;
    float crossIconX,crossIconY=0.0f;
    float crossIconXCentered,crossIconYCentered;//TODO:calculate instead of global var
    boolean crossIconMoving,crossIconSwipedOffScreen;//TODO:make local somehow
    boolean togglingFullscreenFirstTime=true;//TODO:change this! needed to determine crossIconXCentered,crossIconYCentered
    //TODO: make those local somehow.. all for swiping onto screen
    boolean isSwiping=false;
    Vector2d swipeStartPoint;
    char swipeDir;
    float enterAngle, leaveAngle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mainImageView = (ImageView) findViewById(R.id.mainImageView);

       //floating action button to choose image
        fabAddImage = (FloatingActionButton) findViewById(R.id.action_button);
        fabAddImage.setButtonColor(getResources().getColor(R.color.accent));
        fabAddImage.setButtonColorPressed(getResources().getColor(R.color.accent_dark));
        fabAddImage.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));
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


        crossIcon = (FloatingActionButton) findViewById(R.id.cross_icon);
        crossIcon.setButtonColor(getResources().getColor(R.color.accent));
        crossIcon.setButtonColorPressed(getResources().getColor(R.color.accent_dark));
        crossIcon.setImageDrawable(getResources().getDrawable(R.drawable.crossarrowbuttonorange));
        crossIcon.setAnimationOnShow(FloatingActionButton.Animations.FADE_IN);
        crossIcon.setAnimationOnHide(FloatingActionButton.Animations.FADE_OUT);

        //TODO:Ugly!

        crossIcon.setOnTouchListener(this);
        crossIcon.hide();

        mainImageView.setOnTouchListener(this);
        /*mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullScreen();
            }

        });*/
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
    void toggleFullScreen(){
        if(!isFullScreen){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            getSupportActionBar().hide();
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
                crossIconSwipedOffScreen=false;
            }



            isFullScreen =true;

        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getSupportActionBar().show();
            fabAddImage.show();
            crossIcon.hide();
            isFullScreen =false;
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
            Vector2d center, edge;
            float angle;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    crossIconMoving = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (crossIconMoving && !crossIconSwipedOffScreen) {
                        crossIconX = event.getRawX() - crossIcon.getWidth() / 2;
                        crossIconY = event.getRawY() - crossIcon.getHeight() / 2;
                        crossIcon.setX(crossIconX);
                        crossIcon.setY(crossIconY);

                        //dragging the crossIcon off the screen TODO: use  if(event.getEdgeFlags()==MotionEvent.EDGE_RIGHT) instead?
                        if (event.getRawX() >= mainImageView.getWidth() - 30) {

                            center = new Vector2d(mainImageView.getWidth() / 2, mainImageView.getHeight() / 2);
                            edge = new Vector2d(event.getRawX(), event.getRawY());
                            angle = center.angle(edge, 'r');

                            Toast.makeText(getApplicationContext(), "Swiped Off Screen Right " + angle,
                                    Toast.LENGTH_SHORT).show();

                            this.leaveAngle = angle;
                            crossIconSwipedOffScreen = true;
                        }

                        if (event.getRawX() <= 30) {

                            center = new Vector2d(mainImageView.getWidth() / 2, mainImageView.getHeight() / 2);
                            edge = new Vector2d(event.getRawX(), event.getRawY());
                            angle = center.angle(edge, 'l');

                            Toast.makeText(getApplicationContext(), "Swiped Off Screen Left " + angle,
                                    Toast.LENGTH_SHORT).show();

                            this.leaveAngle = angle;
                            crossIconSwipedOffScreen = true;
                        }

                        if (event.getRawY() <= 30) {

                            center = new Vector2d(mainImageView.getWidth() / 2, mainImageView.getHeight() / 2);
                            edge = new Vector2d(event.getRawX(), event.getRawY());
                            angle = center.angle(edge, 't');

                            Toast.makeText(getApplicationContext(), "Swiped Off Screen Top " + angle,
                                    Toast.LENGTH_SHORT).show();

                            this.leaveAngle = angle;
                            crossIconSwipedOffScreen = true;
                        }

                        if (event.getRawY() >= mainImageView.getHeight() - 30) {

                            center = new Vector2d(mainImageView.getWidth() / 2, mainImageView.getHeight() / 2);
                            edge = new Vector2d(event.getRawX(), event.getRawY());
                            angle = center.angle(edge, 'b');

                            Toast.makeText(getApplicationContext(), "Swiped Off Screen Bottom " + angle,
                                    Toast.LENGTH_SHORT).show();

                            this.leaveAngle = angle;
                            crossIconSwipedOffScreen = true;
                        }

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //animation stuff
                    if (!crossIconSwipedOffScreen) {
                        ObjectAnimator animationX = ObjectAnimator.ofFloat(crossIcon, "x", crossIcon.getX(), crossIconXCentered);
                        ObjectAnimator animationY = ObjectAnimator.ofFloat(crossIcon, "y", crossIcon.getY(), crossIconYCentered);
                        AnimatorSet animSetXY = new AnimatorSet();
                        animSetXY.playTogether(animationX, animationY);
                        animSetXY.setDuration(1000);
                        animSetXY.setInterpolator(new OvershootInterpolator());
                        animSetXY.start();
                    }
                /*if(event.getEdgeFlags()==MotionEvent.EDGE_RIGHT){
                    Toast.makeText(getApplicationContext(), "Swiped Off Screen Bottom",
                            Toast.LENGTH_SHORT).show();
                }*/

                    crossIconMoving = false;
                    break;
            }
        } else if(v.equals(mainImageView)){//Swipe onto screen

            Vector2d swipeEndPoint;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getRawX() <= 100) {//touch on left side
                        this.swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        this.swipeDir='l';
                        this.isSwiping=true;
                    } else if(event.getRawX() >= mainImageView.getWidth()-100){//touch on right side
                        this.swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        this.swipeDir='r';
                        this.isSwiping=true;
                    } else if(event.getRawY() <= 100){//touch on top of screen
                        this.swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        this.swipeDir='t';
                        this.isSwiping=true;
                    } else if(event.getRawY() >= mainImageView.getHeight()-100){//touch on bottom of screen
                        this.swipeStartPoint = new Vector2d(event.getRawX(),event.getRawY());
                        this.swipeDir='b';
                        this.isSwiping=true;
                    } else {//touch on center of screen
                        toggleFullScreen();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(this.isSwiping){

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(this.isSwiping) {
                        swipeEndPoint = new Vector2d(event.getRawX(), event.getRawY());

                        if (this.swipeDir == 'l' || this.swipeDir == 'r') {
                            Toast.makeText(getApplicationContext(), "Angle: " + this.swipeStartPoint.angle(swipeEndPoint, 'l'),
                                    Toast.LENGTH_SHORT).show();

                            this.enterAngle = this.swipeStartPoint.angle(swipeEndPoint, 'l');
                        } else if (this.swipeDir == 't' || this.swipeDir == 'b') {
                            Toast.makeText(getApplicationContext(), "Angle: " + this.swipeStartPoint.angle(swipeEndPoint, 't'),
                                    Toast.LENGTH_SHORT).show();
                            this.enterAngle = this.swipeStartPoint.angle(swipeEndPoint, 't');
                        }
                        this.isSwiping=false;
                    } else {
                        Toast.makeText(getApplicationContext(), "click on screen",
                                Toast.LENGTH_SHORT).show();
                        this.isSwiping=false;
                    }
                    break;
            }
        }

        return true;
    }
}
