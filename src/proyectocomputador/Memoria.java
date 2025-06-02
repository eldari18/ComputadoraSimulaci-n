/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador;

import javax.swing.JTable;
import proyectocomputador.Componentes.Memoria.MemoriaDatos;
import proyectocomputador.Componentes.Memoria.MemoriaPrograma;

/**
 *
 * @author 57313
 */
public class Memoria {

    private MemoriaDatos memoriaDatos;
    private MemoriaPrograma memoriaProgramas;

    public Memoria() {
        memoriaDatos = new MemoriaDatos(16); // 16 registros
        memoriaProgramas = new MemoriaPrograma(16); // 16 registros
    }

    public void configurarTablaDatos(JTable tabla) {
        tabla.setModel(memoriaDatos.getTableModel());
        memoriaDatos.configurarAnchoColumnas(tabla); // Aplicar configuración de anchos
    }

    public void configurarTablaProgramas(JTable tabla) {
        // Configurar ancho de columnas
        tabla.setModel(memoriaProgramas.getTableModel());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);  // Dir
        tabla.getColumnModel().getColumn(1).setPreferredWidth(60); // DirB
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120); // Instrucción
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);  // Programa
    }

    public void escribirDato(int direccion, int valor) {
        memoriaDatos.escribir(direccion, valor);
    }

    public int leerDato(int direccion) {
        return memoriaDatos.leer(direccion);
    }

    public void escribirPrograma(int direccion, String instruccion, String programa) {
        memoriaProgramas.escribirPrograma(direccion, instruccion, programa);
    }

    public String[] leerPrograma(int direccion) {
        return memoriaProgramas.leerPrograma(direccion);
    }
    
    public String leerProgramaBinario(int direccion){
        return memoriaProgramas.leerProgramaBin(direccion);
    }
}
