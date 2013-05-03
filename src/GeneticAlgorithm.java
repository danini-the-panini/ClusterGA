public class GeneticAlgorithm<CHR extends Chromosome>
{
    private Chromosome.ReproductionAgent<CHR> repAgent;
    private Chromosome.MutationAgent<CHR> mutAgent;
    private ChromosomeSelector<CHR> selector;
    
    private CHR[] population;
    
    public GeneticAlgorithm(
            Chromosome.PopulationGenerator<CHR> popGen,
            Chromosome.ReproductionAgent<CHR> repAgent,
            Chromosome.MutationAgent<CHR> mutAgent,
            ChromosomeSelector<CHR> selector)
    {
        this.repAgent = repAgent;
        this.mutAgent = mutAgent;
        this.selector = selector;
        
        population = popGen.generate();
    }
    
    public CHR[] selectParents(int n)
    {
        CHR[] parents = (CHR[])(new Chromosome[n]);
        
        for (int i = 0; i < n; i++)
        {
            parents[i] = selector.select(population);
        }
        
        return parents;
    }

    public CHR[] getPopulation() {
        return population;
    }
    
    public CHR getBest()
    {
        CHR best = null;
        float bestFit = Float.NEGATIVE_INFINITY;
        
        for (int i = 0; i < population.length; i++)
        {
            float fit = population[i].calculateFitness();
            if (fit > bestFit)
            {
                bestFit = fit;
                best = population[i];
            }
        }
        
        return best;
    }
    
    public void nextGeneration(float mutationProbability)
    {
        
        CHR[] newPop = (CHR[])(new Chromosome[population.length]);
        
        // elitism ( keep best parent from current generation )
        newPop[0] = getBest();
        
        // children
        int newPopSize = 1;
        
        while (newPopSize < newPop.length)
        {   
            CHR[] parents = selectParents(repAgent.getNumRequiredParents());
            CHR[] children = repAgent.reproduce(parents);
            
            for (int i = 0; i < children.length && i+newPopSize < newPop.length; i++)
            {
                if (Math.random() < mutationProbability)
                {
                    mutAgent.mutate(children[i]);
                }
                
                newPop[i+newPopSize] = children[i];
            }
            
            newPopSize += children.length;
        }
        
        population = newPop;
    }
    
}
