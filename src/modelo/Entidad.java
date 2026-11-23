package modelo;

public abstract class Entidad {

    protected double x;
    protected double y;
    protected int radio;
    protected double velocidad;
    protected boolean activo;

    public Entidad(double x, double y, int radio, double velocidad, boolean activo) {
        this.x = x;
        this.y = y;
        this.radio = radio;
        this.velocidad = velocidad;
        this.activo = activo;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            this.x = 0;
        } else {
            this.x = x;
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (Double.isNaN(y) || Double.isInfinite(y)) {
            this.y = 0;
        } else {
            this.y = y;
        }
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        if (radio <= 0) {
            this.radio = 1;
        } else {
            this.radio = radio;
        }
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        if (velocidad < 0) {
            this.velocidad = 0;
        } else {
            this.velocidad = velocidad;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public abstract void actualizar();
}