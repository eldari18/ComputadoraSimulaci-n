/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectocomputador;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author 57313
 */
public class ProyectoComputador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear la ventana principal (JFrame)
            JFrame frame = new JFrame("Simulador de Computador");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Agregar tu panel Computador al JFrame
            Computador computadorPanel = new Computador();
            frame.add(computadorPanel);

            // Configurar tamaño y visibilidad
            frame.pack(); // Ajusta el tamaño según los componentes
            frame.setSize(1500, 1000); // Tamaño personalizado (ancho, alto)
            frame.setLocationRelativeTo(null); // Centrar en la pantalla
            frame.setVisible(true);
        });
    }

}
