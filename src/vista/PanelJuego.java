package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import modelo.Enemigo;
import modelo.EnemigoTanque;
import modelo.Jugador;
import modelo.Proyectil;

public class PanelJuego extends javax.swing.JPanel {
    private Jugador jugador;
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private ArrayList<Proyectil> proyectiles = new ArrayList<>();
    private int puntuacion = 0;
    private int record = 0;

    // Sprites y recursos gráficos
    private BufferedImage[] baseFrames = new BufferedImage[4];
    private BufferedImage cannonImage;
    private BufferedImage escudoImage;
    private BufferedImage[] enemyFrames = new BufferedImage[4];
    private BufferedImage[] enemyTankFrames = new BufferedImage[4];
    private BufferedImage playerBulletImage;
    private BufferedImage backgroundImage;

    private int currentFrame = 0;
    private int animTimer = 0;

    // Dimensiones de la pantalla y el mundo gigante
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;
    private final int MAP_WIDTH = 4000;
    private final int MAP_HEIGHT = 3000;

    public PanelJuego() {
        initComponents();
        this.setBackground(Color.BLACK);
        loadResources();
    }

    private void loadResources() {
        try {
            BufferedImage baseSheet = ImageIO.read(
                getClass().getResource("/recursos/Tank.png")
            );

            if (baseSheet != null) {
                int frameWidth = baseSheet.getWidth() / 4;
                int frameHeight = baseSheet.getHeight();

                for (int i = 0; i < 4; i++) {
                    baseFrames[i] = baseSheet.getSubimage(
                        i * frameWidth,
                        0,
                        frameWidth,
                        frameHeight
                    );
                }
            }

            cannonImage = ImageIO.read(
                getClass().getResource("/recursos/Canon.png")
            );

            BufferedImage enemySheet = ImageIO.read(
                getClass().getResource("/recursos/EnemigoVeloz.png")
            );

            if (enemySheet != null) {
                for (int i = 0; i < 4; i++) {
                    enemyFrames[i] = enemySheet.getSubimage(
                        i * 48, 0, 48, 48
                    );
                }
            }

            // Cargar spritesheet de enemigos tanque
            BufferedImage enemyTankSheet = ImageIO.read(
                getClass().getResource("/recursos/EnemigoTanque.png")
            );

            if (enemyTankSheet != null) {
                for (int i = 0; i < 4; i++) {
                    enemyTankFrames[i] = enemyTankSheet.getSubimage(
                        i * 64,
                        0,
                        64,
                        79
                    );
                }
            }

            escudoImage = ImageIO.read(
                getClass().getResource("/recursos/Escudo.png")
            );

            playerBulletImage = ImageIO.read(
                getClass().getResource("/recursos/BalaTanque.png")
            );

            backgroundImage = ImageIO.read(
                getClass().getResource("/recursos/FondoPrueba.png")
            );

        } catch (IOException | NullPointerException e) {
            System.err.println(
                "Aviso: No se pudieron cargar algunos recursos gráficos desde /recursos/. Asegúrate de incluir 'FondoPrueba.png'."
            );
        }
    }

    public void actualizarDatos(
        Jugador j,
        ArrayList<Enemigo> e,
        ArrayList<Proyectil> p,
        int puntuacion,
        int record
    ) {
        this.jugador = j;
        this.enemigos = e;
        this.proyectiles = p;
        this.puntuacion = puntuacion;
        this.record = record;

        if (jugador != null && jugador.isMovimiento()) {
            animTimer++;

            if (animTimer % 8 == 0) {
                currentFrame = (currentFrame + 1) % 4;
            }
        } else {
            currentFrame = 0;
        }

        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        if (jugador == null) return;

        // CÁLCULO DE CÁMARA
        double worldTankX = jugador.getX();
        double worldTankY = jugador.getY();

        double cameraX = (SCREEN_WIDTH / 2.0) - worldTankX;
        double cameraY = (SCREEN_HEIGHT / 2.0) - worldTankY;

        // DIBUJAR FONDO TEXTURIZADO EN MOSAICO
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();

            if (imgWidth > 0 && imgHeight > 0) {
                for (int x = 0; x < MAP_WIDTH; x += imgWidth) {
                    for (int y = 0; y < MAP_HEIGHT; y += imgHeight) {
                        int screenX = (int) (x + cameraX);
                        int screenY = (int) (y + cameraY);

                        if (screenX + imgWidth >= 0
                                && screenX <= SCREEN_WIDTH
                                && screenY + imgHeight >= 0
                                && screenY <= SCREEN_HEIGHT) {

                            g2d.drawImage(
                                backgroundImage,
                                screenX,
                                screenY,
                                null
                            );
                        }
                    }
                }
            }
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(
                0, 0, SCREEN_WIDTH, SCREEN_HEIGHT
            );
        }

        // POSICIÓN DEL TANQUE EN PANTALLA
        double screenTankX = worldTankX + cameraX;
        double screenTankY = worldTankY + cameraY;

