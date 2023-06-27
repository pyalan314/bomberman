/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bomberman;
import java.io.*;
import java.nio.charset.Charset;
/**
 *
 * @author kailun
 */
public class Map {
    public static Map instance(String s, boolean is2play) {if(unique==null) unique=new Map(s, is2play); return unique;}
    public static Map instance() {return unique;}
    private Map(String s, boolean is2play) {
        System.out.println("new map");
        DElement = new char[H][W]; UElement = new int[H][W]; MElement = new int[H][W];
        File inFile =new File(s);
        try (FileInputStream fis=new FileInputStream(inFile);
            InputStreamReader isr=new InputStreamReader(fis,Charset.forName("UTF-8"))){
            char[] chars=new char[8 * H * BOUND];
            while (isr.read(chars)<=0);
            for(int i=0;i<H;i++) for(int j=0;j<W;j++) DElement[i][j]=chars[17*i+j];
        } catch (IOException ex) {
            System.out.println("error in reading map file");
        }
        int count = 0;
        for(int i=0;i<H;i++) {
            for(int j=0;j<W;j++) {
                if(DElement[i][j]=='$') {
                    DElement[i][j]=' ';
                    if(!(count>0 && !is2play)) {UElement[i][j] = 1; count++; }
                }
                else if(DElement[i][j]>='0' && DElement[i][j]<='4') {
                    //DElement[i][j] = ' ';
                    MElement[i][j] = 2;
                }
            }
        }
    }
    /**
     * Set the new upper map element
     * @param nB position
     */
    public void setNextElementU(int nB) {
        int nx = nB % W; int ny = nB / W; int nBE = UElement[ny][nx];
        if(nBE>0) UElement[ny][nx] += Math.pow(10,(int)Math.log10(nBE)+1);
        else UElement[ny][nx] = 1;
    }
    /**
     * Set the old upper map element
     * @param oB position
     */
    public void setOldElementU(int oB) {
        int ox = oB % W; int oy = oB / W; int oBE = UElement[oy][ox];
        UElement[oy][ox] -= Math.pow(10,(int)Math.log10(oBE));
    }
    /**
     * Set the new middle map element
     * @param nB position
     */
    public void setNextElementM(int nB) {
        int nx = nB % W; int ny = nB / W; int nBE = MElement[ny][nx];
        if(nBE>0) MElement[ny][nx] += 2*Math.pow(10,(int)Math.log10(nBE)+1);
        else MElement[ny][nx] = 2;
    }
    /**
     * Set the old middle map element
     * @param oB position
     */
    public void setOldElementM(int oB) {
        int ox = oB % W; int oy = oB / W; int oBE = MElement[oy][ox];
        MElement[oy][ox] -= 2*Math.pow(10,(int)Math.log10(oBE));
    }
    /**
     * check if it is an obstacle
     * @param y y position
     * @param x x position
     * @param direction
     * @return indicating number for checking
     */
    public int checkObstacle(int y, int x, char direction) {
        int ox = x / SIZE; int oy = y / SIZE;
        int tx=0,ty=0;
        switch(direction){
            case'u':ty--;break;
            case'd':ty++;break;
            case'l':tx--;break;
            case'r':tx++;break;
        }
        if(!isOver(y,x,direction)) return 0;
        else if (isBomb(oy+ty,ox+tx)) return -150;
        else if(isObstacle(oy+ty,ox+tx)) return -100;
        else{
            switch(direction){
                case'u':if(isObstacle(oy+ty,ox+tx-1)); return 16-x%32;//else if(isObstacle(oy+ty,ox+tx-1)) return -200;break;
                case'd':if(isObstacle(oy+ty,ox+tx-1)); return 16-x%32;//else if(isObstacle(oy+ty,ox+tx-1)) return -200;break;
                case'l':if(isObstacle(oy+ty-1,ox+tx)); return 16-y%32;//else if(isObstacle(oy+ty-1,ox+tx)) return -200;break;
                case'r':if(isObstacle(oy+ty-1,ox+tx)); return 16-y%32;//else if(isObstacle(oy+ty-1,ox+tx)) return -200;break;
            }
        }
            return 0;
    }
    /**
     * find the direction of a player
     * @param y y position
     * @param x x position
     * @param direction
     * @return direction of a player
     */
    public char searchPlayer(int y, int x, char direction) {
        int ox = x / SIZE; int oy = y / SIZE;
        int count = 1;
        while(count <50) {
            if(oy-count<=0) break;
            if(isObstacle(oy-count,ox)) break;
            //System.out.println(oy-count+"$"+ox);
            if(isPlayer(oy-count, ox)) return 'u';
            count++;
        }
        count = 1;
        while(count <50) {
            if(oy+count>=12) break;
            if(isObstacle(oy+count,ox)) break;
            //System.out.println(oy+count+"#"+ox);
            if(isPlayer(oy+count, ox)) return 'd';
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox-count<=0) break;
            if(isObstacle(oy,ox-count)) break;
            //System.out.println(oy+"%"+(ox-count));
            if(isPlayer(oy, ox-count)) return 'l';
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox+count>=14) break;
            if(isObstacle(oy,ox+count)) break;
            //System.out.println(oy+"*"+(ox+count));
            if(isPlayer(oy, ox+count)) return 'r';
            count++;
        }
        return direction;
    }
    /**
     * find the direction of a bomb
     * @param y y position
     * @param x x position
     * @param direction
     * @return direction of a bomb
     */
    public char searchBomb(int y, int x, char direction) {
        int ox = x / SIZE; int oy = y / SIZE;
        int count = 1;
        while(count <50) {
            if(oy-count<=0) break;
            if(isObstacle(oy-count,ox))  {
                if(!isBomb(oy-count, ox)) break;
                else return 'u';
                }
            count++;
        }
        count = 1;
        while(count <50) {
            if(oy+count>=12) break;
            if(isObstacle(oy+count,ox)) {
                if(!isBomb(oy+count, ox)) break;
                else return 'd';
            }
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox-count<=0) break;
            if(isObstacle(oy,ox-count)) {
                if(!isBomb(oy, ox-count)) break;
                else return 'l';
            }
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox+count>=14) break;
            if(isObstacle(oy,ox+count)){
                if(!isBomb(oy, ox+count)) break;
                else return 'r';
            }
            count++;
        }
        return ' ';
    }
    /**
     *
     * find the direction of a bomb or flame
     * @param y y position
     * @param x x position
     * @param direction
     * @return direction of a bomb or flame
     */
    public char searchDanger(int y, int x, char direction) {
        int ox = x / SIZE; int oy = y / SIZE;
        int count = 1;
        while(count <50) {
            if(oy-count<=0) break;
            if(isObstacle(oy-count,ox))  {
                if(!isBomb(oy-count, ox)) break;
                else return 'u';
            } else if(isFire(oy-count,ox))
                return 'u';
            count++;
        }
        count = 1;
        while(count <50) {
            if(oy+count>=12) break;
            if(isObstacle(oy+count,ox)) {
                if(!isBomb(oy+count, ox)) break;
                else return 'd';
            } else if(isFire(oy+count,ox))
                return 'd';
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox-count<=0) break;
            if(isObstacle(oy,ox-count)) {
                if(!isBomb(oy, ox-count)) break;
                else return 'l';
            } else if(isFire(oy,ox-count))
                return 'l';
            count++;
        }
        count = 1;
        while(count <50) {
            if(ox+count>=14) break;
            if(isObstacle(oy,ox+count)){
                if(!isBomb(oy, ox+count)) break;
                else return 'r';
            } else if(isFire(oy,ox+count))
                return 'r';
            count++;
        }
        return ' ';
    }
    /**
     * check if it gets over a half of a tile in the direction
     * @param y y position
     * @param x x position
     * @param dir direction
     * @return whether it is true
     */
    public boolean isOver(int y,int x,char dir){
        switch(dir){
            case'u':if(y%32<=16)return true;break;
            case'd':if(y%32>=16)return true;break;
            case'l':if(x%32<=16)return true;break;
            case'r':if(x%32>=16)return true;break;
        }
        return false;}
    public boolean isObstacle(int y, int x) {return (isWall(y,x) || isBomb(y,x) || isTile(y,x));}
    public void setElement(char type, int y, int x) {DElement[y][x] = type;}
    public void setElement(char type, int B) {int x = B % W; int y = B / W; DElement[y][x] = type;}
    public void setElementU(int symbol, int B) {int x = B % W; int y = B / W; UElement[y][x] = symbol;}
    public void setElementM(int symbol, int B) {int x = B % W; int y = B / W; MElement[y][x] = symbol;}
    public char getElement(int y, int x) {return DElement[y][x];}
    public char getElement(int B){return DElement[B/W][B%W];}
    public int getElementU(int y, int x) {return UElement[y][x];}
    public int getElementU(int B){return UElement[B/W][B%W];}
    public int getElementM(int y, int x) {return MElement[y][x];}
    public int getElementM(int B){return MElement[B/W][B%W];}
    public void setLeveldown(int y,int x){DElement[y][x]=(char)(DElement[y][x]+32);}
    public boolean isEnemy(int y, int x) {return (MElement[y][x]%10==2);}
    public boolean isEnemy(int b) {return (MElement[b/W][b%W]%10==2);}
    public boolean isPlayer(int y, int x) {return (UElement[y][x]%10==1);}
    public boolean isPlayer(int b) {return (UElement[b/W][b%W]%10==1);}
    public boolean isOut(int y,int x){return(y>=0&&y<H&&x>=0&&x<W);}
    public boolean isFire(int y,int x){return((DElement[y][x]>'O'&&DElement[y][x]<='X')||DElement[y][x]=='x') ;}
    public boolean isFire(int b){return(DElement[b/W][b%W]=='X'||DElement[b/W][b%W]=='x') ;}
    public boolean isWFire(int y,int x){return DElement[y][x]=='W';}
    public boolean isTFire(int y,int x){return DElement[y][x]=='x';}
    public boolean isBomb(int y,int x){return(DElement[y][x]=='@');}
    public boolean isWall(int y,int x){return(DElement[y][x]=='#');}
    public boolean isItemTile(int y, int x) {return (DElement[y][x]>='A')&&(DElement[y][x]<='H');}
    public boolean isTile(int y, int x) {return (DElement[y][x]>='A')&&(DElement[y][x]<='M');}
    public boolean isItem(int y, int x) {return (DElement[y][x]>='a')&&(DElement[y][x]<='h');}
    public boolean isItem(int b) {return (DElement[b/W][b%W]>='a')&&(DElement[b/W][b%W]<='h');}
    public void showAllU(){for(int i=0;i<H;i++){for(int j=0;j<W;j++) System.out.print(UElement[i][j]); System.out.print('\n');}}
    public void showAll(){for(int i=0;i<H;i++){for(int j=0;j<W;j++) System.out.print(DElement[i][j]); System.out.print('\n');}}
    public void setPowered(boolean b){isPowered=b;}
    public boolean getPowered(){return isPowered;}
    public void claerMap() {unique = null;}
    private boolean isPowered=false;
    private static Map unique = null;
    public static char[][] DElement;
    public static int[][] UElement, MElement;
    private final int H = 13, W = 15, SIZE = 32, BOUND = 16;
}
