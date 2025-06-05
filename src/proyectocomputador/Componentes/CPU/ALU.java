/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.CPU;

/**
 *
 * @author 57313
 */
public class ALU {

    private int operandoA;
    private int operandoB;
    private boolean ocupado;
    private String operacion; // "ADD", "SUB", "MPY", "DIV".


    public int operar() {
        switch (operacion) {
            case "ADD":
                return operandoA + operandoB;
            case "SUB":
                return operandoA - operandoB;
            case "MPY":
                return operandoA * operandoB;
            case "DIV":
                return operandoA / operandoB;
            case "CMP":
                return operandoA ^ operandoB;
            default:
                return 0;
        }
    }

    // Getters y Setters
    public void setOperandos(int a, int b) {
        this.operandoA = a;
        this.operandoB = b;
    }

    public void setOperacion(String op) {
        this.operacion = op;
    }
}
