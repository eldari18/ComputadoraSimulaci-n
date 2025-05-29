/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador;

/**
 *
 * @author 57313
 */
public class Instruccion {

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
}
