package solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Table;

public class SolutionImpl {

    private final InputAdapter inputAdapter;

    public SolutionImpl(InputAdapter inputAdapter) {
        this.inputAdapter = inputAdapter;
    }

    public List<Assign> simulationForE(int loopScope) {
        // for each turn, loop for all the remained projects to find one has most reward & minimize the contributors
        // waiting time.

        var noScore = 0;
        var skillPersonLevel = inputAdapter.skillPersonLevel();
        var peopleNames = new HashSet<>(inputAdapter.nameToPeople().keySet());
        Map<String, Integer> personFree = initPeronFreeDay(peopleNames);

        var projects = inputAdapter.getProjects();

        var results = new ArrayList<Assign>();

        while (!projects.isEmpty()) {
            Collections.shuffle(projects);
            var maxProjectValue = 0d;
            Project chooseProject = null;
            var finalAssignedPeople = new ArrayList<String>();

            int loop = 0;

            for (Project project : projects) {
                loop++;
                if(loop > loopScope) {
                    break;
                }

                var canAssign = true;
                var assignedPeople = new ArrayList<String>();

                Map<String, Integer> needsMentor = new HashMap<String, Integer>(); // skill type to level
                var canMentor = new HashMap<String, Integer>(); // skill type to level

                var needSkills = project.skills();
                for (Skill skill : needSkills) {

                    var type = skill.type();
                    var candidates = new ArrayList<String>();
                    for (String person : peopleNames) {
                        if (!assignedPeople.contains(person)) {
                            var level = Optional.ofNullable(skillPersonLevel.get(type, person)).orElse(0);
                            if (level >= skill.level() - 1) {
                                candidates.add(person);
                            }
                        }
                    }
                    if (candidates.isEmpty()) {
                        canAssign = false;
                        break;
                    }
                    String person;
                    if (needsMentor.isEmpty()) {
                        person = findAvailablePerson(candidates, personFree);
                    } else {
                        person = findMentor(candidates, needsMentor, skillPersonLevel, personFree);
                    }

                    assignedPeople.add(person);

                    var level = Optional.ofNullable(skillPersonLevel.get(type, person)).orElse(0);
                    skillPersonLevel.column(person)
                        .forEach((sType, sLevel) -> {
                            canMentor.put(sType, Math.max(canMentor.getOrDefault(sType, 0), sLevel));
                        });
                    needsMentor = needsMentor.entrySet().stream()
                        .filter(typeToLevel -> canMentor.getOrDefault(typeToLevel.getKey(), 0) < typeToLevel.getValue())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    if (level == skill.level() - 1) {
                        if (canMentor.getOrDefault(type, 0) < skill.level()) {
                            needsMentor.put(type, skill.level());
                        }
                    }
                }
                if (canAssign && needsMentor.isEmpty()) {
                    var wastTime = getWastTime(assignedPeople, personFree);
                    var reward = project.reward();

                    var currentProjectValue = 1.0 * reward / wastTime; // To try different strategy
                    if (chooseProject == null) {
                        maxProjectValue = currentProjectValue;
                        chooseProject = project;
                        finalAssignedPeople = assignedPeople;
                    } else {

                        if (maxProjectValue < currentProjectValue) {
                            maxProjectValue = currentProjectValue;
                            chooseProject = project;
                            finalAssignedPeople = assignedPeople;
                        }
                    }
                }
            }

            if (chooseProject != null) {
                projects.remove(chooseProject);
                var needSkills = chooseProject.skills();
                var startDay = getStartDay(finalAssignedPeople, personFree);
                var days = chooseProject.days();
                var endDays = startDay + days;
                var assign = new Assign(chooseProject.name(), finalAssignedPeople);
                results.add(assign);
                // update skill
                updateSkill(finalAssignedPeople, needSkills, skillPersonLevel);
                // update person free
                updatePersonFree(personFree, finalAssignedPeople, endDays);
            } else {
                break;
            }
        }
        System.err.println("no score " + noScore);
        System.err.println("totoal assign " + results.size());
        return results;
    }

