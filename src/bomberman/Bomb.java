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
 * @author Steven
 */
public class Bomb extends GObject implements ActionListener{
    public Bomb (int i, int x, int y, int l) {
        super(i,x,y);
        status=0;
        dirPass=new boolean[4];
        dirLength=new int[4];
        for(int k=0;k<4;k++){
            dirPass[k]=true;
            }
        length = l;
        isPick=false;
        flamelength=0;
        shape=0;
        counting=0;
        explosed=false;
        bombTimer = new Timer(300, this);
        bombTimer.start();
    }
    /**
     * new a bomb with given ID, postion(x,y),flame length,power bomb sinature
     * @param i, ID
     * @param x, postion x
     * @param y,position y
     * @param l,flame length
     * @param b, is power bomb
     */
    public Bomb(int i, int x, int y, int l,boolean b) {
        super(i,x,y);
        status=0;
        isRun=false;
        isPowered=b;
        dirPass=new boolean[4];
        dirLength=new int[4];
        for(int k=0;k<4;k++){
            dirPass[k]=true;
            }
        length = l;
        isPick=false;
        flamelength=0;
        shape=0;
        counting=0;
        explosed=false;
        bombTimer = new Timer(300, this);
        bombTimer.start();
    }

        /**
         * active the explose timer and stop other timers
         */
        public void explose() {
        bombTimer.stop();
        if (isRun)
            runTimer.stop();
        shape=0;
        close=false;
        map.setElement('W', this.block());
        exploseTimer=new Timer(150,this);
        exploseTimer.start();
        status=3;
    }
    /**
     * active the explose timer and stop other timers with  given start shape and trigger direction
     * @param s ,shape
     * @param d, direction that cannot pass
     */
    public void explose(int s,int d) {
        bombTimer.stop();
        if (isRun)
            runTimer.stop();
        shape=s;
        close=false;
        dirPass[d]=false;
        map.setElement('W', this.block());
        exploseTimer=new Timer(150,this);
        exploseTimer.start();
        status=3;

    }
    /**
     * active the move timer and stop the bomb timer
     * @param d , moving direction
     */
    public void move(char d){
        direction=d;
        isRun=true;
        runTimer=new Timer(65,this);
        runTimer.start();
        status=4;
        map.setElement(' ', this.getTPositiony(), this.getTPositionx());
        runPosx=positionx;
        runPosy=positiony;
    }

