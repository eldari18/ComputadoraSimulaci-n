/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.CPU;

/**
 *
 * @author 57313
 */
public class UC {

    private String instruccionActual; // Instrucción decodificada

    public void decodificar(String instruccion) {
        this.instruccionActual = instruccion;
        // Lógica de decodificación...
    }

    public String getInstruccionActual() {
        return instruccionActual;
    }
}
