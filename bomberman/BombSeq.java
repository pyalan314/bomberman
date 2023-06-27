/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;

import java.util.Vector;
/**
 *
 * @author kailun
 */
public class BombSeq {
    public static BombSeq instance() {
        if(unique == null)  unique = new BombSeq();
        return unique;
    }
    private BombSeq() {
        bombV = new Vector<Bomb>();        
    }
    /**
     * insert a new bomb
     * @param index
     */
    public void removeB(int index){
        bombV.remove(index);
    }
    /**
     * insert a new bomb
     * @param index
     */
    public void insertB(Bomb b) {
        bombV.add(b);
    }
    public int getMax() {
        return bombV.size();
    }
    public Bomb getB(int key) {
        return (Bomb) bombV.get(key);
    }
    public void clearBomb() {unique = null;}
    private static BombSeq unique = null;
    private static Vector<Bomb> bombV;
}
