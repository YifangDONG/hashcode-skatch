import io.Input;
import io.Output;
import logging.LogObjectFactory;
import solution.*;
import summary.Case;
import summary.Summary;

import java.util.List;
import java.util.Map;

public class Main {

    private static final int LOOP = 1;
    private static final List<Case> CASES = List.of(Case.a, Case.b, Case.c, Case.d, Case.e);
    private static final Input INPUT = new Input("src\\main\\resources\\in\\");
    private static final Output OUTPUT = new Output("src\\main\\resources\\out\\");
    private static final Input READ_OUTPUT = new Input("src\\main\\resources\\out\\");

    public static void main(String[] args) {

//        testExampleA();
//        execute();
//        executeCase(Case.e);
//        getResultSummary();
//        scoreLimit();
        outputStatic();
    }

    private static void scoreLimit() {
        for (Case aCase : CASES) {
            InputAdapter inputAdapter = new InputAdapter(INPUT.read(aCase.name()));
            int score = 0;
            int bonus = inputAdapter.getBonus();
            List<Ride> rides = inputAdapter.getRides();
            score += bonus * rides.size();
            for (Ride ride : rides) {
                score += ride.start().distance(ride.end());
            }
            System.out.println(aCase.name() + " : " + score);
        }
    }

    private static void outputStatic() {
        // adapt to output model
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(Case.e.name()));
        Map<Integer, Ride> idToRide = inputAdapter.getIdToRide();
        InputAdapter resultAdapter = new InputAdapter(READ_OUTPUT.read("e\\12"));
        List<Assign> assigns = resultAdapter.readOutput(idToRide);
        Integer totalRides = assigns.stream()
                .map(assign -> assign.rides().size())
                .reduce(0, Integer::sum);
        System.out.println(totalRides);
    }

    private static void testExampleA() {
        // adapt to output model
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(Case.a.name()));
        int nSteps = inputAdapter.getNSteps();
        int nVehicles = inputAdapter.getNVehicles();
        int bonus = inputAdapter.getBonus();
        Map<Integer, Ride> idToRide = inputAdapter.getIdToRide();
        InputAdapter resultAdapter = new InputAdapter(READ_OUTPUT.read("a\\0"));
        List<Assign> assigns = resultAdapter.readOutput(idToRide);

        // calculate the score to test the score function is correct
        SolutionImpl solution = new SolutionImpl();
        int score = solution.score(assigns, nSteps, nVehicles, bonus);
        System.out.println(score);
    }

    private static void getResultSummary() {
        for (Case aCase : CASES) {
            System.out.println(Summary.getBestScore(aCase));
        }
    }

    private static void execute() {
        for (Case aCase : CASES) {
            executeCase(aCase);
        }
    }

    private static void executeCase(Case aCase) {

        var lastResult = Summary.getLastResult(aCase);

        // read input
        List<List<String>> content = INPUT.read(aCase.name());

        // adapt input to model
        InputAdapter inputAdapter = new InputAdapter(content);
        List<Ride> rides = inputAdapter.getRides();
        int nVehicles = inputAdapter.getNVehicles();
        int nSteps = inputAdapter.getNSteps();
        int bonus = inputAdapter.getBonus();

        // calculate score
        Solution solution = LogObjectFactory.create(new SolutionImpl(), Solution.class);
        List<Assign> result = solution.minWastTime(rides, nSteps, nVehicles, bonus);

        long score = solution.score(result, nSteps, nVehicles, bonus);
        int count = lastResult.count() + 1;
        // adapt score to output

        // write output
        List<List<String>> outContent = OutputAdapter.adapt(result);
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), outContent);
        Summary.addResult(aCase, count, score);
    }

}
