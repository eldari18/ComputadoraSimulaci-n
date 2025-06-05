/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador;

import proyectocomputador.Componentes.CPU.ALU;
import proyectocomputador.Componentes.Registros.BancoRegistros;
import proyectocomputador.Componentes.Registros.IR;
import proyectocomputador.Componentes.Registros.MAR;
import proyectocomputador.Componentes.Registros.MBR;
import proyectocomputador.Componentes.CPU.PC;
import proyectocomputador.Componentes.CPU.PSW;
import proyectocomputador.Componentes.CPU.UC;



/**
 *
 * @author 57313
 */
public class CPU {
    private PC pc;
    private ALU alu;
    private UC uc;
    private BancoRegistros registros;
    private IR ir;
    private MAR mar;
    private MBR mbr;
    private PSW psw;

    public CPU() {
        pc = new PC();
        alu = new ALU();
        uc = new UC();
        registros = new BancoRegistros(); // 4 registros
        ir = new IR();
        mar = new MAR();
        mbr = new MBR();
        psw = new PSW();
    }
    
    // Getters y Setters
    public void setOperandos(int a, int b) {
        getAlu().setOperandos(a, b);
    }

    public void setOperacion(String op) {
        getAlu().setOperacion(op);
    }
    
    public int operar(){
        return getAlu().operar();
    }

    public int getContadorPc() {
        return getPc().getDireccion();
    }

    public void setContadorPc(int dir) {
        getPc().setDireccion(dir);
    }
    public String getInstruccionUc() {
        return getUc().getInstruccionActual();
    }
    
    public void decodificarUc(String instruccion) {
        getUc().decodificar(instruccion);
    }
    
    public String getInstruccionIr() {
        return getIr().getInstruccion();
    }

    public void cargarIr(String instruccion) {
        getIr().cargar(instruccion);
    }
    
    public int getDireccionMar() {
        return getMar().getDireccion();
    }

    public void setDireccionMar(int dir) {
        getMar().setDireccion(dir);
    }

    public String getDatoMbr() {
        return getMbr().getDato();
    }

    public void setDatoMbr(String dato) {
        getMbr().setDato(dato);
    }
    
    public void setZeroFlag(boolean value){
        getPsw().setZeroFlag(value);
    }
    
    public boolean isZeroFlag(){
        return getPsw().isZeroFlag();
    }
    
    public void setEqualsFlag(boolean value){
        getPsw().setEqualsFlag(value);
    }
    
    public boolean isEqualsFlag(){
        return getPsw().isEqualsFlag();
    }
    
    /**
     * @return the alu
     */
    public ALU getAlu() {
        return alu;
    }

    /**
     * @return the pc
     */
    public PC getPc() {
        return pc;
    }

    /**
     * @return the uc
     */
    public UC getUc() {
        return uc;
    }

    /**
     * @return the ir
     */
    public IR getIr() {
        return ir;
    }

    /**
     * @return the mar
     */
    public MAR getMar() {
        return mar;
    }

    /**
     * @return the mbr
     */
    public MBR getMbr() {
        return mbr;
    }

    /**
     * @return the psw
     */
    public PSW getPsw() {
        return psw;
    }
    
    
    
    
    
}
