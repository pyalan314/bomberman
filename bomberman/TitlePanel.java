/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
/**
 *
 * @author kailun
 */
public class TitlePanel extends JPanel implements KeyListener{
    public TitlePanel() {
        try {
            menu = ImageIO.read(new File("Image/menu.png"));
            select=ImageIO.read(new File("Image/select.png"));
            title=ImageIO.read(new File("Image/title.png"));
            bbm=ImageIO.read(new File("Image/bbm.png"));
        }
            catch (IOException ex) {System.out.println("error in reading title image");}
        }
    public void paint(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(0, 0,730, 416);
        g.drawImage(title, 97, 25, this);
        g.drawImage(bbm, 110, 120, this);
        g.drawImage(menu, POSX+25, POSY-7, this);
        if(option<=2) {
            g.drawImage(select, POSX, POSY, this);
            g.drawImage(select, POSX+20, POSY+DEV_Y*option, this);
        } else
        g.drawImage(select, POSX, POSY+DEV_Y*option, this);
    }
    /**
     * Select different option
     * @param i degree of change
     */
    public void changeOption(int i) {
        option += i;
        if(option>4) option=1;
        if(option<1) option=4;
        repaint();
    }
    /**
     * Handle up and down selection
     * @param e keyevent
     */
    public void keyPressed(KeyEvent e) {
        int keyCode=e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_DOWN: changeOption(1); break;
            case KeyEvent.VK_UP: changeOption(-1); break;
        }
    }
    public int getOption() {return option;}
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    private Image title, select, menu, bbm;
    private int option=1;
    final int DEV_Y = 42, DEV_X=20,  POSY=160, POSX=360;
}