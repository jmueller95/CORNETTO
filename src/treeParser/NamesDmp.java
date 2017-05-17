package treeParser;

/**
 * Created by Zeth on 17.05.2017.
 */
public class NamesDmp {
    private int id;
    private String name;
    private String rank;

    public NamesDmp(int id, String name, String rank) {
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
