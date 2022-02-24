package solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// This class is used to adapt the raw content to the adapted data model
public class InputAdapter {

    private final List<List<String>> content;

    public InputAdapter(List<List<String>> content) {
        this.content = content;
    }

    public int nContributors() {
        return Integer.parseInt(content.get(0).get(0));
    }

    public int nProjects() {
        return Integer.parseInt(content.get(0).get(1));
    }

    public Map<String, Person> nameToPeople() {
        return getPeople().stream()
            .collect(Collectors.toMap(Person::name, Function.identity()));
    }

    public Map<String, Project> projects() {
        return getProjects().stream()
            .collect(Collectors.toMap(Project::name, Function.identity()));
    }


    public List<Person> getPeople() {
        var people = new ArrayList<Person>();
        int l = 1;
        for (int i = 0; i < nContributors(); i++) {
            var name = content.get(l).get(0);
            var nskills = Integer.parseInt(content.get(l).get(1));
            var skills = new HashMap<String, Skill>();
            for (int j = 1; j < nskills + 1; j++) {
                var type = content.get(l + j).get(0);
                var level = Integer.parseInt(content.get(l + j).get(1));
                skills.put(type, new Skill(type, level));
            }
            l = l + 1 + nskills;
            people.add(new Person(name, skills));
        }
        return people;
    }

    public List<Project> getProjects() {
        var projects = new ArrayList<Project>();
        var l = 1 + getPeople().stream().map(person -> person.skills().size() + 1).mapToInt(Integer::intValue).sum();
        for (int i = 0; i < nProjects(); i++) {
            var projectDis = content.get(l);
            var name = projectDis.get(0);
            var days = Integer.parseInt(projectDis.get(1));
            var score = Integer.parseInt(projectDis.get(2));
            var before = Integer.parseInt(projectDis.get(3));
            var nSkills = Integer.parseInt(projectDis.get(4));
            var skills = new ArrayList<Skill>();
            l += 1;
            for (int j = 0; j < nSkills; j++) {
                var skillDis = content.get(l++);
                skills.add(new Skill(skillDis.get(0), Integer.parseInt(skillDis.get(1))));
            }
            projects.add(new Project(name, days, score, before, skills));
        }
        return projects;
    }

    public List<Assign> getAssigns() {
        var assigns = new ArrayList<Assign>();
        var nAssign = Integer.parseInt(content.get(0).get(0));
        for (int i = 1; i <= 2 * nAssign; i += 2) {
            var projectName = content.get(i).get(0);
            var people = content.get(i + 1);
            assigns.add(new Assign(projectName, people));
        }
        return assigns;
    }
}