    public List<Assign> simulation(Comparator<Project> comparator) {

        var noScore = 0;
        var skillPersonLevel = inputAdapter.skillPersonLevel();
        var peopleNames = new HashSet<>(inputAdapter.nameToPeople().keySet());
        Map<String, Integer> personFree = initPeronFreeDay(peopleNames);

        var projects = inputAdapter.getProjects()
            .stream()
            .sorted(comparator)
            .collect(Collectors.toCollection(ArrayDeque::new));

        var results = new ArrayList<Assign>();

        var currentSize = projects.size();
        var i = 0;
        while (!projects.isEmpty()) {

            if (projects.size() == currentSize) {
                i++;
            } else {
                currentSize = projects.size();
                i = 0;
            }
            if (i > currentSize) {
                break;
            }

            var project = projects.pollFirst();

            var canAssign = true;
            var assignedPeople = new ArrayList<String>();
            var assign = new Assign(project.name(), assignedPeople);

            Map<String, Integer> needsMentor = new HashMap<String, Integer>(); // skill type to level
            var canMentor = new HashMap<String, Integer>(); // skill type to level
            var mentee = 0;

            var needSkills = project.skills();
            for (Skill skill : needSkills) {

                var type = skill.type();
                var candidates = new ArrayList<String>();
                for (String person : peopleNames) {
                    if (!assignedPeople.contains(person)) {
                        var level = Optional.ofNullable(skillPersonLevel.get(type, person)).orElse(0);
                        if (level >= skill.level() - 1) {
                            candidates.add(person);
                        }
                    }
                }
                if (candidates.isEmpty()) {
                    canAssign = false;
                    break;
                }
                String person;
                if (needsMentor.isEmpty()) {
                    person = findAvailablePerson(candidates, personFree);
                } else {
                    person = findMentor(candidates, needsMentor, skillPersonLevel, personFree);
                }

                assignedPeople.add(person);

                var level = Optional.ofNullable(skillPersonLevel.get(type, person)).orElse(0);
                skillPersonLevel.column(person)
                    .forEach((sType, sLevel) -> {
                        canMentor.put(sType, Math.max(canMentor.getOrDefault(sType, 0), sLevel));
                    });
                needsMentor = needsMentor.entrySet().stream()
                    .filter(typeToLevel -> canMentor.getOrDefault(typeToLevel.getKey(), 0) < typeToLevel.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (level == skill.level() - 1) {
                    mentee++;
                    if (canMentor.getOrDefault(type, 0) < skill.level()) {
                        needsMentor.put(type, skill.level());
                    }
                }
            }

            if (canAssign && needsMentor.isEmpty()) {
                var startDay = getStartDay(assignedPeople, personFree);
                var days = project.days();
                var endDays = startDay + days;
                if (project.reward() - Math.max(0, endDays - project.before()) > 0) {
                    results.add(assign);
                    // update skill
                    updateSkill(assignedPeople, needSkills, skillPersonLevel);
                    // update person free
                    updatePersonFree(personFree, assignedPeople, endDays);
                }
                if (project.reward() - Math.max(0, endDays - project.before()) <= 0) {
                    noScore++;
                }
            } else {
                projects.addLast(project);
            }
        }
        System.err.println("no score " + noScore);
        System.err.println("totoal assign " + results.size());
        return results;
    }

    private String findMentor(List<String> candidates, Map<String, Integer> needsMentor, Table<String, String, Integer> skillPersonLevel, Map<String, Integer> personFree) {
        var sortByAvailable = candidates.stream().sorted(Comparator.comparingInt(personFree::get)).collect(Collectors.toList());
        var mentor = sortByAvailable.get(0);
        var mentorMost = 0L;
        for (String person : sortByAvailable) {
            var nMentee = needsMentor.entrySet().stream()
                .filter(
                    entry -> Optional.ofNullable(skillPersonLevel.get(entry.getKey(), person)).orElse(0)
                             >= entry.getValue()
                ).count();
            //            if (nMentee != 0) {
            //                return mentor;
            //            }
            if (nMentee > mentorMost) {
                mentorMost = nMentee;
                mentor = person;
            }
        }
        return mentor;
    }

    private String findAvailablePerson(List<String> candidates, Map<String, Integer> personFree) {
        return candidates.stream().sorted(Comparator.comparingInt(personFree::get)).collect(Collectors.toList()).get(0);
    }

    public long score(List<Assign> assigns) {
        int score = 0;

        var skillPersonLevel = inputAdapter.skillPersonLevel();
        var peopleNames = inputAdapter.nameToPeople().keySet();
        var nameToProject = inputAdapter.projects();
        Map<String, Integer> personFree = initPeronFreeDay(peopleNames);

        for (Assign assign : assigns) {

            var assignedPeople = assign.people();
            checkPeopleUnique(assignedPeople);
            var project = nameToProject.get(assign.projectName());
            var needSkills = project.skills();
            skillCheck(assignedPeople, needSkills, skillPersonLevel);

            var startDay = getStartDay(assignedPeople, personFree);
            var days = project.days();
            var endDays = startDay + days;

            // update the person available days
            updatePersonFree(personFree, assignedPeople, endDays);

            // update skill level
            updateSkill(assignedPeople, needSkills, skillPersonLevel);

            // add score
            score += Math.max(0, project.reward() - Math.max(0, endDays - project.before()));

        }

        return score;
    }

    private void updatePersonFree(Map<String, Integer> personFree, List<String> assignedPeople, int endDays) {
        for (String person : assignedPeople) {
            personFree.put(person, endDays);
        }
    }

    private void checkPeopleUnique(List<String> assignedPeople) {
        if (assignedPeople.size() != new HashSet<>(assignedPeople).size()) {
            throw new IllegalArgumentException("assign one person to more than one task");
        }
    }

    private void skillCheck(List<String> assignedPeople, List<Skill> needSkills, Table<String, String, Integer> skillPersonLevel) {

        var assigned = new HashSet<String>(assignedPeople);
        if (assignedPeople.size() != needSkills.size()) {
            throw new IllegalArgumentException("people size not match");
        }
        var mentorSkills = new HashSet<String>();
        for (Skill skill : needSkills) {
            var canMentor = skillPersonLevel.row(skill.type())
                .entrySet().stream()
                .anyMatch(personToLevel -> assigned.contains(personToLevel.getKey())
                                           && personToLevel.getValue() >= skill.level());
            if (canMentor) {
                mentorSkills.add(skill.type());
            }
        }

        for (int i = 0; i < assignedPeople.size(); i++) {
            var skill = needSkills.get(i);
            var person = assignedPeople.get(i);
            var currentLevel = Optional.ofNullable(skillPersonLevel.get(skill.type(), person)).orElse(0);
            if (currentLevel >= skill.level()) {

            } else if (currentLevel + 1 == skill.level()) {
                // mentor
                if (mentorSkills.contains(skill.type())) {

                } else {
                    throw new IllegalArgumentException("cannot mentor");
                }
            } else {
                throw new IllegalArgumentException("skill not match");
            }
        }
    }

    private Map<String, Integer> initPeronFreeDay(Set<String> peopleNames) {
        Map<String, Integer> personFree = new HashMap<>();
        for (String name : peopleNames) {
            personFree.put(name, 0);
        }
        return personFree;
    }

    private void updateSkill(List<String> assignedPeople, List<Skill> skillsNeeds, Table<String, String, Integer> skillPersonLevel) {
        var size = assignedPeople.size();
        for (int i = 0; i < size; i++) {
            var skill = skillsNeeds.get(i);
            var person = assignedPeople.get(i);
            var level = Optional.ofNullable(skillPersonLevel.get(skill.type(), person)).orElse(0);
            if (level == skill.level() || level == skill.level() - 1) {
                skillPersonLevel.put(skill.type(), person, level + 1);
            }
        }
    }

    private int getStartDay(List<String> assignedPeople, Map<String, Integer> personFree) {
        return maxDays(assignedPeople, personFree);
    }

    private int getWastTime(List<String> assignedPeople, Map<String, Integer> personFree) {
        return maxDays(assignedPeople, personFree) - minDays(assignedPeople, personFree);
    }

    private int minDays(List<String> assignedPeople, Map<String, Integer> personFree) {
        return assignedPeople.stream()
            .map(personFree::get)
            .mapToInt(Integer::intValue)
            .min()
            .getAsInt();
    }

    private int maxDays(List<String> assignedPeople, Map<String, Integer> personFree) {
        return assignedPeople.stream()
            .map(personFree::get)
            .mapToInt(Integer::intValue)
            .max()
            .getAsInt();
    }
}
