/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador;

/**
 *
 * @author 57313
 */
import proyectocomputador.Componentes.Buses.BusControl;
import proyectocomputador.Componentes.Buses.BusDatos;
import proyectocomputador.Componentes.Buses.BusDirecciones;

/**
 *
 * @author 5731
 */
public class Buses {

    private BusControl busControl;
    private BusDatos busDatos;
    private BusDirecciones busDirecciones;

    public Buses() {

        busControl = new BusControl();
        busDatos = new BusDatos();
        busDirecciones = new BusDirecciones();

    }
}
