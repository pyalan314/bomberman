/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
/**
 *
 * @author kailun
 */
public abstract class GObject {
    protected PropertyChangeSupport change = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        change.removePropertyChangeListener(listener);
    }
    public GObject(int i, int x, int y) {
        id = i;
        positionx = x; positiony = y;
        map = Map.instance();
    }
    public GObject(int x, int y) {
        positionx = x; positiony = y;
        map = Map.instance();
    }
    public int block() { return (getCPositiony())/SIZE*15+(getCPositionx())/SIZE; }
    public int block(int y, int x) { return y/SIZE*15+x/SIZE; }
    public int getPositionx() { return positionx; }
    public int getPositiony() { return positiony; }
    public int getCPositionx() { return positionx +width; }
    public int getCPositiony() { return positiony + height; }
    public int getTPositionx(){return block()%W;}
    public int getTPositiony(){return block()/W;}
    public int getID(){return id;}
    protected int id, positionx, positiony;
    protected int height, width, symbol;
    protected Map map;
    protected final int SIZE = 32, W = 15, H = 13;
}
