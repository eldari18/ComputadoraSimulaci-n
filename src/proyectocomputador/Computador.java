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

        // Dividir por líneas y filtrar líneas vacías
        String[] lineas = Arrays.stream(texto.split("\\r?\\n"))
                .filter(linea -> !linea.trim().isEmpty())
                .toArray(String[]::new);

        // Crear timer para procesamiento secuencial
        Timer timer = new Timer(1000, null);
        final int[] index = {0};
        final int[] direccion = {0};
        final StringBuilder errores = new StringBuilder();
        final boolean[] erroresEncontrados = {false};
        final boolean[] memoriaLlena = {false};

        timer.addActionListener(e -> {
            if (index[0] >= lineas.length || direccion[0] >= 16) {
                ((Timer) e.getSource()).stop();

                // Mostrar resultados finales
                if (erroresEncontrados[0]) {
                    JOptionPane.showMessageDialog(this,
                            "Se encontraron errores:\n\n" + errores.toString()
                            + "\nSolo se cargaron las instrucciones válidas.",
                            "Errores en instrucciones",
                            JOptionPane.ERROR_MESSAGE);
                } else if (memoriaLlena[0]) {
                    JOptionPane.showMessageDialog(this,
                            "Se cargaron solo las primeras 16 instrucciones\nLa memoria de programas está llena",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            String linea = lineas[index[0]].trim();
            System.out.println("Procesando instruccion " + (index[0] + 1) + ": " + linea);
            // Procesar instrucción
            if (linea.toUpperCase().startsWith("MOV ")) {
                if (instruccion.validarInstruccionMOV(linea, index[0] + 1, errores)) {
                    memoria.escribirPrograma(index[0], linea, "Usuario");
                    resaltarCeldaPrograma(index[0]);
                    procesarInstruccionMOV(linea);
                    direccion[0]++;
                }else{
                    erroresEncontrados[0] = true;
                }
            }else if(linea.toUpperCase().startsWith("ADD ") || 
                        linea.toUpperCase().startsWith("SUB ") || 
                        linea.toUpperCase().startsWith("MPY ") || 
                        linea.toUpperCase().startsWith("DIV ")){
                if (instruccion.validarInstruccionOperacion(linea, index[0] + 1, errores)) {
                    memoria.escribirPrograma(index[0], linea, "Usuario");
                    resaltarCeldaPrograma(index[0]);
                    procesarInstruccionADD(linea);
                    direccion[0]++;
                }else{
                    erroresEncontrados[0] = true;
                }
            }else if (linea.matches(instruccion.PATRON_INSTRUCCION)) {
                memoria.escribirPrograma(direccion[0], linea, "Usuario");
                resaltarCeldaPrograma(direccion[0]);
                direccion[0]++;
            } else {
                errores.append("Línea ").append(index[0] + 1).append(": Formato de instrucción inválido\n");
                erroresEncontrados[0] = true;
            }

            index[0]++;

            // Actualizar tablas
            memoria.configurarTablaProgramas(TableProgramas);
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    private void cargarInstrucciones() {
        // Crear timer para mostrar instrucciones secuencialmente
        Timer timer = new Timer(1000, null);
        final int[] index = {0};
        final int[] instruccionesMostradas = {0};

        timer.addActionListener(e -> {
            // Leer la instrucción en la posición actual
            String[] programa = memoria.leerPrograma(index[0]);
            String instruccion = programa[0];
            String tipo = programa[3];

            // Solo mostrar instrucciones de usuario (ignorar "NOP" del sistema)
            if ("Usuario".equals(tipo) && !"NOP".equals(instruccion)) {
                System.out.println("Instrucción " + (instruccionesMostradas[0] + 1)
                        + " (Dirección " + index[0] + "): " + instruccion);
                instruccionesMostradas[0]++;

                // Resaltar la celda en la tabla de programas
                resaltarCeldaPrograma(index[0]);
            }

            index[0]++;

            // Detener el timer cuando se hayan revisado todas las posiciones
            if (index[0] >= 16) {
                ((Timer) e.getSource()).stop();
                System.out.println("Fin de las instrucciones almacenadas");
            }
        });

        System.out.println("Cargando instrucciones desde memoria...");
        timer.setInitialDelay(0);
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

    private void procesarInstruccionMOV(String instruc) {
        // Guardar la instrucción en memoria de programas

        // Procesar efectos en memoria de datos si el destino es una dirección
        String[] partes = instruc.split("\\s*,\\s*|\\s+");
        String destino = partes[1];
        String origen = partes[2];
        int valor;

        if (destino.startsWith("A") || destino.startsWith("B") || destino.startsWith("C") || destino.startsWith("D")) {
            valor = Integer.parseInt(origen);
            registro.getBancoRegistros().setValorRegistro(destino, valor);
            registro.getBancoRegistros().configurarTabla(TableRegistros);
            resaltarRegistro(obtenerIndice(destino));
        }

        if (destino.startsWith("[") && destino.endsWith("]")) {
            int dirMemoriaDatos = Integer.parseInt(destino.substring(1, destino.length() - 1));

            if (origen.startsWith("[") && origen.endsWith("]")) {
                // MOV [x], [y] - Copiar de dirección a dirección
                int dirOrigen = Integer.parseInt(origen.substring(1, origen.length() - 1));
                valor = memoria.leerDato(dirOrigen);
            } else if (origen.matches(instruccion.PATRON_NUMERO_SIMPLE)) {
                // MOV [x], numero
                valor = Integer.parseInt(origen);
            } else {
                // MOV [x], registro - (valor por defecto 0, deberías implementar registros)
                valor = 0;
            }

            // Asegurar que el valor esté en el rango permitido
            valor = Math.max(0, Math.min(511, valor));
            memoria.escribirDato(dirMemoriaDatos, valor);

            // Resaltar la celda modificada
            resaltarDireccionMemoria(dirMemoriaDatos);

            // Actualizar la tabla de datos
            memoria.configurarTablaDatos(TableDatos);
            registro.configurarTablaRegistros(TableRegistros);
        }
    }
    
    private void procesarInstruccionADD(String linea){
        // Procesar efectos en memoria de datos si el destino es una dirección
        String[] partes = linea.split("\\s*,\\s*|\\s+");
        
        String destino = partes[1];
        String origen = partes[2];
        int valor1 = 0, valor2, dirMemoriaDatos1 = 0;
        boolean reg = true;
        //Reconocer operando 1
        if (destino.startsWith("A") || destino.startsWith("B") || destino.startsWith("C") || destino.startsWith("D")) {
            reg = true;
            valor1 = registro.getBancoRegistros().getValorRegistro(destino);
            resaltarRegistro(obtenerIndice(destino));
        }else if (destino.startsWith("[") && destino.endsWith("]")) {
            reg = false;
            dirMemoriaDatos1 = Integer.parseInt(destino.substring(1, destino.length() - 1));
            valor1 = memoria.leerDato(dirMemoriaDatos1);
            resaltarDireccionMemoria(dirMemoriaDatos1);
        }
        //Reconocer operando 2
        if(origen.startsWith("[") && origen.endsWith("]")){
            int dirMemoriaDatos2 = Integer.parseInt(origen.substring(1, origen.length() - 1));
            valor2 = memoria.leerDato(dirMemoriaDatos2);
            resaltarDireccionMemoria(dirMemoriaDatos2);
        }else if(origen.matches(instruccion.PATRON_NUMERO_SIMPLE)){
            valor2 = Integer.parseInt(origen);
        }else{
            valor2 = 0;
        }

            // Asegurar que el valor esté en el rango permitido
            valor1 = Math.max(0, Math.min(511, valor1));
            valor2 = Math.max(0, Math.min(511, valor2));
            
            //Setteamos los operando y operación en la ALU
            cpu.setOperandos(valor1, valor2);
            cpu.setOperacion(partes[0]);
            
            //Hacemos funcional la operación
            int valor_final= cpu.operar();
            
            
            if(valor_final >= 512 || valor_final < 0){
                JOptionPane.showMessageDialog(this,
                            "Los valores genera un valor por encima de 512 omenor que 0, los cuales no se reciben.",
                            "Error de resultado",
                            JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String valor1B = String.format("%8s", Integer.toBinaryString(valor1)).replace(' ', '0');
            String valor2B = String.format("%8s", Integer.toBinaryString(valor2)).replace(' ', '0');
            String valorFinalB = String.format("%8s", Integer.toBinaryString(valor_final)).replace(' ', '0');
            
            
            
            Timer timer = new Timer(1000, null);
            final int[] index = {0};
            final boolean reg_f = reg;
            final int dir = dirMemoriaDatos1;
            timer.addActionListener(e -> {
               if(index[0]>4){
                   ((Timer) e.getSource()).stop();
               }
               switch (index[0]) {
                case 0 -> {
                    this.FieldALU1_1.setText(valor1B);
                    this.resaltarOperandoALU(3);
                }
                case 1 -> {
                    this.FieldALU1_2.setText(valor2B);
                    this.resaltarOperandoALU(4);
                }
                case 2 -> {
                    this.FieldALU1.setText(valorFinalB);
                    this.resaltarOperandoALU(1);
                }
                case 3 -> {
                    if(reg_f){
                        registro.getBancoRegistros().setValorRegistro(destino, valor_final);
                        resaltarRegistro(obtenerIndice(destino));
                    }else{
                        memoria.escribirDato(dir, valor_final);
                        resaltarDireccionMemoria(dir);
                    }
                    memoria.configurarTablaDatos(TableDatos);
                    registro.configurarTablaRegistros(TableRegistros);
                }
                default -> {
                }
               }
               index[0]++;
           });
            timer.start();
            // Actualizar la tabla de datos
            memoria.configurarTablaDatos(TableDatos);
            registro.configurarTablaRegistros(TableRegistros);
        }
    

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
        PanelIO = new javax.swing.JPanel();
        LabelIO = new javax.swing.JLabel();
        FieldIO = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        TextInstrucciones = new javax.swing.JTextArea();
        LabelInstrucciones = new javax.swing.JLabel();
        PanelProcesos = new javax.swing.JPanel();
        LabelPROCESOS = new javax.swing.JLabel();
        LabelProceso2 = new javax.swing.JTextField();
        LabelProceso3 = new javax.swing.JTextField();
        LabelProceso1 = new javax.swing.JTextField();
        PanelMemoriaDatos = new javax.swing.JPanel();
        LabelMemoriaDatos = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableDatos = new javax.swing.JTable();
        ButtonEjecutar = new javax.swing.JButton();
        ButtonInterrupcion = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 0, 0));

        PanelMemoriaProgramas.setBackground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout PanelCPULayout = new javax.swing.GroupLayout(PanelCPU);
        PanelCPU.setLayout(PanelCPULayout);
        PanelCPULayout.setHorizontalGroup(
            PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCPULayout.createSequentialGroup()
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
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(LabelRegistros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addComponent(LabelIR)
                        .addGap(83, 83, 83))
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addComponent(FieldIR)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(FieldMBR)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                                        .addComponent(LabelMBR)
                                        .addGap(72, 72, 72))
                                    .addComponent(FieldMAR, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                                    .addComponent(LabelMAR)
                                    .addGap(72, 72, 72))
                                .addComponent(FieldPC, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCPULayout.createSequentialGroup()
                                .addComponent(LabelPC)
                                .addGap(77, 77, 77)))
                        .addContainerGap())
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(LabelUC)
                                .addGap(77, 77, 77))
                            .addComponent(FieldUC))
                        .addContainerGap())
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(FieldPSW, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelCPULayout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(LabelPSW)))
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
                .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelCPULayout.createSequentialGroup()
                        .addGroup(PanelCPULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCPULayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(FieldUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(LabelUC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addComponent(FieldMBR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE))
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
                        .addComponent(LabelRegistros)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        PanelIO.setBackground(new java.awt.Color(0, 0, 0));

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

        LabelPROCESOS.setForeground(new java.awt.Color(255, 255, 255));
        LabelPROCESOS.setText("Descripción proceso");

        LabelProceso2.setBackground(new java.awt.Color(0, 0, 0));
        LabelProceso2.setForeground(new java.awt.Color(255, 255, 255));
        LabelProceso2.setText("...");

        LabelProceso3.setBackground(new java.awt.Color(0, 0, 0));
        LabelProceso3.setForeground(new java.awt.Color(255, 255, 255));
        LabelProceso3.setText("...");

        LabelProceso1.setBackground(new java.awt.Color(0, 0, 0));
        LabelProceso1.setForeground(new java.awt.Color(255, 255, 255));
        LabelProceso1.setText("...");

        javax.swing.GroupLayout PanelProcesosLayout = new javax.swing.GroupLayout(PanelProcesos);
        PanelProcesos.setLayout(PanelProcesosLayout);
        PanelProcesosLayout.setHorizontalGroup(
            PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelProcesosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(LabelPROCESOS)
                .addContainerGap(329, Short.MAX_VALUE))
            .addGroup(PanelProcesosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelProceso2)
                    .addComponent(LabelProceso3)
                    .addComponent(LabelProceso1))
                .addContainerGap())
        );
        PanelProcesosLayout.setVerticalGroup(
            PanelProcesosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelProcesosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelPROCESOS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelProceso1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(LabelProceso2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelProceso3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        PanelMemoriaDatos.setBackground(new java.awt.Color(0, 0, 0));

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
                .addContainerGap(28, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void FieldIOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FieldIOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FieldIOActionPerformed

    private void ButtonInterrupcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonInterrupcionActionPerformed
        cargarInstrucciones();
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
    private javax.swing.JTextField LabelProceso1;
    private javax.swing.JTextField LabelProceso2;
    private javax.swing.JTextField LabelProceso3;
    private javax.swing.JLabel LabelRegistros;
    private javax.swing.JLabel LabelUC;
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
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}
