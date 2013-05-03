
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;


public class PlotPicker extends Canvas
{
    public static ArrayList<Point> points = new ArrayList<Point>();

    @Override
    public void paint(Graphics g) {
        
        g.setColor(Color.WHITE);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.BLACK);
        
        for (Point p: points)
        {
            g.fillRect(p.x-1, p.y-1, 2, 2);
        }
        
    }
    
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("PlotPicker");
        
        final PlotPicker picker = new PlotPicker();
        
        MouseAdapter mouse = new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());
                System.out.println(e.getX() +"\t" + e.getY());
                picker.repaint();
            }
            
        };
        picker.addMouseListener(mouse);
        
        frame.add(picker);
        
        frame.setSize(800,600);
        
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
