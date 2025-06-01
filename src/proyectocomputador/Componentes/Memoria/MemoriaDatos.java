/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

/**
 *
 * @author 57313
 */
public class MemoriaDatos {

    private int[] celdas; // Datos almacenados

    public MemoriaDatos(int capacidad) {
        celdas = new int[capacidad];
    }

    public int leer(int direccion) {
        return celdas[direccion];
    }

    public void escribir(int direccion, int dato) {
        celdas[direccion] = dato;
    }
}
