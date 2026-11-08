package modelo;

public class Enemigo extends Entidad {
    protected int vida;
    protected int danio;
    protected int recompensaPuntos;
    protected int frameAnimacion = 0; // <--- Frame interno del enemigo
    protected int contadorAnimacion = 0;

    public Enemigo(double x, double y, int radio, double velocidad, int vida, int danio, int recompensaPuntos) {
        super(x, y, radio, velocidad, true);
        this.vida = vida;
        this.danio = danio;
        this.recompensaPuntos = recompensaPuntos;
    }

    @Override
    public void actualizar() {
        this.y += this.velocidad;
        actualizarFrames();
    }

    // Sobrecarga para cuando persigue al jugador
    public void actualizar(double jugadorX, double jugadorY) {
        double angulo = Math.atan2(jugadorY - this.y, jugadorX - this.x);
        this.x += this.velocidad * Math.cos(angulo);
        this.y += this.velocidad * Math.sin(angulo);
        actualizarFrames();
    }

    // Lógica para que el sprite del enemigo rote/anime automáticamente
    private void actualizarFrames() {
        contadorAnimacion++;
        if (contadorAnimacion % 10 == 0) { // Cambia de frame cada 10 ticks
            frameAnimacion = (frameAnimacion + 1) % 4;
        }
    }

    public int getFrameAnimacion() {
        return frameAnimacion;
    }

    public void recibirDanio(int cantidad) {
        this.vida -= cantidad;
        if (this.vida <= 0) {
            this.vida = 0;
            this.activo = false;
        }
    }

    public int getVida() { return vida; }
    public void setVida(int vida) { 
        this.vida = Math.max(0, vida);
        if (this.vida == 0) this.activo = false;
    }
    public int getDanio() { return danio; }
    public void setDanio(int nuevoDanio) { this.danio = Math.max(0, nuevoDanio); }
    public int getRecompensaPuntos() { return recompensaPuntos; }
    public void setRecompensaPuntos(int nuevosPuntos) { this.recompensaPuntos = Math.max(0, nuevosPuntos); }
}