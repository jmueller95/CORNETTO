package treeParser;

/**
 * Created by Zeth on 21.05.2017.
 */
public class NodesDmp {
    private int id;
    private int parentId;
    private String rank;

    public NodesDmp(int id, int parentId, String rank) {
        this.id = id;
        this.parentId = parentId;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
