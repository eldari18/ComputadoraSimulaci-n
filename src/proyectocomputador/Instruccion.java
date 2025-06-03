/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador;
import java.util.Arrays;

/**
 *
 * @author 57313
 */
public class Instruccion {

    public static final String[] CODIGOS_OPERACION = {"ADD", "SUB", "MPY", "DIV", "CMP", "MOV", "JMP", "JZ", "JNZ", "RET"};
    public static final String PATRON_REGISTRO = "(AX|BX|CX|DX|AL|BL|CL|DL|AH|BH|CH|DH)";
    public static final String PATRON_DIRECCION = "\\[(1[0-5]|[0-9])\\]";
    public static final String PATRON_NUMERO_SIMPLE = "(5[0-1][0-9]|[1-4][0-9][0-9]|[1-9][0-9]|[0-9])";
    public static final String PATRON_OPERANDO = "(" + PATRON_REGISTRO + "|" + PATRON_DIRECCION + "|" + PATRON_NUMERO_SIMPLE + ")";
    public static final String PATRON_INSTRUCCION = "^(" + String.join("|", CODIGOS_OPERACION) + ")\\s+"
            + PATRON_OPERANDO + "\\s*,\\s*" + PATRON_OPERANDO
            + "(\\s*,\\s*" + PATRON_OPERANDO + ")?$";

    // Atributos (basados en tu diseño de 32 bits)
    private int codop;          // 5 bits: código de operación
    private int dondeSeGuarda;  // 9 bits: registro/memoria destino
    private int operando1;      // 9 bits: primer operando
    private int operando2;      // 9 bits: segundo operando
    private String tipo;        // Ej: "aritmetica", "salto", "carga", etc. (opcional)

    // Constructor
    public Instruccion(int codop, int dondeSeGuarda, int operando1, int operando2) {
        this.codop = codop & 0x1F;          // Máscara de 5 bits
        this.dondeSeGuarda = dondeSeGuarda & 0x1FF; // Máscara de 9 bits
        this.operando1 = operando1 & 0x1FF;
        this.operando2 = operando2 & 0x1FF;
        this.tipo = determinarTipo(codop);  // Clasifica la instrucción
    }
    
    public Instruccion(){
    
    }

    // Método para clasificar el tipo de instrucción (ejemplo básico)
    private String determinarTipo(int codop) {
        switch (codop) {
            case 0b00001:
                return "suma";
            case 0b00010:
                return "resta";
            case 0b00011:
                return "carga";
            case 0b00100:
                return "salto";
            default:
                return "desconocido";
        }
    }

    // Getters
    public int getCodop() {
        return codop;
    }

    public int getDondeSeGuarda() {
        return dondeSeGuarda;
    }

    public int getOperando1() {
        return operando1;
    }

    public int getOperando2() {
        return operando2;
    }

    public String getTipo() {
        return tipo;
    }

    // Método para imprimir la instrucción (debug)
    @Override
    public String toString() {
        return String.format(
                "CODOP: %5s | Destino: %9s | Op1: %9s | Op2: %9s | Tipo: %s",
                Integer.toBinaryString(codop),
                Integer.toBinaryString(dondeSeGuarda),
                Integer.toBinaryString(operando1),
                Integer.toBinaryString(operando2),
                tipo
        );
    }
    
    public boolean validarInstruccion(String linea){
        if(linea == null || linea.trim().isEmpty()){
            return false;
        }
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        String var = partes[0];
        boolean var1 = Arrays.asList(CODIGOS_OPERACION).contains(var);
        boolean dir = partes.length == 3;
        return dir && var1;
    }
    
    public boolean validarDestino(String destino){
        if(destino == null || destino.trim().isEmpty()){
            return false;
        }
        boolean varReg = destino.matches(PATRON_REGISTRO);
        boolean varDir = destino.matches(PATRON_DIRECCION);
        
        return varReg || varDir;
    }
    
    public boolean validarOrigen(String origen){
        if(origen == null || origen.trim().isEmpty()){
            return false;
        }
        boolean varDato = origen.matches(PATRON_NUMERO_SIMPLE);
        boolean varDir = origen.matches(PATRON_DIRECCION);
        boolean varReg = origen.matches(PATRON_REGISTRO);
        return varDato || varDir || varReg;
    }

    public boolean validarInstruccionMOV(String linea, int numLinea, StringBuilder errores) {
        // Verificar estructura básica
        if (!linea.matches("^MOV\\s+.+")) {
            errores.append("Línea ").append(numLinea).append(": Formato inválido para MOV\n");
            return false;
        }

        // Extraer las partes
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        if (partes.length != 3) {
            errores.append("Línea ").append(numLinea).append(": MOV debe tener exactamente 2 operandos\n");
            return false;
        }

        String destino = partes[1];
        String origen = partes[2];
        boolean errorEncontrado = false;

        // Validar destino (debe ser registro o dirección)
        if (!destino.matches(PATRON_REGISTRO) && !destino.matches(PATRON_DIRECCION)) {
            errores.append("Línea ").append(numLinea).append(": Destino debe ser registro o dirección\n");
            errorEncontrado = true;
        }

        // Validar origen (puede ser registro, dirección o número)
        if (!origen.matches(PATRON_REGISTRO)
                && !origen.matches(PATRON_DIRECCION)
                && !origen.matches(PATRON_NUMERO_SIMPLE)) {
            errores.append("Línea ").append(numLinea).append(": Origen inválido\n");
            errorEncontrado = true;
        }

        // Validar rangos numéricos
        if (destino.matches(PATRON_DIRECCION)) {
            int num = Integer.parseInt(destino.substring(1, destino.length() - 1));
            if (num < 0 || num > 15) {
                errores.append("Línea ").append(numLinea).append(": Dirección destino fuera de rango (0-15)\n");
                errorEncontrado = true;
            }
        }

        if (origen.matches(PATRON_DIRECCION)) {
            int num = Integer.parseInt(origen.substring(1, origen.length() - 1));
            if (num < 0 || num > 15) {
                errores.append("Línea ").append(numLinea).append(": Dirección origen fuera de rango (0-15)\n");
                errorEncontrado = true;
            }
        } else if (origen.matches(PATRON_NUMERO_SIMPLE)) {
            int num = Integer.parseInt(origen);
            if (num < 0 || num > 511) {
                errores.append("Línea ").append(numLinea).append(": Número origen fuera de rango (0-511)\n");
                errorEncontrado = true;
            }
        }

        return !errorEncontrado;
    }
    
    
    
    public boolean validarInstruccionOperacion(String linea, int numLinea, StringBuilder errores){
        if (!validarInstruccion(linea)){
            errores.append("Línea ").append(numLinea).append(": Formato inválido de intrucción.\n");
            return false;
        }
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        String destino = partes[1];
        String origen = partes[2];
        boolean errorEncontrado = false;
        if(!validarDestino(destino)){
            errores.append("Línea ").append(numLinea).append(": Formato inválido de destino. \nUsa dirección de registro o entre un rango correcto de dirección a memoria\n");
            errorEncontrado = true;
        }
        if(!validarOrigen(origen)){
            errores.append("Línea ").append(numLinea).append(": Formato inválido de origen. \nUsa dirección de memoria correcto o un valor entero menor a 511.\n");
            errorEncontrado = true;
        }
        
        return !errorEncontrado;
    }
}
