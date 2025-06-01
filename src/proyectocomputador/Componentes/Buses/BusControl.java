/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Buses;

/**
 *
 * @author 57313
 */
public class BusControl {

    private String señal; // Ej: "read", "write", "fetch"

    // Métodos
    public void setSeñal(String señal) {
        this.señal = señal;
    }

    public String getSeñal() {
        return señal;
    }
}
