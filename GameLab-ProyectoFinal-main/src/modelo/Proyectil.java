package modelo;


public class Proyectil extends Entidad {
    private double anguloViaje;
    private int danio;

    public Proyectil(double x, double y, double anguloViaje) {
        super(x, y, 5, 15.0, true); 
        this.anguloViaje = anguloViaje;
        this.danio = 20; 
    }

    @Override
    public void actualizar() {
        this.x += this.velocidad * Math.cos(this.anguloViaje);
        this.y += this.velocidad * Math.sin(this.anguloViaje);
    }

    public double getAnguloViaje() {
        return anguloViaje;
    }

    public void setAnguloViaje(double anguloViaje) {
        this.anguloViaje = anguloViaje;
    }

    public int getDanio() {
        return danio;
    }

    public void setDanio(int nuevoDanio) {
        if (nuevoDanio < 0) {
            this.danio = 0; 
        } else {
            this.danio = nuevoDanio;
        }
    }
}
