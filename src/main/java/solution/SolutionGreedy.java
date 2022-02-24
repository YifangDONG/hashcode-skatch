package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;

public class SolutionGreedy {

    private InputAdapter inputAdapter;

    public SolutionGreedy(InputAdapter inputAdapter) {
        this.inputAdapter = inputAdapter;
    }

    public List<Assign> calculate() {

        var assigns = new ArrayList<Assign>();
        inputAdapter.getProjects().sort(Comparator.comparingInt(Project::reward));
        Deque<Person> deque = new LinkedList<>();
        List<Person> all = inputAdapter.getPeople();
        for (Project project : inputAdapter.getProjects()) {
            boolean projectDoable = true;
            List<String> personList = new ArrayList<>();
            Assign assign = new Assign(project.name(), personList);
            for (Skill skill : project.skills()) {
                boolean found = false;
                for (Person person: all) {
                    Skill personSkill = person.skills().get(skill.type());
                    if (personSkill != null && personSkill.level() >= skill.level()) {
                        personList.add(person.name());
                        deque.addLast(person);
                        found = true;
                    }
                }
                if (!found) {
                    all.addAll(deque);
                    deque.clear();
                    for (Person person: all) {
                        Skill personSkill = person.skills().get(skill.type());
                        if (personSkill != null && personSkill.level() >= skill.level()) {
                            personList.add(person.name());
                            deque.addLast(person);
                            found = true;
                        }
                    }
                }
                if (!found) {
                    projectDoable = false;
                    break;
                }
            }
            if (projectDoable) {
                assigns.add(assign);
            }
        }
        return assigns;
    }
}
