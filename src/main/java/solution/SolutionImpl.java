package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SolutionImpl implements Solution {

    private final InputAdapter inputAdapter;

    public SolutionImpl(InputAdapter inputAdapter) {
        this.inputAdapter = inputAdapter;
    }

//    public List<Assign> greedyC() {
//        inputAdapter.getProjects()
//    }

    @Override
    public long score(List<Assign> assigns) {

        int score = 0;

        var nameToPeople = inputAdapter.nameToPeople();
        var peopleNames = nameToPeople.keySet();
        var nameToProject = inputAdapter.projects();

        Map<String, List<String>> personToProjects = new HashMap<>();

        for (Assign assign : assigns) {
            var people = assign.people();
            for (String person : people) {
                var projects = personToProjects.getOrDefault(person, new ArrayList<>());
                projects.add(assign.projectName());
                personToProjects.put(person, projects);
            }
        }

        Map<String, Integer> personFree = new HashMap<>();
        for (String name : peopleNames) {
            personFree.put(name, 0);
        }

        for (Assign assign : assigns) {

            var assignedPeople = assign.people().stream().map(name -> nameToPeople.get(name)).collect(Collectors.toList());
            var project = nameToProject.get(assign.projectName());
            var skillsNeeds = project.skills();
            skillCheck(assignedPeople, skillsNeeds);

            var startDay = getStartDay(assignedPeople, personFree);
            var days = project.days();
            var endDays = startDay + days;

            // when project finish update the person available days
            for (Person p : assignedPeople) {
                personFree.put(p.name(), endDays);
            }

            // update skill
            updateSkill(assignedPeople, nameToPeople, skillsNeeds);

            // add score
            score += project.reward() - Math.max(0, endDays - project.before());

        }

        return score;
    }

    private void updateSkill(List<Person> assignedPeople, Map<String, Person> nameToPeople, List<Skill> skillsNeeds) {
        var size = assignedPeople.size();
        for (int i = 0; i < size; i++) {
            var skill = skillsNeeds.get(i);
            var person = assignedPeople.get(i);
            var currentLevel = person.skills().get(skill.type()).level();
            if (currentLevel == skill.level() || currentLevel == skill.level() - 1) {
                nameToPeople.put(person.name(), person.addSkillLevel(skill));
            }
        }
    }

    private int getStartDay(List<Person> people, Map<String, Integer> personFree) {
        return people.stream()
            .map(Person::name)
            .map(name -> personFree.get(name))
            .mapToInt(Integer::intValue)
            .max().getAsInt();
    }

    private void skillCheck(List<Person> people, List<Skill> skillsNeeds) {
        if (people.size() != skillsNeeds.size()) {
            throw new IllegalArgumentException("people size not match");
        }
        var highestLevel = people.stream()
            .flatMap(p -> p.skills().values().stream())
            .collect(Collectors.toSet())
            .stream()
            .collect(Collectors.toMap(
                Skill::type,
                Function.identity(),
                (skill1, skill2) -> skill1.level() >= skill2.level() ? skill1 : skill2
            ));
        var size = people.size();
        for (int i = 0; i < size; i++) {
            var skill = skillsNeeds.get(i);
            var person = people.get(i);

            if (person.skills().get(skill.type()).level() >= skill.level()) {

            } else if (person.skills().get(skill.type()).level() + 1 == skill.level()) {
                // mentor
                if (highestLevel.get(skill.type()).level() >= skill.level()) {

                } else {
                    throw new IllegalArgumentException("cannot mentor");
                }
            } else {
                throw new IllegalArgumentException("skill not match");
            }
        }
    }
}
