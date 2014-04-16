/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaterminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import static java.lang.Thread.sleep;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hdm
 */
public class JavaTerminal {
public static frmTerminal frmT = new frmTerminal();
public static String talkExecIn ;
public static String talkExec ;
public static String talkSh ;
public static String user ;
//public static boolean terminate = false;
public static ArrayList<String[]> Dict = new ArrayList<String[]>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //プロパティファイルの読み込み
        Properties config = new Properties();
        try {
            //config.load(new FileInputStream("card.properties"));
            config.load(new InputStreamReader(new FileInputStream("javaterminal.properties"), "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        
        talkExecIn = config.getProperty("talkExec", "/usr/local/bin/open_jtalk");
        talkExec = config.getProperty("talkExec", "/usr/local/bin/open_jtalk");
        talkSh = config.getProperty("talkSh", "./talk.sh");
        user = config.getProperty("user", "xxxx");
        
        //辞書ファイルの読み込み
        readDict();
        
        frmT.setVisible(true);
        
    }
    public static void writeProp() {
        //プロパティファイルの書き込み
        Properties config = new Properties();
//        Rectangle rect = eijiroFrm.getBounds();
//        config.setProperty("x", String.valueOf(rect.x));
//        config.setProperty("y", String.valueOf(rect.y));
//        config.setProperty("width", String.valueOf(rect.width));
//        config.setProperty("height", String.valueOf(rect.height));
        
        config.setProperty("talkExec", talkExecIn);
        config.setProperty("talkSh", talkSh);
        config.setProperty("user", user);
        
        try {
            config.store(new OutputStreamWriter(new FileOutputStream("javaterminal.properties"),"UTF-8"), "by HDM");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void readDict() {
        Dict = new ArrayList<String[]>();
        ArrayList<String> wk = new ArrayList<String>();
        
        //ファイルの読み込み
        try {
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(new FileInputStream("Dict.csv"), "UTF-8"));
            // 最終行まで読み込む
            String line = "";
            while ((line = br.readLine()) != null) {
                    String[] str = line.toLowerCase().split(" ", 2);
                    //文字数チェック
                    int len = str[0].length();
                    DecimalFormat df4 = new DecimalFormat("0000");
                    wk.add(df4.format(len)+ " " + line.toLowerCase());
                }
            br.close();
            //文字数の多い順に並べ替え
            Object[] oa = wk.toArray(); // 配列に変換
            Arrays.sort(oa, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    String s1 = (String) t;
                    String s2 = (String) t1;
                    s1 = s1.toLowerCase();
                    s2 = s2.toLowerCase();
                    return s2.compareTo(s1);    //1と2を逆にしてみた
                }
            });
            //データ部
            for (int i = 0; i < oa.length; i++) {
                String[] str = ((String)oa[i]).split(" ", 3);
                String[] setStr = new String[2];
                setStr[0] = str[1].toLowerCase();
                setStr[1] = str[2];
                Dict.add(setStr);
            }
        } catch (Exception e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }
    }
    public static void talkNoWait(String str) {
        talk(str, false);
    }
    public static void talk(String str, boolean wait) {
        //System.out.println(str);
        if (talkExec == null) {
            return;
        }
        if (talkExec.equals("")) {
            return;
        }
        str = str.trim();
        if (str.trim().equals("")) {
            return;
        }
        
        //辞書の内容で置換
        str = str.toLowerCase();
        for (int i = 0; i < Dict.size(); i++) {
            if (Dict.get(i).length >= 2) {
//                System.out.println("0:" + Dict.get(i)[0]);
//                System.out.println("1:" + Dict.get(i)[1]);
                str = str.replaceAll(Dict.get(i)[0], Dict.get(i)[1]);
            }
        }
        //OpenJTalkインストール済みかチェック
        File file = new File(talkExec);
        if (!file.exists()){
            frmT.append("OpenJTalkがインストールされていません。\n音声出力がOFFに設定されます。\n");
            talkExec = "";
            return;
        }
        
        //タイムスタンプ
        long now = System.currentTimeMillis();
        
        Runtime r = Runtime.getRuntime();
        try {
            Process process = r.exec(new String[] { talkSh, Long.toString(now), str });
            //プロセス終了まで待ち
            if (wait) {
                InputStream is = process.getInputStream();
                try {
                    while (is.read() != -1) {
                        //待ち
                        sleep(10);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    is.close();
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void talk(String str) {
        talk(str, true);
    }
    
    static class SshThread extends Thread{
        boolean running = false;
        boolean tutorial = false;
        OutputStream osT;
        InputStream isT;
        InputStream esT;
        private String cmdT = "";
        private ProcessBuilder pb = null;
        private Process process = null;
        
        public void run() {
            if (cmdT.equals("")) {
                openSSH();
            } else {
                exec(cmdT);
            }
            cmdT = "";
        }
        public void setCmd(String cmd) {
            cmdT = cmd;
        }
        public void exec(String str) {
            if (cmdT.equals("")) {
                running = false;
                return;
            }
            str = str.trim();
            if (str.trim().equals("")) {
                return;
            }
            //コマンド文字列の分割
            String[] cmdarg = split(str);

            running = true;
            try {
                //pb.redirectErrorStream(false);
                if (cmdarg[0].equals("cd")) {
                    frmT.append("Javaでは、カレントディレクトリを移動できません。udコマンドをご利用下さい。\n");
                    frmT.append("タブキーでユーザーディレクトリを呼び出せます。\n");
                    running = false;
                    return;
                } else if (cmdarg[0].equals("ud")) {
                    File dir = new File("/home/" + user);
                    if (cmdarg.length > 1) {
                        dir = new File(cmdarg[1]);
                    }
                    
                    pb = new ProcessBuilder("/bin/bash", "-c", "cd " + dir.getAbsolutePath());
                    pb.directory(dir.getCanonicalFile());
                    frmT.append(dir.getCanonicalFile().getAbsolutePath());
                    frmT.append("\n");
                    System.setProperty("user.dir", dir.getCanonicalFile().getAbsolutePath());
                    process = pb.start();
                    //process.waitFor();
                }else {
                    pb = new ProcessBuilder(cmdarg);
                    process = pb.start();
                    //process.waitFor();
                }
                //StringBuilder sb = new StringBuilder();
                isT = process.getInputStream();
                osT = process.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(isT));
                try {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        //sb.append(line);
                        //System.out.println("出力:" + line);
                        frmT.append(line);
                        frmT.append("\n");
                        frmT.repaintReq();
                        //talk(line);
                    }
                } finally {
                    br.close();
                    isT.close();
                    //osT.close();
                    running = false;
                    frmT.append("終了しました。\n");
                }
            //  System.out.println("戻り値：" + process.exitValue());
            } catch (Exception ex) {
                ex.printStackTrace();
                talk("エラーです。");
                frmT.append(ex.toString());
                frmT.append("\n");
                talk(ex.toString());
                running = false;
            }
        }
        public void openSSH() {
            if (running) {
                return;
            }
            
            //チュートリアル
            JavaTerminal.talk("喋るターミナルソフトです。");
            JavaTerminal.talk("キーボードを押して、それぞれのキーを確認してみて下さい。");
            JavaTerminal.talkNoWait("その後、「L」「O」「G」「I」「N」「エンター」を順に押してログインしてください。");
            frmT.editableReq(true);
            frmT.sshTrd.tutorial = true;
            
            running = true;
            try {
                
                //チュートリアル終了待ち
                while (frmT.sshTrd.tutorial) {
                    sleep(100);
                }
                JavaTerminal.talkNoWait("パスワードを入力してエンターを押してください。");
                frmT.append("ログインを試みます。\n");
                pb = new ProcessBuilder("ssh", "-t", "-t", user + "@localhost");
                
                frmT.editableReq(true);      //KeyPress取れない・・・
                
                process = pb.start();
                esT = process.getErrorStream();
                isT = process.getInputStream();
                osT = process.getOutputStream();
                
                //frmT.InputTrd.start();    これは必要に応じて呼び出し
                frmT.ErrorTrd.start();
                
                BufferedReader br = new BufferedReader(new InputStreamReader(isT));
                try {
                    char line = 0;
                    while ((line = (char) br.read()) != 65535) {
                        //System.out.println((int)line);
                        frmT.append(line);
//                        frmT.append("\n");
                        frmT.repaintReq();
                        //talk(line);
                    }
                } finally {
                    br.close();
                    isT.close();
                    osT.close();
                    esT.close();
                    running = false;
                    frmT.append("終了しました。\n");
                }
            } catch (Exception ex) {
                talk("エラーです。");
                frmT.append(ex.toString());
                frmT.append("\n");
                talk(ex.toString());
                running = false;
                ex.printStackTrace();
            }
        }
    }
    static class InputThread extends Thread{
        private String cmdT = "";
        public void run() {
            setInput(cmdT);
            cmdT = "";
        }
        public void setInput(String input) {
            try {
                if (frmT.sshTrd.osT == null) {
                    return;
                }
                if (input == null) {
                    return;
                }
                frmT.sshTrd.osT.write(input.trim().getBytes());
                frmT.sshTrd.osT.write("\n".getBytes());
                frmT.sshTrd.osT.flush();
            } catch (IOException ex) {
                Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void setKey(char input) {
            try {
                if (frmT.sshTrd.osT == null) {
                    return;
                }
                if (input == 0x0a) {
                    System.err.println("改行！");
                }
                frmT.sshTrd.osT.write(input);
                frmT.sshTrd.osT.flush();
                System.err.println("setKey:" + (int)input);
            } catch (IOException ex) {
                Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    static class ErrorThread extends Thread{
        private String cmdT = "";
        public void run() {
            setOutput();
        }
        public void setOutput() {
            try {
                if (frmT.sshTrd.esT == null) {
                    return;
                }
                //frmT.ttyTrd.esT = process.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(frmT.sshTrd.esT));
                
                String line = null;
                while (true) {
                    try {
                        line = br.readLine();
                    } catch (Exception e) {
                        line = "";
                    }
                    if (!line.equals("")) {
                        frmT.append(line);
                        frmT.append("\n");
                        frmT.repaintReq();
                        talk(line);
                    }
                    if (cmdT.equals("stop")) {
                        break;
                    }
                }
                
                br.close();
                frmT.sshTrd.esT.close();
                System.out.println("ErrorThreadは終了しました。");
            } catch (IOException ex) {
                ex.printStackTrace();
                talk("エラーです。");
                frmT.append(ex.toString());
                frmT.append("\n");
                frmT.repaintReq();
                talk(ex.toString());
            }
        }
    }
    public static String[] split(String str) {
        
        ArrayList al = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean flgQ = false;
        boolean flgDQ = false;
        
        for (int i = 0; i < str.length(); i++) {
            String wk = str.substring(i, i + 1);
            if (wk.equals("'")) {
                flgQ = !flgQ;
            }
            if (wk.equals("\"")) {
                flgDQ = !flgDQ;
            }
            if (wk.equals(" ")) {
                if ((flgQ) || (flgDQ)) {
                    //文字列は続く
                    sb.append(wk);
                } else {
                    //分割
                    al.add(sb.toString());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(wk);
            }
        }
        al.add(sb.toString());
        
        String[] ret = (String[]) al.toArray(new String[0]);
        return ret;
    }
    /*
    class MyTty implements Tty{

        @Override
        public boolean init(Questioner q) {
            frmT.append("init.");
            return true;
        }

        @Override
        public void close() {
            frmT.append("close.");
        }

        @Override
        public void resize(Dimension termSize, Dimension pixelSize) {
            frmT.append("termSize." + termSize.width + "," + termSize.height);
            frmT.append("pixelSize." + pixelSize.width + "," + pixelSize.height);
            
        }

        @Override
        public String getName() {
            return "JavaTerminal";
        }

        @Override
        public int read(byte[] buf, int offset, int length) throws IOException {
            frmT.append("read.");
            while(frmT.ttyTrd.cmdT.equals("")) {
                try {
                    sleep(10);
                } catch (InterruptedException ex) {
                    throw new IOException(ex.toString());
                }
            }
            buf = frmT.ttyTrd.cmdT.getBytes();
            offset = 0;
            length = buf.length;
            return 0;
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            frmT.append(bytes.toString());
        }
        
    }
    class MyQuestioner implements Questioner {

        @Override
        public String questionVisible(String question, String defValue) {
            frmT.append(question);
            while(frmT.ttyTrd.cmdT.equals("")) {
                try {
                    sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return frmT.ttyTrd.cmdT;
        }

        @Override
        public String questionHidden(String string) {
            frmT.append(string);
            while(frmT.ttyTrd.cmdT.equals("")) {
                try {
                    sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JavaTerminal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return frmT.ttyTrd.cmdT;
        }

        @Override
        public void showMessage(String message) {
            frmT.append(message);
        }
        
    }
    */
}
