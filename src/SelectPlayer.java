
public class SelectPlayer extends javax.swing.JDialog {

    /**
     * Creates new form SelectPlayer
     */
    
    private boolean playingAgainstComputer;
    
    public SelectPlayer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.playingAgainstComputer = false;
    }
    
    // Shows window to user
    public boolean showDialog() {
        this.setVisible(true);
        return this.playingAgainstComputer;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        twoPlayerButton = new javax.swing.JButton();
        onePlayerButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mode Select");
        setMaximumSize(new java.awt.Dimension(853, 478));
        setMinimumSize(new java.awt.Dimension(853, 478));
        getContentPane().setLayout(null);

        twoPlayerButton.setFont(new java.awt.Font("Microsoft JhengHei", 0, 18)); // NOI18N
        twoPlayerButton.setText("Two Player");
        twoPlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoPlayerButtonActionPerformed(evt);
            }
        });
        getContentPane().add(twoPlayerButton);
        twoPlayerButton.setBounds(460, 260, 140, 40);

        onePlayerButton.setFont(new java.awt.Font("Microsoft JhengHei", 0, 18)); // NOI18N
        onePlayerButton.setText("One Player");
        onePlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onePlayerButtonActionPerformed(evt);
            }
        });
        getContentPane().add(onePlayerButton);
        onePlayerButton.setBounds(250, 260, 140, 40);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Microsoft JhengHei", 1, 48)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Select Mode:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 40, 850, 150);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chess-4-s.jpg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 853, 480);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void twoPlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoPlayerButtonActionPerformed
        // TODO add your handling code here:
        this.playingAgainstComputer = false;
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_twoPlayerButtonActionPerformed

    private void onePlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onePlayerButtonActionPerformed
        // TODO add your handling code here:
        this.playingAgainstComputer = true;
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_onePlayerButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelectPlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectPlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectPlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectPlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelectPlayer dialog = new SelectPlayer(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton onePlayerButton;
    private javax.swing.JButton twoPlayerButton;
    // End of variables declaration//GEN-END:variables
}
