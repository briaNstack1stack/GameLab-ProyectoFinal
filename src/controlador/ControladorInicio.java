package controlador;

import vista.PantallaInicio;
import vista.PantallaJuego;

public class ControladorInicio {

    private final PantallaInicio vista;

    public ControladorInicio(PantallaInicio vista) {
        this.vista = vista;

        this.vista.getBtnStart().addActionListener(e -> iniciarJuego());
        this.vista.getBtnExit().addActionListener(e -> salir());
        this.vista.getBtnRecord().addActionListener(e -> mostrarRecord());
        this.vista.getBtnContinuar().addActionListener(e -> continuarJuego());
        this.vista.getBtnContinuar().setEnabled(PersistenciaJuego.existePartidaGuardada());
    }

    public void iniciar() {
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void iniciarJuego() {

        // START siempre comienza una partida nueva
        PersistenciaJuego.eliminarPartidaGuardada();

        PantallaJuego ventanaJuego = new PantallaJuego();

        new ControladorJuego(ventanaJuego).iniciar(false);

        vista.dispose();
    }
    
    private void continuarJuego() {

        if (!PersistenciaJuego.existePartidaGuardada()) {
            return;
        }

        PantallaJuego ventanaJuego = new PantallaJuego();

        new ControladorJuego(ventanaJuego).iniciar(true);

        vista.dispose();
    }    

    private void mostrarRecord() {

        String historial =
            PersistenciaJuego.cargarHistorialRecords();

        javax.swing.JTextArea area =
            new javax.swing.JTextArea(historial, 15, 35);

        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));

        javax.swing.JScrollPane scroll =
            new javax.swing.JScrollPane(area);

        javax.swing.JOptionPane.showMessageDialog(
            vista,
            scroll,
            "HISTORIAL DE RECORDS",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void salir() {
        System.exit(0);
    }
}