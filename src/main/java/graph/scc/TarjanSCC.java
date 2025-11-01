package graph.scc;
import graph.core.Graph;
import graph.core.Metrics;
import java.util.*;

public class TarjanSCC {
    private Graph graph;
    private Metrics metrics;
    private int[] ids;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int idCounter;
    private List<Component> components;
    private int[] componentId;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public void detectSCCs() {
        metrics.reset();
        metrics.startTimer();

        int n = graph.getNumVertices();
        ids = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        idCounter = 0;
        components = new ArrayList<>();
        componentId = new int[n];

        Arrays.fill(ids, -1);
        Arrays.fill(componentId, -1);

        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();
    }

    private void dfs(int at) {
        metrics.recordDFSVisit();
        stack.push(at);
        onStack[at] = true;
        ids[at] = lowlinks[at] = idCounter++;

        for (Graph.Edge edge : graph.getAdjacent(at)) {
            metrics.recordEdgeProcessed();
            int to = edge.to;

            if (ids[to] == -1) {
                dfs(to);
                lowlinks[at] = Math.min(lowlinks[at], lowlinks[to]);
            } else if (onStack[to]) {
                lowlinks[at] = Math.min(lowlinks[at], ids[to]);
            }
        }

        if (ids[at] == lowlinks[at]) {
            Component component = new Component(components.size());
            while (true) {
                int node = stack.pop();
                onStack[node] = false;
                componentId[node] = component.getId();
                component.addVertex(node);
                if (node == at) break;
            }
            components.add(component);
        }
    }

    public List<Component> getComponents() { return components; }
    public Metrics getMetrics() { return metrics; }


    public CondensationGraph getCondensationGraph() {
        int numComps = components.size();
        CondensationGraph condGraph = new CondensationGraph(numComps, components);

        Set<String> edges = new HashSet<>();
        for (int v = 0; v < graph.getNumVertices(); v++) {
            for (Graph.Edge e : graph.getAdjacent(v)) {
                int compFrom = componentId[v];
                int compTo = componentId[e.to];
                if (compFrom != compTo) {
                    String edgeKey = compFrom + "-" + compTo;
                    if (!edges.contains(edgeKey)) {
                        edges.add(edgeKey);
                        condGraph.addEdge(compFrom, compTo, e.weight);
                    }
                }
            }
        }
        return condGraph;
    }
}