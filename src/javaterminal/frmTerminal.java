/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaterminal;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.SwingUtilities;

/**
 *
 * @author hdm
 */
public class frmTerminal extends javax.swing.JFrame {
//JavaTerminal.ExecThread ExecTrd = new JavaTerminal.ExecThread();
JavaTerminal.ttyThread ttyTrd ;
JavaTerminal.InputThread InputTrd ;
JavaTerminal.ErrorThread ErrorTrd ;
String mode = "";
String filename = "";

    /**
     * Creates new form frmTerminal
     */
    public frmTerminal() {
        initComponents();
        ttyTrd = new JavaTerminal.ttyThread();
        InputTrd = new JavaTerminal.InputThread();
        ErrorTrd = new JavaTerminal.ErrorThread();
        //SwingUtilities.();
        
        //ttyTrd.setCmd(cmd);
        ttyTrd.start();          //別スレッドで動作させる場合
        //InputTrd.start();
        //ErrorTrd.start();
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
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JavaTerminal");
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
        textMain.setFont(new java.awt.Font("VL ゴシック", 0, 12)); // NOI18N
        textMain.setLineWrap(true);
        textMain.setRows(5);
        textMain.setTabSize(0);
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

        jMenuItem4.setText("保存");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("中止");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

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

        jMenu3.setText("Help");

        jMenuItem6.setText("ヘルプ");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

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

        
        if (evt.getKeyCode() == evt.VK_TAB) {
            textMain.insert(System.getProperty("user.dir") + "/", textMain.getCaretPosition());
            return;
        }
        
        if (evt.getKeyCode() == evt.VK_ESCAPE) {
            String cmd = getLine();
            JavaTerminal.talk(cmd);
            return;
        }
        
        if (evt.getKeyCode() == evt.VK_ENTER) {
            //編集中は何もしない
            if (mode.equals("edit")) {
                return;
            }
            String cmd = getLine();
            textMain.append("\n");
            
            
            //コマンドの実行処理
            if (!ttyTrd.running) {
                
                //特殊なコマンド
                String[] cmdA = cmd.split(" ");
                //edit
                if (cmdA[0].equals("edit")) {
                    //ファイル編集モード
                    mode = "edit";
                    JavaTerminal.talk("編集モードに移行します。保存はコントロールエス、中止はコントロールエックスです。");
                    filename = "tmp.txt";
                    try {
                        filename = cmdA[1];
                    } catch (Exception e) {
                        //何もしない
                    }
                    editFile(filename);
                    return;
                }
                
                if (cmdA[0].equals("clear")) {
                    //画面クリア
                    textMain.setText("");
                    evt.consume();  //キー入力をなかったことにする
                    return;
                }
                
                //System.out.println("Exec:" + cmd);
                ttyTrd = new JavaTerminal.ttyThread();
                InputTrd = new JavaTerminal.InputThread();
                ttyTrd.setCmd(cmd);
                ttyTrd.start();          //別スレッドで動作させる場合
                //InputTrd.start();
                //ErrorTrd.start();
                evt.consume();  //キー入力をなかったことにする
            
            } else {
                //System.out.println("Input:" + cmd);
                InputTrd.setInput(cmd);
                evt.consume();  //キー入力をなかったことにする
            }
            
            return;
        }

        if (evt.getKeyCode() == evt.VK_ENTER) {
            //編集中は何もしない
            if (mode.equals("edit")) {
                return;
            }
            String cmd = getLine();
            textMain.append("\n");
            
            
            //コマンドの実行処理
            //System.out.println("Input:" + cmd);
            InputTrd.setInput(cmd);
            //evt.consume();  //キー入力をなかったことにする
            return;
        }
        if (((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (evt.getKeyCode() == evt.VK_X)) {
            if (!mode.equals("")) {
                textMain.setText(mode);
                append("\n");
                append("強制終了\n");
                JavaTerminal.talk("強制終了");
                mode = "";
            }else if (ttyTrd.running) {
                append("強制終了\n");
                JavaTerminal.talk("強制終了");
                mode = "";
                ttyTrd.running = false;
                ttyTrd.stop();     
            }
        }
        if (((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (evt.getKeyCode() == evt.VK_S)) {
            if (mode.equals("edit")) {
                writeFile();
                append("編集を終了しました。\n");
                JavaTerminal.talk("編集を終了しました。");
                mode = "";
            }
        }
    }//GEN-LAST:event_textMainKeyPressed
    private void editFile(String filename) {
        textMain.setText("");
        if (filename.equals("")) {
            return;
        }
        String crlf = "\n";
        try {
                crlf = System.getProperty("line.separator");
        } catch(SecurityException e) {
        }
        //ファイルの読み込み
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            // 最終行まで読み込む
            String line = "";
            while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(crlf);
                }
            br.close();
        } catch (Exception e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }
        textMain.setText(sb.toString().trim() + crlf);
    }
    
    private void writeFile() {
        
        //CSVの書き込み
        try {
            File csv = new File(filename); // CSVデータファイル
            //古いファイルのバックアップ
            if (csv.exists()) {
                File fileB = new File(csv.getAbsolutePath() + "~");
                if (fileB.exists()) {
                    fileB.delete();
                }
                csv.renameTo(fileB);
            }
            // 常に新規作成
            PrintWriter bw;
            bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv),"UTF-8")));
            
