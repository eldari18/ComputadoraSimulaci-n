/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.CPU;

/**
 *
 * @author 57313
 */
public class PC {

    private int direccion; // Dirección actual

    public void incrementar() {
        direccion++;
    }

    public void setDireccion(int dir) {
        this.direccion = dir;
    }

    public int getDireccion() {
        return direccion;
    }
}
