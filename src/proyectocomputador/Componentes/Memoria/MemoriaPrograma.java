/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Memoria;

import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import proyectocomputador.Instruccion;

/**
 *
 * @author 57313
 */
public class MemoriaPrograma {

    private String[][] programas; // Matriz para almacenar Dir, DirB, Instruccion, Programa
    private DefaultTableModel tableModel;
    private Instruccion instruccion;
    private static final Map<String, String> CODOPS_BINARIOS = new HashMap<>();
    static {
        CODOPS_BINARIOS.put("MOV", "0000");
        CODOPS_BINARIOS.put("ADD", "0001");
        CODOPS_BINARIOS.put("SUB", "0010");
        CODOPS_BINARIOS.put("MPY", "0011");
        CODOPS_BINARIOS.put("DIV", "0100");
        CODOPS_BINARIOS.put("CMP", "0101");
        CODOPS_BINARIOS.put("JMP", "0110");
        CODOPS_BINARIOS.put("JZ",  "0111");
        CODOPS_BINARIOS.put("JNZ", "1000");
    }
    private static final Map<String, String> REGISTROS_BINARIOS = new HashMap<>();
    static {
        REGISTROS_BINARIOS.put("AX", "100000");
        REGISTROS_BINARIOS.put("BX", "100001");
        REGISTROS_BINARIOS.put("CX", "100010");
        REGISTROS_BINARIOS.put("DX", "100011");
        REGISTROS_BINARIOS.put("AL", "100100");
        REGISTROS_BINARIOS.put("BL", "100101");
        REGISTROS_BINARIOS.put("CL", "100110");
        REGISTROS_BINARIOS.put("DL", "100111");
        REGISTROS_BINARIOS.put("AH", "101000");
        REGISTROS_BINARIOS.put("BH", "101001");
        REGISTROS_BINARIOS.put("CH", "101010");
        REGISTROS_BINARIOS.put("DH", "101011");
        // Puedes agregar más si necesitas
    }

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
    
    public String leerProgramaBin(int direccion){
        String instr = leerPrograma(direccion)[2];
        String[] partes = instr.split("\\s*,\\s*|\\s+");
        String codop = CODOPS_BINARIOS.getOrDefault(partes[0], "XXXX");
        String destino = "";
        String origen = "";
        if(partes[1].matches(instruccion.PATRON_REGISTRO)){
            destino = REGISTROS_BINARIOS.getOrDefault(partes[1], "XXXX");
        }else{
            int numDes = Integer.parseInt(partes[1].substring(1, partes[1].length() - 1));
            destino = String.format("%6s", Integer.toBinaryString(numDes)).replace(' ', '0');
        }
        if(partes[2].matches(instruccion.PATRON_DIRECCION)){
            int numOri = Integer.parseInt(partes[2].substring(1, partes[2].length() - 1));
            origen = String.format("%11s", Integer.toBinaryString(numOri)).replace(' ', '0');
        }else{
            int numOri = Integer.parseInt(partes[2]);
            origen = String.format("%9s", Integer.toBinaryString(numOri)).replace(' ', '0');
            origen = "01"+ origen;
        }
        return codop+destino+origen;
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
