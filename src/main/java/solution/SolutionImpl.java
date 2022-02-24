package solution;

import java.util.List;

public class SolutionImpl implements Solution {

    private final InputAdapter inputAdapter;
    private final List<Assign> assigns;

    public SolutionImpl(InputAdapter inputAdapter, List<Assign> assigns) {
        this.inputAdapter = inputAdapter;
        this.assigns = assigns;
    }

    @Override
    public long score() {

        return 0;
    }
}
