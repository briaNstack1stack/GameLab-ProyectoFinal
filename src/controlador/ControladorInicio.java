package controlador;

import vista.PantallaInicio;
import vista.PantallaJuego;

public class ControladorInicio {

    private final PantallaInicio vista;

    public ControladorInicio(PantallaInicio vista) {
        this.vista = vista;

        this.vista.getBtnStart().addActionListener(e -> iniciarJuego());
        this.vista.getBtnExit().addActionListener(e -> salir());
    }

    public void iniciar() {
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void iniciarJuego() {
        PantallaJuego ventanaJuego = new PantallaJuego();

        new ControladorJuego(ventanaJuego).iniciar();

        vista.dispose();
    }

    private void salir() {
        System.exit(0);
    }
}