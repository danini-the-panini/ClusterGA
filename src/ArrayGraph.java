
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class ArrayGraph extends Canvas
{
    private Color colour;
    private float[] array;
    
    private float max = Float.NEGATIVE_INFINITY, min = 0, origin;
    private float rangeX, rangeY, stepX, stepY;

    public ArrayGraph(Color colour, float[] array) {
        this.colour = colour;
        this.array = array;
        
        for (int i = 0; i < array.length; i++)
        {
            max = Math.max(max,array[i]);
            min = Math.min(min,array[i]);
        }
        
        rangeY = max-min;
        rangeX = array.length;
    }

    @Override
    public void paint(Graphics g) {
        
        g.setColor(Color.WHITE);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        
        
        stepX = rangeX/(float)getWidth();
        stepY = rangeY/(float)getHeight();
        
        g.setColor(Color.BLACK);
        
        // draw origin
        g.drawLine(0, Y(0), getWidth(), Y(0));
        
        g.setColor(colour);
        
        // draw graph thing
        for (int i = 0; i < array.length-1; i++)
        {
            g.drawLine(X(i), Y(array[i]), X(i+1), Y(array[i+1]));
        }
        
    }
        
    int X(float x)
    {
        return (int)((x)/stepX);
    }

    int Y(float y)
    {
        return getHeight() - (int)((y-min)/stepY);
    }
    
}
