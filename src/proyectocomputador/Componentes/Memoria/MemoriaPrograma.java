/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

/**
 *
 * @author 57313
 */
public class MemoriaPrograma {

    private int[] instrucciones; // Array de instrucciones

    public MemoriaPrograma(int capacidad) {
        instrucciones = new int[capacidad];
    }

    public int leer(int direccion) {
        return instrucciones[direccion];
    }

    public void escribir(int direccion, int instruccion) {
        instrucciones[direccion] = instruccion;
    }
}
