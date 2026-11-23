package controlador;

import vista.PantallaJuego;
import vista.PantallaInicio;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ControladorJuego {

    private static final int ANCHO = 800;
    private static final int ALTO = 600;

    private final PantallaJuego vista;
    private MotorJuego motor;

    public ControladorJuego(PantallaJuego vista) {
        this.vista = vista;
    }

    public void iniciar(boolean continuar) {

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);

        motor = new MotorJuego(
            vista.getPanelJuego(),
            ANCHO,
            ALTO,
            continuar
        );

        // Configurar tecla ESC
        vista.getRootPane().getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(KeyStroke.getKeyStroke("ESCAPE"), "volverMenu");

        vista.getRootPane().getActionMap().put(
            "volverMenu",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    volverAlMenu();
                }
            }
        );

        Thread hiloJuego = new Thread(motor);
        hiloJuego.start();
    }

    private void volverAlMenu() {

        // Detener la partida actual
        motor.salirAlMenu();

        // Cerrar ventana del juego
        vista.dispose();

        // Abrir menú principal
        PantallaInicio menu = new PantallaInicio();

        ControladorInicio controlador =
            new ControladorInicio(menu);

        controlador.iniciar();
    }

    public MotorJuego getMotor() {
        return motor;
    }
}