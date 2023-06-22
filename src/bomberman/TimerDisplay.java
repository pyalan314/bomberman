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
public class TimerDisplay{
    public TimerDisplay() {
        interval = new Timer(1000, adder);
        interval.start();
        timeRemained = 300;
        updateText();
    }
    /**
     * Do action for timeout (stop the timer)
     */
    public void timeout() {
        interval.stop();
    }
    /**
     * calculate new minute and second
     */
    public void updateText() {
        minute = (int) timeRemained/60;
        second = (timeRemained - minute*60);
        if(timeRemained==0) {
            timeout();
            return;
        }
        timeRemained--;
    }
    public int getSecond() {return second;}
    public int getMinute() {return minute;}
    ActionListener adder=new ActionListener(){
        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if(obj instanceof Timer) {
                updateText();
            }
        }
    };
    private Timer interval;
    private int minute;
    private int second;
    private int timeRemained;
}
