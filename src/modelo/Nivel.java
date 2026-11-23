package modelo;

/**
 * Configuración de dificultad de un nivel.
 * Los multiplicadores se aplican sobre los valores base de cada enemigo.
 */
public class Nivel {
    private final int numero;
    private final int duracionSegundos;
    private final int intervaloSpawn;       // frames entre un enemigo y el siguiente (60 frames = 1 s)
    private final double multVelocidad;
    private final double multDanio;
    private final double multVida;
    private final int probabilidadTanque;   // 0 a 100: % de enemigos que serán tanque

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

    public int getNumero() { return numero; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public int getIntervaloSpawn() { return intervaloSpawn; }
    public double getMultVelocidad() { return multVelocidad; }
    public double getMultDanio() { return multDanio; }
    public double getMultVida() { return multVida; }
    public int getProbabilidadTanque() { return probabilidadTanque; }
}
