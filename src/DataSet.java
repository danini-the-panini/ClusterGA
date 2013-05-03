import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataSet
{
    private ArrayList<float[]> data = new ArrayList<float[]>();
    
    private float[] min;
    private float[] max;
    
    private int numFeatures;
    
    public static DataSet importFromFile(String filename)
            throws IOException
    {
        DataSet data = null;
        
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try
        {
            
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] tokens = line.split("\t");
                if (data == null) data = new DataSet(tokens.length);
                float[] pattern = new float[tokens.length];
                
                for (int i = 0; i < pattern.length; i++)
                {
                    pattern[i] = Float.parseFloat(tokens[i]);
                    data.max[i] = Math.max(data.max[i], pattern[i]);
                    data.min[i] = Math.min(data.min[i], pattern[i]);
                }
                
                data.insert(pattern);
            }
            
        }
        finally 
        {
            br.close();
        }
        return data;
    }
    
    public DataSet(int numFeatures)
    {
        this.numFeatures = numFeatures;
        min = new float[numFeatures];
        max = new float[numFeatures];
        for (int i = 0; i < numFeatures; i++)
        {
            min[i] = Float.POSITIVE_INFINITY;
            max[i] = Float.NEGATIVE_INFINITY;
        }
    }
    
    public void insert(float[] pattern)
    {
        if (pattern.length != numFeatures)
        {
            throw new IllegalArgumentException("Feature Count Mismatch.");
        }
        
        data.add(pattern);
    }
    
    public int[] getClassification(float[][] centroids)
    {
        int[] classification = new int[data.size()];
        float dist, temp;
        for (int pat = 0; pat < classification.length; pat++)
        {
            dist = Float.POSITIVE_INFINITY;
            for (int cent = 0; cent < centroids.length; cent++)
            {
                temp = distanceSquared(data.get(pat),centroids[cent]);
                if (temp < dist)
                {
                    dist = temp;
                    classification[pat] = cent;
                }
            }
        }
        return classification;
    }
    
    public Canvas getVisualRepresentation(float[][] centroids)
    {
        if (numFeatures != 2) return null;
        
        return new VisualRepresentation(centroids);
    }
    
    private class VisualRepresentation extends Canvas
    {
        int[] classification;
        float[][] centroids;
        
        float rangeX, rangeY, stepX, stepY;
        
        VisualRepresentation(float[][] centroids)
        {
            this.centroids = centroids;
            
            classification = getClassification(centroids);
            
            rangeX = max[0]-min[0];
            rangeY = max[1]-min[1];
        }

        @Override
        public void paint(Graphics g)
        {
            g.setColor(Color.WHITE);
            
            g.fillRect(0, 0, getWidth(), getHeight());
            
            
            stepX = rangeX/(float)getWidth();
            stepY = rangeY/(float)getHeight();
            
            float[] p,c;
            
            g.setColor(new Color(0.8f,0.8f,0.8f));
            
            for (int i = 0; i < data.size(); i++)
            {
                p = data.get(i);
                c = centroids[classification[i]];
                
                g.drawLine(X(p[0]), Y(p[1]), X(c[0]), Y(c[1]));
            }
            
            g.setColor(Color.RED);
            
            for (int i = 0; i < data.size(); i++)
            {
                p = data.get(i);
                
                g.fillRect(X(p[0])-2, Y(p[1])-2, 4, 4);
            }
            
            g.setColor(Color.BLUE);
            
            for (int i = 0; i < centroids.length; i++)
            {
                c = centroids[i];
                
                g.fillRect(X(c[0])-3, Y(c[1])-3, 6, 6);
            }
        }
        
        int X(float x)
        {
            return (int)((x-min[0])/stepX);
        }
        
        int Y(float y)
        {
            return getHeight() - (int)((y-min[1])/stepY);
        }
        
    }
    
    /**
     * Calculates the distance squared between a given centroid and the specified pattern.
     * @param centroid The centroid.
     * @param i The index of the pattern in this data set.
     * @return The distance squared between the centroid and the specified pattern.
     */
    public float getDistanceSquaredFrom(float[] centroid, int i)
    {
        return distanceSquared(centroid, data.get(i));
    }
    
    public float getDistanceSquaredBetween(int a, int b)
    {
        return distanceSquared(data.get(a), data.get(b));
    }
    
    /**
     * Calculates the distance squared between two vectors.
     * @param a
     * @param b
     * @return The distance squared between a and b.
     */
    public static float distanceSquared(float[] a, float[] b)
    {
        float distSq = 0;
        float temp;
        for (int i = 0; i < Math.min(a.length, b.length); i++)
        {
            temp = a[i] - b[i];
            distSq += temp * temp;
        }
        return distSq;
    }
    
    public float getMin(int i)
    {
        return min[i];
    }
    
    public float getMax(int i)
    {
        return max[i];
    }

    public int getNumFeatures() {
        return numFeatures;
    }
    
    public int getNumPatterns()
    {
        return data.size();
    }
    
    public float[] clonePattern(int i)
    {
        float[] copy = new float[numFeatures];
        
        System.arraycopy(data.get(i),0,copy,0,numFeatures);
        
        return copy;
    }
    
}
