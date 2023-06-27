/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;
import   java.awt.event.ActionEvent;
import   java.awt.event.ActionListener;
import   javax.swing.Timer;
/**
 *
 * @author kailun
 */
public abstract class MObject extends GObject implements ActionListener{
    public MObject(int i, int x, int y, char t) {
        super(i,x,y);
        type = t; gesture = 0; status=0;
        isMoving = false; isDisable =false;
    }
    /**
     * active the timer to function
     * @param d direction
     */
    public void move(char d) {
        isMoving = true;
        direction = d;
    }
    /**
     * deactive the timer
     */
    public void stop() {
        isMoving = false;
    }
    /**
     * change the gesture
     */
    public void changeGesture() {
        setChanged();
        notifyObservers();
        if (gesture == gestureMax-1) gesture = 0;
        else gesture++;
    }
    /**
     * die
     * @param t type of dead
     */
    public void die(int t) {
        dieTimer=new Timer(800,this);
        dieTimer.start();
        dietype=t;dieAction();
    }
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if(obj.equals(moveTimer)) {
            if(isMoving) {
                changeGesture();
                changePosition();
            }
        }else if(obj.equals(dieTimer)) {
            reset();
            dieTimer.stop();
        }
    }
    public abstract void changePosition();
    public abstract void reset();
    public abstract void dieAction();
    public boolean isDisable (){return isDisable;}
    public void setDisable(boolean b){isDisable=b;}
    public int getStatus(){return status;}
    public void setStatus(int s){status=s;}
    public void setDirection(char d) { direction = d; }
    public int getGesture() { return gesture; }
    public char getDirection() { return direction; }
    protected char direction, type;
    protected int gesture, gestureMax, status;
    protected Timer moveTimer,dieTimer;
    protected boolean isMoving,isDisable;
    protected int PACE;
    protected int dietype;
}
