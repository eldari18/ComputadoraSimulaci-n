/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.CPU;

/**
 *
 * @author 57313
 */
public class PSW {

    private boolean zeroFlag;   // Flag "cero"
    private boolean carryFlag; // Flag "acarreo"
    // ... otros flags

    // Métodos para actualizar flags
    public void setZeroFlag(boolean value) {
        this.zeroFlag = value;
    }
}
