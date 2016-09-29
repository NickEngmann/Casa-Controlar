package io.particle.cloudsdk.example_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.button;
import static io.particle.cloudsdk.example_app.R.styleable.View;


public class SplashActivity extends AppCompatActivity {
    ImageView imageView3;
    TextView textView3;
    Animation animationfadein;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView3 = (ImageView)findViewById(R.id.imageView3);
        textView3 = (TextView)findViewById(R.id.textview3);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textView3.setAnimation(anim);
        imageView3.setAnimation(anim);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

}
