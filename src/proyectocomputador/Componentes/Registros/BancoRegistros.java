package proyectocomputador.Componentes.Registros;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BancoRegistros {

    private String[][] registros; // Matriz para almacenar Nombre, Valor, ValorB
    private DefaultTableModel tableModel;

    // Nombres de los registros (12 registros)
    private final String[] NOMBRES_REGISTROS = {
        "AH", "AL", "AX",
        "BH", "BL", "BX",
        "CH", "CL", "CX",
        "DH", "DL", "DX"
    };

    public BancoRegistros() {
        registros = new String[NOMBRES_REGISTROS.length][3];
        inicializarRegistros();
        inicializarTabla();
    }

    private void inicializarRegistros() {
        for (int i = 0; i < NOMBRES_REGISTROS.length; i++) {
            registros[i][0] = NOMBRES_REGISTROS[i]; // Nombre del registro
            registros[i][1] = "0";                  // Valor en decimal
            registros[i][2] = "00000000";           // Valor en binario (8 bits)
        }
    }

    private void inicializarTabla() {
        tableModel = new DefaultTableModel(
                new Object[]{"Registro", "Valor", "ValorB"},
                NOMBRES_REGISTROS.length
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        actualizarModeloTabla();
    }

    private void actualizarModeloTabla() {
        for (int i = 0; i < registros.length; i++) {
            tableModel.setValueAt(registros[i][0], i, 0); // Nombre
            tableModel.setValueAt(registros[i][1], i, 1); // Valor
            tableModel.setValueAt(registros[i][2], i, 2); // ValorB
        }
    }

    public void setValorRegistro(String nombreRegistro, int valor) {
        for (int i = 0; i < NOMBRES_REGISTROS.length; i++) {
            if (NOMBRES_REGISTROS[i].equalsIgnoreCase(nombreRegistro)) {
                registros[i][1] = String.valueOf(valor); // Valor decimal
                registros[i][2] = String.format("%8s", Integer.toBinaryString(valor & 0xFF))
                        .replace(' ', '0');    // Valor binario
                actualizarModeloTabla();
                break;
            }
        }
    }

    public int getValorRegistro(String nombreRegistro) {
        for (int i = 0; i < NOMBRES_REGISTROS.length; i++) {
            if (NOMBRES_REGISTROS[i].equalsIgnoreCase(nombreRegistro)) {
                return Integer.parseInt(registros[i][1]);
            }
        }
        return 0;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void configurarTabla(JTable tabla) {
        tabla.setModel(tableModel);
        // Configurar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  // Nombre
        tabla.getColumnModel().getColumn(1).setPreferredWidth(60);  // Valor
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100); // ValorB
    }
}
