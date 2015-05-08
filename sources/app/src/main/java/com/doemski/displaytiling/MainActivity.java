package com.doemski.displaytiling;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.software.shell.fab.FloatingActionButton;


public class MainActivity extends ActionBarActivity {

    ImageView mainImageView;
    FloatingActionButton fabAddImage;
    FloatingActionButton crossIcon;
    boolean isFullScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        crossIcon.hide();

        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullScreen();
            }

        });
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

    void toggleFullScreen(){
        if(!isFullScreen){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            getSupportActionBar().hide();
            fabAddImage.hide();
            crossIcon.show();
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
}
