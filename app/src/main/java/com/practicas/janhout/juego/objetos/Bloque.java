package com.practicas.janhout.juego.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bloque {

    private Bitmap bmp;
    private int ancho, alto;
    private float ejeX, ejeY;
    private int anchoVista, altoVista;
    private int golpes;

    /* ************************************************************************* */
    /* *********** Constructor ************************************************* */
    /* ************************************************************************* */

    public Bloque(Bitmap bmp, int anchoV, int altoV, float posX, float posY, int golpes) {
        this.bmp = bmp;
        this.ancho = bmp.getWidth();
        this.alto = bmp.getHeight();
        this.anchoVista = anchoV;
        this.altoVista = altoV;
        this.golpes = golpes;
        this.ejeX = posX;
        this.ejeY = posY;
    }

    /* ************************************************************************* */
    /* *********** Dibujar Bloque ********************************************** */
    /* ************************************************************************* */

    public void dibujar(Canvas canvas){
        canvas.drawBitmap(this.bmp, this.ejeX, this.ejeY, null);
    }

    /* ************************************************************************* */
    /* *********** Baja Bloque ************************************************* */
    /* ************************************************************************* */

    public void mover(){
        this.ejeY = this.ejeY + alto;
    }

    /* ************************************************************************* */
    /* *********** Gestión colisión Bloque ************************************* */
    /* ************************************************************************* */

    public void colision(){
        this.golpes = this.golpes - 1;
    }

    /* ************************************************************************* */
    /* *********** Getters y Setters ******************************************* */
    /* ************************************************************************* */

    public int getGolpes() {
        return golpes;
    }

    public float getEjeX() {
        return ejeX;
    }

    public float getEjeY() {
        return ejeY;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}
