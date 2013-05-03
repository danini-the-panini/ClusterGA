
import java.util.Random;

public class ClusterChromosome extends Chromosome
{
    
    private DataSet dataSet;
    
    private float[][] centroids;
    
    public ClusterChromosome(DataSet dataSet, int numClusters)
    {
        this.dataSet = dataSet;
        centroids = new float[numClusters][];
    }

    @Override
    public float calculateFitness()
    {
        // return negative error (less is more)
        return -getQuatizationError();
    }
    
    public float getQuatizationError()
    {
        int[] classification = dataSet.getClassification(centroids);
        
        float error = 0; // quantization error (TODO: I think)
        
        for (int i = 0; i < classification.length; i++)
        {
            error += dataSet.getDistanceSquaredFrom(centroids[classification[i]], i);
        }
        
        // return average error.
        return error/classification.length;
    }
    
    // average distance between any two points in a cluster
    public float getIntraClusterDistance()
    {
        int[] classification = dataSet.getClassification(centroids);
        
        float distance = 0;
        
        int numConnections = 0;
        
        for (int i = 0; i < classification.length-1; i++)
        {
            for (int j = i+1; j < classification.length; j++)
            {
                if (classification[i] != classification[j]) continue;
                distance += dataSet.getDistanceSquaredBetween(i, j);
                numConnections ++;
            }
        }
        
        return distance/numConnections;
    }
    
    // average distance between any two centroids
    public float getInterClusterDistance()
    {
        float distance = 0;
        
        for (int i = 0; i < centroids.length-1; i++)
        {
            for (int j = i+1; j < centroids.length; j++)
            {
                distance += DataSet.distanceSquared(centroids[i], centroids[j]);
            }
        }
        
        return distance / ((centroids.length * (centroids.length-1))/2);
    }
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    public int getNumCentroids()
    {
        return centroids.length;
    }

    public float[][] getCentroids() {
        return centroids;
    }
    
    public static class CentroidPopulationGenerator extends Chromosome.PopulationGenerator<ClusterChromosome>
    {
        
        private DataSet dataSet;
        private int numCentroids;
        private CentroidGenerator generator;
        private int size;

        public CentroidPopulationGenerator(DataSet dataSet, int numCentroids, CentroidGenerator generator, int size) {
            this.dataSet = dataSet;
            this.numCentroids = numCentroids;
            this.generator = generator;
            this.size = size;
        }

        @Override
        public ClusterChromosome[] generate() {
            
            ClusterChromosome[] population = new ClusterChromosome[size];
            
            for (int i = 0; i < population.length; i++)
            {
                population[i] = new ClusterChromosome(dataSet, numCentroids);
                
                for (int j = 0; j < population[i].centroids.length; j++)
                {
                    population[i].centroids[j] = generator.generateCluster();
                }
            }
            
            return population;
            
        }
        
    }
    
    public static class OnePointCrossover extends Chromosome.ReproductionAgent<ClusterChromosome>
    {

        @Override
        public int getNumRequiredParents() {
            return 2;
        }


        @Override
        public ClusterChromosome[] reproduce(Chromosome[] cparents)
        {
            if (cparents.length != 2)
                throw new IllegalArgumentException("Only two parents may participate in reproduction");
            
            ClusterChromosome[] parents = new ClusterChromosome[]{(ClusterChromosome)cparents[0],(ClusterChromosome)cparents[1]};
            
            if (parents[0].dataSet != parents[1].dataSet || parents[0].getNumCentroids() != parents[1].getNumCentroids())
                throw new IllegalArgumentException("Parents are not of same species.");
            
            ClusterChromosome[] children = new ClusterChromosome[2];
            children[0] = new ClusterChromosome(parents[0].dataSet, parents[0].getNumCentroids());
            children[1] = new ClusterChromosome(parents[0].dataSet, parents[0].getNumCentroids());
            
            // pick a random crossover point
            int n = (int)(Math.random()*parents[0].getNumCentroids());
            
            // get first n centroids from one parent...
            for (int i = 0; i < n; i++)
            {
                children[0].centroids[i] = new float[parents[0].getDataSet().getNumFeatures()];
                System.arraycopy(parents[0].centroids[i], 0, children[0].centroids[i], 0, children[0].dataSet.getNumFeatures());
                
                children[1].centroids[i] = new float[parents[1].getDataSet().getNumFeatures()];
                System.arraycopy(parents[1].centroids[i], 0, children[1].centroids[i], 0, children[1].dataSet.getNumFeatures());
            }
            
            // get the rest from other parent
            for (int i = n; i < parents[0].getNumCentroids(); i++)
            {
                children[0].centroids[i] = new float[parents[1].getDataSet().getNumFeatures()];
                System.arraycopy(parents[1].centroids[i], 0, children[0].centroids[i], 0, children[0].dataSet.getNumFeatures());
                
                children[1].centroids[i] = new float[parents[0].getDataSet().getNumFeatures()];
                System.arraycopy(parents[0].centroids[i], 0, children[1].centroids[i], 0, children[1].dataSet.getNumFeatures());
            }
            
            return children;
        }
        
    }
    
    public static class Average extends Chromosome.ReproductionAgent<ClusterChromosome>
    {

        @Override
        public int getNumRequiredParents() {
            return 2;
        }
        
        @Override
        public ClusterChromosome[] reproduce(Chromosome[] cparents)
        {
            if (cparents.length != 2)
                throw new IllegalArgumentException("Only two parents may participate in reproduction");
            
            ClusterChromosome[] parents = new ClusterChromosome[]{(ClusterChromosome)cparents[0],(ClusterChromosome)cparents[1]};
            
            if (parents[0].dataSet != parents[1].dataSet || parents[0].getNumCentroids() != parents[1].getNumCentroids())
                throw new IllegalArgumentException("Parents are not of same species.");
            
            ClusterChromosome child = new ClusterChromosome(parents[0].dataSet, parents[1].getNumCentroids());
            
            for (int i = 0; i < child.centroids.length; i++)
            {
                child.centroids[i] = new float[parents[0].getDataSet().getNumFeatures()];
                for (int j = 0; j < child.centroids[i].length; j++)
                {
                    child.centroids[i][j] = (parents[0].centroids[i][j] + parents[1].centroids[i][j])/2.0f;
                }
            }
            
            return new ClusterChromosome[]{child};
        }
        
    }
    
    public static class Swap extends Chromosome.MutationAgent<ClusterChromosome>
    {
        @Override
        public void mutate(ClusterChromosome victim) {
            
            // pick two random centroids
            int centroidA = (int)(Math.random() * victim.getNumCentroids());
            int centroidB = (int)(Math.random() * (victim.getNumCentroids()-1));
            if (centroidB >= centroidA) centroidB++;
            
            // pick a random feature
            int feature = (int)(Math.random() * victim.getDataSet().getNumFeatures());
            
            // swap the features
            float temp = victim.centroids[centroidA][feature];
            victim.centroids[centroidA][feature] = victim.centroids[centroidB][feature];
            victim.centroids[centroidB][feature] = temp;
            
        }
        
    }
    
    public static class GaussianNoise extends Chromosome.MutationAgent<ClusterChromosome>
    {

        @Override
        public void mutate(ClusterChromosome victim) {
            
            Random random = new Random();
            
            for (int i = 0; i < victim.getNumCentroids(); i++)
            {
                for (int j = 0; j < victim.getDataSet().getNumFeatures(); j++)
                {
                    victim.centroids[i][j] += random.nextGaussian()*0.1f;
                }
            }
            
        }
        
    }
    
}
