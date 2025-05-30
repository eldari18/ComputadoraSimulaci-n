package proyectocomputador.Componentes.Registros;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BancoRegistros {

    private JTable tablaRegistros;
    private DefaultTableModel modelo;
    private int[] valoresRegistros;

    // Nombres de los registros (12 registros: AH, AL, AX, BH, BL, BX, CH, CL, CX, DH, DL, DX)
    private final String[] NOMBRES_REGISTROS = {
        "AH", "AL", "AX",
        "BH", "BL", "BX",
        "CH", "CL", "CX",
        "DH", "DL", "DX"
    };

    public BancoRegistros() {
        valoresRegistros = new int[NOMBRES_REGISTROS.length];
        inicializarTabla();
        inicializarRegistros();
    }

    private void inicializarTabla() {
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer todas las celdas no editables
            }
        };

        // Configurar columnas
        modelo.addColumn("Registro");
        modelo.addColumn("Valor");

        // Añadir filas para cada registro
        for (String reg : NOMBRES_REGISTROS) {
            modelo.addRow(new Object[]{reg, 0});
        }

        // Crear la tabla con el modelo
        tablaRegistros = new JTable(modelo);
    }

    private void inicializarRegistros() {
        // Inicializar todos los registros a 0
        for (int i = 0; i < valoresRegistros.length; i++) {
            valoresRegistros[i] = 0;
        }
    }

    public JTable getTablaRegistros() {
        return tablaRegistros;
    }

    public void setValorRegistro(String nombreRegistro, int valor) {
        // Buscar el registro por nombre y actualizar su valor
        for (int i = 0; i < NOMBRES_REGISTROS.length; i++) {
            if (NOMBRES_REGISTROS[i].equalsIgnoreCase(nombreRegistro)) {
                valoresRegistros[i] = valor;
                actualizarTabla(i, valor);
                break;
            }
        }
    }

    private void actualizarTabla(int indiceRegistro, int valor) {
        // Actualizar la fila correspondiente en la tabla
        modelo.setValueAt(valor, indiceRegistro, 1);  // Columna Valor
    }

    public int getValorRegistro(String nombreRegistro) {
        // Obtener el valor de un registro por nombre
        for (int i = 0; i < NOMBRES_REGISTROS.length; i++) {
            if (NOMBRES_REGISTROS[i].equalsIgnoreCase(nombreRegistro)) {
                return valoresRegistros[i];
            }
        }
        return 0; // Si no se encuentra, devolver 0
    }
}
