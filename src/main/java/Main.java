import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import collection.ListUtils;
import io.Input;
import io.Output;
import solution.InputAdapter;
import solution.OutputAdapter;
import solution.Project;
import solution.Skill;
import solution.SolutionGreedy;
import solution.SolutionImpl;
import summary.Case;
import summary.Summary;

public class Main {

    private static final List<Case> CASES = List.of(Case.a, Case.b, Case.c, Case.d, Case.e, Case.f);
    private static final Input INPUT = new Input("src\\main\\resources\\in\\");
    private static final Output OUTPUT = new Output("src\\main\\resources\\out\\");
    private static final Input READ_OUTPUT = new Input("src\\main\\resources\\out\\");

    public static void main(String[] args) {

        //        testExampleA();
        //        execute();
                executeCase(Case.f);
//        analyse(Case.c);
                getResultSummary();
    }

    private static void analyse(Case f) {
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(f.name()));
        Map<String, Integer> uniqueSkills = uniqueSkills(inputAdapter);
        var daysHist = daysHist(inputAdapter);
        System.out.println(daysHist);
        System.out.println(uniqueSkills);
    }

    private static Map<Integer, Integer> daysHist(InputAdapter inputAdapter) {
        return ListUtils.valueCountInt(inputAdapter.getProjects().stream().map(Project::days).collect(Collectors.toList()));
    }

    private static Map<String, Integer> uniqueSkills(InputAdapter inputAdapter) {
        var uniqueSkills = inputAdapter.getProjects()
            .stream()
            .collect(Collectors.toMap(
                Project::name,
                project -> project.skills().stream().map(Skill::type).collect(Collectors.toSet()).size()
            ));
        return uniqueSkills;
    }

    private static void testExampleA() {
        // adapt to output model
        InputAdapter inputAdapter = new InputAdapter(INPUT.read(Case.a.name()));
        InputAdapter resultAdapter = new InputAdapter(READ_OUTPUT.read("a\\0"));
        // calculate the score to test the score function is correct
        var people = inputAdapter.getPeople();
        var projects = inputAdapter.getProjects();
        var assigns = resultAdapter.getAssigns();

        var solution = new SolutionImpl(inputAdapter);
        var score = solution.score(assigns);
        System.err.println(score);
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

        // calculate score
        var solutionGreedy = new SolutionGreedy(inputAdapter);
        var solution = new SolutionImpl(inputAdapter);

        var maxReward = Comparator.comparingInt(Project::reward).reversed();
        var minSkillLevel = Comparator.<Project>comparingInt(project ->
            project.skills().stream().mapToInt(Skill::level).sum() / project.skills().size());
        var minSkillSize = Comparator.<Project>comparingInt(project -> project.skills().size());

        var result = solution.simulationForE(1000);
        long score = solution.score(result);
        System.err.println("score = " + score);

        // adapt score to output
        var outputAdapter = new OutputAdapter(result);
        List<List<String>> outputContent = outputAdapter.adapt();

        // write output
        int count = lastResult.count() + 1;
        OUTPUT.write(String.format("%s\\%d", aCase.name(), count), outputContent);
        Summary.addResult(aCase, count, score);
    }

}
