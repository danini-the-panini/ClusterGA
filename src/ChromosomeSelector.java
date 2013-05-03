public abstract class ChromosomeSelector<CHR extends Chromosome>
{
    
    public abstract CHR select(CHR[] list);
    
    public static class Roulette<CHR extends Chromosome> extends ChromosomeSelector<CHR>
    {

        @Override
        public CHR select(CHR[] list)
        {
            // calculate the fitness of each chromosome.
            // add up a total so as to "normalize" the chromosomes.
            // (i.e. make sure they all add up to 1)
            float[] prob = new float[list.length];
            float total = 0;

            for (int i = 0; i < list.length; i++)
            {
                prob[i] = list[i].calculateFitness();
                total += prob[i];
            }

            // Now use "Roulette" to pick random chromosome.

            int n = 0;
            float sum = prob[n]/total;

            float epsilon = (float)Math.random();

            while (sum < epsilon)
            {
                n++;
                sum += prob[n]/total;
            }

            return list[n];
        }
    }
    
    public static class Tournament<CHR extends Chromosome> extends ChromosomeSelector<CHR>
    {
        private int size;

        public Tournament(int size) {
            this.size = size;
        }

        @Override
        public CHR select(CHR[] list) {

            // shuffle list
            // TODO: is this a good idea, modifying the list?
            // alternate option is to shuffle indices and pick those...
            for (int i = 0; i < list.length; i++)
            {
                int random = (int)(Math.random()*list.length);
                CHR temp = list[random];
                list[random] = list[i];
                list[i] = temp;
            }

            // We now select the best chromosome out of the first "size" chromosomes
            // in the list.

            float bestFitness = Float.NEGATIVE_INFINITY, fitness;
            CHR bestChr = null;

            for (int i = 0; i < size; i++)
            {
                fitness = list[i].calculateFitness();
                if (fitness > bestFitness)
                {
                    bestChr = list[i];
                    bestFitness = fitness;
                }
            }

            return bestChr;
        }
    }

    
}