    public void actionPerformed(ActionEvent e) {{
            Object obj = e.getSource();
            if(obj.equals(bombTimer)) {
                counting++;
                if(shape==0)shape=5;
                else if (shape==5)shape=0;
                if (counting==8){explose();}
            }else if(obj.equals(exploseTimer)){
                int i,j,k;
                int tempx=0,tempy=0,dir=0;
                if(!close){
                    if(shape<4&&flamelength<length)
                        shape++;
                    if(flamelength<length){
                        flamelength++;
                    for(dir=0;dir<4;dir++){
                        dirLength[dir]=0;
                        tempx=this.getTPositionx();tempy=this.getTPositiony();
                        for(j=1;j<=flamelength;j++){
                            if(dir<=1)
                                tempx=oneStep(tempx,tempy,dir);
                            else
                                tempy=oneStep(tempx,tempy,dir);
                            if(map.isOut(tempy, tempx)&&dirPass[dir]){
                                if(map.isWall(tempy, tempx)||map.getElement(tempy, tempx)=='W') break;
                                if(isPowered){
                                    if(map.isEnemy(tempy, tempx)){
                                        setChanged();
                                        this.notifyObservers(tempy*15+tempx);
                                    }
                                    if(map.isBomb(tempy, tempx))
                                        map.setElement('W', tempy*15+tempx);
                                    else map.setElement('X', tempy*15+tempx);
                                    dirLength[dir]++;
                                    continue;
                                }else if(map.isBomb(tempy, tempx)){
                                    map.setElement('W', tempy*15+tempx);
                                    dirLength[dir]++;
                                    break;
                                }
                                if(map.getElement(tempy, tempx)=='x'){
                                    dirLength[dir]++;
                                    break;
                                }
                                if ((map.isTile(tempy,tempx)&&!map.isItemTile(tempy, tempx))||map.isItem(tempy, tempx)){
                                    map.setElement('x', tempy*15+tempx);
                                    dirLength[dir]++;
                                    break;
                                }
                                else if(map.isItemTile(tempy, tempx)){
                                    map.setLeveldown(tempy, tempx);
                                    if(dir<=1)
                                        tempx=oneStepb(tempx,tempy,dir);
                                    else
                                        tempy=oneStepb(tempx,tempy,dir);
                                    if((tempy*15+tempx)!=this.block())
                                        map.setElement('x', tempy*15+tempx);
                                    else dirPass[dir]=false;
                                    dirLength[dir]++;
                                    break;
                                }
                                if(map.isPlayer(tempy, tempx)||map.isEnemy(tempy, tempx)){
                                    if(map.isEnemy(tempy, tempx)){
                                        setChanged();
                                        this.notifyObservers(tempy*15+tempx);
                                    }
                                    if(dir<=1)
                                        tempx=oneStepb(tempx,tempy,dir);
                                    else
                                        tempy=oneStepb(tempx,tempy,dir);
                                    if((tempy*15+tempx)!=this.block())
                                        map.setElement('x', tempy*15+tempx);
                                    else dirPass[dir]=false;
                                    for(i=j;i<flamelength;i++){
                                        if(dir<=1)
                                            tempx=oneStep(tempx,tempy,dir);
                                        else
                                            tempy=oneStep(tempx,tempy,dir);
                                    deFire(tempx,tempy);
                                    }
                                    dirLength[dir]++;
                                    break;
                                }
                                map.setElement('X', tempy*15+tempx);
                                dirLength[dir]++;
                            }
                        }
                    }
                    }
                    else close=true;
                }
                else{
                    if(shape>1)
                        shape--;
                    else if(shape==1){
                        exploseTimer.stop();
                        status=1;
                        i=flamelength;
                        flamelength=0;
                        if(isPowered)
                            map.setPowered(false);
                        for(dir=0;dir<4;dir++){
                            tempx=this.getTPositionx();tempy=this.getTPositiony();
                            for(j=1;j<=dirLength[dir];j++){
                                if(dir<=1)
                                    tempx=oneStep(tempx,tempy,dir);
                                else
                                    tempy=oneStep(tempx,tempy,dir);
                                if(map.isFire(tempy, tempx)){
                                    map.setElement(' ', tempy*15+tempx);
                                }
                            }
                        }
                        disable();
                    }
                }
             }else if(obj.equals(runTimer)){
                 int x=0,y=0;
                 if(runShape<3)
                    runShape++;
                 else runShape=0;
                 switch(direction){
                     case'u':y--;break;
                     case'd':y++;break;
                     case'l':x--;break;
                     case'r':x++;break;
                 }
                 if(map.getElement(this.getTPositiony()+y, this.getTPositionx()+x)==' '&&!map.isEnemy(this.getTPositiony()+y, this.getTPositionx()+x)&&!map.isPlayer(this.getTPositiony()+y, this.getTPositionx()+x)){
                     runPosx=runPosx+8*x;
                     runPosy=runPosy+8*y;
                     runCounting++;
                     if(runCounting%4==2){
                        positiony=32*(this.getTPositiony()+y);
                        positionx=32*(this.getTPositionx()+x);
                     }
                 }else {runTimer.stop();status=0;map.setElement('@', getTPositiony(), getTPositionx());}
             }
        }
    }
    /**
     * clear the fire signal on map
     * @param
     * tempx, postion x
     * @param 
     * tempy, position y
     */
    public void deFire(int tempx,int tempy){
        if(map.isFire(tempy, tempx)&&!(map.getElement(tempy, tempx)=='W')){
            map.setElement(' ', tempy*15+tempx);
        }
    }
    /**
     * move one step to given direction
     * @param x
     * position x
     * @param y
     * position y
     * @param dir
     * moving direction
     * @return position after moving
     */
    public int oneStep(int x,int y,int dir){
        switch (dir){
           //l
           case 0:
               return(x-1);
           //r  break;
           case 1:
               return(x+1);
           //u
           case 2:
               return(y-1);
           //d
           case 3:
               return(y+1);
           }
        return 0;
    }
    /**
     * move back one step for given direction
     * @param x
     * posiotn x
     * @param y
     * position y
     * @param dir
     * moving direction
     * @return position after moving
     */
    public int oneStepb(int x,int y,int dir){
        switch (dir){
           //l
           case 0:
               return(x+1);
           //r  break;
           case 1:
               return(x-1);
           //u
           case 2:
               return(y+1);
           //d
           case 3:
               return(y-1);
           }
        return 0;
    }
    /**
     * stop all timer as it is eaten by enemis
     */
    public void eaten() {
        if(isRun) runTimer.stop();
        bombTimer.stop();
        disable();
    }
    /**
     * set the bomb disable
     */
    public void disable(){map.setElement(' ', this.block());explosed=true;status=2;setChanged();notifyObservers();}
    /**
     * return the flame length of given direction
     * @param dir
     * direction
     * @return
     * flame length in that direction
     */
    public int getDLength(int dir){return dirLength[dir];}
    /**
     * return whether the fire pass or not of given direction
     * @param dir direction
     * @return is pass or not on that direction
     */
    public boolean getPass(int dir){return dirPass[dir];}
    public int getShape(){return shape;}
    public boolean isPowered(){return isPowered;}
    public int getRunShape(){return runShape;}
    public int getRPositionx(){return runPosx;}
    public int getRPositiony(){return runPosy;}
    public int getFLength(){return flamelength;}
    public int getStatus(){return status;}
    public boolean isExplosed(){return explosed;}
    /**
     * set the status of bomb become picked
     */
    public void setPicked(){
        bombTimer.stop();
        map.setElement(' ',block());
        isPick=true;
    }
    /**
     * release the bomb to given position
     * @param tempy
     * position y
     * @param tempx
     * position x
     */
    public void setRelease(int tempy,int tempx){
        map.setElement('@', tempy,tempx);
        positionx=tempx*32;
        positiony=tempy*32;
        bombTimer.start();
        isPick=false;
    }
    public boolean isPicked(){return isPick;}
    private int length;
    private char direction;
    private Timer bombTimer,exploseTimer,runTimer;
    final int PERIOD = 1000;
    private int shape,status,runShape,runPosx,runPosy;
    private int flamelength;
    private int counting,runCounting=0;
    private boolean close;
    private boolean explosed,isPick,isPowered,isRun;
    private int dirLength[];
    private boolean dirPass[];
}