            bw.print(textMain.getText());
            
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

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
        
        //入力の度にコードを読ませる場合
//        JavaTerminal.talk(String.valueOf((char)evt.getKeyCode()));
    }//GEN-LAST:event_textMainKeyReleased

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        //辞書編集
        textMain.append("edit Dict.csv");
        textMain.setCaretPosition(textMain.getText().length());
        java.awt.event.KeyEvent evtT = new java.awt.event.KeyEvent(this, 0, 0, 0, KeyEvent.VK_ENTER);
        textMainKeyPressed(evtT);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        //辞書再読込
        JavaTerminal.readDict();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        //保存
        java.awt.event.KeyEvent evtT = new java.awt.event.KeyEvent(this, 0, 0, KeyEvent.CTRL_MASK, KeyEvent.VK_S);
        textMainKeyPressed(evtT);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        //中止
        java.awt.event.KeyEvent evtT = new java.awt.event.KeyEvent(this, 0, 0, KeyEvent.CTRL_MASK, KeyEvent.VK_X);
        textMainKeyPressed(evtT);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        StringBuilder sb = new StringBuilder();
        sb.append("ヘルプです。現在カーソルは１行目にあります。エスケープキーで１行ずつ喋ります。\n");
        sb.append("コマンドを入力してエンターを押すと、そのコマンドが実行できます。\n");
        sb.append("コマンドの中止はコントロールエックスです。\n");
        sb.append("使い方の例を、以下に記述します。\n");
        sb.append("エルエスと入力してエンターを押すとファイルの一覧が出ます。\n");
        sb.append("上下のキーで移動しながら、エスケープを押してファイル名を確認します。\n");
        sb.append("行頭にエディットと英語で入れて、スペースを入力してエンターを押します。\n");
        sb.append("すると、そのファイルが編集できます。\n");
        sb.append("使い方の例は、以上です。\n");
        textMain.setText(sb.toString());
        textMain.setCaretPosition(0);
        java.awt.event.KeyEvent evtT = new java.awt.event.KeyEvent(this, 0, 0, 0, KeyEvent.VK_ESCAPE);
        textMainKeyPressed(evtT);
    }//GEN-LAST:event_jMenuItem6ActionPerformed
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
        String cmd = textMain.getSelectedText().trim();
        textMain.setSelectionStart(pos);
        textMain.setSelectionEnd(pos);
        //System.out.println(cmd);
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
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textMain;
    // End of variables declaration//GEN-END:variables
}
