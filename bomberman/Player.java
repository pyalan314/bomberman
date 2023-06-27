/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Timer;
/**
 *
 * @author kailun
 */
public class Player extends MObject implements PropertyChangeListener{
    public Player(int i, int x, int y) {
        super(i,x,y, '$');
        direction = 'd'; symbol = 1; PACE = 8; gestureMax = 3;
        height = 42; width = 16;
        life = 3; score = 0; bombnum = 1; bombpower = 1; speed = 1;
        bombs = BombSeq.instance();
        isPowered = false; isKickale = false; isThrowable = false; isJacketed = false;
        powerBombUsed = false; isDiseased = false;
        moveTimer = new Timer((int) (80/Math.pow(1.2, getSpeed()-1)), this); moveTimer.start();
    }
    public Player(int i, int x, int y, int[] info) {
        super(i,x,y, '$');
        direction = 'd'; symbol = 1; PACE = 8; gestureMax = 3;
        height = 42; width = 16;
        life = info[0]; score = info[1]; bombnum = info[2]; bombpower = info[3]; speed = info[4];
        bombs = BombSeq.instance();
        isPowered = info[5]==1; isKickale = info[6]==1; isThrowable = info[7]==1; isJacketed = info[8]==1;
        powerBombUsed = false; isDiseased = false;
        moveTimer = new Timer((int) (80/Math.pow(1.2, speed-1)), this); moveTimer.start();
    }
    /**
     * move or change position
     */
    public void changePosition() {
        int oldB = block();
        int shift;
        shift=map.checkObstacle(getCPositiony(), getCPositionx(),direction);
        if (shift>-99) {
            switch (direction) {
            case 'u': positionx+=shift;positiony-=PACE;break;
            case 'd': positionx+=shift;positiony+=PACE; break;
            case 'l': positionx-=PACE;positiony+=shift; break;
            case 'r': positionx+=PACE;positiony+=shift; break;
        }
            int newB = block();
            if(newB != oldB){
                map.setNextElementU(newB);
                map.setOldElementU(oldB);
                if(map.isItem(newB)){
                    setProperty(map.getElement(newB));
                    map.setElement(' ', newB);
                }
            }
        }else if(shift==-150 )
            if(getIsKickale())
            kick();
    }
    /**
     * use the item effect
     * @param c the type of item
     */
    public void setProperty(char c) {
        switch(c) {
            case 'a': if(bombnum<MAX_BOMBNUM) bombnum++; break;
            case 'b': if(bombpower<MAX_BOMBPOWER) bombpower++; break;
            case 'c': isPowered=true; break;
            case 'd': isDiseased=true; changeSpeed(); break;
            case 'e': if(speed<MAX_SPEED) {speed++;changeSpeed();} break;
            case 'f': isJacketed=true; break;
            case 'g': isThrowable=true; break;
            case 'h': isKickale=true; break;
        }
    }
    /**
     * put thr bomb on the same location as player's
     */
    public void putBomb() {
        if(bombused<getBombnum() &&map.getElement(this.block())!='@') {
            int tempx, tempy;
            tempx = getTPositionx()*SIZE;
            tempy = getTPositiony()*SIZE;
            Bomb tempbomb;
            if (getIsPowered() && !map.getPowered()) {
                map.setPowered(true);
                tempbomb = new Bomb(id, tempx, tempy, 9,true);
                bombused++;
            }
            else{
                tempbomb = new Bomb(id, tempx, tempy, getBombpower(),false);
                bombused++;
            }
            tempbomb.addPropertyChangeListener(this);
            bombs.insertB(tempbomb);
            map.setElement('@', tempbomb.block());
            this.change.firePropertyChange(null, null, tempbomb);
        }
    }
    /**
     * pick up the bomb
     * @param b The location of occurance
     */
    public void pickBomb(int b) {
        int max,k;
        if(map.getElement(b)=='@'&& getIsThrowable()){
            max=bombs.getMax();
            for(k=0;k<max;k++){
                Bomb tempBomb=(Bomb)bombs.getB(k);
                if(tempBomb.block()==b) {
                    tempBomb.setPicked();
                    pickBomb=tempBomb;
                }
                status=2;
            }
        }
    }
    /**
     * throw the bomb
     */
    public void throwBomb() {
        if (status==2){
        char dir=this.getDirection();
        int tempx=this.getTPositionx(),tempy=this.getTPositiony(),x=0,y=0;
        status=0;
        switch(dir){
            case 'l':x-=1;
            case 'r':x+=1;
            case 'u':y-=1;
            case 'd':y+=1;
        }
        if(map.isOut(tempy, tempx))
            if(map.getElement(tempy+2*y, tempx+2*x)==' ')
                pickBomb.setRelease(tempy+2*y, tempx+2*x);
            else if(map.getElement(tempy+y, tempx+x)==' ')
                pickBomb.setRelease(tempy+y, tempx+x);
            else pickBomb.setRelease(tempy, tempx);
        }
    }
    /**
     * kick the bomb
     */
    public void kick(){
        int numBomb=bombs.getMax(),x=0,y=0,b;
        Bomb tempBomb;
        switch(this.getDirection()){
            case 'u':y--;break;
            case 'd':y++;break;
            case'l':x--;break;
            case'r':x++;break;
        }
        b=15*(this.getTPositiony()+y)+this.getTPositionx()+x;
        for(int k=0;k<numBomb;k++){
            tempBomb=(Bomb)bombs.getB(k);
            if(tempBomb.block()==b&&(tempBomb.getStatus()==0))
                tempBomb.move(this.getDirection());
        }
    }
    /**
     * initialize the status of player
     * @param i indicate dirrerent player
     */
    public void init(int i) {
        if(life>0) {
            if(i==0) {positionx=32*1; positiony=6; }
            else {positionx=32*13; positiony=6; }
            status=0; isDisable=false;
            map.setNextElementU(block());
            moveTimer.start();
        }
    }
    /**
     *set the status of player after a dying period
     */
    public void reset(){
        if(dietype==0) 
            if(isJacketed) {status=0;isJacketed=false;}
            else status =-1;
        else status =-1;
    }
    /**
     * perform the action when dying
     */
    public void dieAction(){
        if(dietype==0) {if(isJacketed) status=3;}
        if(status!=1) {
            System.out.println("dying");
            status=1;
            if(dietype!=2) dieOnce();
            else dieAll();
            isDisable=true;
            stop();
            moveTimer.stop();
            map.setOldElementU(block());
        }
    }
    /**
     * set the delay of timer to achieve changing speed
     */
    public void changeSpeed() {moveTimer.setDelay((int) (80/Math.pow(1.2, getSpeed()-1)));}
    /**
     * to reduce life by 1 if it is positive
     */
    public void dieOnce() {
        if(life>0) life--;
        else if(life<=0) dieAll();
    }
    /**
     * set life to be zero
     */
    public void dieAll() {life =0;}
    /**
     * check if life is zero
     * @return if life is zero
     */
    public boolean isAllLifeUsed() {return (life<=0);}
    /**
     * update the score
     * @param type the type of the enemy
     */
    public void updateScore(int type) {
        score += (type+1)*100;
    }
    public void propertyChange(PropertyChangeEvent evt)  {bombused--;}
    public void setBombnum(int N) { bombnum = N; }
    public void addBombnum() { bombnum++; }
    public void setBombpower(int N) { bombpower = N; }
    public void setSpeed(int s) { speed = s; }
    public void setIsPowered(boolean B) { isPowered = B; }
    public void setIsDiseased(boolean B) { isDiseased = B; }
    public void setIsJacketed(boolean B) { isJacketed = B; }
    public void setIsThrowable(boolean B) { isThrowable = B; }
    public void setIsKickale(boolean B) { isKickale = B; }
    public int getBombnum() { if(isDiseased) return (int)((bombnum+1)/2); else return bombnum; }
    public int getBombpower() { if(isDiseased) return (int)((bombpower+1)/2); else return bombpower; }
    public int getSpeed() { if(isDiseased) return (int)((speed+1)/2); else return speed; }
    public int getLife(){return life;}
    public int getScore(){return score;}
    public boolean getIsPowered() { if(isDiseased) return false; else return isPowered; }
    public boolean getIsDiseased() { return isDiseased; }
    public boolean getIsJacketed() { if(isDiseased) return false; else return isJacketed; }
    public boolean getIsThrowable() { if(isDiseased) return false; else return isThrowable; }
    public boolean getIsKickale() { if(isDiseased) return false; else return isKickale; }
    private int bombnum, bombpower, life, score, speed,bombused;
    private BombSeq bombs;
    private Bomb pickBomb;
    // private boolean isPick;
    private boolean isPowered, powerBombUsed, isDiseased, isJacketed, isThrowable, isKickale;
    final int MAX_BOMBNUM = 9, MAX_BOMBPOWER = 9,  MAX_SPEED = 9;
    public void addLife() {
        life++;
    }
}