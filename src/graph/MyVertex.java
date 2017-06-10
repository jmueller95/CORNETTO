package graph;

/**
 * Created by julian on 10.06.17.
 */
public class MyVertex {
    private Object content; //TODO: Change this e.g. to TaxonNode
    private boolean isHidden = false;
    //TODO: Attributes


    public MyVertex(Object content) {
        this.content = content;
    }

    public void hideVertex() {
        this.isHidden = true;
    }

    public void showVertex() {
        this.isHidden = false;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public Object getContent() {
        return content;
    }
}
