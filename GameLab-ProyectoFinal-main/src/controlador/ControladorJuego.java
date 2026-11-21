package controlador;

import vista.PantallaJuego;

public class ControladorJuego {
    
    private static final int ANCHO = 800;
    private static final int ALTO = 600;

    private final PantallaJuego vista;
    private MotorJuego motor;

    public ControladorJuego(PantallaJuego vista) {
        this.vista = vista;
    }

    public void iniciar() {
        vista.setVisible(true);

        motor = new MotorJuego(vista.getPanelJuego(), ANCHO, ALTO);
        Thread hiloJuego = new Thread(motor, "hilo-juego");
        hiloJuego.start();
    }

    public MotorJuego getMotor() {
        return motor;
    }
}
