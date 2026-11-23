package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosPartida implements Serializable {

    private static final long serialVersionUID = 1L;

    // Datos del jugador
    public double jugadorX;
    public double jugadorY;
    public int vidaJugador;
    public double anguloApuntado;
    public double anguloCuerpo;
    public int tiempoEscudo;
    public int cooldownDisparo;
    public int radioJugador;
    public double velocidadJugador;
    public String nombreJugador;

    // Datos generales de la partida
    public int puntuacion;
    public int indiceNivel;
    public int segundosRestantes;
    public int framesParaSpawn;
    public long tiempoNivelRestanteMs;

    // Entidades de la partida
    public ArrayList<String> enemigos = new ArrayList<>();
    public ArrayList<String> proyectiles = new ArrayList<>();
}