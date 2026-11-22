package proyectofinaljuego;

import controlador.ControladorInicio;
import vista.PantallaInicio;
import javax.swing.SwingUtilities;

public class ProyectoFinalJuego {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            PantallaInicio menu = new PantallaInicio();

            ControladorInicio controlador = new ControladorInicio(menu);

            controlador.iniciar();
        });
    }
}