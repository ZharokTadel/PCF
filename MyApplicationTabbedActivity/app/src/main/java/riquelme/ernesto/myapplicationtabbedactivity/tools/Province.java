package riquelme.ernesto.myapplicationtabbedactivity.tools;

/**
 *
 * @author william
 */
public class Province implements Comparable<Province> {

    private String id;
    private String name;

    public Province(String id) {
        this.id = id;
    }

    public Province(String id, String name) {
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
    public int compareTo(Province p) {
        return this.id.compareTo(p.id);
    }
}
