import java.awt.*;
import java.io.Serializable;

public class Line implements Serializable {
    private Color colour;
    private int thickness;
    private int x1,y1, x2,y2;
    public Line(Color c, int thickness, int x1, int y1, int x2, int y2){
        colour = c;
        this.thickness = thickness;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Color getColour(){
        return colour;
    }
    public int getThickness(){
        return thickness;
    }
    public int[] getStart(){
        int[] initial = {x1,y1};
        return initial;
    }
    public int[] getEnd(){
        int[] endpoint = {x2,y2};
        return endpoint;
    }
}
