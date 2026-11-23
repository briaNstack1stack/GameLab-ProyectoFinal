package controlador;

import modelo.DatosPartida;
import modelo.Entidad;
import modelo.Jugador;
import modelo.Enemigo;
import modelo.EnemigoVeloz;
import modelo.EnemigoTanque;
import modelo.Nivel;
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
    private boolean partidaFinalizada = false;
    private int anchoPantalla;
    private int altoPantalla;
    
    
    private final int MAP_WIDTH = 4000;
    private final int MAP_HEIGHT = 3000;
    
    private Random random;

    // --- NIVELES (3 niveles x 100 s = 300 s = 5 minutos) ---
    private static final int DURACION_NIVEL_SEGUNDOS = 100;
    private static final int MS_MENSAJE_NIVEL = 3000; // cuánto se muestra el cartel "NIVEL X"

    //                                 nº, duración,               spawn, vel,  daño, vida, %tanque
    private final Nivel[] niveles = {
        new Nivel(1, DURACION_NIVEL_SEGUNDOS, 180, 1.00, 1.0, 1.00, 30),
        new Nivel(2, DURACION_NIVEL_SEGUNDOS, 120, 1.25, 1.5, 1.25, 40),
        new Nivel(3, DURACION_NIVEL_SEGUNDOS,  75, 1.50, 2.0, 1.50, 50)
    };
    private int indiceNivel;
    private long inicioNivelMs;
    private int framesParaSpawn;
    private int segundosRestantes;
    private boolean victoria;
    private String mensajeCentral;

    public MotorJuego(PanelJuego panelJuego, int anchoPantalla, int altoPantalla, boolean continuar) {
        this.panelJuego = panelJuego;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        
        this.listaEnemigos = new ArrayList<>();
        this.listaProyectiles = new ArrayList<>();
        this.record = PersistenciaJuego.cargarRecord();
        
        this.random = new Random();

        if (continuar) {

            DatosPartida datos = PersistenciaJuego.cargarPartida();

            if (datos != null) {
                restaurarPartida(datos);
            } else {
                iniciarPartidaNueva();
            }

        } else {
            iniciarPartidaNueva();
        }

        this.panelJuego.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                Proyectil nuevaBala = jugadorActual.disparar();
                if (nuevaBala != null) {
                    agregarProyectil(nuevaBala);
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
                    case KeyEvent.VK_E -> jugadorActual.activarEscudo();
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
    
    private void iniciarPartidaNueva() {

        this.listaEnemigos.clear();
        this.listaProyectiles.clear();

        this.jugadorActual = new Jugador(
                "Piloto 1",
                MAP_WIDTH / 2.0,
                MAP_HEIGHT / 2.0
        );

        this.juegoEnCurso = true;
        this.puntuacion = 0;
        this.indiceNivel = 0;
        this.inicioNivelMs = System.currentTimeMillis();
        this.framesParaSpawn = 0;
        this.segundosRestantes = niveles[0].getDuracionSegundos();

        this.victoria = false;
        this.mensajeCentral = null;
        this.partidaFinalizada = false;
    }
    
    @Override
    public void run() {

        while (juegoEnCurso) {

            jugadorActual.actualizar();

            actualizarProyectiles();
            actualizarEnemigos();

            // Generar enemigos
            generarEnemigos();

            // Revisar colisiones
            detectarColisiones();
            limpiarEntidadesInactivas();

            // Revisar si el jugador perdió
            if (jugadorActual.getVida() <= 0) {
                juegoEnCurso = false;
                victoria = false;
                mensajeCentral = "GAME OVER";
            }

            // Actualizar el nivel y el tiempo
            if (juegoEnCurso) {
                actualizarNivel();
            }

            // Registrar el resultado al terminar la partida
            if (!juegoEnCurso) {
                finalizarPartida();
            }

            // Actualizar pantalla
            if (panelJuego != null) {

                panelJuego.actualizarNivel(
                    niveles[indiceNivel].getNumero(),
                    niveles.length,
                    segundosRestantes,
                    mensajeCentral
                );

                panelJuego.actualizarDatos(
                    jugadorActual,
                    listaEnemigos,
                    listaProyectiles,
                    puntuacion,
                    record
                );
            }

            // Controlar velocidad del ciclo
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    private void actualizarProyectiles() {
        synchronized (listaProyectiles) {
            for (Proyectil p : listaProyectiles) {
                if (p.isActivo()) {
                    p.actualizar();
                }
            }
        }
    }

    private void actualizarEnemigos() {
        for (Enemigo e : listaEnemigos) {
            if (e.isActivo()) {
                e.actualizar(jugadorActual.getX(), jugadorActual.getY());
            }
        }
    }

    /**
     * Controla el paso del tiempo: cambia de nivel cuando se acaba el tiempo
     * del actual y termina la partida con victoria al acabar el último.
     */
    private void actualizarNivel() {
        long ahora = System.currentTimeMillis();
        long transcurridoNivel = ahora - inicioNivelMs;
        Nivel nivel = niveles[indiceNivel];

        if (transcurridoNivel >= nivel.getDuracionSegundos() * 1000L) {
            if (indiceNivel < niveles.length - 1) {
                // Pasa al siguiente nivel
                indiceNivel++;
                nivel = niveles[indiceNivel];
                inicioNivelMs = ahora;
                transcurridoNivel = 0;
                framesParaSpawn = 0; // oleada inmediata al empezar el nivel
            } else {
                // Terminó el último nivel: ganó
                victoria = true;
                juegoEnCurso = false;
                segundosRestantes = 0;
                mensajeCentral = "¡VICTORIA!";
                return;
            }
        }

        // Tiempo que falta de toda la partida
        long msRestantes = nivel.getDuracionSegundos() * 1000L - transcurridoNivel;
        for (int i = indiceNivel + 1; i < niveles.length; i++) {
            msRestantes += niveles[i].getDuracionSegundos() * 1000L;
        }
        segundosRestantes = (int) Math.ceil(msRestantes / 1000.0);

        // Cartel "NIVEL X" durante los primeros segundos de cada nivel
        mensajeCentral = (transcurridoNivel < MS_MENSAJE_NIVEL) ? "NIVEL " + nivel.getNumero() : null;
    }

    private void generarEnemigos() {
        if (framesParaSpawn > 0) {
            framesParaSpawn--;
            return;
        }

        Nivel nivel = niveles[indiceNivel];
        framesParaSpawn = nivel.getIntervaloSpawn() - 1;

        double spawnX, spawnY;
        int lado = random.nextInt(4);

        switch (lado) {
            case 0: // Arriba
                spawnX = jugadorActual.getX() + random.nextInt(1001) - 500;
                spawnY = jugadorActual.getY() - 800;
                break;

            case 1: // Abajo
                spawnX = jugadorActual.getX() + random.nextInt(1001) - 500;
                spawnY = jugadorActual.getY() + 800;
                break;

            case 2: // Izquierda
                spawnX = jugadorActual.getX() - 800;
                spawnY = jugadorActual.getY() + random.nextInt(1001) - 500;
                break;

            default: // Derecha
                spawnX = jugadorActual.getX() + 800;
                spawnY = jugadorActual.getY() + random.nextInt(1001) - 500;
                break;
        }

        // Evitar que aparezcan fuera del mapa
        spawnX = Math.max(100, Math.min(MAP_WIDTH - 100, spawnX));
        spawnY = Math.max(100, Math.min(MAP_HEIGHT - 100, spawnY));

        Enemigo nuevo;
        if (random.nextInt(100) < nivel.getProbabilidadTanque()) {
            nuevo = new EnemigoTanque(spawnX, spawnY);
        } else {
            nuevo = new EnemigoVeloz(spawnX, spawnY);
        }

        // Sube velocidad, daño y vida según el nivel
        nuevo.aplicarDificultad(nivel.getMultVelocidad(), nivel.getMultDanio(), nivel.getMultVida());
        listaEnemigos.add(nuevo);
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
        synchronized (listaProyectiles) {
            listaProyectiles.removeIf(p -> !p.isActivo());
        }

        synchronized (listaEnemigos) {
            listaEnemigos.removeIf(e -> !e.isActivo());
        }
    }
    
    public Jugador getJugadorActual() { return jugadorActual; }
    public int getNivelActual() { return niveles[indiceNivel].getNumero(); }
    public boolean isVictoria() { return victoria; }
    public void agregarProyectil(Proyectil p) {
        if (p != null) {
            synchronized (listaProyectiles) {
                listaProyectiles.add(p);
            }
        }
    }
    
    

    private void finalizarPartida() {

        if (partidaFinalizada) {
            return;
        }

        partidaFinalizada = true;
        PersistenciaJuego.eliminarPartidaGuardada();

        // Solicitar iniciales al jugador
        String nombre = pedirIniciales();

        // Si cancela, guardar igualmente la partida
        if (nombre == null) {
            nombre = "CPU";
        }

        // Guardar TODAS las partidas
        PersistenciaJuego.guardarRecord(nombre, puntuacion);

        // Actualizar el récord máximo en memoria
        if (puntuacion > record) {
            record = puntuacion;
        }
    }

    
    
    private String pedirIniciales() {

        java.util.concurrent.atomic.AtomicReference<String> resultado =
            new java.util.concurrent.atomic.AtomicReference<>(null);

        try {

            javax.swing.SwingUtilities.invokeAndWait(() -> {

                while (true) {

                    String nombre = javax.swing.JOptionPane.showInputDialog(
                        panelJuego,
                        "¡NUEVO RECORD!\n\n"
                        + "Ingresa tus 3 iniciales:",
                        "NUEVO RECORD",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );

                    if (nombre == null) {
                        break;
                    }

                    nombre = nombre.trim().toUpperCase();

                    if (nombre.matches("[A-Z]{3}")) {
                        resultado.set(nombre);
                        break;
                    }

                    javax.swing.JOptionPane.showMessageDialog(
                        panelJuego,
                        "Debes ingresar exactamente 3 letras.",
                        "Nombre inválido",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                }
            });

        } catch (Exception e) {
            System.err.println("Error al solicitar iniciales: " + e.getMessage());
        }

        return resultado.get();
    }
    
    private void guardarEstadoPartida() {

        DatosPartida datos = new DatosPartida();

        // Datos del jugador
        datos.jugadorX = jugadorActual.getX();
        datos.jugadorY = jugadorActual.getY();
        datos.vidaJugador = jugadorActual.getVida();
        datos.anguloApuntado = jugadorActual.getAnguloApuntado();
        datos.anguloCuerpo = jugadorActual.getAnguloCuerpo();
        datos.tiempoEscudo = jugadorActual.getTiempoEscudo();
        datos.cooldownDisparo = jugadorActual.getCooldownDisparo();
        datos.radioJugador = jugadorActual.getRadioJugador();
        datos.velocidadJugador = jugadorActual.getVelocidadMovimiento();
        datos.nombreJugador = jugadorActual.getNombre();

        // Datos generales de la partida
        datos.puntuacion = puntuacion;
        datos.indiceNivel = indiceNivel;
        datos.segundosRestantes = segundosRestantes;
        datos.framesParaSpawn = framesParaSpawn;

        long transcurrido = System.currentTimeMillis() - inicioNivelMs;
        long duracion = niveles[indiceNivel].getDuracionSegundos() * 1000L;

        datos.tiempoNivelRestanteMs = Math.max(0, duracion - transcurrido);

        // Guardar enemigos
        synchronized (listaEnemigos) {
            for (Enemigo e : listaEnemigos) {

                String tipo = (e instanceof EnemigoTanque) ? "T" : "V";

                String enemigo = tipo + ","
                        + e.getX() + ","
                        + e.getY() + ","
                        + e.getRadio() + ","
                        + e.getVelocidad() + ","
                        + e.getVida() + ","
                        + e.getVidaMaxima() + ","
                        + e.getDanio() + ","
                        + e.getRecompensaPuntos() + ","
                        + e.isActivo() + ","
                        + e.getFrameAnimacion() + ","
                        + e.getContadorAnimacion();

                datos.enemigos.add(enemigo);
            }
        }

        // Guardar proyectiles
        synchronized (listaProyectiles) {
            for (Proyectil p : listaProyectiles) {

                String proyectil = p.getX() + ","
                        + p.getY() + ","
                        + p.getRadio() + ","
                        + p.getVelocidad() + ","
                        + p.getAnguloViaje() + ","
                        + p.getDanio() + ","
                        + p.isActivo();

                datos.proyectiles.add(proyectil);
            }
        }

        PersistenciaJuego.guardarPartida(datos);
    }
    
    private void cargarEntidadesGuardadas(DatosPartida datos) {

        listaEnemigos.clear();
        listaProyectiles.clear();

        // Recuperar enemigos
        for (String registro : datos.enemigos) {

            String[] partes = registro.split(",");

            String tipo = partes[0];

            double x = Double.parseDouble(partes[1]);
            double y = Double.parseDouble(partes[2]);

            Enemigo enemigo;

            if (tipo.equals("T")) {
                enemigo = new EnemigoTanque(x, y);
            } else {
                enemigo = new EnemigoVeloz(x, y);
            }

            enemigo.setRadio(Integer.parseInt(partes[3]));
            enemigo.setVelocidad(Double.parseDouble(partes[4]));
            enemigo.setVida(Integer.parseInt(partes[5]));
            enemigo.setVidaMaxima(Integer.parseInt(partes[6]));
            enemigo.setDanio(Integer.parseInt(partes[7]));
            enemigo.setRecompensaPuntos(Integer.parseInt(partes[8]));
            enemigo.setActivo(Boolean.parseBoolean(partes[9]));
            enemigo.setFrameAnimacion(Integer.parseInt(partes[10]));
            enemigo.setContadorAnimacion(Integer.parseInt(partes[11]));

            listaEnemigos.add(enemigo);
        }

        // Recuperar proyectiles
        for (String registro : datos.proyectiles) {

            String[] partes = registro.split(",");

            double x = Double.parseDouble(partes[0]);
            double y = Double.parseDouble(partes[1]);
            int radio = Integer.parseInt(partes[2]);
            double velocidad = Double.parseDouble(partes[3]);
            double angulo = Double.parseDouble(partes[4]);
            int danio = Integer.parseInt(partes[5]);
            boolean activo = Boolean.parseBoolean(partes[6]);

            Proyectil proyectil = new Proyectil(x, y, angulo);

            proyectil.setRadio(radio);
            proyectil.setVelocidad(velocidad);
            proyectil.setDanio(danio);
            proyectil.setActivo(activo);

            listaProyectiles.add(proyectil);
        }
    }
    
    private void restaurarPartida(DatosPartida datos) {

        // Recuperar jugador
        jugadorActual = new Jugador(
                datos.nombreJugador,
                datos.jugadorX,
                datos.jugadorY
        );

        jugadorActual.setVida(datos.vidaJugador);
        jugadorActual.setAnguloApuntado(datos.anguloApuntado);
        jugadorActual.setAnguloCuerpo(datos.anguloCuerpo);
        jugadorActual.setTiempoEscudo(datos.tiempoEscudo);
        jugadorActual.setCooldownDisparo(datos.cooldownDisparo);
        jugadorActual.setRadio(datos.radioJugador);
        jugadorActual.setVelocidadMovimiento(datos.velocidadJugador);

        // Recuperar datos generales
        puntuacion = datos.puntuacion;

        indiceNivel = datos.indiceNivel;

        // Evitar índices inválidos
        if (indiceNivel < 0 || indiceNivel >= niveles.length) {
            indiceNivel = 0;
        }

        segundosRestantes = datos.segundosRestantes;
        framesParaSpawn = datos.framesParaSpawn;

        // Recuperar tiempo restante del nivel
        long duracionNivel = niveles[indiceNivel].getDuracionSegundos() * 1000L;

        long tiempoTranscurrido = duracionNivel - datos.tiempoNivelRestanteMs;

        inicioNivelMs = System.currentTimeMillis() - tiempoTranscurrido;

        // Recuperar enemigos y proyectiles
        cargarEntidadesGuardadas(datos);

        // Preparar partida
        victoria = false;
        mensajeCentral = null;
        partidaFinalizada = false;
        juegoEnCurso = true;
    }
    
    public void salirAlMenu() {

        if (juegoEnCurso && !partidaFinalizada) {

            // Guardar el estado actual antes de salir
            guardarEstadoPartida();

            // Evitar que se registre como una partida terminada
            partidaFinalizada = true;
            juegoEnCurso = false;
        }
    }
}