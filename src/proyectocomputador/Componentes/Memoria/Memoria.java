/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

/**
 *
 * @author 57313
 */
public class Memoria {
    private String[] instrucciones;

    public Memoria(String[] instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String leer(int direccion) {
        if (direccion >= 0 && direccion < instrucciones.length) {
            return instrucciones[direccion];
        } else {
            return "NOP"; // Instrucción nula si se sale de rango
        }
    }

    public void cargarPrograma(String[] programa) {
        this.instrucciones = programa;
    }
}

