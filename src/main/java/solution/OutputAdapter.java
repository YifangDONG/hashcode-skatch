package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutputAdapter {
    private List<Assign> assigns;

    // inject the result to adapt
    public OutputAdapter(List<Assign> assigns) {
        this.assigns = assigns;
    }

    public List<List<String>> adapt() {
        var results = new ArrayList<List<String>>();
        results.add(List.of(String.valueOf(assigns.size())));
        for (int i = 0; i < assigns.size(); i++) {
            results.add(List.of(assigns.get(i).projectName()));
            results.add(assigns.get(i).people());
        }
        return results;
    }
}
