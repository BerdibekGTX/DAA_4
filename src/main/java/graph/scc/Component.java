package graph.scc;
import java.util.*;

public class Component {
    private List<Integer> vertices;
    private int id;

    public Component(int id) {
        this.id = id;
        this.vertices = new ArrayList<>();
    }

    public void addVertex(int vertex) {
        vertices.add(vertex);
    }

    public int getId() { return id; }
    public int getSize() { return vertices.size(); }

    @Override
    public String toString() {
        return "Component{" + "id=" + id + ", vertices=" + vertices + "}";
    }
}