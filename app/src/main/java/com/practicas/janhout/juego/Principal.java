package com.practicas.janhout.juego;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


public class Principal extends Activity {

    private TextView puntuacion;

    private static final int NUEVO_JUEGO = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == NUEVO_JUEGO){
            int puntuacion = data.getIntExtra(getString(R.string.puntuacion), 0);
            guardarPuntuacion(puntuacion);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_principal);
        Button boton = (Button) findViewById(R.id.boton_nuevo);
        Animation parpadeoBoton = AnimationUtils.loadAnimation(this, R.anim.parpadeo);
        boton.startAnimation(parpadeoBoton);
        puntuacion = (TextView)findViewById(R.id.tv_puntuacion);
        puntuacion.setText(leerPuntuacion()+"");
    }

    private void guardarPuntuacion(int puntuacion){
        int punt_max = leerPuntuacion();
        if(puntuacion > punt_max) {
            SharedPreferences pc;
            SharedPreferences.Editor ed;
            pc = getSharedPreferences(getString(R.string.puntuacion), MODE_PRIVATE);
            ed = pc.edit();
            ed.putInt(getString(R.string.puntuacion), puntuacion);
            ed.apply();
            this.puntuacion.setText(puntuacion+"");
        }
    }

    private int leerPuntuacion(){
        SharedPreferences pc;
        pc = getSharedPreferences(getString(R.string.puntuacion), MODE_PRIVATE);
        return pc.getInt(getString(R.string.puntuacion), 0);
    }

    public void nuevoJuego(View v){
        Intent i = new Intent(this, ActividadJuego.class);
        startActivityForResult(i, NUEVO_JUEGO);
    }
}