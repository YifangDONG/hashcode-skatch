package solution;

import java.util.List;

public interface Solution {
    // default impl is done in the interface to be able to use the logging
    long score( List<Assign> assigns);
}
