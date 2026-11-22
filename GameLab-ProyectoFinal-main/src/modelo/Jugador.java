package modelo;

public class Jugador extends Entidad {
    private String nombre;
    private int vida;
    private double anguloApuntado;
    private double anguloCuerpo;
    
    private int cooldownDisparo;
    private int tiempoEscudo;
    private boolean escudoActivo;
    private int cooldownBomba;

    private boolean up, down, left, right;
    private double velocidadMovimiento = 6.5; 
    
    private final double MAP_WIDTH = 4000;
    private final double MAP_HEIGHT = 3000;

    public Jugador(String nombre, double x, double y) {
        super(x, y, 20, 0, true);
        this.nombre = nombre;
        this.vida = 100;
        this.anguloApuntado = 0.0;
        this.anguloCuerpo = 0.0;
        
        this.cooldownDisparo = 0;
        this.tiempoEscudo = 0;
        this.escudoActivo = false;
        this.cooldownBomba = 0;
    }

    @Override
    public void actualizar() {
        double dx = 0;
        double dy = 0;
        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        if (dx != 0 || dy != 0) {
            double length = Math.hypot(dx, dy);
            dx /= length;
            dy /= length;
            
            this.x += dx * velocidadMovimiento;
            this.y += dy * velocidadMovimiento;
            
            this.anguloCuerpo = Math.atan2(dy, dx);
        }

        this.x = Math.max(50, Math.min(MAP_WIDTH - 50, this.x));
        this.y = Math.max(50, Math.min(MAP_HEIGHT - 50, this.y));

        if (cooldownDisparo > 0) cooldownDisparo--;
        if (tiempoEscudo > 0) {
            tiempoEscudo--;
            if (tiempoEscudo == 0) escudoActivo = false;
        }
        if (cooldownBomba > 0) cooldownBomba--;
    }

    public boolean isMovimiento() {
        return up || down || left || right;
    }
    
    public Proyectil disparar() {
        if (cooldownDisparo == 0) {
            cooldownDisparo = 15; 
            return new Proyectil(this.x, this.y, this.anguloApuntado);
        }
        return null;
    }
    
    public void actualizarAngulo(int mouseX, int mouseY, double screenTankX, double screenTankY) {
        double deltaX = mouseX - screenTankX;
        double deltaY = mouseY - screenTankY;
        this.anguloApuntado = Math.atan2(deltaY, deltaX);
    }
    
    public void recibirDanio(int cantidad) {
        if (escudoActivo) return;
        this.vida = Math.max(0, this.vida - cantidad);
    }

    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }

    public double getAnguloCuerpo() { return anguloCuerpo; }
    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public double getAnguloApuntado() { return anguloApuntado; }
    public boolean isEscudoActivo() { return escudoActivo; }
}