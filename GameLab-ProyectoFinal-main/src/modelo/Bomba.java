package modelo;


public class Bomba extends Entidad {
    private int radioExplosion;
    private int danio;

    private int tiempoExplosion; 

    public Bomba(double x, double y) {
        super(x, y, 10, 0, true);
        
        this.radioExplosion = 300; 
        this.danio = 150;          
        this.tiempoExplosion = 30; 
    }

    @Override
    public void actualizar() {
        if (this.tiempoExplosion > 0) {
            this.tiempoExplosion--;
        }

        if (this.tiempoExplosion <= 0) {
            this.activo = false;
        }
    }

    public void detonar() {
        if (this.activo) {
            this.setRadio(this.radioExplosion);
        }
    }

    public int getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(int radioExplosion) {
        if (radioExplosion <= 0) {
            this.radioExplosion = 100; 
        } else {
            this.radioExplosion = radioExplosion;
        }
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
