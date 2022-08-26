package com.kogo.iroad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewbinding.ViewBindings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.kogo.iroad.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private LottieAnimationView lottieStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         binding = ActivitySplashBinding.inflate(getLayoutInflater());
          setContentView(binding.getRoot());

       // lottieStart=findViewById(R.id.lottieStart);

        Handler h=new Handler();
        h.postDelayed(new Runnable()
                      {
                          @Override public void run(){
                            //  binding.imageView.setVisibility(View.INVISIBLE);
                              //  lottieStart.setVisibility(View.VISIBLE);
                              Log.e("deneme","deneme");
                               binding.lottieStart.cancelAnimation();
                              Intent intent = new Intent(SplashActivity.this,MapsActivity.class);
                              intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                              startActivity(intent);
                              Log.e("aa","aa");
                              finish();

                          }
                      }
                ,1100);

        View decorView=getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                );


    }


}