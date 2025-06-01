/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.CPU;

import proyectocomputador.Componentes.Memoria.MemoriaDatos;
import proyectocomputador.Componentes.Memoria.MemoriaPrograma;
import proyectocomputador.Componentes.Registros.*;


public class UC {

    private PC pc;
    private IR ir;
    private MAR mar;
    private MBR mbr;
    private MemoriaDatos MemoriaDatos;
    private MemoriaPrograma MemoriaPrograma;

    public UC(PC pc, IR ir, MAR mar, MBR mbr, MemoriaDatos memoriaDatos, MemoriaPrograma memoriaPrograma) {
        this.pc = pc;
        this.ir = ir;
        this.mar = mar;
        this.mbr = mbr;
        this.MemoriaDatos = memoriaDatos;
        this.MemoriaPrograma = memoriaPrograma;
    }

    public void fetch() {
        int direccion = pc.getDireccion();                // 1. Obtener dirección de la instrucción
        mar.setDireccion(direccion);                 // 2. Cargar en el MAR
        //String instruccion = memoriaDatos.leer(direccion); // 3. Leer de memoria
        //mbr.setDato(dato);                   // 4. Guardar en MBR
        //ir.setInstruccion(instruccion);              // 5. Pasar a IR
        pc.incrementar();                             // 6. Incrementar PC

        // Opcional: imprimir o actualizar GUI
        System.out.println("FETCH:");
        //System.out.println("PC: " + direccion + " | Instrucción: " + instruccion);
    }
}

