
/**
 * Write a description of class Lid here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Lid
{
    private int lidHeight;
    private Cup ofCup;
    private String lidColor;
    private Rectangle lidRectangle;
    private int xPositionLid;
    private int yPositionLid;
    private int lidWidth;
    private int lidNumber;

    /**
     * Constructor for objects of class Lid
     */
    public Lid(int lidNumber, int lidWidth)
    {
        this.lidNumber = lidNumber;
        int scale = 20;
        this.lidHeight = 20;
        this.lidWidth = lidWidth;
        this.ofCup = null;
        this.lidColor = assignColor(lidNumber);
        this.xPositionLid = 0;
        this.yPositionLid = 0;
        this.lidRectangle = new Rectangle(0,0);
        lidRectangle.changeSize(lidHeight, lidWidth);
        lidRectangle.changeColor(lidColor);
    }

    private String assignColor(int number){
        String[] colors = {"yellow","blue","red","green","magenta","orange"};
        return colors[(number - 1)%colors.length];
    }
    
 
    public void setPosition(int x, int y){
        int dx = x - this.xPositionLid;
        int dy = y - this.yPositionLid;
        this.xPositionLid = x;
        this.yPositionLid = y;
        lidRectangle.moveHorizontal(dx);
        lidRectangle.moveVertical(dy);
    }
    
    //CAMBIO
    public int getYPosition(){
        return yPositionLid;
    }
    
    //CAMBIO
    public int getXPosition(){
        return xPositionLid;
    }
    
    public void setLidNumber(int number){
        this.lidNumber = number;
    }

    public int getLidNumber(){
        return this.lidNumber;
    }

    public int getLidWidth(){
        return this.lidWidth;
    }
    
    
    public int getLidHeight()
    {
        return lidHeight;
    }
    
    public String getLidColor()
    {
        return lidColor;
    }
    
    public void setLidColor(String color)
    {
        this.lidColor = color;
        lidRectangle.changeColor(color);
    }
    
    public void makeVisible()
    {
        lidRectangle.makeVisible();
    }
    
    public void makeInvisible()
    {
        lidRectangle.makeInvisible();
    }
    
    public void setCup(Cup cup)
    {
        this.ofCup = cup;
    }
    
    public Cup getCup(){
        return ofCup;
    }
    
    
    
    
}