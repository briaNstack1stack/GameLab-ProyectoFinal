package controlador;

import modelo.Entidad;
import modelo.Jugador;
import modelo.Enemigo;
import modelo.EnemigoVeloz;
import modelo.EnemigoTanque;
import modelo.Proyectil;
import vista.PanelJuego;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class MotorJuego implements Runnable {
    private Jugador jugadorActual;
    private ArrayList<Enemigo> listaEnemigos;
    private ArrayList<Proyectil> listaProyectiles;
    
    private PanelJuego panelJuego;
    private boolean juegoEnCurso;
    private int puntuacion;
    private int record;
    private int anchoPantalla;
    private int altoPantalla;
    
    // --- MAPA AMPLIADO ---
    private final int MAP_WIDTH = 4000;
    private final int MAP_HEIGHT = 3000;
    
    private int contadorFrames;
    private Random random;

    public MotorJuego(PanelJuego panelJuego, int anchoPantalla, int altoPantalla) {
        this.panelJuego = panelJuego;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        
        this.listaEnemigos = new ArrayList<>();
        this.listaProyectiles = new ArrayList<>();
        this.record = GestorPersistencia.cargarRecord();
        
        // El jugador nace en el centro del nuevo mapa grande
        this.jugadorActual = new Jugador("Piloto 1", MAP_WIDTH / 2.0, MAP_HEIGHT / 2.0);
        
        this.juegoEnCurso = true;
        this.puntuacion = 0;
        this.contadorFrames = 0;
        this.random = new Random();

        this.panelJuego.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                Proyectil nuevaBala = jugadorActual.disparar();
                if (nuevaBala != null) {
                    listaProyectiles.add(nuevaBala);
                }
            }
        });

        this.panelJuego.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                jugadorActual.actualizarAngulo(e.getX(), e.getY(), anchoPantalla / 2.0, altoPantalla / 2.0);
            }
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                jugadorActual.actualizarAngulo(e.getX(), e.getY(), anchoPantalla / 2.0, altoPantalla / 2.0);
            }
        });

        this.panelJuego.setFocusable(true);
        this.panelJuego.requestFocusInWindow();
        this.panelJuego.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> jugadorActual.setUp(true);
                    case KeyEvent.VK_S -> jugadorActual.setDown(true);
                    case KeyEvent.VK_A -> jugadorActual.setLeft(true);
                    case KeyEvent.VK_D -> jugadorActual.setRight(true);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> jugadorActual.setUp(false);
                    case KeyEvent.VK_S -> jugadorActual.setDown(false);
                    case KeyEvent.VK_A -> jugadorActual.setLeft(false);
                    case KeyEvent.VK_D -> jugadorActual.setRight(false);
                }
            }
        });
    }

    @Override
    public void run() {
        while (juegoEnCurso) {
            jugadorActual.actualizar();
            actualizarProyectiles();
            actualizarEnemigos();
            
            generarEnemigos();
            detectarColisiones();
            limpiarEntidadesInactivas();
            
            if (jugadorActual.getVida() <= 0) {
                juegoEnCurso = false;
                if (puntuacion > record) {
                    record = puntuacion;
                    GestorPersistencia.guardarRecord(record);
                }
            }
            
            if (panelJuego != null) {
                panelJuego.actualizarDatos(jugadorActual, listaEnemigos, listaProyectiles, puntuacion, record);
            }
            
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            contadorFrames++;
        }
    }

    private void actualizarProyectiles() {
        for (Proyectil p : listaProyectiles) {
            if (p.isActivo()) p.actualizar();
        }
    }

    private void actualizarEnemigos() {
        for (Enemigo e : listaEnemigos) {
            if (e.isActivo()) {
                e.actualizar(jugadorActual.getX(), jugadorActual.getY());
            }
        }
    }

    private void generarEnemigos() {
        if (contadorFrames % 180 == 0) {
            double spawnX = jugadorActual.getX() + (random.nextBoolean() ? 800 : -800);
            double spawnY = jugadorActual.getY() + (random.nextBoolean() ? 800 : -800);
            
            spawnX = Math.max(100, Math.min(MAP_WIDTH - 100, spawnX));
            spawnY = Math.max(100, Math.min(MAP_HEIGHT - 100, spawnY));
            
            if (random.nextInt(100) < 30) {
                listaEnemigos.add(new EnemigoTanque(spawnX, spawnY));
            } else {
                listaEnemigos.add(new EnemigoVeloz(spawnX, spawnY));
            }
        }
    }

    private void detectarColisiones() {
        for (Proyectil p : listaProyectiles) {
            if (!p.isActivo()) continue;
            for (Enemigo e : listaEnemigos) {
                if (!e.isActivo()) continue;
                if (hayColisionCircular(p, e)) {
                    e.recibirDanio(p.getDanio());
                    p.setActivo(false);
                    if (e.getVida() <= 0) {
                        puntuacion += e.getRecompensaPuntos();
                        if (puntuacion > record) {
                            record = puntuacion;
                            GestorPersistencia.guardarRecord(record);
                        }
                    }
                }
            }
        }
        
        for (Enemigo e : listaEnemigos) {
            if (!e.isActivo()) continue;
            if (hayColisionCircular(e, jugadorActual)) {
                jugadorActual.recibirDanio(e.getDanio());
                e.setActivo(false);
            }
        }
    }

    private boolean hayColisionCircular(Entidad a, Entidad b) {
        double distanciaX = a.getX() - b.getX();
        double distanciaY = a.getY() - b.getY();
        return Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY) <= (a.getRadio() + b.getRadio());
    }

    private void limpiarEntidadesInactivas() {
        listaProyectiles.removeIf(p -> !p.isActivo());
        listaEnemigos.removeIf(e -> !e.isActivo());
    }
    
    public Jugador getJugadorActual() { return jugadorActual; }
    public void agregarProyectil(Proyectil p) { if (p != null) listaProyectiles.add(p); }
}