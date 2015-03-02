package com.practicas.janhout.juego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.practicas.janhout.juego.objetos.Bloque;
import com.practicas.janhout.juego.objetos.Pelota;
import com.practicas.janhout.juego.objetos.Plataforma;

import java.io.IOException;
import java.util.ArrayList;

public class VistaJuego extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private Bitmap bmpBola;
    private Bitmap bmpPlataforma;
    private Bitmap bmpBloque;

    private int alto, ancho;
    private HebraJuego hebraJuego;

    private SoundPool soundPool;
    private int idBolaPlataforma;
    private int idBolaBloque;
    private int idGameOver;

    private int rebotes;
    private int puntuacion;
    boolean seguir;


    private Pelota pelota;
    private Plataforma plataforma;

    private ArrayList<Bloque[]> matriz;

    private AlertDialog alerta;

    private Context contexto;

    private final static int FILAS = 8;
    private final static int COLUMNAS = 8;

    /* ************************************************************************* */
    /* *********** Constructor ************************************************* */
    /* ************************************************************************* */

    public VistaJuego(Context context) {
        super(context);
        getHolder().addCallback(this);
        hebraJuego = new HebraJuego(this);
        contexto = context;
        puntuacion = 0;
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        matriz = new ArrayList<>(FILAS);
        for (int i = 0; i < FILAS; i++) {
            matriz.add(new Bloque[COLUMNAS]);
        }
        rebotes = 0;
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor s1 = assetManager.openFd("ball_block.wav");
            AssetFileDescriptor s2 = assetManager.openFd("ball_platform.wav");
            AssetFileDescriptor s3 = assetManager.openFd("game_over.wav");
            idBolaBloque = soundPool.load(s1, 1);
            idBolaPlataforma = soundPool.load(s2, 1);
            idGameOver = soundPool.load(s3, 1);
        } catch (IOException e) {
        }
    }

    /* ************************************************************************* */
    /* *********** Dibujar en el canvas **************************************** */
    /* ************************************************************************* */

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (seguir) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            pintaPelota(canvas);
            pintaPlataforma(canvas);
            pintaBloques(canvas);
            pintaPuntuacion(canvas);
        }
    }

    private void pintaBloques(Canvas canvas) {
        limpiarMatriz();
        actualizarMatriz();
        for (int i = 0; i < matriz.size(); i++) {
            for (int j = 0; j < matriz.get(i).length; j++) {
                if (matriz.get(i)[j] != null && matriz.get(i)[j].getGolpes() > 0) {
                    matriz.get(i)[j].dibujar(canvas);
                }
            }
        }
    }

    public void pintaPelota(Canvas canvas) {
        if (pelota != null) {
            colisionPelotaPlataforma();
            colisionPelotaBloques();
            seguir = pelota.dibujar(canvas);
            if (!seguir) {
                soundPool.play(idGameOver, 1, 1, 1, 0, 1);
                mostrarPuntuacion();
            }
        }
    }

    private void pintaPlataforma(Canvas canvas) {
        if (plataforma != null)
            plataforma.dibujar(canvas);
    }

    private void pintaPuntuacion(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.WHITE);

        paint.setTextSize(Math.round(alto * 2.5 / 100));
        int xPos = ancho * 3 / 100;
        int yPos = (int) ((alto * 3 / 100) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(puntuacion + "", xPos, yPos, paint);
    }

    /* ************************************************************************* */
    /* *********** Métodos SurfaceView ***************************************** */
    /* ************************************************************************* */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        alto = height;
        ancho = width;
        crearPelota();
        crearPlataforma();
        crearBloques();
        hebraJuego.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hebraJuego.setFuncionando(true);
        seguir = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pararHebra();
    }

    /* ************************************************************************* */
    /* *********** AUXILIARES ************************************************** */
    /* ************************************************************************* */

    private void actualizarMatriz() {
        if (rebotes == 3) {
            boolean mover = true;
            if (matriz.get(matriz.size() - 1)[0].getEjeY() > this.alto * 65 / 100) {
                mover = false;
            }
            for (int i = 0; i < matriz.size() && mover; i++) {
                for (int j = 0; j < matriz.get(i).length; j++) {
                    if (matriz.get(i)[j] != null) {
                        matriz.get(i)[j].mover();
                    }
                }
            }
            if (mover) {
                matriz.add(0, new Bloque[8]);
            }
            for (int k = 0; k < matriz.get(0).length; k++) {
                matriz.get(0)[k] = new Bloque(bmpBloque, ancho, alto,
                        k * bmpBloque.getWidth(), alto * 6 / 100, 1);
            }
            pelota.aumentarVelocidad();
            rebotes = 0;
        }
    }

    private void crearBloques(){
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bl_1);
        bmpBloque = Bitmap.createScaledBitmap(b, ancho / matriz.get(0).length, alto * 3 / 100, false);
        for (int i = 0; i < matriz.size(); i++) {
            for (int j = 0; j < matriz.get(i).length; j++) {
                matriz.get(i)[j] = new Bloque(bmpBloque, ancho, alto,
                        j * bmpBloque.getWidth(), i * bmpBloque.getHeight() + alto * 6 / 100, 1);
            }
        }
    }

    private void crearPelota(){
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        bmpBola = Bitmap.createScaledBitmap(b, ancho * 5 / 100, ancho * 5 / 100, false);
        pelota = new Pelota(bmpBola, ancho, alto);
    }

    private void crearPlataforma(){
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.plataforma);
        bmpPlataforma = Bitmap.createScaledBitmap(b, ancho * 20 / 100, ancho * 3 / 100, false);
        plataforma = new Plataforma(bmpPlataforma, ancho, alto);
    }

    private void limpiarMatriz() {
        boolean limpio;
        for (int i = 0; i < matriz.size(); i++) {
            limpio = true;
            for (int j = 0; j < matriz.get(i).length && limpio; j++) {
                if (matriz.get(i)[j].getGolpes() > 0) {
                    limpio = false;
                }
            }
            if (limpio) {
                matriz.remove(i);
            }
        }
    }

    private void mostrarPuntuacion() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        alert.setTitle(contexto.getString(R.string.puntuacion));
        LayoutInflater inflater = LayoutInflater.from(contexto);
        final View vista = inflater.inflate(R.layout.dialogo_puntuacion, null);
        alert.setView(vista);
        alert.setCancelable(false);
        TextView texto = (TextView) vista.findViewById(R.id.tvPuntuacion);
        texto.setText(puntuacion + "");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent();
                i.putExtra(contexto.getString(R.string.puntuacion), puntuacion);
                ((Activity) contexto).setResult(Activity.RESULT_OK, i);
                ((Activity) contexto).finish();
            }
        });
        ((Activity) contexto).runOnUiThread(new Runnable() {
            public void run() {
                alerta = alert.create();
                alerta.show();
            }
        });
    }

    private void pararHebra() {
        boolean reintentar = true;
        hebraJuego.setFuncionando(false);
        while (reintentar) {
            try {
                hebraJuego.join();
                reintentar = false;
                soundPool.release();
                soundPool = null;
            } catch (InterruptedException e) {
            }
        }
    }

    /* ************************************************************************* */
    /* *********** Auxiliares colisiones *************************************** */
    /* ************************************************************************* */

    private void colisionPelotaBloques() {
        if (matriz != null) {
            boolean colision = false;
            for (int i = matriz.size() - 1; i >= 0 && !colision; i--) {
                for (int j = matriz.get(i).length - 1; j >= 0 && !colision; j--) {
                    if (matriz.get(i)[j] != null && matriz.get(i)[j].getGolpes() > 0) {
                        colision = pelota.colisionBloque(matriz.get(i)[j].getEjeX(), matriz.get(i)[j].getEjeY(),
                                matriz.get(i)[j].getAncho(), matriz.get(i)[j].getAlto(), bmpBloque);
                        if (colision) {
                            matriz.get(i)[j].colision();
                            puntuacion = puntuacion + 50;
                            soundPool.play(idBolaBloque, 1, 1, 1, 0, 1);
                        }
                    }
                }
            }
        }
    }

    private void colisionPelotaPlataforma() {
        boolean colision = pelota.colisionPlataforma(plataforma.getEjeX(), plataforma.getEjeY(),
                plataforma.getAncho(), plataforma.getAlto(), bmpPlataforma, plataforma.getMovimientoX());
        if (colision) {
            rebotes++;
            soundPool.play(idBolaPlataforma, 1, 1, 1, 0, 1);
        }
    }

    /* ************************************************************************* */
    /* *********** Control Evento Touch **************************************** */
    /* ************************************************************************* */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evento = event.getAction();
        float x;
        x = event.getX();
        switch (evento) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                plataforma.setMovimientoTouch(x);
                break;
            case MotionEvent.ACTION_UP:
                plataforma.setMovimientoTouch(0);
                break;
        }
        return true;
    }

    /* ************************************************************************* */
    /* *********** Control del Acelerómetro ************************************ */
    /* ************************************************************************* */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;

        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
        if (plataforma != null) {
            plataforma.setMovimientoSensor(linear_acceleration[0]);
        }
    }
}