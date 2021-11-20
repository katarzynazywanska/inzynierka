package com.kontakt.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.kontakt.sample.R;

public class Directions extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        imageView = findViewById(R.id.arrow);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        //blink
        imageView.startAnimation(animation);
    }
}