
/**
 * Write a description of class Cup here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Cup
{
    private int cupHeight;
    private Lid ofLid;
    private String colorCup;
    private Rectangle[] cupRectangles;
    private int xPositionCup;
    private int yPositionCup;
    private int cupNumber;
    private int cupWidth;
    private int cupHeightPx;
    private int scale;
    private int grosor;
    
    

    /**
     * Constructor for objects of class Cup
     */
    public Cup(int cupNumber, int cupWidth)
    {
        this.cupNumber = cupNumber;
        this.cupHeight = 2 * cupNumber - 1;
        this.cupWidth = cupWidth;
        this.ofLid = null;
        this.colorCup = assignColor(cupNumber);
        this.xPositionCup = 0;
        this.yPositionCup = 0;
        this.cupRectangles = new Rectangle[3];
        this.scale = 20;
        this.grosor = scale;
        int alturaBase = scale;
        int alturaMinima = alturaBase + (cupNumber - 1)*scale;
        int alturaCalculada = (2*cupNumber-1)*scale;
        if (alturaCalculada < alturaMinima){
            this.cupHeightPx = alturaMinima;
        } else {
            this.cupHeightPx = alturaCalculada;
        }
        construirTaza();
    }

    private String assignColor(int number) {
        String[] colors = {"yellow", "blue", "red", "green", "magenta", "orange", "black", "white", "cyan", "pink", "gray", "lightGray", "darkGray"};
        return colors[(number-1) % colors.length];
    }
    
    private void construirTaza(){
        int grosor = scale;
        cupRectangles[0] = new Rectangle(0, 0);
        cupRectangles[0].changeSize(cupHeightPx, grosor);
        cupRectangles[0].changeColor(colorCup);
        
        cupRectangles[1] = new Rectangle(0, 0);
        cupRectangles[1].changeSize(cupHeightPx, grosor);
        cupRectangles[1].changeColor(colorCup);
        
        cupRectangles[2] = new Rectangle(0, 0);
        cupRectangles[2].changeSize(grosor, cupWidth);
        cupRectangles[2].changeColor(colorCup);
        
    }
    
    public void setPosition(int x, int y){
        this.xPositionCup = x;
        this.yPositionCup = y;
        int grosor = scale;
        cupRectangles[0].setPosition(x,y);
        cupRectangles[1].setPosition(x + cupWidth - grosor, y);
        cupRectangles[2].setPosition(x, y + cupHeightPx - grosor);
    }
    
    public boolean hasLid()
    {
        return ofLid != null;
    }
    
    public int getCupHeight()
    {
        return cupHeight;
    }
    
    public int getCupNumber()
    {
        return cupNumber;
    }
    
    public String getCupColor()
    {
        return colorCup;
    }
    
    public void setCupColor(String color)
    {
        this.colorCup = color;
        for (Rectangle r : cupRectangles){
            r.changeColor(color);
        }
    }
    
    public void makeVisible()
    {
        for (Rectangle r: cupRectangles){
            r.makeVisible();
        }
    }
    
    public void makeInvisible()
    {
        for (Rectangle r: cupRectangles){
            r.makeInvisible();
        }
    }
    
    public void setLid(Lid lid)
    {
        this.ofLid = lid;
    }   
    
    public Lid getLid(){
        return ofLid;
    }
    
    public int getCupHeightPx(){
        return cupHeightPx;
    }
}