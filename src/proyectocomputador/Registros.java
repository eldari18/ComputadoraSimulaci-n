package proyectocomputador;

import javax.swing.JTable;
import proyectocomputador.Componentes.Registros.BancoRegistros;
import proyectocomputador.Componentes.Registros.IR;
import proyectocomputador.Componentes.Registros.MBR;
import proyectocomputador.Componentes.Registros.MAR;

public class Registros {

    private BancoRegistros bancoRegistros;
    private IR ir;
    private MBR mbr;
    private MAR mar;

    public Registros() {
        bancoRegistros = new BancoRegistros(); // 16 registros
        ir = new IR();
        mbr = new MBR();
        mar = new MAR();
    }

    // Método para obtener la tabla de registros
    public JTable getTablaRegistros() {
        return bancoRegistros.getTablaRegistros();
    }

    // Métodos para acceder a los registros individuales
    public void setValorRegistro(String nombre, int valor) {
        bancoRegistros.setValorRegistro(nombre, valor);
    }

    public int getValorRegistro(String nombre) {
        return bancoRegistros.getValorRegistro(nombre);
    }

    public BancoRegistros getBancoRegistros() {
        return bancoRegistros;
    }

    // Getters para los otros registros
    public IR getIR() {
        return ir;
    }

    public MBR getMBR() {
        return mbr;
    }

    public MAR getMAR() {
        return mar;
    }
}
