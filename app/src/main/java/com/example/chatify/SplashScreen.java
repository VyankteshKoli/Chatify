package com.example.chatify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView imageView = findViewById(R.id.imageView);

        // Load the zoom animation
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom);
        imageView.startAnimation(zoomIn);

        // Set a listener to redirect to MainActivity after the animation
        zoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do something when the animation starts, if needed
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the MainActivity after the animation ends
                Intent intent = new Intent(SplashScreen.this, LoginPage.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do something when the animation repeats, if needed
            }
        });
    }
}
