package algo.genetic;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.*;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class TimedGeneticAlgo {

    private static RandomGenerator randomGenerator = new JDKRandomGenerator();
    private final CrossoverPolicy crossoverPolicy;
    private final double crossoverRate;
    private final MutationPolicy mutationPolicy;
    private final double mutationRate;
    private final SelectionPolicy selectionPolicy;
    private int generationsEvolved = 0;

    public TimedGeneticAlgo(CrossoverPolicy crossoverPolicy, double crossoverRate, MutationPolicy mutationPolicy, double mutationRate, SelectionPolicy selectionPolicy) throws OutOfRangeException {
        if (!(crossoverRate < 0.0D) && !(crossoverRate > 1.0D)) {
            if (!(mutationRate < 0.0D) && !(mutationRate > 1.0D)) {
                this.crossoverPolicy = crossoverPolicy;
                this.crossoverRate = crossoverRate;
                this.mutationPolicy = mutationPolicy;
                this.mutationRate = mutationRate;
                this.selectionPolicy = selectionPolicy;
            } else {
                throw new OutOfRangeException(LocalizedFormats.MUTATION_RATE, mutationRate, 0, 1);
            }
        } else {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, crossoverRate, 0, 1);
        }
    }

    public static synchronized void setRandomGenerator(RandomGenerator random) {
        randomGenerator = random;
    }

    public static synchronized RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    public Population evolve(Population initial, FixedGenerationCount2 condition) {
        Population current = initial;
        try(ProgressBar progress = new ProgressBar("evolve", condition.getMaxGenerations());) {
            for(this.generationsEvolved = 0; !condition.isSatisfied(current); ++this.generationsEvolved) {
                progress.step();
                current = this.nextGeneration(current);
            }
        }
        return current;
    }

    public Population nextGeneration(Population current) {
        Population nextGeneration = current.nextGeneration();
        RandomGenerator randGen = getRandomGenerator();

        while(nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
            ChromosomePair pair = this.getSelectionPolicy().select(current);
            if (randGen.nextDouble() < this.getCrossoverRate()) {
                pair = this.getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
            }

            if (randGen.nextDouble() < this.getMutationRate()) {
                pair = new ChromosomePair(this.getMutationPolicy().mutate(pair.getFirst()), this.getMutationPolicy().mutate(pair.getSecond()));
            }

            nextGeneration.addChromosome(pair.getFirst());
            if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
                nextGeneration.addChromosome(pair.getSecond());
            }
        }

        return nextGeneration;
    }

    public CrossoverPolicy getCrossoverPolicy() {
        return this.crossoverPolicy;
    }

    public double getCrossoverRate() {
        return this.crossoverRate;
    }

    public MutationPolicy getMutationPolicy() {
        return this.mutationPolicy;
    }

    public double getMutationRate() {
        return this.mutationRate;
    }

    public SelectionPolicy getSelectionPolicy() {
        return this.selectionPolicy;
    }

    public int getGenerationsEvolved() {
        return this.generationsEvolved;
    }
}
