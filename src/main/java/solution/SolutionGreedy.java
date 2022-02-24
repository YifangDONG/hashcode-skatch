package solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

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
            Set<Person> used = new HashSet<>();
            for (Skill skill : project.skills()) {
                boolean found = false;
                for (Person person: all) {
                    Skill personSkill = person.skills().get(skill.type());
                    if (!used.contains(person) && personSkill != null && personSkill.level() >= skill.level()) {
                        personList.add(person.name());
                        deque.addLast(person);
                        used.add(person);
                        found = true;
                    }
                }
                if (!found) {
                    all.addAll(deque);
                    deque.clear();
                    for (Person person: all) {
                        Skill personSkill = person.skills().get(skill.type());
                        if (!used.contains(person) && personSkill != null && personSkill.level() >= skill.level()) {
                            personList.add(person.name());
                            deque.addLast(person);
                            used.add(person);
                            found = true;
                        }
                    }
                }
                if (!found) {
                    projectDoable = false;
                    break;
                }
            }
            if (projectDoable && assign.people().size() == project.skills().size()) {
                assigns.add(assign);
            }
        }
        return assigns;
    }

}
