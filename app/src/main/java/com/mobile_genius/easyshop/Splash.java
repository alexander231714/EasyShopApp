package com.mobile_genius.easyshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    boolean botonAtrasPresionado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_splash);

        Handler manejador = new Handler();
        manejador.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!botonAtrasPresionado){
                    finish();
                    Intent ocambiar = new Intent(Splash.this, MainActivity.class);
                    startActivity(ocambiar);
                }
            }
        },3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        botonAtrasPresionado = true;
    }
}