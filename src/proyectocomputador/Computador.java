/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package proyectocomputador;

import proyectocomputador.Buses;
import proyectocomputador.CPU;
import proyectocomputador.Memoria;
import proyectocomputador.Registros;
import Utils.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import proyectocomputador.Instruccion;

/**
 *
 * @author 57313
 */
public class Computador extends javax.swing.JPanel {

    private Buses buses;
    private CPU cpu;
    private Memoria memoria;
    private Registros registro;
    private Utils utils;
    private Instruccion instruccion;

    

    /**
     * Creates new form NewJPanel
     */
    public Computador() {
        initComponents();
        logo();
        inicializarComponentes();
    }

    private void setScaledIcon(JLabel label, String imagePath, int width, int height) {

        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImage));
    }

    private void logo() {
        String ruta = System.getProperty("user.dir") + "\\src\\IMAGES\\";

        File testFile = new File(ruta);
        if (testFile.exists()) {
            setScaledIcon(this.ImageALU1, ruta + "ALU1.png", 160, 80);
            setScaledIcon(this.ImageALU2, ruta + "ALU2.png", 150, 80);
            return;
        }
        System.err.println("No se encontró la carpeta IMAGES");
    }

    private void inicializarComponentes() {
        // Inicializar componentes
        memoria = new Memoria();
        instruccion = new Instruccion();
        cpu = new CPU();
        buses = new Buses();

        ButtonEjecutar.addActionListener(e -> guardarInstrucciones());

        // Configurar tabla de datos
        memoria.configurarTablaDatos(TableDatos);
        memoria.configurarTablaProgramas(TableProgramas);

        registro = new Registros(); // Asegúrate de que esta variable de instancia existe

        // Configurar tabla de registros
        registro.configurarTablaRegistros(TableRegistros);
        
//        registro.setValorRegistro("CL", 42);
        // Escribir algunos valores de ejemplo
        memoria.escribirDato(0, 10);
        memoria.escribirDato(1, 255);
        memoria.escribirDato(2, 128);
        memoria.escribirDato(3, 85);
        memoria.escribirDato(4, 170);
        memoria.escribirDato(5, 15);
        memoria.escribirDato(6, 240);
        memoria.escribirDato(7, 63);
        memoria.escribirDato(8, 127);
        memoria.escribirDato(9, 1);
        memoria.escribirDato(10, 254);
        memoria.escribirDato(11, 51);
        memoria.escribirDato(12, 204);
        memoria.escribirDato(13, 0);
        memoria.escribirDato(14, 255);
        memoria.escribirDato(15, 7);

//        memoria.escribirPrograma(0, "MOV A, 5", "Programa1");
        TableDatos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                c.setBackground(Color.BLACK);
                c.setForeground(Color.WHITE);
                return c;
            }
        });

        // Configurar renderizado de la tabla de registros (similar a la tabla de datos)
        TableRegistros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                c.setBackground(Color.BLACK);
                c.setForeground(Color.WHITE);
                return c;
            }
        });
    }
    
    public volatile boolean interrupcion = false;

    private void guardarInstrucciones() {
        String texto = TextInstrucciones.getText();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay instrucciones para cargar",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiar programas de usuario existentes
        for (int i = 0; i < 16; i++) {
            String[] programa = memoria.leerPrograma(i);
            if ("Usuario".equals(programa[3])) {
                memoria.escribirPrograma(i, "NOP", "Sistema");
            }
        }
        this.LabelProceso.setText("");

        // Dividir por líneas y filtrar líneas vacías
        String[] lineas = Arrays.stream(texto.split("\\r?\\n"))
                .filter(linea -> !linea.trim().isEmpty())
                .toArray(String[]::new);
        
        int i = 0;
        int dir = 0;
        boolean erroresEnc = false;
        final StringBuilder errores = new StringBuilder();
        while (i <16 && i != lineas.length){
            if (i >= lineas.length || dir >= 16) {
                // Mostrar resultados finales
                if (erroresEnc) {
                    JOptionPane.showMessageDialog(this,
                            "Se encontraron errores:\n\n" + errores.toString()
                            + "\nSolo se cargaron las instrucciones válidas.",
                            "Errores en instrucciones",
                            JOptionPane.ERROR_MESSAGE);
                } else if (dir >= 16) {
                    JOptionPane.showMessageDialog(this,
                            "Se cargaron solo las primeras 16 instrucciones\nLa memoria de programas está llena",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
            String linea = lineas[i].trim();
            if (linea.matches(instruccion.PATRON_INSTRUCCION)) {
                memoria.escribirPrograma(dir, linea, "Usuario");
                dir++;
            } else {
                errores.append("Línea ").append(i + 1).append(": Formato de instrucción inválido\n");
                erroresEnc = true;
            }
            i++;
        }
        // Actualizar tablas
        memoria.configurarTablaProgramas(TableProgramas);

        // Crear timer para procesamiento secuencial
        Timer timer = new Timer(1000, null);
        final int[] index = {0};
        final int[] direccion = {0};
        final String[] instruccion = {""};
        final String[] instrBin = {""};
        System.out.println("ayuda");
        timer.addActionListener(e -> {
            if(interrupcion){
                timer.stop();
                    interrupcion(valor -> {
                        direccion[0] = valor;
                        interrupcion = false;
                        index[0]=0;
                        timer.start();
                    });
            }
            switch(index[0]){
                case 0 -> {
                    this.FieldPC.setText(String.format("%4s", Integer.toBinaryString(direccion[0])).replace(' ', '0'));
                    System.out.println(direccion[0]);
                    cpu.setContadorPc(direccion[0]);
                    resaltarRegsitroEstatico(0);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nInicia contador: "+ direccion[0]);
                }
                case 1 ->{
                    this.FieldMAR.setText(String.format("%4s", Integer.toBinaryString(direccion[0])).replace(' ', '0'));
                    cpu.setContadorPc(direccion[0]);
                    cpu.setDireccionMar(cpu.getContadorPc());
                    resaltarRegsitroEstatico(1);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nLleva a la MAR el valor: "+ direccion[0]);
                }
                case 2 -> {
                    this.FieldBusDirecciones.setText(String.format("%4s", Integer.toBinaryString(direccion[0])).replace(' ', '0'));
                    this.FieldBusControl.setText("00");
                    buses.setDireccion(cpu.getDireccionMar());
                    buses.setSeñal("00");
                    resaltarBuses(0);
                    resaltarBuses(1);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nBuses de Control para leer instruccion y bus de direcciones con la direccion: "+ direccion[0]);
                }
                case 3 -> {
                    instruccion[0] = (memoria.leerPrograma(direccion[0]))[2];
                    instrBin[0] = memoria.leerProgramaBinario(direccion[0]);
                    resaltarCeldaPrograma(direccion[0]);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe capta en la memoria de programa la direccion: "+ direccion[0]);
                }
                case 4 ->{
                    this.FieldDatos.setText(instrBin[0]);
                    buses.setDato(instrBin[0]);
                    resaltarBuses(2);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva en el bus de datos devuelta el dato: "+ instrBin[0]);
                }
                case 5 -> {
                    this.FieldMBR.setText(instrBin[0]);
                    cpu.setDatoMbr(instrBin[0]);
                    resaltarRegsitroEstatico(3);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe guarda en el MBR el dato: "+ instrBin[0]);
                }
                case 6 -> {
                    this.FieldIR.setText(instrBin[0]);
                    cpu.cargarIr(instrBin[0]);
                    resaltarRegsitroEstatico(4);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva al IR el dato: "+ instrBin[0] + " para decodificar");
                }
                case 7 -> {
                    this.FieldUC.setText(instruccion[0]);
                    resaltarRegsitroEstatico(2);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe decodificó el dato a: "+ instruccion[0]);
                }
                case 8 -> {
                    String[] partes = instruccion[0].split("\\s*,\\s*|\\s+");
                    this.FieldUCcodop.setText(partes[0]);
                    resaltarUC(0);
                    if(partes.length == 3){
                        this.FieldUCdestino.setText(partes[1]);
                        this.FieldUCorigen.setText(partes[2]);
                        resaltarUC(1);
                        resaltarUC(2);
                    }
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe realiza el CODOP: "+ partes[0]);
                    ejecutarCodop(instruccion[0], timer, val ->{
                        direccion[0] = direccion[0] + val;
                    });
                    
                }
                case 9 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe terminó de ejecutar la instruccion: "+ instruccion[0]);
                    System.out.println("REINICIO DE INDEX");
                    if(direccion[0] < lineas.length-1){
                        direccion[0]++;
                        index[0]=0;
                    }
                }
                default -> {
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe termina el proceso.");
                    System.out.println("Fin totazo");
                    ((Timer)e.getSource()).stop();
                }
            }
            index[0]++;
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    private void ejecutarCodop(String instruccion, Timer timer, Consumer<Integer> callback){
        String[] linea = instruccion.split("\\s*,\\s*|\\s+");
        timer.stop();
        switch(linea[0]){
            case "MOV" -> {
                procesarInstruccionMOV(instruccion, val ->{
                    System.out.println("Fin de operacion");
                    callback.accept(val);
                        timer.start();
                });
                break;
            }
            case "SUB", "DIV", "MPY", "ADD", "CMP" -> {
                procesarInstruccionOP(instruccion, val ->{
                    System.out.println("Fin de operacion");
                    callback.accept(val);
                    timer.start();
                });
                break;
            }
            case "JMP", "JZ", "JNZ" ->{
                procesarInstruccionJMP(instruccion, val ->{
                    System.out.println("Fin de operacion");
                    callback.accept(val);
                        timer.start();
                });
                break;
            }
        }
    }
    
    private void senalPSW(int[] valores){
        if(valores[0] == valores[1] && valores[2]==0){
            this.LabelProceso.setText(this.LabelProceso.getText() + "\nLos operandos son 0 y el resultado tambien");
            this.FieldPSW.setText("00");
            cpu.setZeroFlag(true);
            cpu.setEqualsFlag(true);
        }else if(valores[0] == valores[1]){
            this.LabelProceso.setText(this.LabelProceso.getText() + "\nLos operandos son iguales");
            this.FieldPSW.setText("01");
            cpu.setEqualsFlag(true);
        }else if(valores[2]==0){
            this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl resultado es 0");
            cpu.setZeroFlag(true);
            this.FieldPSW.setText("00");
        }else{
            this.LabelProceso.setText(this.LabelProceso.getText() + "\nLos operandos son diferentes");
            this.FieldPSW.setText("11");
            cpu.setEqualsFlag(false);
            cpu.setZeroFlag(false);
        }
        
        resaltarRegsitroEstatico(5);
    }
    
    private void interrupcion(Consumer<Integer> callback){
        Timer timer = new Timer(1000, null);
        final int[] index = {0, 0};
        final int direccion = 15;
        final String dirBin = "1111";
        final int contador = cpu.getContadorPc();
        final String contBin = String.format("%4s", Integer.toBinaryString(cpu.getContadorPc())).replace(' ', '0');
        
        timer.addActionListener(e ->{
            switch(index[0]){
                case 0 ->{
                    this.FieldUC.setText("INTERRUPTION");
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe generó una Interrupcion");
                    resaltarRegsitroEstatico(2);
                }
                case 1 ->{
                    this.FieldMAR.setText(dirBin);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe calculó la direccion de interrupcion a: "+ dirBin);
                    cpu.setDireccionMar(direccion);
                    resaltarRegsitroEstatico(1);
                }
                case 2 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe recupera el PC: "+contBin);
                    this.FieldPC.setText(contBin);
                    resaltarRegsitroEstatico(0);
                }
                case 3 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe guarda el contador en el MBR: "+ contBin);
                    this.FieldMBR.setText(contBin);
                    cpu.setDatoMbr(contBin);
                    resaltarRegsitroEstatico(3);
                }
                case 4 ->{
                    this.FieldBusControl.setText("10");
                    this.FieldBusDirecciones.setText(dirBin);
                    this.FieldDatos.setText(contBin);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nTodos los buses llevan datos: \nControl: 10\nDatos: "+contBin+"\nDirecciones: "+dirBin);
                    buses.setSeñal("10");
                    buses.setDireccion(direccion);
                    buses.setDato(dirBin);
                    resaltarBuses(0);
                    resaltarBuses(1);
                    resaltarBuses(2);
                }
                case 5 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe guarda el contador en la memoria de datos");
                    memoria.escribirDato(direccion, contador);
                    resaltarDireccionMemoria(direccion);
                }
                case 6 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe busca el valor de la interrupcion");
                    obtenerValorDestino("[15]", valor ->{
                        index[1] = valor;
                    }, timer);
                }
                case 7 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe guarda valor de interrupcion en el PC");
                    this.FieldPC.setText(contBin);
                    cpu.setContadorPc(index[1]);
                    resaltarRegsitroEstatico(0);
                }
                default ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nFinaliza la interrupcion");
                    callback.accept(index[1]);
                    ((Timer)e.getSource()).stop();
                }
            }
            index[0]++;
        });
        timer.setInitialDelay(1000);
        timer.start();
            
        // Actualizar la tabla de datos
        memoria.configurarTablaDatos(TableDatos);
    }
    
    //procesos de codop
    private void procesarInstruccionMOV(String instruc, Consumer<Integer> callback) {
        // Procesar efectos en memoria de datos si el destino es una dirección
        String[] partes = instruc.split("\\s*,\\s*|\\s+");
        String destino = partes[1];
        String origen = partes[2];
        final int[] valor = {0};
        Timer timer = new Timer(1000, null);
        if (destino.startsWith("A") || destino.startsWith("B") || destino.startsWith("C") || destino.startsWith("D")) {
            final int[] index = {0};
            timer.addActionListener(e ->{
                if(interrupcion){
                    callback.accept(0);
                    timer.stop();
                }
                switch(index[0]){
                    case 0 ->{
                        resaltarUC(1);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nDestino de MOV es: "+ destino);
                    }
                    case 1 ->{
                        resaltarUC(2);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nOrigen de MOV es: "+ origen);
                        obtenerValorOrigen(origen, val -> {
                              valor[0] = val;
                        }, timer);
                    }
                    case 2 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl valor del origen fue: "+ valor[0] + " e ira a la direccion: "+ destino);
                        registro.getBancoRegistros().setValorRegistro(destino, valor[0]);
                        registro.getBancoRegistros().configurarTabla(TableRegistros);
                        resaltarRegistro(obtenerIndice(destino));
                    }
                    default -> {
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nFin de MOV");
                        callback.accept(0);
                        ((Timer)e.getSource()).stop();
                    }
                }
                index[0]++;
            });
            timer.setInitialDelay(0);
            timer.start();
        } else if (destino.startsWith("[") && destino.endsWith("]")) {
            int dirMemoriaDatos = Integer.parseInt(destino.substring(1, destino.length() - 1));
            final int[] index = {0, dirMemoriaDatos};
            timer.addActionListener(e ->{
                if(interrupcion){
                    callback.accept(0);
                    timer.stop();
                }
                switch(index[0]){
                    case 0 ->{
                        resaltarUC(1);
                    }
                    case 1 ->{
                        this.FieldUCdestinoValor.setText(String.format("%4s", Integer.toBinaryString(index[1])).replace(' ', '0')); 
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nDestino de MOV es: "+ String.format("%4s", Integer.toBinaryString(index[1])).replace(' ', '0'));
                        resaltarUC(3);
                    }
                    case 2 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nOrigen de MOV es: "+ origen);
                        resaltarUC(2);
                        obtenerValorOrigen(origen, val -> {
                              valor[0] = val;
                        }, timer);
                        
                    }
                    case 3 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl valor del origen fue: "+ valor[0] + " e ira a la direccion: "+ destino);
                        llamadoDeBus(destino, valor[0], timer);
                        
                    }
                    default -> {
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nFin de MOV");
                        callback.accept(0);
                        ((Timer)e.getSource()).stop();
                    }
                }
                index[0]++;
            });
            timer.setInitialDelay(1000);
            timer.start();
            
            // Actualizar la tabla de datos
            memoria.configurarTablaDatos(TableDatos);
            registro.configurarTablaRegistros(TableRegistros);
        }
    }
    
    private void procesarInstruccionOP(String linea, Consumer<Integer> callback){
        // Procesar efectos en memoria de datos si el destino es una dirección
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        String operacion = partes[0];
        String destino = partes[1];
        String origen = partes[2];
        final int[] valor = {0, 0, 0};
        Timer timer = new Timer(1000, null);
        if (destino.startsWith("A") || destino.startsWith("B") || destino.startsWith("C") || destino.startsWith("D")) {
            final int[] index = {0};
            timer.addActionListener(e ->{
                if(interrupcion){
                    callback.accept(0);
                    timer.stop();
                }
                switch(index[0]){
                    case 0 ->{
                        resaltarUC(1);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nRecoger el valor de: "+ destino);
                    }
                    case 1 ->{
                        valor[0] = registro.getBancoRegistros().getValorRegistro(destino);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nValor del destino es: "+ valor[0]);
                        resaltarRegistro(obtenerIndice(destino));
                    }
                    case 2 ->{
                       this.FieldUCdestinoValor.setText(valor[0]+"");
                       resaltarUC(3);
                    }
                    case 3 ->{
                        resaltarUC(2);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nReconocer el valor de: "+ origen);
                        obtenerValorOrigen(origen, val -> {
                              valor[1] = val;
                        }, timer);
                    }
                    case 4 ->{
                        cpu.setOperandos(valor[0], valor[1]);
                        cpu.setOperacion(operacion);
                        valor[2] = cpu.operar();
                        if(valor[2]>512){
                            this.LabelProceso.setText(this.LabelProceso.getText() + "\nValor sobrepasado para memoria: "+ valor[2]);
                            JOptionPane.showMessageDialog(this,
                            "Los valores genera un número por encima de 512 o menor que 0,\n los cuales no se reciben.",
                            "Error de resultado",
                            JOptionPane.ERROR_MESSAGE);
                            callback.accept(0);
                            ((Timer)e.getSource()).stop();
                                return;
                        }
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nPrimer valor: "+ valor[0]);
                        this.FieldALU1_1.setText(String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                        resaltarOperandoALU(3);
                    }
                    case 5 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nSegundo valor: "+ valor[1]);
                        this.FieldALU1_2.setText(String.format("%9s", Integer.toBinaryString(valor[1])).replace(' ', '0'));
                        resaltarOperandoALU(4);
                    }
                    case 6 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nResultado: "+ valor[2]);
                        this.FieldALU1.setText(String.format("%9s", Integer.toBinaryString(valor[2])).replace(' ', '0'));
                        resaltarOperandoALU(1);
                    }
                    case 7 ->{
                        senalPSW(valor);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl resultado será guardado: "+ destino);
                        registro.getBancoRegistros().setValorRegistro(destino, valor[2]);
                        registro.getBancoRegistros().configurarTabla(TableRegistros);
                        resaltarRegistro(obtenerIndice(destino));
                    }
                    default -> {
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nFinaliza la operacion: "+ operacion);
                        callback.accept(0);
                        ((Timer)e.getSource()).stop();
                    }
                }
                index[0]++;
            });
            timer.setInitialDelay(0);
            timer.start();
        } else if (destino.startsWith("[") && destino.endsWith("]")) {
            int dirMemoriaDatos = Integer.parseInt(destino.substring(1, destino.length() - 1));
            final int[] index = {0, dirMemoriaDatos};
            timer.addActionListener(e ->{
                if(interrupcion){
                    callback.accept(0);
                    timer.stop();
                }
                switch(index[0]){
                    case 0 ->{
                        resaltarUC(0);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nRecoger el valor de: "+ destino);
                    }
                    case 1 ->{
                        resaltarUC(1);
                        obtenerValorDestino(destino, val -> {
                              valor[0] = val;
                        }, timer);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nValor del destino es: "+ valor[0]);
                    }
                    case 2 ->{
                        resaltarUC(2);
                        obtenerValorOrigen(origen, val -> {
                              valor[1] = val;
                        }, timer);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nValor del origen es: "+ valor[1]);
                    }
                    case 3 ->{
                        cpu.setOperandos(valor[0], valor[1]);
                        cpu.setOperacion(operacion);
                        valor[2] = cpu.operar();
                        if(valor[2]>512){
                            this.LabelProceso.setText(this.LabelProceso.getText() + "\nValor sobrepasado para memoria: "+ valor[2]);
                            JOptionPane.showMessageDialog(this,
                            "Los valores genera un número por encima de 512 o menor que 0,\n los cuales no se reciben.",
                            "Error de resultado",
                            JOptionPane.ERROR_MESSAGE);
                            callback.accept(0);
                            ((Timer)e.getSource()).stop();
                                return;
                        }
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nPrimer valor: "+ valor[0]);
                        this.FieldALU1_1.setText(String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                        resaltarOperandoALU(3);
                    }
                    case 4 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nSegundo valor: "+ valor[1]);
                        this.FieldALU1_2.setText(String.format("%9s", Integer.toBinaryString(valor[1])).replace(' ', '0'));
                        resaltarOperandoALU(4);
                    }
                    case 5 ->{
                        senalPSW(valor);
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nResultado: "+ valor[2]);
                        this.FieldALU1.setText(String.format("%9s", Integer.toBinaryString(valor[2])).replace(' ', '0'));
                        resaltarOperandoALU(1);
                    }
                    case 6 ->{
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl resultado será guardado: "+ destino);
                        llamadoDeBus(destino, valor[2], timer);
                    }
                    default -> {
                        this.LabelProceso.setText(this.LabelProceso.getText() + "\nFinaliza la operacion: "+ operacion);
                        callback.accept(0);
                        ((Timer)e.getSource()).stop();
                    }
                }
                index[0]++;
            });
            timer.setInitialDelay(1000);
            timer.start();
        }
        // Actualizar la tabla de datos
        memoria.configurarTablaDatos(TableDatos);
        registro.configurarTablaRegistros(TableRegistros);
    }
    
    private void procesarInstruccionJMP(String linea, Consumer<Integer> callback){
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        String operacion = partes[0];
        switch(operacion){
            case "JMP" -> {
                callback.accept(1);
            }
            case "JZ" -> {
                if(cpu.isZeroFlag()){
                    callback.accept(1);
                }else{
                    callback.accept(0);
                }
            }
            case "JNZ" -> {
                if(!cpu.isZeroFlag()){
                    callback.accept(1);
                }else{
                    callback.accept(0);
                }
            }
        }
    }
    
    
    //Paso de buses
    private void pasoBusesMemoriaDatos(String destino, Consumer<Integer> callback, String senal){
        int dirMemoriaDatos = Integer.parseInt(destino.substring(1, destino.length() - 1));
        String dirBin = String.format("%4s", Integer.toBinaryString(dirMemoriaDatos)).replace(' ', '0');
        final int[] index = {0};
        final int[] valor = {0};
        Timer timer = new Timer(1000, e -> {
            if(interrupcion){
                    callback.accept(0);
                    ((Timer)e.getSource()).stop();
                }
            switch(index[0]){
                case 0 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva a la MAR el valor: "+ dirBin);
                    this.FieldMAR.setText(dirBin);
                    cpu.setDireccionMar(dirMemoriaDatos);
                    resaltarRegsitroEstatico(1);
                    break;
                }
                case 1 ->{
                    this.FieldBusDirecciones.setText(dirBin);
                    this.FieldBusControl.setText(senal);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nEl bus de Control tiene la senal: "+ senal + " y el de direcciones tiene: "+ dirBin);
                    buses.setDireccion(dirMemoriaDatos);
                    buses.setSeñal(senal);
                    resaltarBuses(0);
                    resaltarBuses(1);
                    break;
                }
                case 2 ->{
                    valor[0] = memoria.leerDato(dirMemoriaDatos);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe encontró en la memoria de datos el dato: "+ valor[0]);
                    resaltarDireccionMemoria(dirMemoriaDatos);
                    break;
                }
                case 3 ->{
                    this.FieldDatos.setText(String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva en el bus de datos el valor: "+ String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                    buses.setDato(Integer.toString(valor[0]));
                    resaltarBuses(2);
                    break;
                }
                case 4 -> {
                    this.FieldMBR.setText(String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva a la MBR el valor: "+ String.format("%9s", Integer.toBinaryString(valor[0])).replace(' ', '0'));
                    cpu.setDatoMbr(Integer.toString(valor[0]));
                    resaltarRegsitroEstatico(3);
                    break;
                }
                default ->{
                    ((Timer)e.getSource()).stop();
                    callback.accept(valor[0]);
                    break;
                }    
            }
            index[0]++;
        });
        timer.setInitialDelay(1000);
        timer.start();
    }
    
    private void guardaBuses(String destino, Consumer<Integer> callback, int dato){
        int dirMemoriaDatos = Integer.parseInt(destino.substring(1, destino.length() - 1));
        String dirBin = String.format("%4s", Integer.toBinaryString(dirMemoriaDatos)).replace(' ', '0');
        String datoBin = String.format("%4s", Integer.toBinaryString(dirMemoriaDatos)).replace(' ', '0');
        final int[] index = {0};
        Timer timer = new Timer(1000, e -> {
            if(interrupcion){
                callback.accept(0);
                ((Timer)e.getSource()).stop();
            }
            switch(index[0]){
                case 0 ->{
                    this.FieldMAR.setText(dirBin);
                    this.FieldMBR.setText(datoBin);
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe lleva a la MAR la direccion: "+ dirBin+ "y al MBR el dato: "+ datoBin);
                    cpu.setDireccionMar(dirMemoriaDatos);
                    cpu.setDatoMbr(datoBin);
                    resaltarRegsitroEstatico(1);
                    resaltarRegsitroEstatico(3);
                    break;
                }
                case 1 ->{
                    this.FieldBusDirecciones.setText(dirBin);
                    this.FieldDatos.setText(datoBin);
                    this.FieldBusControl.setText("11");
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nTodos los buses llevan datos: \nControl: 11\nDatos: "+datoBin+"\nDirecciones: "+dirBin);
                    buses.setDireccion(dirMemoriaDatos);
                    buses.setSeñal("11");
                    buses.setDato(datoBin);
                    resaltarBuses(0);
                    resaltarBuses(1);
                    resaltarBuses(2);
                    break;
                }
                case 2 ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe escribio en memoria el valor: "+ dato + " en la direccion: "+dirMemoriaDatos);
                    memoria.escribirDato(dirMemoriaDatos, dato);
                    resaltarDireccionMemoria(dirMemoriaDatos);
                    break;
                }
                default ->{
                    this.LabelProceso.setText(this.LabelProceso.getText() + "\nSe termina el guardado de dato");
                    callback.accept(dato);
                    ((Timer)e.getSource()).stop();
                    break;
                }    
            }
            index[0]++;
        });
        timer.setInitialDelay(0);
        timer.start();
    }
    
    public void llamadoDeBus(String destino, int dato, Timer timer){
        timer.stop();
            guardaBuses(destino, valor -> {
                int val = valor;
                System.out.println("Se termino el llamado a bus");
                timer.start();
            },dato);
    }
    
    //obtencion de datos
    public void obtenerValorDestino(String destino, Consumer<Integer> callback, Timer timer){
        timer.stop();
            pasoBusesMemoriaDatos(destino, valor -> {
                FieldUCdestinoValor.setText(String.valueOf(valor));
                resaltarUC(3);
                callback.accept(valor);
                timer.start();
            },"10");
    }
    
    private void obtenerValorOrigen(String origen, Consumer<Integer> callback, Timer timer) {
        if (origen.startsWith("[") && origen.endsWith("]")) {
            timer.stop();
            pasoBusesMemoriaDatos(origen, valor -> {
                FieldUCorigenValor.setText(String.valueOf(valor));
                resaltarUC(4);
                callback.accept(valor);
                timer.start();
            },"10");
        }else if (origen.matches(instruccion.PATRON_REGISTRO)) {
            int valor = registro.getValorRegistro(origen);
            resaltarRegistro(obtenerIndice(origen));
            FieldUCorigenValor.setText(String.valueOf(valor));
            resaltarUC(4);
            callback.accept(valor);
        } else if (origen.matches(instruccion.PATRON_NUMERO_SIMPLE)) {
            int valor = Integer.parseInt(origen);
            FieldUCorigenValor.setText(String.valueOf(valor));
            resaltarUC(4);
            callback.accept(valor);
        }  else {
            FieldUCorigenValor.setText("0");
            resaltarUC(4);
            callback.accept(0);
        }
    }
    
    //Resaltar los campos deseados
    private void resaltarCeldaMemoria(int fila, int columna, Color colorFondo, Color colorTexto) {
        TableDatos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row == fila) {
                    c.setBackground(colorFondo);
                    c.setForeground(colorTexto);
                } else {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        TableDatos.repaint();
    }
    
    public static int obtenerIndice(String registro) {
        String[] letras = {"A", "B", "C", "D"};
        String[] sufijos = {"H", "L", "X"};

        int index = 0;

        for (String letra : letras) {
            for (String sufijo : sufijos) {
                if ((letra + sufijo).equalsIgnoreCase(registro)) {
                    return index;
                }
                index++;
            }
        }

        throw new IllegalArgumentException("Registro inválido: " + registro);
    }

    private void resaltarDireccionMemoria(int direccion) {
        // Cambiar color temporalmente
        resaltarCeldaMemoria(direccion, 2, Color.CYAN, Color.BLACK); // Columna 2 es "Valor"

        // Crear un timer para restaurar el color después de 1 segundo
        Timer timer = new Timer(1500, e -> {
            resaltarCeldaMemoria(direccion, 2, Color.BLACK, Color.WHITE);
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void resaltarRegistro(int fila) {
        // Cambiar color temporalmente
        TableRegistros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row == fila) {
                    c.setBackground(Color.CYAN);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        TableRegistros.repaint();

        // Restaurar después de 1.5 segundos
        Timer timer = new Timer(1500, e -> {
            TableRegistros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                    return c;
                }
            });
            TableRegistros.repaint();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void resaltarOperandoALU(int valor){
        switch(valor){
            case 1 -> {
                this.FieldALU1.setBackground(Color.CYAN);
                this.FieldALU1.setForeground(Color.BLACK);
            }
            case 2 -> {
                this.FieldALU2.setBackground(Color.CYAN);
                this.FieldALU2.setForeground(Color.BLACK);
            }
            case 3 -> {
                this.FieldALU1_1.setBackground(Color.CYAN);
                this.FieldALU1_1.setForeground(Color.BLACK);
            }
            case 4 -> {
                this.FieldALU1_2.setBackground(Color.CYAN);
                this.FieldALU1_2.setForeground(Color.BLACK);
            }
            case 5 -> {
                this.FieldALU2_1.setBackground(Color.CYAN);
                this.FieldALU2_1.setForeground(Color.BLACK);
            }
            case 6 -> {
                this.FieldALU2_2.setBackground(Color.CYAN);
                this.FieldALU2_2.setForeground(Color.BLACK);
            }
        }
        
        Timer timer = new Timer(1000, e -> {
            switch(valor){
            case 1 -> {
                this.FieldALU1.setBackground(Color.BLACK);
                this.FieldALU1.setForeground(Color.WHITE);
                }
            case 2 -> {
                this.FieldALU2.setBackground(Color.BLACK);
                this.FieldALU2.setForeground(Color.WHITE);
                }
            case 3 -> {
                this.FieldALU1_1.setBackground(Color.BLACK);
                this.FieldALU1_1.setForeground(Color.WHITE);
                }
            case 4 -> {
                this.FieldALU1_2.setBackground(Color.BLACK);
                this.FieldALU1_2.setForeground(Color.WHITE);
                }
            case 5 -> {
                this.FieldALU2_1.setBackground(Color.BLACK);
                this.FieldALU2_1.setForeground(Color.WHITE);
                }
            case 6 -> {
                this.FieldALU2_2.setBackground(Color.BLACK);
                this.FieldALU2_2.setForeground(Color.WHITE);
                }
            }
            ((Timer) e.getSource()).stop();
        });
        timer.start();
    }
    
    private void resaltarProcesoCompleto(String instru, int dirPrograma) {
        // Resaltar en tabla de programas
        resaltarCeldaPrograma(dirPrograma);

        // Procesar efectos de la instrucción MOV
        if (instru.toUpperCase().startsWith("MOV ")) {
            String[] partes = instru.split("\\s*,\\s*|\\s+");
            String destino = partes[1];
            String origen = partes[2];

            // Resaltar registro destino si es un registro
            if (destino.matches(instruccion.PATRON_REGISTRO)) {
                resaltarRegistro(obtenerIndice(destino));
            } // Resaltar dirección memoria si es una dirección
            else if (destino.matches(instruccion.PATRON_DIRECCION)) {
                int dirMemoria = Integer.parseInt(destino.substring(1, destino.length() - 1));
                resaltarDireccionMemoria(dirMemoria);
            }

            // Resaltar origen si es dirección de memoria
            if (origen.matches(instruccion.PATRON_DIRECCION)) {
                int dirMemoria = Integer.parseInt(origen.substring(1, origen.length() - 1));
                resaltarDireccionMemoria(dirMemoria);
            }
        }
    }

    private void resaltarCeldaPrograma(int fila) {
        // Cambiar color temporalmente
        TableProgramas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row == fila) {
                    c.setBackground(Color.YELLOW);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        TableProgramas.repaint();

        // Restaurar después de 1.5 segundos
        Timer timer = new Timer(1500, e -> {
            TableProgramas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                    return c;
                }
            });
            TableProgramas.repaint();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void resaltarRegsitroEstatico(int valor){
        switch(valor){
            case 0 -> {
                this.FieldPC.setBackground(Color.CYAN);
                this.FieldPC.setForeground(Color.BLACK);
            }
            case 1 -> {
                this.FieldMAR.setBackground(Color.CYAN);
                this.FieldMAR.setForeground(Color.BLACK);
            }
            case 2 -> {
                this.FieldUC.setBackground(Color.CYAN);
                this.FieldUC.setForeground(Color.BLACK);
            }
            case 3 -> {
                this.FieldMBR.setBackground(Color.CYAN);
                this.FieldMBR.setForeground(Color.BLACK);
            }
            case 4 -> {
                this.FieldIR.setBackground(Color.CYAN);
                this.FieldIR.setForeground(Color.BLACK);
            }
            case 5 -> {
                this.FieldPSW.setBackground(Color.CYAN);
                this.FieldPSW.setForeground(Color.BLACK);
            }
        }
        
        Timer timer = new Timer(1000, e -> {
            switch(valor){
            case 0 -> {
                this.FieldPC.setBackground(Color.BLACK);
                this.FieldPC.setForeground(Color.WHITE);
                }
            case 1 -> {
                this.FieldMAR.setBackground(Color.BLACK);
                this.FieldMAR.setForeground(Color.WHITE);
                }
            case 2 -> {
                this.FieldUC.setBackground(Color.BLACK);
                this.FieldUC.setForeground(Color.WHITE);
                }
            case 3 -> {
                this.FieldMBR.setBackground(Color.BLACK);
                this.FieldMBR.setForeground(Color.WHITE);
                }
            case 4 -> {
                this.FieldIR.setBackground(Color.BLACK);
                this.FieldIR.setForeground(Color.WHITE);
                }
            case 5 -> {
                this.FieldPSW.setBackground(Color.BLACK);
                this.FieldPSW.setForeground(Color.WHITE);
                }
            }
            ((Timer) e.getSource()).stop();
        });
        timer.start();
    }
    
    private void resaltarUC(int uc){
        switch(uc){
            case 0 -> {
                this.FieldUCcodop.setBackground(Color.MAGENTA);
                this.FieldUCcodop.setForeground(Color.BLACK);
            }
            case 1 -> {
                this.FieldUCdestino.setBackground(Color.MAGENTA);
                this.FieldUCdestino.setForeground(Color.BLACK);
            }
            case 2 -> {
                this.FieldUCorigen.setBackground(Color.MAGENTA);
                this.FieldUCorigen.setForeground(Color.BLACK);
            }
            case 3 -> {
                this.FieldUCdestinoValor.setBackground(Color.CYAN);
                this.FieldUCdestinoValor.setForeground(Color.BLACK);
            }
            case 4 -> {
                this.FieldUCorigenValor.setBackground(Color.CYAN);
                this.FieldUCorigenValor.setForeground(Color.BLACK);
            }
            
        }
        
        Timer timer = new Timer(1000, e -> {
            switch(uc){
            case 0 -> {
                this.FieldUCcodop.setBackground(Color.BLACK);
                this.FieldUCcodop.setForeground(Color.WHITE);
                }
            case 1 -> {
                this.FieldUCdestino.setBackground(Color.BLACK);
                this.FieldUCdestino.setForeground(Color.WHITE);
                }
            case 2 -> {
                this.FieldUCorigen.setBackground(Color.BLACK);
                this.FieldUCorigen.setForeground(Color.WHITE);
                }
            case 3 -> {
                this.FieldUCdestinoValor.setBackground(Color.BLACK);
                this.FieldUCdestinoValor.setForeground(Color.WHITE);
                }
            case 4 -> {
                this.FieldUCorigenValor.setBackground(Color.BLACK);
                this.FieldUCorigenValor.setForeground(Color.WHITE);
                }
            }
            ((Timer) e.getSource()).stop();
        });
        timer.start();
    }
    
    private void resaltarBuses(int bus){
        switch(bus){
            case 0 -> {
                this.FieldBusControl.setBackground(Color.MAGENTA);
                this.FieldBusControl.setForeground(Color.BLACK);
            }
            case 1 -> {
                this.FieldBusDirecciones.setBackground(Color.MAGENTA);
                this.FieldBusDirecciones.setForeground(Color.BLACK);
            }
            case 2 -> {
                this.FieldDatos.setBackground(Color.MAGENTA);
                this.FieldDatos.setForeground(Color.BLACK);
            }
        }
        
        Timer timer = new Timer(1000, e -> {
            switch(bus){
            case 0 -> {
                this.FieldBusControl.setBackground(Color.BLACK);
                this.FieldBusControl.setForeground(Color.WHITE);
                }
            case 1 -> {
                this.FieldBusDirecciones.setBackground(Color.BLACK);
                this.FieldBusDirecciones.setForeground(Color.WHITE);
                }
            case 2 -> {
                this.FieldDatos.setBackground(Color.BLACK);
                this.FieldDatos.setForeground(Color.WHITE);
                }
            }
            ((Timer) e.getSource()).stop();
        });
        timer.start();
    }
    
    private void cargarInterrupcion() {
        interrupcion = true;
    }
    
    public static void main(String[] args) {
        ProyectoComputador.main(args);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMemoriaProgramas = new javax.swing.JPanel();
        LabelMemoriaProgramas = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TableProgramas = new javax.swing.JTable();
        PanelBusControl = new javax.swing.JPanel();
        LabelBusControl = new javax.swing.JLabel();
        FieldBusControl = new javax.swing.JTextField();
        PanelBusDirecciones = new javax.swing.JPanel();
        LabelBusDirecciones = new javax.swing.JLabel();
        FieldBusDirecciones = new javax.swing.JTextField();
        PanelBusDatos = new javax.swing.JPanel();
        LabelBusDatos = new javax.swing.JLabel();
        FieldDatos = new javax.swing.JTextField();
        PanelCPU = new javax.swing.JPanel();
        LabelMAR = new javax.swing.JLabel();
        LabelIR = new javax.swing.JLabel();
        LabelMBR = new javax.swing.JLabel();
        LabelPC = new javax.swing.JLabel();
        LabelUC = new javax.swing.JLabel();
        FieldUC = new javax.swing.JTextField();
        FieldIR = new javax.swing.JTextField();
        FieldMBR = new javax.swing.JTextField();
        FieldPC = new javax.swing.JTextField();
        FieldMAR = new javax.swing.JTextField();
        ImageALU1 = new javax.swing.JLabel();
        LabelRegistros = new javax.swing.JLabel();
        ImageALU2 = new javax.swing.JLabel();
        FieldALU1 = new javax.swing.JTextField();
        FieldALU2 = new javax.swing.JTextField();
        LabelPSW = new javax.swing.JLabel();
        FieldPSW = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TableRegistros = new javax.swing.JTable();
        FieldALU2_1 = new javax.swing.JTextField();
        FieldALU2_2 = new javax.swing.JTextField();
        FieldALU1_1 = new javax.swing.JTextField();
        FieldALU1_2 = new javax.swing.JTextField();
        FieldUCcodop = new javax.swing.JTextField();
        FieldUCdestino = new javax.swing.JTextField();
        FieldUCorigen = new javax.swing.JTextField();
        FieldUCorigenValor = new javax.swing.JTextField();
        FieldUCdestinoValor = new javax.swing.JTextField();
        LabelUC1 = new javax.swing.JLabel();
        PanelIO = new javax.swing.JPanel();
        LabelIO = new javax.swing.JLabel();
        FieldIO = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        TextInstrucciones = new javax.swing.JTextArea();
        LabelInstrucciones = new javax.swing.JLabel();
        PanelProcesos = new javax.swing.JPanel();
        LabelPROCESOS = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        LabelProceso = new javax.swing.JTextArea();
        PanelMemoriaDatos = new javax.swing.JPanel();
        LabelMemoriaDatos = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableDatos = new javax.swing.JTable();
        ButtonEjecutar = new javax.swing.JButton();
        ButtonInterrupcion = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 0, 0));

        PanelMemoriaProgramas.setBackground(new java.awt.Color(0, 0, 0));
        PanelMemoriaProgramas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelMemoriaProgramas.setForeground(new java.awt.Color(255, 255, 255));
        LabelMemoriaProgramas.setText("Memoria Programas");

        TableProgramas.setBackground(new java.awt.Color(0, 0, 0));
        TableProgramas.setForeground(new java.awt.Color(255, 255, 255));
        TableProgramas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(TableProgramas);

        javax.swing.GroupLayout PanelMemoriaProgramasLayout = new javax.swing.GroupLayout(PanelMemoriaProgramas);
        PanelMemoriaProgramas.setLayout(PanelMemoriaProgramasLayout);
        PanelMemoriaProgramasLayout.setHorizontalGroup(
            PanelMemoriaProgramasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMemoriaProgramasLayout.createSequentialGroup()
                .addGroup(PanelMemoriaProgramasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMemoriaProgramasLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(LabelMemoriaProgramas))
                    .addGroup(PanelMemoriaProgramasLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        PanelMemoriaProgramasLayout.setVerticalGroup(
            PanelMemoriaProgramasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMemoriaProgramasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelMemoriaProgramas, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelBusControl.setBackground(new java.awt.Color(0, 0, 0));
        PanelBusControl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelBusControl.setBackground(new java.awt.Color(255, 255, 255));
        LabelBusControl.setForeground(new java.awt.Color(255, 255, 255));
        LabelBusControl.setText("Bus Control");

        FieldBusControl.setBackground(new java.awt.Color(0, 0, 0));
        FieldBusControl.setForeground(new java.awt.Color(255, 255, 255));
        FieldBusControl.setText("...");

        javax.swing.GroupLayout PanelBusControlLayout = new javax.swing.GroupLayout(PanelBusControl);
        PanelBusControl.setLayout(PanelBusControlLayout);
        PanelBusControlLayout.setHorizontalGroup(
            PanelBusControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusControlLayout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(LabelBusControl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelBusControlLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(FieldBusControl)
                .addContainerGap())
        );
        PanelBusControlLayout.setVerticalGroup(
            PanelBusControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusControlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelBusControl, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FieldBusControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        PanelBusDirecciones.setBackground(new java.awt.Color(0, 0, 0));
        PanelBusDirecciones.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelBusDirecciones.setForeground(new java.awt.Color(255, 255, 255));
        LabelBusDirecciones.setText("Bus Direcciones");

        FieldBusDirecciones.setBackground(new java.awt.Color(0, 0, 0));
        FieldBusDirecciones.setForeground(new java.awt.Color(255, 255, 255));
        FieldBusDirecciones.setText("...");

        javax.swing.GroupLayout PanelBusDireccionesLayout = new javax.swing.GroupLayout(PanelBusDirecciones);
        PanelBusDirecciones.setLayout(PanelBusDireccionesLayout);
        PanelBusDireccionesLayout.setHorizontalGroup(
            PanelBusDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusDireccionesLayout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(LabelBusDirecciones)
                .addContainerGap(90, Short.MAX_VALUE))
            .addGroup(PanelBusDireccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FieldBusDirecciones)
                .addContainerGap())
        );
        PanelBusDireccionesLayout.setVerticalGroup(
            PanelBusDireccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusDireccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelBusDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FieldBusDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        PanelBusDatos.setBackground(new java.awt.Color(0, 0, 0));
        PanelBusDatos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelBusDatos.setForeground(new java.awt.Color(255, 255, 255));
        LabelBusDatos.setText("Bus Datos");

        FieldDatos.setBackground(new java.awt.Color(0, 0, 0));
        FieldDatos.setForeground(new java.awt.Color(255, 255, 255));
        FieldDatos.setText("...");

        javax.swing.GroupLayout PanelBusDatosLayout = new javax.swing.GroupLayout(PanelBusDatos);
        PanelBusDatos.setLayout(PanelBusDatosLayout);
        PanelBusDatosLayout.setHorizontalGroup(
            PanelBusDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusDatosLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(LabelBusDatos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelBusDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelBusDatosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(FieldDatos)
                    .addContainerGap()))
        );
        PanelBusDatosLayout.setVerticalGroup(
            PanelBusDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelBusDatos)
                .addContainerGap(86, Short.MAX_VALUE))
            .addGroup(PanelBusDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelBusDatosLayout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(FieldDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(48, Short.MAX_VALUE)))
        );

        PanelCPU.setBackground(new java.awt.Color(0, 0, 0));
        PanelCPU.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        LabelMAR.setForeground(new java.awt.Color(255, 255, 255));
        LabelMAR.setText("MAR");

        LabelIR.setForeground(new java.awt.Color(255, 255, 255));
        LabelIR.setText("IR");

        LabelMBR.setForeground(new java.awt.Color(255, 255, 255));
        LabelMBR.setText("MBR");

        LabelPC.setForeground(new java.awt.Color(255, 255, 255));
        LabelPC.setText("PC");

        LabelUC.setForeground(new java.awt.Color(255, 255, 255));
        LabelUC.setText("UC");

        FieldUC.setBackground(new java.awt.Color(0, 0, 0));
        FieldUC.setForeground(new java.awt.Color(255, 255, 255));
        FieldUC.setText("...");

        FieldIR.setBackground(new java.awt.Color(0, 0, 0));
        FieldIR.setForeground(new java.awt.Color(255, 255, 255));
        FieldIR.setText("...");

        FieldMBR.setBackground(new java.awt.Color(0, 0, 0));
        FieldMBR.setForeground(new java.awt.Color(255, 255, 255));
        FieldMBR.setText("...");

        FieldPC.setBackground(new java.awt.Color(0, 0, 0));
        FieldPC.setForeground(new java.awt.Color(255, 255, 255));
        FieldPC.setText("...");

        FieldMAR.setBackground(new java.awt.Color(0, 0, 0));
        FieldMAR.setForeground(new java.awt.Color(255, 255, 255));
        FieldMAR.setText("...");

        ImageALU1.setForeground(new java.awt.Color(255, 255, 255));
        ImageALU1.setText(".");

        LabelRegistros.setForeground(new java.awt.Color(255, 255, 255));
        LabelRegistros.setText("Registros");

        ImageALU2.setForeground(new java.awt.Color(255, 255, 255));
        ImageALU2.setText(".");

        FieldALU1.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU1.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU1.setText("...");

        FieldALU2.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU2.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU2.setText("...");

        LabelPSW.setForeground(new java.awt.Color(255, 255, 255));
        LabelPSW.setText("PSW");

        FieldPSW.setBackground(new java.awt.Color(0, 0, 0));
        FieldPSW.setForeground(new java.awt.Color(255, 255, 255));
        FieldPSW.setText("...");

        TableRegistros.setBackground(new java.awt.Color(0, 0, 0));
        TableRegistros.setForeground(new java.awt.Color(255, 255, 255));
        TableRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(TableRegistros);

        FieldALU2_1.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU2_1.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU2_1.setText("...");

        FieldALU2_2.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU2_2.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU2_2.setText("...");

        FieldALU1_1.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU1_1.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU1_1.setText("...");

        FieldALU1_2.setBackground(new java.awt.Color(0, 0, 0));
        FieldALU1_2.setForeground(new java.awt.Color(255, 255, 255));
        FieldALU1_2.setText("...");

        FieldUCcodop.setBackground(new java.awt.Color(0, 0, 0));
        FieldUCcodop.setForeground(new java.awt.Color(255, 255, 255));
        FieldUCcodop.setText("...");

        FieldUCdestino.setBackground(new java.awt.Color(0, 0, 0));
        FieldUCdestino.setForeground(new java.awt.Color(255, 255, 255));
        FieldUCdestino.setText("...");

        FieldUCorigen.setBackground(new java.awt.Color(0, 0, 0));
        FieldUCorigen.setForeground(new java.awt.Color(255, 255, 255));
        FieldUCorigen.setText("...");

        FieldUCorigenValor.setBackground(new java.awt.Color(0, 0, 0));
        FieldUCorigenValor.setForeground(new java.awt.Color(255, 255, 255));
        FieldUCorigenValor.setText("...");

        FieldUCdestinoValor.setBackground(new java.awt.Color(0, 0, 0));
        FieldUCdestinoValor.setForeground(new java.awt.Color(255, 255, 255));
        FieldUCdestinoValor.setText("...");

        LabelUC1.setForeground(new java.awt.Color(255, 255, 255));
        LabelUC1.setText("INSTRUCTION");

        javax.swing.GroupLayout PanelCPULayout = new javax.swing.GroupLayout(PanelCPU);
        PanelCPU.setLayout(PanelCPULayout);
        PanelCPULayout.setHorizontalGroup(
            PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(FieldALU2)
                                .addGroup(PanelCPULayout.createSequentialGroup()
                                    .addComponent(FieldALU2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(FieldALU2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(ImageALU1)
                                        .addGroup(PanelCPULayout.createSequentialGroup()
                                            .addComponent(FieldALU1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(FieldALU1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(FieldALU1))
                                    .addComponent(ImageALU2))
                                .addGap(0, 27, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(LabelRegistros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                        .addComponent(LabelIR)
                        .addGap(83, 83, 83))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                        .addComponent(FieldIR)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(LabelUC)
                                .addGap(77, 77, 77))
                            .addComponent(FieldUC))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(FieldMBR, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addComponent(LabelMBR)
                                .addGap(72, 72, 72))
                            .addComponent(FieldMAR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addComponent(LabelMAR)
                                .addGap(72, 72, 72))
                            .addComponent(FieldPC, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addComponent(LabelPC)
                                .addGap(77, 77, 77))
                            .addComponent(FieldPSW, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelCPULayout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(LabelPSW)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(FieldUCcodop, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(FieldUCdestino, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(FieldUCorigen))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(FieldUCorigenValor, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                                    .addComponent(FieldUCdestinoValor))))
                        .addContainerGap())
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(LabelUC1)
                        .addContainerGap())))
        );
        PanelCPULayout.setVerticalGroup(
            PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(LabelIR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FieldIR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FieldALU1_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FieldALU1_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addComponent(ImageALU1)
                        .addGap(18, 18, 18)
                        .addComponent(FieldALU1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FieldALU2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FieldALU2_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(ImageALU2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(FieldALU2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelRegistros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(FieldUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(LabelUC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelUC1)
                        .addGap(8, 8, 8)
                        .addComponent(FieldUCcodop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FieldUCdestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FieldUCdestinoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FieldUCorigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FieldUCorigenValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LabelPSW)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FieldPSW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelPC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FieldPC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelMAR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FieldMAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelMBR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(FieldMBR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );

        PanelIO.setBackground(new java.awt.Color(0, 0, 0));
        PanelIO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelIO.setForeground(new java.awt.Color(255, 255, 255));
        LabelIO.setText("I/O");

        FieldIO.setBackground(new java.awt.Color(0, 0, 0));
        FieldIO.setForeground(new java.awt.Color(255, 255, 255));
        FieldIO.setText("...");
        FieldIO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FieldIOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelIOLayout = new javax.swing.GroupLayout(PanelIO);
        PanelIO.setLayout(PanelIOLayout);
        PanelIOLayout.setHorizontalGroup(
            PanelIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIOLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(FieldIO, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                    .addGroup(PanelIOLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(LabelIO)))
                .addGap(65, 65, 65))
        );
        PanelIOLayout.setVerticalGroup(
            PanelIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIOLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(LabelIO)
                .addGap(40, 40, 40)
                .addComponent(FieldIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        TextInstrucciones.setBackground(new java.awt.Color(102, 102, 102));
        TextInstrucciones.setColumns(20);
        TextInstrucciones.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        TextInstrucciones.setForeground(new java.awt.Color(255, 255, 255));
        TextInstrucciones.setRows(5);
        TextInstrucciones.setText("MOV [0], 211");
        jScrollPane2.setViewportView(TextInstrucciones);

        LabelInstrucciones.setForeground(new java.awt.Color(255, 255, 255));
        LabelInstrucciones.setText("Comando");

        PanelProcesos.setBackground(new java.awt.Color(0, 0, 0));
        PanelProcesos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelPROCESOS.setForeground(new java.awt.Color(255, 255, 255));
        LabelPROCESOS.setText("Descripción proceso");

        LabelProceso.setBackground(new java.awt.Color(0, 0, 0));
        LabelProceso.setColumns(20);
        LabelProceso.setForeground(new java.awt.Color(255, 255, 255));
        LabelProceso.setRows(5);
        jScrollPane4.setViewportView(LabelProceso);

        javax.swing.GroupLayout PanelProcesosLayout = new javax.swing.GroupLayout(PanelProcesos);
        PanelProcesos.setLayout(PanelProcesosLayout);
        PanelProcesosLayout.setHorizontalGroup(
            PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelProcesosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelPROCESOS))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        PanelProcesosLayout.setVerticalGroup(
            PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelProcesosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelPROCESOS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        PanelMemoriaDatos.setBackground(new java.awt.Color(0, 0, 0));
        PanelMemoriaDatos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 3, true));

        LabelMemoriaDatos.setForeground(new java.awt.Color(255, 255, 255));
        LabelMemoriaDatos.setText("Memoria Datos");

        TableDatos.setBackground(new java.awt.Color(0, 0, 0));
        TableDatos.setForeground(new java.awt.Color(255, 255, 255));
        TableDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(TableDatos);

        javax.swing.GroupLayout PanelMemoriaDatosLayout = new javax.swing.GroupLayout(PanelMemoriaDatos);
        PanelMemoriaDatos.setLayout(PanelMemoriaDatosLayout);
        PanelMemoriaDatosLayout.setHorizontalGroup(
            PanelMemoriaDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMemoriaDatosLayout.createSequentialGroup()
                .addGroup(PanelMemoriaDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMemoriaDatosLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(LabelMemoriaDatos))
                    .addGroup(PanelMemoriaDatosLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelMemoriaDatosLayout.setVerticalGroup(
            PanelMemoriaDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMemoriaDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelMemoriaDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ButtonEjecutar.setBackground(new java.awt.Color(0, 0, 0));
        ButtonEjecutar.setForeground(new java.awt.Color(255, 255, 255));
        ButtonEjecutar.setText("Ejecutar");
        ButtonEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonEjecutarActionPerformed(evt);
            }
        });

        ButtonInterrupcion.setBackground(new java.awt.Color(0, 0, 0));
        ButtonInterrupcion.setForeground(new java.awt.Color(255, 255, 255));
        ButtonInterrupcion.setText("Interrupción");
        ButtonInterrupcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonInterrupcionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PanelBusDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PanelBusControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PanelBusDirecciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(PanelIO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(PanelProcesos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelMemoriaProgramas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(LabelInstrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(ButtonEjecutar))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(ButtonInterrupcion))))
                    .addComponent(PanelMemoriaDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(PanelBusControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PanelBusDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(PanelBusDirecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(PanelCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PanelProcesos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PanelIO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(LabelInstrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonEjecutar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ButtonInterrupcion))
                            .addComponent(PanelMemoriaProgramas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(PanelMemoriaDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void FieldIOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FieldIOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FieldIOActionPerformed

    private void ButtonInterrupcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonInterrupcionActionPerformed
        cargarInterrupcion();
    }//GEN-LAST:event_ButtonInterrupcionActionPerformed

    private void ButtonEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonEjecutarActionPerformed

    }//GEN-LAST:event_ButtonEjecutarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonEjecutar;
    private javax.swing.JButton ButtonInterrupcion;
    private javax.swing.JTextField FieldALU1;
    private javax.swing.JTextField FieldALU1_1;
    private javax.swing.JTextField FieldALU1_2;
    private javax.swing.JTextField FieldALU2;
    private javax.swing.JTextField FieldALU2_1;
    private javax.swing.JTextField FieldALU2_2;
    private javax.swing.JTextField FieldBusControl;
    private javax.swing.JTextField FieldBusDirecciones;
    private javax.swing.JTextField FieldDatos;
    private javax.swing.JTextField FieldIO;
    private javax.swing.JTextField FieldIR;
    private javax.swing.JTextField FieldMAR;
    private javax.swing.JTextField FieldMBR;
    private javax.swing.JTextField FieldPC;
    private javax.swing.JTextField FieldPSW;
    private javax.swing.JTextField FieldUC;
    private javax.swing.JTextField FieldUCcodop;
    private javax.swing.JTextField FieldUCdestino;
    private javax.swing.JTextField FieldUCdestinoValor;
    private javax.swing.JTextField FieldUCorigen;
    private javax.swing.JTextField FieldUCorigenValor;
    private javax.swing.JLabel ImageALU1;
    private javax.swing.JLabel ImageALU2;
    private javax.swing.JLabel LabelBusControl;
    private javax.swing.JLabel LabelBusDatos;
    private javax.swing.JLabel LabelBusDirecciones;
    private javax.swing.JLabel LabelIO;
    private javax.swing.JLabel LabelIR;
    private javax.swing.JLabel LabelInstrucciones;
    private javax.swing.JLabel LabelMAR;
    private javax.swing.JLabel LabelMBR;
    private javax.swing.JLabel LabelMemoriaDatos;
    private javax.swing.JLabel LabelMemoriaProgramas;
    private javax.swing.JLabel LabelPC;
    private javax.swing.JLabel LabelPROCESOS;
    private javax.swing.JLabel LabelPSW;
    private javax.swing.JTextArea LabelProceso;
    private javax.swing.JLabel LabelRegistros;
    private javax.swing.JLabel LabelUC;
    private javax.swing.JLabel LabelUC1;
    private javax.swing.JPanel PanelBusControl;
    private javax.swing.JPanel PanelBusDatos;
    private javax.swing.JPanel PanelBusDirecciones;
    private javax.swing.JPanel PanelCPU;
    private javax.swing.JPanel PanelIO;
    private javax.swing.JPanel PanelMemoriaDatos;
    private javax.swing.JPanel PanelMemoriaProgramas;
    private javax.swing.JPanel PanelProcesos;
    private javax.swing.JTable TableDatos;
    private javax.swing.JTable TableProgramas;
    private javax.swing.JTable TableRegistros;
    private javax.swing.JTextArea TextInstrucciones;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}
