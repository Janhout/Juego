package com.practicas.janhout.juego.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class Pelota {

    private Bitmap bmp;
    private int ancho, alto;
    private float ejeX, ejeY;
    private float direccionX, direccionY;
    private int anchoVista, altoVista;

    /* ************************************************************************* */
    /* *********** Constructor Palota ****************************************** */
    /* ************************************************************************* */

    public Pelota(Bitmap bmp, int ancho, int alto) {
        this.bmp = bmp;
        this.ancho = bmp.getWidth();
        this.alto = bmp.getHeight();
        this.anchoVista = ancho;
        this.altoVista = alto;
        ejeX = this.anchoVista / 2;
        ejeY = this.altoVista / 2;
        direccionX = 1;
        direccionY = 5;
    }

    /* ************************************************************************* */
    /* *********** Dibujar la pelota ******************************************* */
    /* ************************************************************************* */

    public boolean dibujar(Canvas canvas) {
        boolean seguir = movimiento();
        canvas.drawBitmap(bmp, ejeX, ejeY, null);
        return seguir;
    }

    /* ************************************************************************* */
    /* *********** Mover la pelota ********************************************* */
    /* ************************************************************************* */

    private boolean movimiento() {
        if (ejeX > anchoVista - ancho - direccionX ||
                ejeX + direccionX < 0) {
            direccionX = -direccionX;
        }
        ejeX = ejeX + direccionX;
        if (ejeY + direccionY < altoVista * 6 / 100) {
            direccionY = -direccionY;
        } else if (ejeY > altoVista - alto - direccionY) {
            return false;
        }
        ejeY = ejeY + direccionY;
        return true;
    }

    public void aumentarVelocidad() {
        if (direccionY > 0 && direccionY < 12) {
            direccionY = direccionY + 1;
        } else if(direccionY > -12){
            direccionY = direccionY - 1;
        }
    }

    /* ************************************************************************* */
    /* *********** COLISIONES ************************************************** */
    /* ************************************************************************* */

    /* ************************************************************************* */
    /* *********** Colision Plataforma ***************************************** */
    /* ************************************************************************* */

    public boolean colisionPlataforma(float ejeXPlataforma, float ejeYPlataforma,
                                      int anchoPlataforma, int altoPlataforma,
                                      Bitmap bmpPlataforma, float movimientoXPlataforma) {
        boolean colision = false;
        if (direccionY > 0) {
            colision = colisiones(bmpPlataforma, ejeXPlataforma, ejeYPlataforma,
                    anchoPlataforma, altoPlataforma, anchoPlataforma / 2, ancho/2);

            if (colision) {
                /*if(ejeX + ancho/2 <= ejeXPlataforma + 5*anchoPlataforma/100
                        && ejeX + ancho/2 >= ejeXPlataforma - 5*anchoPlataforma/100){
                }else */
                if(ejeX + ancho/2 > ejeXPlataforma + 5*anchoPlataforma/100){
                    direccionX = (ejeX + ancho/2 - ejeXPlataforma + 5*anchoPlataforma/100)
                            *(4.5f/anchoPlataforma)+1;
                    if(movimientoXPlataforma > 0){
                        direccionX = direccionX + 3;
                    }
                } else if(ejeX + ancho/2 < ejeXPlataforma - 5*anchoPlataforma/100){
                    direccionX = (ejeX + ancho/2 - ejeXPlataforma + 5*anchoPlataforma/100)
                            *(4.5f/anchoPlataforma)-1;
                    if(movimientoXPlataforma < 0){
                        direccionX = direccionX - 3;
                    }
                }
                direccionY = -direccionY;
            }
        }
        return colision;
    }

    /* ************************************************************************* */
    /* *********** Colisión Bloques ******************************************** */
    /* ************************************************************************* */

    public boolean colisionBloque(float ejeXBloque, float ejeYBloque, int anchoBloque,
                                  int altoBloque, Bitmap bmpBloque) {
        boolean colision;
        colision = colisiones(bmpBloque, ejeXBloque, ejeYBloque, anchoBloque, altoBloque, 0, 0);
        if (colision) {
            if (direccionY < 0) {
                if (this.ejeY + this.alto / 2 == ejeYBloque + altoBloque) {
                    direccionX = -direccionX;
                } else if (this.ejeY + this.alto / 2 < ejeYBloque + altoBloque) {
                    direccionX = -direccionX;
                } else {
                    direccionY = -direccionY;
                }
            } else {
                if (this.ejeY + this.alto / 2 == ejeYBloque) {
                    direccionX = -direccionX;
                } else if (this.ejeY + this.alto / 2 > ejeYBloque) {
                    direccionX = -direccionX;
                } else {
                    direccionY = -direccionY;
                }
            }
        }

        return colision;
    }

    /* ************************************************************************* */
    /* *********** Busca colisión ********************************************** */
    /* ************************************************************************* */

    private boolean colisiones(Bitmap bmpObjeto, float ejeXObjeto, float ejeYObjeto,
                               int anchoObjeto, int altoObjeto, float offsetX, float offsetY) {
        boolean colision = false;
        Rect rect1 = new Rect((int) ejeX, (int) (ejeY + offsetY), (int) ejeX + ancho, (int) ejeY + alto);
        Rect rect2 = new Rect((int) (ejeXObjeto - offsetX), (int) ejeYObjeto, (int) (ejeXObjeto + anchoObjeto - offsetX), (int) ejeYObjeto + altoObjeto);

        if (Rect.intersects(rect1, rect2)) {
            Rect col = rectanguloColision(rect1, rect2);
            for (int i = col.left; i < col.right && !colision; i++) {
                for (int j = col.top; j < col.bottom && !colision; j++) {
                    int a = bmpObjeto.getPixel(Math.abs((int) ejeXObjeto - i), Math.abs((int) ejeYObjeto - j));
                    int b = bmp.getPixel(Math.abs((int) ejeX - i), Math.abs((int) ejeY - j));
                    if (colisionReal(a) && colisionReal(b)) {
                        colision = true;
                    }
                }
            }
        }
        return colision;
    }

    private static Rect rectanguloColision(Rect rect1, Rect rect2) {
        int left = Math.max(rect1.left, rect2.left);
        int top = Math.max(rect1.top, rect2.top);
        int right = Math.min(rect1.right, rect2.right);
        int bottom = Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean colisionReal(int pixel) {
        return pixel != Color.TRANSPARENT;
    }
}
