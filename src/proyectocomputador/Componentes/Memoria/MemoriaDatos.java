/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 57313
 */
public class MemoriaDatos {

    private String[][] datos; // Matriz para almacenar Dir, DirB, Valor, ValorB
    private DefaultTableModel tableModel;

    public MemoriaDatos(int capacidad) {
        datos = new String[capacidad][4];

        // Inicializar los datos
        for (int i = 0; i < capacidad; i++) {
            datos[i][0] = String.valueOf(i);               // Dir en decimal
            datos[i][1] = Integer.toBinaryString(i);        // DirB en binario
            datos[i][2] = "0";                             // Valor inicial 0
            datos[i][3] = String.format("%8s", Integer.toBinaryString(0))
                    .replace(' ', '0');          // ValorB en binario (8 bits)
        }

        // Crear modelo de tabla
        tableModel = new DefaultTableModel(
                new Object[]{"Dir", "DirB", "Valor", "ValorB"},
                capacidad
        );

        actualizarModeloTabla();
    }

    public void escribir(int direccion, int valor) {
        if (direccion >= 0 && direccion < datos.length) {
            datos[direccion][2] = String.valueOf(valor);    // Valor en decimal
            datos[direccion][3] = String.format("%8s", Integer.toBinaryString(valor & 0xFF))
                    .replace(' ', '0');     // ValorB en binario (8 bits)
            actualizarModeloTabla();
        }
    }

    public int leer(int direccion) {
        if (direccion >= 0 && direccion < datos.length) {
            return Integer.parseInt(datos[direccion][2]);
        }
        return 0;
    }

    private void actualizarModeloTabla() {
        for (int i = 0; i < datos.length; i++) {
            tableModel.setValueAt(datos[i][0], i, 0); // Dir
            tableModel.setValueAt(datos[i][1], i, 1); // DirB
            tableModel.setValueAt(datos[i][2], i, 2); // Valor
            tableModel.setValueAt(datos[i][3], i, 3); // ValorB
        }
    }

    public void configurarAnchoColumnas(JTable tabla) {
        // Configurar anchos preferidos para cada columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);  // Dir (más estrecha)
        tabla.getColumnModel().getColumn(1).setPreferredWidth(60);  // DirB
        tabla.getColumnModel().getColumn(2).setPreferredWidth(60);  // Valor
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // ValorB (más ancha)

    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
