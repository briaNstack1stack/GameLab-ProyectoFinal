package modelo;


public class Escudo extends Entidad {
    private int duracion; 

    public Escudo(double x, double y) {
        super(x, y, 30, 0, true);
        this.duracion = 180; 
    }

    @Override
    public void actualizar() {

        if (this.duracion > 0) {
            this.duracion--;
        }

        if (this.duracion <= 0) {
            this.activo = false;
        }
    }

    public void desgastar(int cantidad) {
        if (this.activo) {
            this.duracion -= cantidad;

            if (this.duracion <= 0) {
                this.duracion = 0;
                this.activo = false; 
            }
        }
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int nuevaDuracion) {
        if (nuevaDuracion < 0) {
            this.duracion = 0;
            this.activo = false;
        } else {
            this.duracion = nuevaDuracion;
        }
    }
}
