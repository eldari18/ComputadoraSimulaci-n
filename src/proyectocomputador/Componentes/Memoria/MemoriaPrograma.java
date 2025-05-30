/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 57313
 */
public class MemoriaPrograma {

    private String[][] programas; // Matriz para almacenar Dir, DirB, Instruccion, Programa
    private DefaultTableModel tableModel;

    public MemoriaPrograma(int capacidad) {
        programas = new String[capacidad][4];

        // Inicializar los datos
        for (int i = 0; i < capacidad; i++) {
            programas[i][0] = String.valueOf(i);               // Dir en decimal
            programas[i][1] = String.format("%4s", Integer.toBinaryString(i))
                    .replace(' ', '0');           // DirB en binario (4 bits)
            programas[i][2] = "NOP";                          // Instrucción por defecto
            programas[i][3] = "Sistema";                      // Programa por defecto
        }

        // Crear modelo de tabla con 4 columnas
        tableModel = new DefaultTableModel(
                new Object[]{"Dir", "DirB", "Instrucción", "Programa"},
                capacidad
        );

        actualizarModeloTabla();
    }

    public void escribirPrograma(int direccion, String instruccion, String programa) {
        if (direccion >= 0 && direccion < programas.length) {
            programas[direccion][2] = instruccion;
            programas[direccion][3] = programa;
            actualizarModeloTabla();
        }
    }

    public String[] leerPrograma(int direccion) {
        if (direccion >= 0 && direccion < programas.length) {
            return new String[]{
                programas[direccion][0], // Dir
                programas[direccion][1], // DirB
                programas[direccion][2], // Instrucción
                programas[direccion][3] // Programa
            };
        }
        return new String[]{"0", "0000", "NOP", "Sistema"};
    }

    private void actualizarModeloTabla() {
        for (int i = 0; i < programas.length; i++) {
            tableModel.setValueAt(programas[i][0], i, 0); // Dir
            tableModel.setValueAt(programas[i][1], i, 1); // DirB
            tableModel.setValueAt(programas[i][2], i, 2); // Instrucción
            tableModel.setValueAt(programas[i][3], i, 3); // Programa
        }
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
