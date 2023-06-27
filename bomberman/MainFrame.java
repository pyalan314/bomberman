package bomberman;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import javax.swing.*;

public class MainFrame extends JFrame implements WindowListener,ActionListener, KeyListener{
    private BombPanel bpanel;
    private TitlePanel tpanel;
    private SavePanel spanel;
    private JMenuBar jmb;
    private JMenu jms[];
    private JMenuItem jmis[];
    private JFileChooser fileChooser;
    private int status;
    //private String fname;
    private boolean is2play;
    private int stage, pinfo[][], check;
    private boolean isNew;
    private boolean isOK;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainFrame frame=new MainFrame();
        frame.setResizable(false);
        frame.pack();
		frame.setVisible(true);
    }
    public MainFrame() {
        super.setTitle("BOMBERMAN");
        addWindowListener(this);
        addKeyListener(this);
        jmb=new JMenuBar();
        jms=new JMenu[2];
        jms[0]=new JMenu("Game");
        jms[1]=new JMenu("Help");
        jmb.add(jms[0]);
        jmb.add(jms[1]);
        jmis= new JMenuItem[5];
        jms[0].add(jmis[0]=new JMenuItem("New Game"));
        jms[0].add(jmis[3]=new JMenuItem("Load Moves"));
        jms[0].add(jmis[4]=new JMenuItem("Save Moves"));
        jms[0].add(jmis[1]=new JMenuItem("Quit"));
        jms[1].add(jmis[2]=new JMenuItem("About"));
        for (int i = 0; i < jmis.length; i++) jmis[i].addActionListener(this);
        this.setJMenuBar(jmb);
        //pinfo = new int[2][9];
        addTP();
    }
    /**
     * Handle the option selected in the title page
     */
    public void runOption() {
        int option = tpanel.getOption();
        if(option==1) {
            isNew = true;
            is2play = false;
            removeTP();
            addBP();
        }
        else if(option==2) {
            isNew = true;
            is2play = true;
            removeTP();
            addBP();
        }
        else if(option==3) {
            isNew = false;
            addFC();
            if(isOK) {
                removeTP();
                addBP();
            }
        }
        else if(option==4) {dispose();System.exit(0);}
    }
    /**
     * Handle the scene when passing a stage
     */
    public void runCont() {
        stage = bpanel.getStage();
        is2play = bpanel.getIs2Play();
        pinfo = bpanel.getInfo();
        nextStage();
        if(bpanel.getIsWin()) {removeBP();addSP();}
        else {removeBP();addTP();}
    }
    /**
     * Handle save function
     */
    public void runSave() {
        addSC();
    }
    /**
     * Add the title page
     */
    public void addTP() {
        status = 0;
        isNew = true;
        tpanel=new TitlePanel();
        tpanel.setPreferredSize(new Dimension(730, 416));
        this.addKeyListener(tpanel);
        this.add(tpanel);
        this.pack();
    }
    /**
     * Remove the title page
     */
    public void removeTP() {
        this.remove(tpanel);
        this.removeKeyListener(tpanel);
    }
    /**
     * Add the game panel
     */
    public void addBP() {
        System.out.println(isNew);
        status =1;
        if(isNew) bpanel=new BombPanel(is2play);
        else bpanel=new BombPanel(stage, is2play, pinfo);
        bpanel.init();
        bpanel.setPreferredSize(new Dimension(730, 416));
        this.addKeyListener(bpanel);
        this.add(bpanel);
        this.pack();
    }
    /**
     * Remove the game panel
     */
    public void removeBP() {
        bpanel.clear();
        this.remove(bpanel);
        this.removeKeyListener(bpanel);
    }
    /**
     * Show an file chooser for loading
     */
    public void addFC() {
        String fname="";
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("Save/"));
        int result = fileChooser.showOpenDialog(null);
        if(result==JFileChooser.APPROVE_OPTION) fname = fileChooser.getSelectedFile().getPath();
        if(fname!="") {
            try {
                File inFile =new File(fname);
                FileInputStream fis=new FileInputStream(inFile);
                InputStreamReader isr=new InputStreamReader(fis,Charset.forName("UTF-8"));
                char[] chars=new char[500];
                while (isr.read(chars)<=0);
                int r=0;
                stage = Integer.parseInt(""+chars[r++]+chars[r++]);
                is2play = (chars[r++]>'0');
                pinfo = new int[2][9];
                pinfo[0] = new int[] {Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++])};
                pinfo[1] = new int[] {Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++]), Integer.parseInt(""+chars[r++])};
                check = Integer.parseInt(""+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r++]+chars[r]);
                System.out.println(encrypt(stage, is2play, pinfo));
                if(check == encrypt(stage, is2play, pinfo)){System.out.println("The file is ok");isOK = true;}
                else {System.out.println("The file is not ok");isOK = false;}
            } catch (IOException ex) {System.out.println("The file is not ok"); isOK = false; }
        } else {isOK = false;System.out.println("No file is loaded");}
    }
    /**
     * Show a file chooser for saving
     */
    public void addSC(){
        String fname="";
        fileChooser =new JFileChooser();
        fileChooser.setCurrentDirectory(new File("Save/"));
        if(JFileChooser.APPROVE_OPTION==fileChooser.showOpenDialog(this)){
            FileWriter out=null;
            String temp="";
            temp += stage;
            temp += is2play?1:0;
            for(int i=0; i<2; i++) temp += pinfo[i][0]+""+convertStr(pinfo[i][1], 5)+""+pinfo[i][2]+""+pinfo[i][3]+""+pinfo[i][4]+""+pinfo[i][5]+""+pinfo[i][6]+""+pinfo[i][7]+""+pinfo[i][8];
            temp += convertStr(encrypt(stage, is2play, pinfo), 8);
            System.out.println(temp);
            try {
                fname = fileChooser.getSelectedFile().getPath();
                File file=new File(fname);
                out = new FileWriter(file);
                out.write(temp);
                out.close();
            } catch (IOException ex) {}
        }

    }
    /**
     * Show a page for choosing save or continue
     */
    public void addSP() {
        status = 2;
        spanel = new SavePanel();
        spanel.setPreferredSize(new Dimension(200,200));
        this.add(spanel);
        System.out.println("paint");
        this.pack();
    }
    /**
     * remove the saving page
     */
    public void removeSP() {
        this.remove(spanel);
    }
    /**
     * encrypt the data saved
     * @param a life
     * @param b is2play
     * @param info player info
     * @return a checking number
     */
    public int encrypt(int a, boolean b, int[][] info) {
        double sum = 0;
        sum += a + info[0][0]+info[1][0] + Math.sqrt(info[0][1]+info[1][1]);
        sum += b?1:0;
        for(int i=0; i<2; i++)for(int j=2; j<9; j++) sum += Math.pow(info[i][j], j+1);
        return (int)sum;
    }
    public String convertStr(int i, int j) {
        String temp="";
        temp += (int)(i/Math.pow(10, j-1));
        for(int k=j-2; k>=0; k--)
            temp += ((int)(i/Math.pow(10, k)))%10;
        return temp;
    }
    /**
     * get the next stage
     */
    public void nextStage() {
        if(stage%10<3) stage++;
        else stage +=8;
    }
    public void keyPressed(KeyEvent e) {
        int keyCode=e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_ENTER: if(status==0)runOption(); break;
            case KeyEvent.VK_ESCAPE: if(status==1 && bpanel.getIsEnd()) runCont(); break;
            case KeyEvent.VK_A: if(status==2) runSave();
            case KeyEvent.VK_B: if(status==2) {isNew=false; removeSP(); addBP();}
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void windowClosing(WindowEvent e) {dispose();System.exit(0);}
	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
    public void actionPerformed(ActionEvent e) {}
}
class SavePanel extends JPanel{
    public SavePanel() {}
    public void paint(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(0, 0,200,200);
        g.setColor(Color.WHITE);
        g.drawString("Press 'a' to save the game", 20,50);
        g.drawString("Press 'b' to continue", 20,100);
    }
}
