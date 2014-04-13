/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaterminal;

import java.awt.RenderingHints;

/**
 *
 * @author hdm
 */
public class frmTerminal extends javax.swing.JFrame {

    /**
     * Creates new form frmTerminal
     */
    public frmTerminal() {
        initComponents();
    }

    public void append(String str) {
        textMain.append(str);
        textMain.setCaretPosition(textMain.getText().length());
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textMain = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        textMain.setColumns(20);
        textMain.setRows(5);
        textMain.setFocusCycleRoot(true);
        textMain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textMainKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textMainKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(textMain);

        jMenu1.setText("File");

        jMenuItem1.setText("終了");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem2.setText("辞書編集");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("辞書再読込");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        JavaTerminal.talk("ターミナルがフォーカスを得ました。");
    }//GEN-LAST:event_formWindowGainedFocus

    private void textMainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textMainKeyPressed
        if (evt.getKeyCode() == evt.VK_ESCAPE) {
            String cmd = getLine();
            JavaTerminal.talk(cmd);
            return;
        }
        if (evt.getKeyCode() == evt.VK_ENTER) {
            JavaTerminal.terminate = false;
            String cmd = getLine();
//            JavaTerminal.exec(cmd);
            JavaTerminal.ExecThread trd = new JavaTerminal.ExecThread();
            trd.setCmd(cmd);
            trd.start();
            return;
        }
        if (evt.getKeyCode() == evt.VK_END) {
            JavaTerminal.terminate = true;
        }
    }//GEN-LAST:event_textMainKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        JavaTerminal.writeProp();
    }//GEN-LAST:event_formWindowClosing

    private void textMainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textMainKeyReleased
        
        if (evt.getKeyCode() == evt.VK_ESCAPE) {
            return;
        }
        if (evt.getKeyCode() == evt.VK_ENTER) {
            return;
        }
//        if (evt.getKeyCode() == evt.VK_END) {
//            JavaTerminal.terminate = true;
//        }
//        if ((evt.getKeyCode() <= evt.VK_A) && (evt.getKeyCode() <= evt.VK_Z)) {
            JavaTerminal.talk(String.valueOf((char)evt.getKeyCode()));
//        }
    }//GEN-LAST:event_textMainKeyReleased

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JavaTerminal.ExecThread trd = new JavaTerminal.ExecThread();
        trd.setCmd("gedit Dict.csv");
        trd.start();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JavaTerminal.readDict();
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    private String getLine() {
        int pos = textMain.getCaretPosition();
        String text = textMain.getText();
        //行の開始位置を探す
        int sta = 0;
        for (sta = pos - 1; sta > 0; sta--) {
            char ch = text.charAt(sta);
            if (ch == '\r') {
                break;
            }
            if (ch == '\n') {
                break;
            }
        }
        //行の終了位置を探す
        int ed = textMain.getText().length();
        for (ed = pos; ed < textMain.getText().length(); ed++) {
            char ch = text.charAt(ed);
            if (ch == '\r') {
                break;
            }
            if (ch == '\n') {
                break;
            }
        }
        
        textMain.setSelectionStart(sta);
        textMain.setSelectionEnd(ed);
        String cmd = textMain.getSelectedText();
        textMain.setSelectionStart(pos);
        textMain.setSelectionEnd(pos);
        System.out.println(cmd);
        return cmd;
    }
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
            java.util.logging.Logger.getLogger(frmTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmTerminal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textMain;
    // End of variables declaration//GEN-END:variables
}
