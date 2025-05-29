/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Registros;

/**
 *
 * @author 57313
 */
public class IR {

    private int instruccion; // Instrucción actual

    public void cargar(int instruccion) {
        this.instruccion = instruccion;
    }

    public int getInstruccion() {
        return instruccion;
    }
}
