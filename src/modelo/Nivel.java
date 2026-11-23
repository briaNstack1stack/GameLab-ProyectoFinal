package modelo;

public class Nivel {

    private final int numero;
    private final int duracionSegundos;
    private final int intervaloSpawn;
    private final double multVelocidad;
    private final double multDanio;
    private final double multVida;
    private final int probabilidadTanque;

    public Nivel(int numero, int duracionSegundos, int intervaloSpawn,
                 double multVelocidad, double multDanio, double multVida,
                 int probabilidadTanque) {

        this.numero = numero;
        this.duracionSegundos = duracionSegundos;
        this.intervaloSpawn = intervaloSpawn;
        this.multVelocidad = multVelocidad;
        this.multDanio = multDanio;
        this.multVida = multVida;
        this.probabilidadTanque = probabilidadTanque;
    }

    public int getNumero() {
        return numero;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public int getIntervaloSpawn() {
        return intervaloSpawn;
    }

    public double getMultVelocidad() {
        return multVelocidad;
    }

    public double getMultDanio() {
        return multDanio;
    }

    public double getMultVida() {
        return multVida;
    }

    public int getProbabilidadTanque() {
        return probabilidadTanque;
    }
}