
import java.awt.Canvas;
import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2)
        {
            System.out.println("Please specify a path to the data set and the desired number of clusters.\n");
            System.exit(-1);
        }
        
        int numClusters = 0;
        try
        {
            numClusters = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("Desired number of clusters must be a valid integer value.");
            System.exit(-1);
        }
        
        String filename = args[0];
        
        DataSet dataSet = null;
        
        try
        {
            dataSet = DataSet.importFromFile(filename);
        }
        catch (IOException ioe)
        {
            System.out.println("Error reading file: " + ioe.getMessage());
            System.exit(-1);
        }
        
        Chromosome.ReproductionAgent<ClusterChromosome> repAgent = null;
        Chromosome.MutationAgent<ClusterChromosome> mutAgent = null;
        ChromosomeSelector<ClusterChromosome> selector = null;
        CentroidGenerator centGen = null;
        
        Scanner in = new Scanner(System.in);
        
        boolean ready = false;
        
        System.out.println("Choose a Centroid Generation Algorithm:");
        System.out.println("\t1. Random centroids from within data set bounds.");
        System.out.println("\t2. Pick random points from data set at centroids.");
        while (!ready)
        {
            System.out.print("> ");

            ready = true;
            
            switch (in.nextInt())
            {
                case 1:
                    centGen = new CentroidGenerator.Random(dataSet);
                    break;
                case 2:
                    centGen = new CentroidGenerator.Shuffle(dataSet);
                    break;
                default:
                    System.out.println("Invalid Input.");
                    ready = false;
                    break;
            }
        }
        
        ready = false;
        System.out.println("Choose a Parent Selector:");
        System.out.println("\t1. Roulette.");
        System.out.println("\t2. Tournament.");
        while (!ready)
        {
            System.out.print("> ");

            ready = true;
            
            switch (in.nextInt())
            {
                case 1:
                    selector = new ChromosomeSelector.Roulette<ClusterChromosome>();
                    break;
                case 2:
                    System.out.println("What will be the tournament size? ");
                    System.out.print("> ");
                    selector = new ChromosomeSelector.Tournament<ClusterChromosome>(in.nextInt());
                    break;
                default:
                    System.out.println("Invalid Input.");
                    ready = false;
                    break;
            }
        }
        
        System.out.println("Population Size? ");
        System.out.print("> ");
        int popSize = in.nextInt();
        
        System.out.println("How many Iterations? ");
        System.out.print("> ");
        int maxIterations = in.nextInt();
        
        ready = false;
        System.out.println("Choose a Reproduction Agent:");
        System.out.println("\t1. One-Point Crossover.");
        System.out.println("\t2. Average of parents.");
        while (!ready)
        {
            System.out.print("> ");

            ready = true;
            
            switch (in.nextInt())
            {
                case 1:
                    repAgent = new ClusterChromosome.OnePointCrossover();
                    break;
                case 2:
                    repAgent = new ClusterChromosome.Average();
                    break;
                default:
                    System.out.println("Invalid Input.");
                    ready = false;
                    break;
            }
        }

	ready = false;
	System.out.println("What will be the reproduction probabiliy? ");
        System.out.print("> ");
	float reproductionProbablity = in.nextFloat();
	if (reproductionProbablity < 0.0f)
	{
		System.out.println("Clamping to [0,1]");
		reproductionProbablity = 0.0f;
	}
	else if (reproductionProbablity > 1.0f)
	{
		System.out.println("Clamping to [0,1]");
		reproductionProbablity = 1.0f;
	}

        
        ready = false;
        System.out.println("Choose a Mutation Agent:");
        System.out.println("\t1. Swap.");
        System.out.println("\t2. Gaussian Noise.");
        while (!ready)
        {
            System.out.print("> ");

            ready = true;
            
            switch (in.nextInt())
            {
                case 1:
                    mutAgent = new ClusterChromosome.Swap();
                    break;
                case 2:
                    mutAgent = new ClusterChromosome.GaussianNoise();
                    break;
                default:
                    System.out.println("Invalid Input.");
                    ready = false;
                    break;
            }
        }
        
        
        GeneticAlgorithm<ClusterChromosome> ga = new GeneticAlgorithm<ClusterChromosome>(
                new ClusterChromosome.CentroidPopulationGenerator(
                    dataSet,
                    numClusters,
                    centGen,
                    popSize),
                repAgent,
                mutAgent,
                selector);
        
        // statistics...
        float[] interConnections = new float[maxIterations+1];
        float[] intraConnections = new float[maxIterations+1];
        float[] quantizationErrors = new float[maxIterations+1];
        
        float mutationProbability = 1.0f;
        
        float percentDone = 0.0f;
        float percent = 0.0f;
        
        // commence genetic algorithm!
        for (int i = 0; i < maxIterations; i++)
        {
            ClusterChromosome best = ga.getBest();
            
            // collect stats
	    interConnections[i] = best.getInterClusterDistance();
            intraConnections[i] = best.getIntraClusterDistance();
	    quantizationErrors[i] = best.getQuatizationError();
            
            // perform GA
            ga.nextGeneration(reproductionProbablity, mutationProbability);
            
            mutationProbability -= 0.99f/maxIterations;
            
            percentDone += 100.0f/(float)maxIterations;
            percent += 100.0f/(float)maxIterations;
            if (percent > 5.0f)
            {
                percent -= 5.0f;
                System.out.print(Math.round(percentDone) + "% ... ");
            }
        }
        System.out.println("DONE!");
        
        ClusterChromosome best = ga.getBest();

        // collect stats
        interConnections[maxIterations] = best.getInterClusterDistance();
        intraConnections[maxIterations] = best.getIntraClusterDistance();
        quantizationErrors[maxIterations] = best.getQuatizationError();
        
        System.out.println("Inter-Cluster Distance: " + interConnections[maxIterations]);
        System.out.println("Intra-Cluster Distance: " + intraConnections[maxIterations]);
        System.out.println("Quantization Errors: " + quantizationErrors[maxIterations]);
        
        Canvas canvas = dataSet.getVisualRepresentation(best.getCentroids(), 20);
        
        JFrame frame = new JFrame("DataSet: " + filename);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane view = new JTabbedPane();
        
        if (canvas != null)
        {
            view.addTab("Clustering", canvas);
        }
        else   
        {
            System.out.println("Centroids:");
            float[][] centroids = best.getCentroids();
            for (int i = 0; i < centroids.length; i++)
            {
                for (int j = 0; j < centroids[i].length; j++)
                {
                    System.out.print(centroids[i][j]+"\t");
                }
                System.out.println();
            }
        }
        
        Canvas[] canvases = new Canvas[]{
            new ArrayGraph(Color.blue, interConnections, 1, 20),
            new ArrayGraph(Color.red, intraConnections, 1, 20),
            new ArrayGraph(Color.orange, quantizationErrors, 1, 20)
        };
        String[] canvasNames = new String[]{
            "Inter Connection", "Intra Connection", "Quantization Error"
        };
        
        for (int i = 0; i < canvases.length; i++)
        {
            view.addTab(canvasNames[i], canvases[i]);
        }
        
        frame.add(view);
        
	frame.setSize(new Dimension(800,600));
        frame.setVisible(true);
    }
}
