package solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Table;

public class SolutionImpl implements Solution {

    private final InputAdapter inputAdapter;

    public SolutionImpl(InputAdapter inputAdapter) {
        this.inputAdapter = inputAdapter;
    }

    public List<Assign> greedyF() {

        var skillMaster = inputAdapter.skillMaster();
        var skillPL = inputAdapter.skillPersonLevel();
        var skillToPersons = inputAdapter.skillToPersons();
        var nameToPeople = inputAdapter.nameToPeople();
        var peopleNames = nameToPeople.keySet();
        var nameToProject = inputAdapter.projects();

        Map<String, Integer> personFree = new HashMap<>();
        for (String name : peopleNames) {
            personFree.put(name, 0);
        }

        var minLevels = inputAdapter.getProjects()
            .stream()
            .filter(project -> project.skills().stream().map(Skill::type).collect(Collectors.toSet()).size() == 1)
            .sorted(Comparator.<Project>comparingInt(p -> p.skills().get(0).level()))
            .collect(Collectors.toList());

        var assigns = new ArrayList<Assign>();

        for (Project project : minLevels) {

            Set<String> chosedPerson = new HashSet<>();
            List<String> roles = new ArrayList<>();

            var skills = project.skills();
            var type = skills.get(0).type();
            var master = skillMaster.get(type);
            roles.add(master);
            chosedPerson.add(master);

            for (int i = 1; i < skills.size(); i++) {
                var skill = skills.get(i);
                for(String p : peopleNames) {
                    var l = skillPL.get(type, p);
                    if(l == null) {
                        l = 0;
                    }
                    if((l >= skill.level() || l == skill.level() - 1) && !chosedPerson.contains(p))  {
                        chosedPerson.add(p);
                        roles.add(p);
                        break;
                    }
                }
            }


            if(roles.size() == skills.size()) {
                assigns.add(new Assign(project.name(), roles));
            }
        }
        return assigns;
    }

    public List<Assign> greedyE() {
        var random = new Random();
        var skillToPersons = inputAdapter.skillToPersons();

        var sortByValue = inputAdapter.getProjects()
            .stream()
            .filter(p -> p.reward() < 600)
            .sorted(Comparator.<Project>comparingInt(p -> p.reward()).reversed())
            .collect(Collectors.toList());

        var assigns = new ArrayList<Assign>();

        for (Project project : sortByValue) {

            boolean canAdd = true;
            Set<String> chosedPerson = new HashSet<>();
            List<String> roles = new ArrayList<>();


            var skills = project.skills();
            for(Skill skill : skills) {
                var possiblePerson = skillToPersons.get(skill.type());
                if(chosedPerson.containsAll(possiblePerson)) {
                    canAdd = false;
                    break;
                }
                var chosed = possiblePerson.get(random.nextInt(possiblePerson.size()));
                while (chosedPerson.contains(chosed)) {
                    chosed = possiblePerson.get(random.nextInt(possiblePerson.size()));
                }
                chosedPerson.add(chosed);
                roles.add(chosed);
            }
            if(canAdd) {
                assigns.add(new Assign(project.name(), roles));
            }
        }
        return assigns;
    }

    @Override
    public long score(List<Assign> assigns) {

        int score = 0;

        var nameToPeople = inputAdapter.nameToPeople();
        var peopleNames = nameToPeople.keySet();
        var nameToProject = inputAdapter.projects();
        Map<String, Integer> personFree = new HashMap<>();
        for (String name : peopleNames) {
            personFree.put(name, 0);
        }


        Map<String, List<String>> personToProjects = new HashMap<>();
        for (Assign assign : assigns) {
            var people = assign.people();
            for (String person : people) {
                var projects = personToProjects.getOrDefault(person, new ArrayList<>());
                projects.add(assign.projectName());
                personToProjects.put(person, projects);
            }
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
            score += Math.max(0, project.reward() - Math.max(0, endDays - project.before()));

        }

        return score;
    }

    private void updateSkill(List<Person> assignedPeople, Map<String, Person> nameToPeople, List<Skill> skillsNeeds) {
        var size = assignedPeople.size();
        for (int i = 0; i < size; i++) {
            var skill = skillsNeeds.get(i);
            var person = assignedPeople.get(i);
            var currentLevel = person.skills().getOrDefault(skill.type(), new Skill(skill.type(), 0)).level();
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

            if (person.skills().getOrDefault(skill.type(), new Skill(skill.type(), 0)).level() >= skill.level()) {

            } else if (person.skills().getOrDefault(skill.type(), new Skill(skill.type(), 0)).level() + 1 == skill.level()) {
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
