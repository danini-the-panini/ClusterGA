public abstract class Chromosome
{
    public abstract float calculateFitness();
    
    public static abstract class ReproductionAgent<CHR extends Chromosome>
    {
        public abstract int getNumRequiredParents();
        
        public abstract CHR[] reproduce(Chromosome[] parents);
    }
    
    public static abstract class MutationAgent<CHR extends Chromosome>
    {
        public abstract void mutate(CHR victim);
    }
    
    public static abstract class PopulationGenerator<CHR extends Chromosome>
    {
        public abstract CHR[] generate();
    }

}
