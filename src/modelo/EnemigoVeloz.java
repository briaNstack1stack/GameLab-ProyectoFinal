package modelo;

public class EnemigoVeloz extends Enemigo {
    public EnemigoVeloz(double x, double y) {
        super(x, y, 15, 4.0, 30, 10, 50); // Velocidad 4.0 (menor que el jugador)
    }
}
