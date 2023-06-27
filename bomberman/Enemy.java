/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bomberman;

import javax.swing.Timer;

/**
 *
 * @author kailun
 */
public class Enemy extends MObject {
    public Enemy(int i,int x, int y,  char t) {
        super(i,x,y,t);
        symbol = 2; PACE = 4; gestureMax = 3;
        height = 16; width = 16;
        moveTimer = new Timer(100, this); moveTimer.start();
        initDir();
        move(direction);
    }
    public void AIlow() {
        if(type=='0') AIzero();
        else AIone();
    }
    public void AIhigh() {
        if(type=='0' || type=='1') return;
        else if(type=='2') AItwo();
        else if(type=='3') AIthree();
        else if(type=='4') AIfour();
    }    
    public void AIzero() {
        reverseDir();
    }
    public void AIone() {
        initDir();
    }
    public void AItwo() {
        direction = map.searchPlayer(getCPositiony(), getCPositionx(), direction);
    }
    public void AIthree() {
        char newdirection = map.searchBomb(getCPositiony(), getCPositionx(), direction);
        if(newdirection != ' ') direction = newdirection;
    }
    public void AIfour() {
        char newdirection = map.searchDanger(getCPositiony(), getCPositionx(), direction);
        if(newdirection != ' ') {
            direction = newdirection;
            reverseDir();
            moveTimer.setDelay(50);
            isRunning = true;
            if(isRunning) runcount ++;
        }
    }
    public void changePosition() {
        if(type=='4') {
            if(isRunning) runcount ++;
            System.out.println(runcount);
            if(runcount>50) {
                isRunning = false;
                moveTimer.setDelay(100);
                runcount =0;
            }
        }
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
            AIhigh();
            if(newB != oldB){
                map.setNextElementM(newB);
                map.setOldElementM(oldB);
                count=0;
            }
        } else if(shift==-150 && type=='3') {
            int tempx = this.getTPositionx();
            int tempy = this.getTPositiony();
            switch (direction) {
                case 'u': tempy -= 1;break;
                case 'd': tempy += 1; break;
                case 'l': tempx -= 1; break;
                case 'r': tempx += 1; break;
            }
            positionx = tempx*SIZE;
            positiony = tempy*SIZE;
            this.change.firePropertyChange(null, null, block());
        }
        else AIlow();
    }
    public void reset(){
        status =-1;
    }
    public void dieAction(){
        if(status!=1) {
            status=1;
            isDisable=true;
            stop();
            map.setOldElementM(block());
            moveTimer.stop();
        }
    }
    public void reverseDir() {
        if(++count>4) {count =0;changeDir(1); return;}
        changeDir(2);
    }
    public void changeDir(int i) {
        int j = convertDir(direction);
        direction = convertDir(j+i);
    }
    public void initDir() {
        int temp = (int) (Math.random()*4);
        direction= convertDir(temp);
    }

    public char convertDir(int i) {
        switch(i%4) {
            case 0: return 'u';
            case 1: return 'l';
            case 2: return 'd';
            case 3: return 'r';
            default: return ' ';
        }
    }
    public int convertDir(char d) {
        switch(d) {
            case 'u': return 0;
            case 'l': return 1;
            case 'd': return 2;
            case 'r': return 3;
            default: return 0;
        }
    }
    public int getType() {return type-'0';}
    private int count=0, runcount=0;
    private boolean isRunning=false;
}
