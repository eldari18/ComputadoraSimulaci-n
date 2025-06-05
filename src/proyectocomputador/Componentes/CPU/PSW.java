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
    private boolean EqualsFlag; // Flag "acarreo"
    // ... otros flags

    public PSW() {
        this.zeroFlag = false;
        this.EqualsFlag = false;
    }


    // Métodos para actualizar flags
    public void setZeroFlag(boolean value) {
        this.zeroFlag = value;
    }
    
    public boolean isZeroFlag(){
        return this.zeroFlag;
    }

    /**
     * @return the EqualsFlag
     */
    public boolean isEqualsFlag() {
        return EqualsFlag;
    }

    /**
     * @param EqualsFlag the EqualsFlag to set
     */
    public void setEqualsFlag(boolean EqualsFlag) {
        this.EqualsFlag = EqualsFlag;
    }
    
}
