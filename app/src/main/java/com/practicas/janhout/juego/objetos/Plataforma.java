package com.practicas.janhout.juego.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Plataforma {

    private Bitmap bmp;
    private int ancho, alto;
    private float ejeX, ejeY;
    private float movimientoXS, movimientoXT;
    private int anchoVista, altoVista;

    /* ************************************************************************* */
    /* *********** Constructor ************************************************* */
    /* ************************************************************************* */

    public Plataforma(Bitmap bmp, int ancho, int alto) {
        this.bmp = bmp;
        this.ancho = bmp.getWidth();
        this.alto = bmp.getHeight();
        this.anchoVista = ancho;
        this.altoVista = alto;
        ejeX = this.anchoVista / 2;
        ejeY = (this.altoVista * 80 / 100);
    }

    /* ************************************************************************* */
    /* *********** Dibujar Plataforma ****************************************** */
    /* ************************************************************************* */

    public void dibujar(Canvas canvas) {
        setPosicion();
        canvas.drawBitmap(bmp, ejeX - ancho / 2, ejeY, null);
    }

    private void setPosicion() {
        ejeX = ejeX + movimientoXS + movimientoXT;
        if (ejeX < ancho / 2) {
            ejeX = ancho / 2;
        }
        if (ejeX > anchoVista - ancho / 2) {
            ejeX = anchoVista - ancho / 2;
        }
    }

    /* ************************************************************************* */
    /* *********** Control Movimiento ****************************************** */
    /* ************************************************************************* */


    public void setMovimientoSensor(float x) {
        if ((x > 1f)) {
            movimientoXS = -3.0f;
        } else if ((x < -1.3f)) {
            movimientoXS = 3.0f;
        } else {
            movimientoXS = 0;
        }
    }

    public void setMovimientoTouch(float x) {
        if(x == 0) {
            movimientoXT = 0;
        } else if ((ejeX > x)) {
            movimientoXT = -3.0f;
        } else if ((ejeX < x)) {
            movimientoXT = 3.0f;
        }
    }

    /* ************************************************************************* */
    /* *********** Getters y Setters ******************************************* */
    /* ************************************************************************* */

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public float getEjeX() {
        return ejeX;
    }

    public float getEjeY() {
        return ejeY;
    }

    public float getMovimientoX() {
        return this.movimientoXS + this.movimientoXT;
    }
}