        // 1. DIBUJAR ENEMIGOS
        for (Enemigo en : enemigos) {
            double screenEnX = en.getX() + cameraX;
            double screenEnY = en.getY() + cameraY;

            BufferedImage currentEnFrame;

            if (en instanceof EnemigoTanque) {
                currentEnFrame = enemyTankFrames[
                    en.getFrameAnimacion()
                ];

                if (currentEnFrame != null) {
                    g2d.drawImage(
                        currentEnFrame,
                        (int) screenEnX - 32,
                        (int) screenEnY - 39,
                        null
                    );
                }

            } else {
                currentEnFrame = enemyFrames[
                    en.getFrameAnimacion()
                ];

                if (currentEnFrame != null) {
                    g2d.drawImage(
                        currentEnFrame,
                        (int) screenEnX - 24,
                        (int) screenEnY - 24,
                        null
                    );
                }
            }

            int vidaMax = (en instanceof EnemigoTanque) ? 150 : 30;

            dibujarBarraVida(
                g2d,
                (int) screenEnX,
                (int) screenEnY - en.getRadio() - 15,
                en.getVida(),
                vidaMax,
                30,
                5
            );
        }

        // 2. DIBUJAR BASE DEL JUGADOR
        if (baseFrames[currentFrame] != null) {
            AffineTransform oldAT = g2d.getTransform();

            g2d.rotate(
                jugador.getAnguloCuerpo() + Math.PI / 2,
                screenTankX,
                screenTankY
            );

            g2d.drawImage(
                baseFrames[currentFrame],
                (int) (
                    screenTankX
                    - baseFrames[currentFrame].getWidth() / 2.0
                ),
                (int) (
                    screenTankY
                    - baseFrames[currentFrame].getHeight() / 2.0
                ),
                null
            );

            g2d.setTransform(oldAT);
        }

        // 3. DIBUJAR PROYECTILES DEL JUGADOR
        for (Proyectil p : proyectiles) {
            double screenBulletX = p.getX() + cameraX;
            double screenBulletY = p.getY() + cameraY;

            if (playerBulletImage != null) {
                AffineTransform oldAT = g2d.getTransform();

                // La imagen original está inclinada 45 grados hacia la derecha
                double angulo = p.getAnguloViaje() + Math.PI / 4;

                g2d.rotate(
                    angulo,
                    screenBulletX,
                    screenBulletY
                );

                g2d.drawImage(
                    playerBulletImage,
                    (int) (
                        screenBulletX
                        - playerBulletImage.getWidth() / 2.0
                    ),
                    (int) (
                        screenBulletY
                        - playerBulletImage.getHeight() / 2.0
                    ),
                    null
                );

                g2d.setTransform(oldAT);
            }
        }

        // 4. DIBUJAR CAÑÓN
        if (cannonImage != null) {
            AffineTransform oldAT = g2d.getTransform();

            double mouseX = getMousePosition() != null
                ? getMousePosition().getX()
                : screenTankX;

            double mouseY = getMousePosition() != null
                ? getMousePosition().getY()
                : screenTankY;

            double cannonAngle = Math.atan2(
                mouseY - screenTankY,
                mouseX - screenTankX
            ) + Math.PI / 2;

            g2d.rotate(
                cannonAngle,
                screenTankX,
                screenTankY
            );

            g2d.drawImage(
                cannonImage,
                (int) (
                    screenTankX - cannonImage.getWidth() / 2.0
                ),
                (int) (
                    screenTankY - cannonImage.getHeight() / 2.0
                ),
                null
            );

            g2d.setTransform(oldAT);
        }

        // 5. DIBUJAR ESCUDO POR ENCIMA DEL TANQUE Y LAS BALAS
        if (jugador.isEscudoActivo()
                && escudoImage != null
                && baseFrames[currentFrame] != null) {

            int anchoEscudo =
                baseFrames[currentFrame].getWidth() + 32;

            int altoEscudo =
                baseFrames[currentFrame].getHeight() + 32;

            AffineTransform transformacionAnterior =
                g2d.getTransform();

            // Girar el escudo junto con el cuerpo del tanque
            g2d.rotate(
                jugador.getAnguloCuerpo() + Math.PI / 2,
                screenTankX,
                screenTankY
            );

            g2d.drawImage(
                escudoImage,
                (int) (screenTankX - anchoEscudo / 2.0),
                (int) (screenTankY - altoEscudo / 2.0),
                anchoEscudo,
                altoEscudo,
                null
            );

            g2d.setTransform(transformacionAnterior);
        }

        // 6. BARRA DE VIDA DEL JUGADOR
        dibujarBarraVida(
            g2d,
            (int) screenTankX,
            (int) screenTankY - 35,
            jugador.getVida(),
            100,
            40,
            6
        );

        // 7. HUD (PUNTUACIÓN Y RÉCORD)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("SCORE: " + puntuacion, 20, 30);

        g2d.setColor(Color.YELLOW);
        g2d.drawString("HIGH SCORE: " + record, 20, 55);
    }

    private void dibujarBarraVida(
        Graphics2D g2d,
        int centerX,
        int topY,
        int vidaActual,
        int vidaMax,
        int ancho,
        int alto
    ) {
        int x = centerX - (ancho / 2);
        int porcentaje = Math.max(
            0,
            (vidaActual * ancho) / vidaMax
        );

        g2d.setColor(Color.RED);
        g2d.fillRect(x, topY, ancho, alto);

        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, topY, porcentaje, alto);

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, topY, ancho, alto);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
