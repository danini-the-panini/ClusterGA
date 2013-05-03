public abstract class CentroidGenerator
{

    protected DataSet dataSet;
    
    public CentroidGenerator(DataSet dataSet)
    {
        this.dataSet = dataSet;
    }
    
    public abstract float[] generateCluster();
    
    public static class Random extends CentroidGenerator
    {

        public Random(DataSet dataSet) {
            super(dataSet);
        }

        @Override
        public float[] generateCluster() {

            float[] cluster = new float[dataSet.getNumFeatures()];

            float min, max;
            for (int i = 0; i < cluster.length; i++)
            {
                min = dataSet.getMin(i);
                max = dataSet.getMax(i);
                cluster[i] = (float)(Math.random()*(max-min)+min);
            }

            return cluster;
        }



    }
    
    public static class Shuffle extends CentroidGenerator
    {
        int[] indices;
        int currentIndex = 0;

        public Shuffle(DataSet data)
        {
            super(data);
            indices = new int[data.getNumPatterns()];

            for (int i = 0; i < indices.length; i++)
            {
                indices[i] = i;
            }
            int temp, random;
            for (int i = 0; i < indices.length; i++)
            {
                random = (int)(Math.random()*indices.length);
                temp = indices[random];
                indices[random] = indices[i];
                indices[i] = temp;
            }
        }

        @Override
        public float[] generateCluster()
        {
            float[] cluster = dataSet.clonePattern(currentIndex++);
            currentIndex %= indices.length;
            return cluster;
        }

    }

    
}
