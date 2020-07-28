package com.t.smartLoggerGnss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity {

    // time of animation
    private static int SPLASH_SCREEN = 2000;

    //Variables
    Animation topAnim, bottomAnim;
    ImageView logo, logoEnac;
    TextView name, enac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //elements
        logo = findViewById(R.id.logo);
        name = findViewById(R.id.name);
        logoEnac = findViewById(R.id.logoEnac);
        enac = findViewById(R.id.enac);

        logo.setAnimation(topAnim);

        //start animation and open MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);


    }
}
