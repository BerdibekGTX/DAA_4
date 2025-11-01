package graph.scc;
import graph.core.Graph;
import java.util.*;

public class CondensationGraph extends Graph {
    private List<Component> components;

    public CondensationGraph(int numComponents, List<Component> components) {
        super(numComponents);
        this.components = components;
    }

}