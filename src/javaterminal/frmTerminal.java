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
import java.util.ArrayList;
import static javaterminal.JavaTerminal.frmT;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 *
 * @author hdm
 */
public class frmTerminal extends javax.swing.JFrame {
//JavaTerminal.ExecThread ExecTrd = new JavaTerminal.ExecThread();
JavaTerminal.SshThread sshTrd ;
JavaTerminal.InputThread InputTrd ;
JavaTerminal.ErrorThread ErrorTrd ;
String mode = "";
String filename = "";
int lastPos = 0;
int execPos = 0;
private ArrayList<Integer> escS;
private String escMode = "";
private int Pn = 0;
private int Pn2 = 0;
PrintWriter bwC;
private boolean uwagaki = true;

    /**
     * Creates new form frmTerminal
     */
    public frmTerminal() {
        initComponents();
        
        //IME止めておく
        textMain.enableInputMethods(false);
        //タブの動きを止める
        InputMap imputMap=textMain.getInputMap(textMain.WHEN_IN_FOCUSED_WINDOW);
        imputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0), "none");
        
/*
        ArrayList<KeyStroke> keyS = new ArrayList<KeyStroke>();
        for (int i = 0; i < 255; i++) {
            if ((i < KeyEvent.VK_LEFT) || (KeyEvent.VK_DOWN < i)) {
                keyS.add(KeyStroke.getKeyStroke((i),0));
                imputMap.put(keyS.get(i), "none");
            }
        }
        */
        /*
        textMain.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e){}
                public void keyPressed(KeyEvent e)
                {
                        int mod = e.getModifiersEx();
                        if(((mod & InputEvent.CTRL_DOWN_MASK)!=0)&&(e.getKeyCode()==KeyEvent.VK_ENTER))//ctrl + enter が押下された時、改行挿入
                        {
                                JTextArea target=(JTextArea)e.getSource();
                                int caretPosition=target.getCaretPosition();
                                target.insert("\n",caretPosition);
                        }
                        else if((e.getKeyCode()==KeyEvent.VK_ENTER)){}//enterが押下された時
                        else{}//その他のキー
                }
                public void keyReleased(KeyEvent e){}
        });*/

        //CSVの書き込み
        try {
            File csv = new File("./console.log"); // CSVデータファイル
            //古いファイル
            if (csv.exists()) {
                csv.delete();
            }
            // 常に新規作成
            
            bwC = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv),"UTF-8")));
            
            //bwC.print(textMain.getText());
            
            //bwC.close();
            
            //JavaTerminal.talk("保存しました。");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
	
        sshTrd = new JavaTerminal.SshThread();
        InputTrd = new JavaTerminal.InputThread();
        ErrorTrd = new JavaTerminal.ErrorThread();
        
        sshTrd.start();          //SSHモードで動作
    }
    
    public void consoleLogger(char c) {
        String hex = Integer.toHexString( (int)c );
        bwC.println("log:" + hex + ":" + (int)c + ":" + String.valueOf(c));
    }
    public void editableReq(boolean flg) {
        textMain.setEditable(flg);
    }
    public void repaintReq() {
        textMain.repaint();
    }
    public void append(String str) {
        str.replaceAll("[\\00-\\x08\\x0a-\\x1f\\x7f]", "");
        
        textMain.append(str);
        textMain.setCaretPosition(textMain.getText().length());
//        textMain.setCaretPosition(execPos);
        lastPos = textMain.getText().length();
    }
    private void migiSakujyo() {
        //カーソル右を削除します。
        StringBuilder sb = new StringBuilder();
        sb.append(textMain.getText());
        int len = textMain.getText().length();
        int pos = textMain.getCaretPosition();
        for (int i = pos; i < len; i++) {
            if (sb.toString().charAt(pos) != '\n') {
                sb.deleteCharAt(pos);
            }
        }
    }
    private void insertLine(int num) {
        System.err.println("改行の挿入:" + num + "行");
        StringBuilder sb = new StringBuilder(textMain.getText());
        int pos = textMain.getCaretPosition();
        for (int i = 0; i < num; i++) {
            sb.insert(pos, "\r\n");
        }
        textMain.setText(sb.toString());
    }
    private void setPos(int y) {
        int row = 0;
        String str = textMain.getText();
        //行番号を無理やり数える
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (row == y) {
                break;
            }
            String ch = str.substring(i, i+1);
            if (ch.equals("\n")) {
                row = row + 1;
            }
        }
        textMain.setCaretPosition(i);
        if (row < y) {
            for (int j = row; j < y; j++) {
                textMain.append("\n");
            }
            textMain.setCaretPosition(textMain.getText().length());
        }
    }
    private int getPos() {
        int ret = 0;
        
        int pos = textMain.getCaretPosition();
        String str = textMain.getText();
        //行番号を無理やり数える
        for (int i = 0; i < pos; i++) {
            String ch = str.substring(i, i+1);
            if (ch.equals("\n")) {
                ret = ret + 1;
            }
        }
        
        return ret;
    }
    private void upCar(int num) {
        int y = getPos();
        y = y - num;
        if (y < 0) {
            y = 0;
        }
        setPos(num);
    }
    private void downCar(int num) {
        int y = getPos();
        y = y + num;
        setPos(y);
    }
    private void migiDel() {
        int pos = textMain.getCaretPosition();
        StringBuilder sb = new StringBuilder(textMain.getText());
        int len = sb.length();
        for (int i = pos; i < len; i++) {
            try {
                if (sb.toString().substring(i, i + 1).equals("\n")) {
                    break;
                }
            } catch (Exception e) {
                break;
            }
            sb.deleteCharAt(pos);
        }
    }
    private void hidariDel() {
        int pos = textMain.getCaretPosition();
        StringBuilder sb = new StringBuilder(textMain.getText());
        int len = sb.length();
        for (int i = pos; i > 0; i--) {
            try {
                if (sb.toString().substring(i, i + 1).equals("\n")) {
                    break;
                }
            } catch (Exception e) {
                break;
            }
            sb.deleteCharAt(i);
        }
    }
    private void lightCar(int num) {
        int pos = textMain.getCaretPosition();
        for (int i = 0; i < num; i++) {
            try {
                if (textMain.getText().toString().substring(pos + i, pos + i + 1).equals("\n")) {
                    break;
                }
                textMain.setCaretPosition(pos + i + 1);
            } catch (Exception e) {
                break;
            }
        }
    }
    public void appendC(char c) {
        consoleLogger(c);
        String hex = Integer.toHexString( (int)c );
        System.err.println("echo:" + hex);
//        if (c == '位') {
//            System.err.println("debug:BreakPoint");
//        }
        //エスケープシーケンス対応
        if (c == 0x1b) {
            Pn = 0;
            Pn2 = 0;
            escMode = "";
            //エスケープ開始
            if (escS != null) {
                //試しに出力
                for (int i = 0; i < escS.size(); i++) {
                    System.err.print(":" + String.valueOf((char)(int)escS.get(i)));
                }
                System.err.println();
            }
            escS = new ArrayList<Integer>();
            escS.add((int)c);
            return;
        } else {
            if (escS != null) {
                escS.add((int)c);
                //^[
                if (c == '[') {
                    //CSI 開始
                    escMode = "CSI";
                    Pn = 0;
                    Pn2 = 0;
                }
                //CSI A 上
                if ((escMode.equals("CSI")) && (c == 'A')) {
                    upCar(Pn);
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI B 下
                if ((escMode.equals("CSI")) && (c == 'B')) {
                    downCar(Pn);
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI C 右
                if ((escMode.equals("CSI")) && (c == 'C')) {
                    lightCar(Pn);
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI m 文字属性コマンド 無視
                if ((escMode.equals("CSI")) && (c == 'm')) {
                    if (Pn == 0) {
                        //Clear
                        //textMain.setText("");
                    }
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI J コマンド
                if ((escMode.equals("CSI")) && (c == 'J')) {
                    if (Pn == 0) {
                        //以降クリア
                        textMain.setText(textMain.getText().substring(0, textMain.getCaretPosition()));
                    }
                    if (Pn == 0) {
                        //以前クリア
                        textMain.setText(textMain.getText().substring(textMain.getCaretPosition()));
                    }
                    if (Pn == 2) {
                        //Clear
                        textMain.setText("");
                    }
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI H カーソル位置変更
                if ((escMode.equals("CSI")) && (c == 'H')) {
                    //textMain.setRows(Pn);
                    //textMain.setColumns(Pn2);
                    setPos(Pn2);
                    int pos = textMain.getCaretPosition();
                    try {
                        textMain.setCaretPosition(pos + Pn);
                    } catch (Exception e) {
                        //textMain.setCaretPosition(textMain.getText().length());
                    }
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI K 右側削除
                if ((escMode.equals("CSI")) && (c == 'K')) {
                    if ((Pn == 0) || (Pn == 2)) {
                        //右側削除
                        migiDel();
                    }
                    if ((Pn == 1) || (Pn == 2)) {
                        //左側削除
                        hidariDel();
                    }
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI r スクロール範囲指定
                if ((escMode.equals("CSI")) && (c == 'r')) {
                    textMain.setText("");
                    insertLine(Pn);
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI L カーソル位置に行挿入
                if ((escMode.equals("CSI")) && (c == 'L')) {
                    insertLine(Pn);
                    //ESC Mode 終了
                    escMode = "";
                    escS = null;
                    return;
                }
                //CSI 数字
                if ((escMode.equals("CSI")) && (('0' <= c) && (c <= '9'))) {
                    Pn = Pn * 10 + Integer.parseInt(String.valueOf(c));
                    return;
                }
                //CSI ; 数字シフト
                if ((escMode.equals("CSI")) && (c <= ';')) {
                    Pn2 = Pn;
                    Pn = 0;
                    return;
                }
                
                return;
            }
        }
        if ((0 <= c) && (c <= 0x08)) {
            System.err.println("ctrlCd:" + hex);
            return;
        }
        if ((0x0a <= c) && (c <= 0x0c)) {
            //return; 改行、垂直タブ、改ページ
        }
        if ((0x0e <= c) && (c <= 0x1f)) {
            System.err.println("ctrlCd:" + hex);
            return;
        }
        if ((c == 0x7f)) {
            System.err.println("ctrlCd:" + hex);
            return;
        }
        //泥縄対応
        if ((c == 65535)) {
            System.err.println("ctrlCd:" + hex);
            return;
        }
        if ((c == 65533)) {
            System.err.println("ctrlCd:" + hex);
            return;
        }
        
        if (uwagaki) {
            //カーソル位置に挿入する必要あり
            int pos = textMain.getCaretPosition();
            StringBuilder sb = new StringBuilder(textMain.getText());
            int len = textMain.getText().length();
            if (pos >= len) {
                sb.append(String.valueOf(c));
                lastPos = sb.length();
                textMain.setText(sb.toString());
                textMain.setCaretPosition(textMain.getText().length());
            } else {
                //改行コード以外なら削除しておく
                if (sb.toString().charAt(pos) != '\n') {
                    sb.deleteCharAt(pos);
                }
                sb.insert(pos, String.valueOf(c));
                textMain.setText(sb.toString());
                try {
                    lastPos = pos + 1;
                    textMain.setCaretPosition(lastPos);
                }catch (Exception e) {
                    System.err.println("lastPos" + lastPos + ": len" + len);
                    textMain.setCaretPosition(textMain.getText().length());
                }
            }
        } else {
            //カーソル位置に挿入する必要あり
            int pos = textMain.getCaretPosition();
            StringBuilder sb = new StringBuilder(textMain.getText());
            int len = textMain.getText().length();
            if (pos >= len) {
                sb.append(String.valueOf(c));
                lastPos = sb.length();
                textMain.setText(sb.toString());
                textMain.setCaretPosition(lastPos);
            } else {
                sb.insert(pos, String.valueOf(c));
                textMain.setText(sb.toString());
                try {
                    lastPos = pos + 1;
                    textMain.setCaretPosition(lastPos);
                }catch (Exception e) {
                    System.err.println("lastPos" + lastPos + ": len" + len);
                    textMain.setCaretPosition(textMain.getText().length());
                }
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll = new javax.swing.JScrollPane();
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
        jMenu4 = new javax.swing.JMenu();
        Web = new javax.swing.JMenuItem();
        viEndMenu = new javax.swing.JMenuItem();

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

        textMain.setEditable(false);
        textMain.setColumns(20);
        textMain.setFont(new java.awt.Font("VL ゴシック", 0, 12)); // NOI18N
        textMain.setLineWrap(true);
        textMain.setRows(5);
        textMain.setTabSize(4);
        textMain.setDoubleBuffered(true);
        textMain.setFocusCycleRoot(true);
        textMain.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textMainCaretUpdate(evt);
            }
        });
        textMain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textMainKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textMainKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textMainKeyTyped(evt);
            }
        });
        scroll.setViewportView(textMain);

        jMenu1.setText("File");
        jMenu1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenu1FocusGained(evt);
            }
        });

        jMenuItem4.setText("保存");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenuItem4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem4FocusGained(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("中止");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenuItem5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem5FocusGained(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem1.setText("終了");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenuItem1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem1FocusGained(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenu2FocusGained(evt);
            }
        });

        jMenuItem2.setText("辞書編集");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenuItem2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem2FocusGained(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("辞書再読込");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenuItem3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem3FocusGained(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");
        jMenu3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenu3FocusGained(evt);
            }
        });

        jMenuItem6.setText("ヘルプ");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenuItem6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenuItem6FocusGained(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("拡張機能");
        jMenu4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMenu4FocusGained(evt);
            }
        });

        Web.setText("Web");
        Web.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebActionPerformed(evt);
            }
        });
        Web.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                WebFocusGained(evt);
            }
        });
        jMenu4.add(Web);

        viEndMenu.setText("vi終了");
        viEndMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viEndMenuActionPerformed(evt);
            }
        });
        jMenu4.add(viEndMenu);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        //JavaTerminal.talk("ターミナルがフォーカスを得ました。");
    }//GEN-LAST:event_formWindowGainedFocus

    private void talkKeyPressed(java.awt.event.KeyEvent evt) {
        //押したキーを喋らせる
        int code = evt.getKeyCode();
        String hex = Integer.toHexString( code );
        System.out.println( "hex:" + hex );
        String key = String.valueOf((char)code);
        String keyChar = String.valueOf(evt.getKeyChar());
        System.out.println( "key:" + key );
        if (!keyChar.trim().equals("")) {
            JavaTerminal.talkNoWait(keyChar);
        } else {
            JavaTerminal.talkNoWait(key);
        }
        if (code == evt.VK_UP) {
            JavaTerminal.talkNoWait("うえ");
        }
        if (code == evt.VK_DOWN) {
            JavaTerminal.talkNoWait("した");
        }
        if (code == evt.VK_LEFT) {
            JavaTerminal.talkNoWait("ひだり");
        }
        if (code == evt.VK_RIGHT) {
            JavaTerminal.talkNoWait("みぎ");
        }
        if (code == evt.VK_CONTROL) {
            JavaTerminal.talkNoWait("コントロール");
        }
        if (code == evt.VK_SHIFT) {
            JavaTerminal.talkNoWait("シフト");
        }
        if (code == evt.VK_ALT) {
            JavaTerminal.talkNoWait("アルト");
        }
        if (code == evt.VK_KANJI) {
            JavaTerminal.talkNoWait("全角半角");
        }
        if (code == evt.VK_TAB) {
            JavaTerminal.talkNoWait("タブ");
        }
        if (code == evt.VK_F1) {
            JavaTerminal.talkNoWait("エフ1");
        }
        if (code == evt.VK_F2) {
            JavaTerminal.talkNoWait("エフ2");
        }
        if (code == evt.VK_F3) {
            JavaTerminal.talkNoWait("エフ3");
        }
        if (code == evt.VK_F4) {
            JavaTerminal.talkNoWait("エフ4");
        }
        if (code == evt.VK_F5) {
            JavaTerminal.talkNoWait("エフ5");
        }
        if (code == evt.VK_F6) {
            JavaTerminal.talkNoWait("エフ6");
        }
        if (code == evt.VK_F7) {
            JavaTerminal.talkNoWait("エフ7");
        }
        if (code == evt.VK_F8) {
            JavaTerminal.talkNoWait("エフ8");
        }
        if (code == evt.VK_F9) {
            JavaTerminal.talkNoWait("エフ9");
        }
        if (code == evt.VK_F10) {
            JavaTerminal.talkNoWait("エフ10");
        }
        if (code == evt.VK_F11) {
            JavaTerminal.talkNoWait("エフ11");
        }
        if (code == evt.VK_F12) {
            JavaTerminal.talkNoWait("エフ12");
        }
    }
    private void textMainMyKeyEvent(java.awt.event.KeyEvent evt) {

        int keyCode = evt.getKeyCode();
        //if (!textMain.isEditable()) {
        //    return;
        //}
        //System.out.println("Code" + evt.getKeyCode());
        //System.out.println("Mod" + evt.getModifiers());
        
        //チュートリアル専用メッセージ
        if (frmT.sshTrd.tutorial) {
            if (keyCode == evt.VK_ESCAPE) {
                JavaTerminal.talkNoWait("エスケープ。シフトキーを押しながらこれを押して、カーソルのある行の内容を読み上げます。");
            }
            if (keyCode == evt.VK_SPACE) {
                JavaTerminal.talkNoWait("スペース");
            }
        } else {
            if (((evt.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) != 0) && (evt.getKeyCode() == evt.VK_ESCAPE)) {
                String line = getLine();
                JavaTerminal.talkNoWait(line);
                //return;
            } else {
                if (evt.getKeyCode() == evt.VK_ESCAPE) {
                    JavaTerminal.talkNoWait("エスケープ");
                }
            }
            
        }
        
        //選択範囲あれば(矢印キーを押した時のみ)
        if ((keyCode == evt.VK_LEFT) || (keyCode == evt.VK_RIGHT)) {
            String sel = textMain.getSelectedText();
            if (sel != null) {
                if (!sel.equals("")) {
                    JavaTerminal.talkNoWait(sel);
                    //return;
                }
            }
        }
        
        //チュートリアルモード抜けるかチェック
        if (frmT.sshTrd.tutorial) {
            if (keyCode == evt.VK_ENTER) {
                String cmd = getLine();
                if (cmd.toLowerCase().trim().endsWith("login")) {
                    System.out.println("login...");
                    frmT.sshTrd.tutorial = false;
                    return;
                }
            }
        } else {
            if (sshTrd.running) {
                //SSH向け入力処理
                char cd = (char) evt.getKeyCode();
                char kc = evt.getKeyChar();
                char setChar = 0;
                if (!String.valueOf(kc).trim().equals("")) {
                    setChar = kc;
                } else {
                    setChar = cd;
                }
                //コントロールキーと同時押しの場合 キャラクターコードを調整
                if (((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    setChar = (char)((int)setChar - 0x40);
                }
                InputTrd.setKey(setChar);
                
                evt.consume();  //キー入力をなかったことにする。
                return;         //有無をいわさず抜ける
            }
        }
        
        //チュートリアル中に、以降のコードを実行しないように。
        if (frmT.sshTrd.tutorial) {
            return;
        }
        
        //ログアウト後は、ここを通る
        if (evt.getKeyCode() == evt.VK_ENTER) {
            //編集中は何もしない
            if (mode.equals("edit")) {
                return;
            }
            String cmd = getLine();
            System.err.println(cmd);
            //コマンドの実行処理
            if (cmd.contains("reboot")) {
                JavaTerminal.talk("リブート処理は、まだ実装されていません。");
            }
            if (cmd.contains("shutdown")) {
                JavaTerminal.talk("シャットダウン処理は、まだ実装されていません。");
            }
            if (cmd.contains("login")) {
                JavaTerminal.talk("再ログイン処理は、まだ実装されていません。");
            }
            return;
        }
        if (((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (evt.getKeyCode() == evt.VK_X)) {
            if (!mode.equals("")) {
                textMain.setText(mode);
                append("\n");
                append("強制終了\n");
                JavaTerminal.talk("強制終了");
                mode = "";
            }else if (sshTrd.running) {
                append("強制終了\n");
                JavaTerminal.talk("強制終了");
                mode = "";
                sshTrd.running = false;
                sshTrd.stop();     
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
    }
    private void textMainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textMainKeyPressed
        textMainMyKeyEvent(evt);
        //押したキーを喋らせる
        talkKeyPressed(evt);
        
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
            JavaTerminal.talk("そのようなファイルはありません。中止はコントロールエックスです。");
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
            
            JavaTerminal.talk("保存しました。");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        JavaTerminal.writeProp();
        bwC.close();
    }//GEN-LAST:event_formWindowClosing

    private void textMainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textMainKeyReleased

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
        java.awt.event.KeyEvent evtT = new java.awt.event.KeyEvent(this, 0, 0, KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_ESCAPE);
        textMainKeyPressed(evtT);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void WebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebActionPerformed
        // Web取得
        NetClass net = new NetClass();
        //net.NetClass("http://www.google.co.jp", frmHtml.getTextHtml());
        net.NetClass("http://tanaka-cs.co.jp", textMain);
        //frmHtml.setVisible(true);
    }//GEN-LAST:event_WebActionPerformed

    private void jMenu1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenu1FocusGained
        JavaTerminal.talk("ファイルメニュー");
    }//GEN-LAST:event_jMenu1FocusGained

    private void jMenuItem4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem4FocusGained
        JavaTerminal.talk("保存");
    }//GEN-LAST:event_jMenuItem4FocusGained

    private void jMenuItem5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem5FocusGained
        JavaTerminal.talk("中止");
    }//GEN-LAST:event_jMenuItem5FocusGained

    private void jMenuItem1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem1FocusGained
        JavaTerminal.talk("終了");
    }//GEN-LAST:event_jMenuItem1FocusGained

    private void jMenu2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenu2FocusGained
        JavaTerminal.talk("エディットメニュー");
    }//GEN-LAST:event_jMenu2FocusGained

    private void jMenuItem2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem2FocusGained
        JavaTerminal.talk("辞書編集");
    }//GEN-LAST:event_jMenuItem2FocusGained

    private void jMenuItem3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem3FocusGained
        JavaTerminal.talk("辞書再読込");
    }//GEN-LAST:event_jMenuItem3FocusGained

    private void jMenu3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenu3FocusGained
        JavaTerminal.talk("ヘルプメニュー");
    }//GEN-LAST:event_jMenu3FocusGained

    private void jMenuItem6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenuItem6FocusGained
        JavaTerminal.talk("ヘルプ");
    }//GEN-LAST:event_jMenuItem6FocusGained

    private void jMenu4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMenu4FocusGained
        JavaTerminal.talk("拡張機能メニュー");
    }//GEN-LAST:event_jMenu4FocusGained

    private void WebFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_WebFocusGained
        JavaTerminal.talk("ウェブ");
    }//GEN-LAST:event_WebFocusGained

    private void viEndMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viEndMenuActionPerformed
        String str = "viをとにかく終了したい場合は、コントロールエーを押して全選択し、\nデリートしてからだとハマらなくて済みます。\n";
        textMain.setText(str);
        InputTrd.setInput((char)0x1b + ":q!\n");
    }//GEN-LAST:event_viEndMenuActionPerformed

    private void textMainCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_textMainCaretUpdate
        
    }//GEN-LAST:event_textMainCaretUpdate

    private void textMainKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textMainKeyTyped
        //エコーと重複するので１文字消す
        if ((frmT.sshTrd.running) && (!frmT.sshTrd.tutorial)) {
            String str = String.valueOf(evt.getKeyChar());
            if ((!str.trim().equals("")) || (str.equals(" "))) {
                //１文字消す
                StringBuilder sb = new StringBuilder(textMain.getText());
                sb.deleteCharAt(textMain.getCaretPosition() - 1);
                textMain.setText(sb.toString());
            }
        }
    }//GEN-LAST:event_textMainKeyTyped
    private String getLine() {
        int pos = textMain.getCaretPosition();
        String text = textMain.getText();
        //行の開始位置を探す
        int sta = 0;
        //現在のキャレット位置が、ラストポジションより後なら、ラスト位置から行末まで
        if (lastPos < pos) {
            sta = lastPos;
        } else {
            for (sta = pos - 1; sta > 0; sta--) {
                char ch = text.charAt(sta);
                if (ch == '\r') {
                    break;
                }
                if (ch == '\n') {
                    break;
                }
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
        String cmd = "";
        try {
            cmd = textMain.getSelectedText().trim();
            textMain.setSelectionStart(pos);
            textMain.setSelectionEnd(pos);
        }catch (Exception e) {
            //何もしない
        }
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
    private javax.swing.JMenuItem Web;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTextArea textMain;
    private javax.swing.JMenuItem viEndMenu;
    // End of variables declaration//GEN-END:variables
}
