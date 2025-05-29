/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectocomputador.Componentes.Registros;

/**
 *
 * @author 57313
 */
public class BancoRegistros {
    private int[] registros; // Ej: AX, BX, CX, DX...

    public BancoRegistros(int cantidadRegistros) {
        registros = new int[cantidadRegistros];
    }
    public int leer(int numRegistro) {
        return registros[numRegistro];
    }
    public void escribir(int numRegistro, int valor) {
        registros[numRegistro] = valor;
    }
}
