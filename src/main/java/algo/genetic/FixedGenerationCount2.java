package algo.genetic;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

public class FixedGenerationCount2 implements StoppingCondition {
    private int numGenerations = 0;
    private final int maxGenerations;

    public FixedGenerationCount2(int maxGenerations) throws NumberIsTooSmallException {
        if (maxGenerations <= 0) {
            throw new NumberIsTooSmallException(maxGenerations, 1, true);
        } else {
            this.maxGenerations = maxGenerations;
        }
    }

    public boolean isSatisfied(Population population) {
        if (this.numGenerations < this.maxGenerations) {
            ++this.numGenerations;
            return false;
        } else {
            return true;
        }
    }

    public int getMaxGenerations() {
        return this.maxGenerations;
    }

    public int getNumGenerations() {
        return this.numGenerations;
    }
}
