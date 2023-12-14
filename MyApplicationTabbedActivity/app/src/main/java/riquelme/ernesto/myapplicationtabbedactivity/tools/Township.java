package riquelme.ernesto.myapplicationtabbedactivity.tools;

/**
 *
 * @author william
 */
public class Township implements Comparable<Township> {

    private String id;
    private String name;

    public Township(String id) {
        this.id = id;
    }

    public Township(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Township m) {
        return this.id.compareTo(m.id);
    }
}
