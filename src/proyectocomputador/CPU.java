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

    public CPU() {
        pc = new PC();
        alu = new ALU();
        uc = new UC();
        registros = new BancoRegistros(4); // 4 registros
        ir = new IR();
        mar = new MAR();
        mbr = new MBR();
    }
}
