package bomberman;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.*;
import javax.imageio.ImageIO;
import java.lang.Object.*;
import java.util.Observable;
import java.util.Vector;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BombPanel extends JPanel implements KeyListener, Observer, ActionListener{
    int i=0,j=0,tileShape=1;
    final int NUM_KEY = 12;
    private boolean is2play, isNew, tileEffect=false;;
    private int stage, pinfo[][];
    private Player player[];
    private Vector item, enemy, bomb;
    //private Item item[];
    private int numPlayer, numItem, numEnemy, numBomb;
    private Image wall[], temp, floor[], tile[][], enemyImage[][][], man[][][],ItemImage[],bombImage[],flameImage[][][],manD[],enemyImageD[],kickImage[][],manT[][],powerImage[];
    private Image info, timer, disabled, xnum[], num[], win, lose;
    private CropImageFilter cropFilter;
    private int[] isPressed = new int[2];
    private Map map;
    private BombSeq bombs;
    private TimerDisplay tm;
    private boolean isEnd = false, isWin=true;
    private Timer paintTimer, aniTimer[];
    /**
     * Create BombPanel and use default setting
     * @param is2play whether it is a 2 players game
     */
    public BombPanel(boolean is2play){
        this.is2play = is2play;
        stage = 11;
        pinfo = new int[2][9];
        isNew = true;
        tm = new TimerDisplay();
    }
/**
 * Create BombPanel and use set setting
 * @param stage the number of stage
 * @param is2play whether it is a 2 players game
 * @param pinfo infomation of the players
 */
    public BombPanel(int stage, boolean is2play, int[][] pinfo){
        this.is2play = is2play;
        this.stage = stage;
        this.pinfo = pinfo;
        isNew = false;
        tm = new TimerDisplay();
    }
    /**
     * initialize the setting, load image
     */
    public void init(){
        aniTimer=new Timer[3];
        paintTimer = new Timer(50,this);
        paintTimer.start();
        if(is2play) player=new Player[2];
        else player=new Player[1];
        item=new Vector();
        enemy=new Vector();
        map = Map.instance("Map/map."+stage, is2play);
        bombs = bombs.instance();
        numPlayer=0; numEnemy=0; numItem=0; numBomb=0;
        int p=0;
        for(i=0;i<13;i++){
            for(j=0;j<15;j++){
                char c = map.getElement(i,j);
                if(map.isPlayer(i, j)){
                        if(isNew) player[numPlayer]=new Player(numPlayer++,j*32,(i-1)*32+6);
                        else if(pinfo[p][0]>0) {player[numPlayer]=new Player(numPlayer++,j*32,(i-1)*32+6, pinfo[p++]);
                    }
                }
                if (map.isEnemy(i, j)){
                    Enemy tempEnemy=new Enemy(numEnemy++,j*32,i*32,c);
                    map.setElement(' ', i, j);
                    enemy.add(tempEnemy);
                    tempEnemy.addObserver(this);}
                //if (map.isItemTile(i, j)){Item tempItem=new Item(numItem++,i,j,c);item.add(tempItem);}
            }
        }
        for(int k=0;k<numPlayer;k++)
            player[k].addObserver(this);
        readIMap(); readIMan(); readIEnemy();readIItems();readIBomb();readIFlame(); readIInfo();
        tileAni();
    }
    public void paint(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(480, 0, 250,416 );
        for( i=0;i<15;i++) {
            for( j=0;j<13;j++) {
                g.drawImage(floor[stage/10-1], i*32, j*32, this);
                switch (map.getElement(j,i)){
                    case '#': g.drawImage(wall[stage/10-1], i*32, j*32, this); break;
                    default:
                        if(map.isTile(j, i)) drawTile(g,i,j);
                        if(map.isItem(j, i)) g.drawImage(ItemImage[map.getElement(j, i)-'a'], i*32, j*32, this);
                }
            }
        }
        drawBomb(g); drawEnemy(g); drawPlayer(g); drawInfo(g);
        //drawMap(g);
        if(isEnd) drawEnd(g);
    }
    public void keyPressed(KeyEvent e) {
        int keyCode=e.getKeyCode();
            switch(keyCode) {
                case KeyEvent.VK_M: isEnd=true; isWin=true; break;
                case KeyEvent.VK_L: player[0].addLife(); player[1].addLife(); break;
            }
        if(!player[0].isDisable()) {
            switch(keyCode) {
                case KeyEvent.VK_LEFT: player[0].move('l'); break;
                case KeyEvent.VK_RIGHT: player[0].move('r'); break;
                case KeyEvent.VK_UP: player[0].move('u'); break;
                case KeyEvent.VK_DOWN: player[0].move('d'); break;
                case KeyEvent.VK_0: player[0].putBomb(); break;
                case KeyEvent.VK_9:  player[0].pickBomb(player[0].block()); break;
            }
        } else {
            switch(keyCode) {
                case KeyEvent.VK_F1:  player[0].init(0); break;
            }
        }
        if(is2play) {
            if(!player[1].isDisable()) {
                switch(keyCode) {
                    case KeyEvent.VK_A: player[1].move('l'); break;
                    case KeyEvent.VK_D: player[1].move('r'); break;
                    case KeyEvent.VK_W: player[1].move('u'); break;
                    case KeyEvent.VK_S: player[1].move('d'); break;
                    case KeyEvent.VK_F: player[1].putBomb(); break;
                    case KeyEvent.VK_G: player[1].pickBomb(player[1].block()); break;
                }
            }else{
                switch(keyCode) {
                case KeyEvent.VK_F2:  player[1].init(1); break;
                }
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        int keyCode=e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_LEFT: player[0].stop(); break;
            case KeyEvent.VK_RIGHT: player[0].stop(); break;
            case KeyEvent.VK_UP: player[0].stop(); break;
            case KeyEvent.VK_DOWN: player[0].stop(); break;
            case KeyEvent.VK_9: player[0].throwBomb(); break;
        }
        if(is2play) {
            switch(keyCode) {
                case KeyEvent.VK_A: player[1].stop(); break;
                case KeyEvent.VK_D: player[1].stop(); break;
                case KeyEvent.VK_W: player[1].stop(); break;
                case KeyEvent.VK_S: player[1].stop(); break;
                case KeyEvent.VK_G: player[1].throwBomb(); break;
            }
        }
    }
    /**
     * kill enemy located on given position
     * @param b
     */
    public void killE(int b){
        Enemy tempEnemy;
        for(int k=0;k<numEnemy;k++){
            tempEnemy=(Enemy)enemy.get(k);
            if(tempEnemy.block()==b) tempEnemy.die(0);
        }
    }
    /**
     * kill player located on given position
     * @param b position
     */
    public void killM(int b){
        for(int k=0;k<numPlayer;k++){
            if(player[k].block()==b)
                player[k].die(0);
        }
    }
    /**
     * convert the direction of a player
     * @param c diretion in char
     * @return diretion in integer
     */
    public int convertDir(char c) {
        switch(c) {
            case 'l': return 0;
            case 'r': return 1;
            case 'u': return 2;
            case 'd': return 3;
        }
        return 3;
    }
    /**
     * convert  direction of an enemy
     * @param c diretion in char
     * @return diretion in integer
     */
    public int convertDir2(char c) {
        switch(c) {
            case 'l': return 1;
            case 'r': return 3;
            case 'u': return 2;
            case 'd': return 0;
        }
        return 0;
    }
    public int convertDir3(char c){
        switch(c) {
            case 'l': return 1;
            case 'r': return 3;
            case 'u': return 2;
            case 'd': return 0;
        }
        return 0;
    }
    /**
     * triger a bomb on given direction
     * @param b position
     * @param shape shape of the bomb
     * @param dir  direction
     */
    public void triger(int b,int shape,int dir){
        switch(dir){
            case 0: dir=1; break;
            case 1: dir=0; break;
            case 2: dir=3; break;
            case 3: dir=2; break;
        }
        numBomb=bombs.getMax();
        Bomb tempBomb;
        for(int k=0;k<numBomb;k++){
            tempBomb=(Bomb)bombs.getB(k);
            if(tempBomb.block()==b&&(tempBomb.getStatus()==0)) tempBomb.explose(shape,dir);
        }
    }
    /**
     * read the data from file to get information of map display
     */
    public void readIMap() {
        try {
            wall=new Image[3];
            floor=new Image[3];
            tile=new Image[3][6];
            temp = ImageIO.read(new File("Image/tile_stage1.bmp"));
            cropFilter=new CropImageFilter(0,0,32,32);
            wall[0]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            cropFilter=new CropImageFilter(64,0,32,32);
            floor[0]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            cropFilter=new CropImageFilter(0,32,32,32);
            tile[0][0]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            temp = ImageIO.read(new File("Image/tile_stage2.gif"));
            cropFilter=new CropImageFilter(0,96,32,32);
            wall[1]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            cropFilter=new CropImageFilter(64,96,32,32);
            floor[1]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            for(int k=1;k<5;k++){
                cropFilter=new CropImageFilter((k-1)*32,4*32,32,32);
                tile[0][k]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            }
            for(int k=0;k<6;k++){
                cropFilter=new CropImageFilter(k*32,6*32,32,32);
                tile[1][k]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            }
            temp = ImageIO.read(new File("Image/tile_stage3.gif"));
            cropFilter=new CropImageFilter(0,32*3,32,32);
            wall[2]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            cropFilter=new CropImageFilter(32,64,32,32);
            floor[2]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            for(int k=0;k<6;k++){
                cropFilter=new CropImageFilter((k+1)*32,96,32,32);
                tile[2][k]=createImage(new FilteredImageSource(temp.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading map image");}
    }
    /**
     * read the image of playes
     */
    public void readIMan() {
        man=new Image[4][3][2];
        manD=new Image[2];
        manT=new Image[4][2];
        try {
            int k;
            for (k=1;k<=2;k++){
                Image tempImage = ImageIO.read(new File("Image/bomberman"+k+".gif"));
                for (i=0;i<4;i++){
                    for(j=0;j<3;j++){
                        cropFilter=new CropImageFilter(j*32,i*64,32,62);
                        man[i][j][k-1]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                    }
                    cropFilter=new CropImageFilter(i*32,4*64,32,62);
                    manT[i][k-1]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                }
                cropFilter=new CropImageFilter(3*32,3*64,32,62);
                manD[k-1]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading bomberman image");}
    }
    /**
     * read the image of enemies
     */
    public void readIEnemy() {
        int type;
        enemyImage=new Image[4][3][5];
        enemyImageD=new Image[5];
        try {
            for (type=0;type<5;type++){
            Image tempImage = ImageIO.read(new File("Image/enemy"+type+".gif"));
            for (i=0;i<4;i++){
                for(j=0;j<3;j++){
                    cropFilter=new CropImageFilter((3*i+j)*32,0,32,32);
                    enemyImage[i][j][type]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                }
            }
            cropFilter=new CropImageFilter(12*32,0,32,32);
            enemyImageD[type]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading enemy image");}
    }
    /**
     * read the image of items
     */
    public void readIItems() {
        ItemImage=new Image[8];
        try {
            Image tempImage = ImageIO.read(new File("Image/items.gif"));
            for (i=0;i<8;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                ItemImage[i]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading items image");}
    }
    /**
     * read the image of bombs
     */
    public void readIBomb(){
        bombImage=new Image[6];
        kickImage=new Image[4][2];
        powerImage=new Image[6];
        try {
            Image tempImage = ImageIO.read(new File("Image/bombs.gif"));
            for (i=0;i<6;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                bombImage[5-i]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                powerImage[5-i]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
            tempImage = ImageIO.read(new File("Image/kick.gif"));
            for (i=0;i<4;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                kickImage[i][0]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
            tempImage = ImageIO.read(new File("Image/kick2.gif"));
            for (i=0;i<4;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                kickImage[i][1]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
            tempImage = ImageIO.read(new File("Image/power.gif"));
            for (i=0;i<2;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                powerImage[i*5]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading bombs image");}
    }
    /**
     * read the image of flame
     */
    public void readIFlame(){
        int power,dir;
        flameImage=new Image[2][4][4];
        try {
            Image tempImage = ImageIO.read(new File("Image/flames.gif"));
            for (power=0;power<4;power++){
                dir=0;
                for(i=0;i<6;i++){
                    cropFilter=new CropImageFilter(power*32,i*32,32,32);
                    if(i==0||i==3){
                        flameImage[0][dir][power]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                        flameImage[0][dir+1][power]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                    }
                    else flameImage[1][dir++][power]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
                }
            }
        } catch (IOException ex) {System.out.println("error in reading flame image");}
    }
    /**
     * to read image using to display game information
     */
    public void readIInfo(){
        xnum = new Image[10];
        num = new Image[10];
        try {
            info=ImageIO.read(new File("Image/info.png"));
            timer = ImageIO.read(new File("Image/timer.png"));
            disabled = ImageIO.read(new File("Image/disabled.png"));
            win = ImageIO.read(new File("Image/win.png"));
            lose = ImageIO.read(new File("Image/lose.png"));
            Image tempImage = ImageIO.read(new File("Image/xnum.png"));
            for (i=0;i<10;i++){
                cropFilter=new CropImageFilter(i*32,0,32,32);
                xnum[i]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
            tempImage = ImageIO.read(new File("Image/num.png"));
            for (i=0;i<10;i++){
                cropFilter=new CropImageFilter(i*18,0,18,18);
                num[i]=createImage(new FilteredImageSource(tempImage.getSource(),cropFilter));
            }
        } catch (IOException ex) {System.out.println("error in reading info image");}
    }
    /**
     * draw the image of tiles
     * @param g graphic
     * @param i x position
     * @param j y position
     */
    public void drawTile(Graphics g,int i,int j){
        if(stage/10==1)
            g.drawImage(tile[0][0], i*32, j*32, this);
        if(stage/10==2)
            g.drawImage(tile[0][tileShape], i*32, j*32, this);
        if(stage/10==3)
            g.drawImage(tile[2][0], i*32, j*32, this);
    }
    /**
     * active the timer of tile animation
     */
    public void tileAni(){
        if (tileEffect==false){
            aniTimer[0]=new Timer(350,this);
            aniTimer[0].start();
            tileEffect=true;
        }
    }
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if(obj.equals(aniTimer[0])) {
                if (tileShape<4)
                    tileShape++;
                else tileShape=1;
                repaint();
        }
        if(obj.equals(paintTimer)) {
            if(enemy.size() == 0){ isWin=true; isEnd=true;}
            if(is2play) {
                if(player[0].isAllLifeUsed() && player[1].isAllLifeUsed()){isWin=false;isEnd=true;}
            } else
                if(player[0].isAllLifeUsed()){isWin=false;isEnd=true;}
            if(isEnd) gameover();
            repaint();
        }
    }
    /**
     * draw the image of enemy
     * @param g graphic
     */
    public void drawEnemy(Graphics g) {
        for(int k=0; k< numEnemy;k++){
            Enemy tempEnemy=(Enemy)enemy.get(k);
            if(tempEnemy.getStatus()<0){
                enemy.remove(k);
                numEnemy--;
                k--;
                continue;
            }
            else if(tempEnemy.getStatus()==0) {
                //System.out.println(convertDir2(tempEnemy.getDirection())+" "+tempEnemy.getGesture()+" "+tempEnemy.getType()+" "+tempEnemy.getPositionx()+" "+tempEnemy.getPositiony());
                g.drawImage(enemyImage[convertDir2(tempEnemy.getDirection())][tempEnemy.getGesture()][tempEnemy.getType()], tempEnemy.getPositionx(), tempEnemy.getPositiony(), this);
            }
            else g.drawImage(enemyImageD[tempEnemy.getType()], tempEnemy.getPositionx(), tempEnemy.getPositiony(), this);
            if(map.isPlayer(tempEnemy.block())) {
                for(int j=0;j<numPlayer;j++){
                    if(player[j].block()==tempEnemy.block())
                        player[j].die(1);
                }
            }
        }
    }
    /**
     * draw the image of player
     * @param g graphic
     */
    public void drawPlayer(Graphics g) {
        for (int k=0; k< numPlayer;k++){
            if(player[k].getStatus()<0){
                continue;
            }
            if(player[k].getStatus()==0) g.drawImage(man[convertDir(player[k].getDirection())][player[k].getGesture()][k], player[k].getPositionx(), player[k].getPositiony(),this);
            else if(player[k].getStatus()==1) g.drawImage(manD[k],player[k].getPositionx(), player[k].getPositiony(),this);
            else if(player[k].getStatus()==2) g.drawImage(manT[convertDir3(player[k].getDirection())][k],player[k].getPositionx(), player[k].getPositiony(),this);
        }
    }
    /**
     * draw bomb and the animation of fire
     * it can also trigger the fuction of killing players, enemies or bombs
     * @param g graphic
     */
    public void drawBomb(Graphics g) {
        numBomb=bombs.getMax();
        for(int k=0;k<numBomb;k++){
            Bomb tempBomb=(Bomb)bombs.getB(k);
            if(tempBomb.isExplosed()){
                bombs.removeB(k);
                numBomb--;
                k--;
                continue;
            }
            if(tempBomb.isPicked()) continue;
            if(tempBomb.getStatus()==4){g.drawImage(kickImage[tempBomb.getRunShape()][tempBomb.isPowered()?1:0], tempBomb.getRPositionx(), tempBomb.getRPositiony(), this);continue;}
            if(!tempBomb.isPowered())g.drawImage(bombImage[tempBomb.getShape()], tempBomb.getPositionx(), tempBomb.getPositiony(), this);
            else g.drawImage(powerImage[tempBomb.getShape()], tempBomb.getPositionx(), tempBomb.getPositiony(), this);
           if(tempBomb.getStatus()==3&&map.isPlayer(tempBomb.block()))
               this.killM(tempBomb.block());
            int dirL[];
            dirL=new int[4];
            int tempx=0,tempy=0,dir=0;
            if(tempBomb.getFLength()<=0) continue;
            for(dir=0;dir<4;dir++){
                dirL[dir]=tempBomb.getDLength(dir);
                tempx=tempBomb.getTPositionx();
                tempy=tempBomb.getTPositiony();
                for(j=1;j<=dirL[dir];j++){
                    switch (dir){
                       case 0: tempx -= 1; break;
                       case 1: tempx += 1; break;
                       case 2: tempy -= 1; break;
                       case 3: tempy += 1; break;
                    }
                    if(j<tempBomb.getFLength()) g.drawImage(flameImage[0][dir][4-tempBomb.getShape()],tempx*32 , tempy*32, this);
                    else g.drawImage(flameImage[1][dir][4-tempBomb.getShape()],tempx*32 , tempy*32, this);
                    if(map.isBomb(tempy, tempx)||map.isWFire(tempy, tempx)) this.triger(tempy*15+tempx,4-tempBomb.getShape(),dir);
                    if(map.isEnemy(tempy, tempx)) this.killE(tempy*15+tempx);
                    if(map.isPlayer(tempy, tempx)) this.killM(tempy*15+tempx);
                }
            }
       }
    }
    /**
     * draw information of players and game status
     * @param g graphic
     */
    public void drawInfo(Graphics g) {
        g.drawImage(timer, 480+100,10, this);
        g.drawImage(info, 480,0, this);
        g.drawImage(num[stage/10], 480+10,32, this);
        g.drawImage(num[stage%10], 480+10+18+18,32, this);
        g.drawImage(num[tm.getMinute()], 480+112,16, this);
        g.drawImage(num[(int)tm.getSecond()/10], 480+148,16, this);
        g.drawImage(num[tm.getSecond()%10], 480+166,16, this);
        if((tm.getSecond()+tm.getMinute())==0 && !isEnd) gameover();
        for(int i=0; i<numPlayer; i++) {
            g.drawImage(xnum[player[i].getLife()], 480+42,64+i*144, this);
            for(int j=0; j<5; j++) g.drawImage(num[(player[i].getScore()/(int)Math.pow(10, 4-j))%10], 480+96+j*18,64+i*144+7, this);
            g.drawImage(xnum[player[i].getBombnum()], 480+42,112+i*144, this);
            g.drawImage(xnum[player[i].getBombpower()], 480+106,112+i*144, this);
            g.drawImage(xnum[player[i].getSpeed()], 480+170,112+i*144, this);
            if(!player[i].getIsPowered()) g.drawImage(disabled, 480+10,160+i*144, this);
            if(!player[i].getIsKickale()) g.drawImage(disabled, 480+52,160+i*144, this);
            if(!player[i].getIsThrowable()) g.drawImage(disabled, 480+94,160+i*144, this);
            if(!player[i].getIsJacketed()) g.drawImage(disabled, 480+136,160+i*144, this);
            if(!player[i].getIsDiseased()) g.drawImage(disabled, 480+178,160+i*144, this);
        }
        if(!is2play){
            g.drawImage(disabled, 480+10,208, this);
            for(int i=0; i<3; i++) g.drawImage(disabled, 480+10+i*64,256, this);
            for(int i=0; i<5; i++) g.drawImage(disabled, 480+10+i*42,160+144, this);
        }
    }
    /**
     * draw the map
     * @param g graphic
     */
    public void drawMap(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(480+250, 0, 250,416 );
        String inf="";
        g.setColor(Color.BLACK);
        for(int y=0;y<13;y++){
            inf="";
            for(int x=0;x<15;x++){
                inf=inf+map.getElementU(y, x);
                Font f=new Font("Helvetica",Font.BOLD,64);
                g.drawString(inf, 480+250, y*12);
            }
        }
        for(int y=0;y<13;y++){
            inf="";
            for(int x=0;x<15;x++){
                inf=inf+map.getElementM(y, x);
                Font f=new Font("Helvetica",Font.BOLD,64);
                g.drawString(inf, 480+250, (y+13)*12);
            }
        }
        for(int y=0;y<13;y++){
            inf="";
            for(int x=0;x<15;x++){
                inf=inf+map.getElement(y, x);
                Font f=new Font("Helvetica",Font.BOLD,64);
                g.drawString(inf, 480+250, (y+26)*12);
            }
        }
    }
    /**
     * show game over
     * @param g
     */
    public void drawEnd(Graphics g) {
        if(isWin) g.drawImage(win, 0,0, this);
        else g.drawImage(lose, 0,0, this);
    }
    public boolean getIsEnd() {return isEnd;}
    public boolean getIsWin() {return isWin;}
    public int getStage() {return stage;}
    public boolean getIs2Play() {return is2play;}
    /**
     * save the info of players
     * @return an array of info
     */
    public int[][] getInfo() {
        for(int i=0; i<2; i++) {
            if(i==1 && !is2play) pinfo[i] = new int[] {0,0,0,0,0,0,0,0,0};
            else {
                player[i].setIsDiseased(false);
                pinfo[i]= new int[] {player[i].getLife(), player[i].getScore(), player[i].getBombnum(), player[i].getBombpower(), player[i].getSpeed(), player[i].getIsPowered()?1:0, player[i].getIsKickale()?1:0, player[i]. getIsThrowable()?1:0, player[i].getIsJacketed()?1:0};
            }
        }
        return pinfo;
    }
    /**
     * clear timers and datas
     */
    public void clear() {
        paintTimer.stop();
        if (tileEffect==true) aniTimer[0].stop();
        map.claerMap();
        bombs.clearBomb();
        player[0].die(0);
        if(is2play) player[1].die(0);
        for(int k=0;k<enemy.size();k++){
            Enemy tempEnemy;
            tempEnemy=(Enemy)enemy.get(k);
            tempEnemy.die(0);
        }
        enemy.clear();
    }
    /**
     * set the gameover status
     */
    public void gameover() {
        tm.timeout();
        System.out.println("gameover");
    }
    public void update() {}
    public void update(Observable o, Object arg) {
        if(arg instanceof Bomb){
            Bomb tempBomb;
            tempBomb=(Bomb)arg;
            tempBomb.addObserver(this);
            System.out.println("added");
        }else if(arg instanceof Integer && o instanceof Bomb){
            System.out.println("update");
            int b=(Integer)arg;
            Bomb tempBomb2;
            tempBomb2=(Bomb)o;
            for(int k=0;k<enemy.size();k++){
                Enemy tempEnemy;
                tempEnemy=(Enemy)enemy.get(k);
                if(tempEnemy.block()==b){
                    player[tempBomb2.getID()].updateScore(tempEnemy.getType());
                break;
                }
            }
        }else if(arg instanceof Integer && o instanceof Enemy){
            int temp = (Integer) arg;
            numBomb=bombs.getMax();
            Bomb tempBomb;
            for(int k=0;k<numBomb;k++){
                tempBomb=(Bomb)bombs.getB(k);
                if(tempBomb.block()==temp){
                    tempBomb.eaten();
                }
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
}