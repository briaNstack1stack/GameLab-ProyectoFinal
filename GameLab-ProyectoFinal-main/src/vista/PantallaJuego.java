package vista;

import java.awt.BorderLayout;
import java.util.logging.Logger;

public class PantallaJuego extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(PantallaJuego.class.getName());

    private PanelJuego panelJuego;
    
    public PantallaJuego() {
        initComponents();
        
        // Configurar el layout y agregar el panel de juego correctamente al ContentPane
        this.getContentPane().setLayout(new BorderLayout());
        
        PanelJuego panel = new PanelJuego();
        this.getContentPane().add(panel, BorderLayout.CENTER);
        
        // Configuración de la ventana
        this.setSize(800, 600);
        this.setResizable(false);          
        this.setLocationRelativeTo(null);   

        // Iniciar el motor del juego
        controlador.MotorJuego motor = new controlador.MotorJuego(panel, 800, 600);
        Thread hiloJuego = new Thread(motor);
        hiloJuego.start();
    }
    
    public PanelJuego getPanelJuego() {
        return panelJuego;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PantallaJuego().setVisible(true));
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PantallaJuego().setVisible(true));
    }

    

    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
