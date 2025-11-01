package graph.topo;
import graph.core.Graph;
import graph.core.Metrics;
import java.util.*;

public class TopoSortKahn {
    private Graph graph;
    private Metrics metrics;
    private List<Integer> topologicalOrder;

    public TopoSortKahn(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public boolean computeTopologicalOrder() {
        metrics.reset();
        metrics.startTimer();

        int n = graph.getNumVertices();
        int[] inDegree = new int[n];

        for (int v = 0; v < n; v++) {
            for (Graph.Edge e : graph.getAdjacent(v)) {
                inDegree[e.to]++;
                metrics.recordEdgeProcessed();
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        topologicalOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.recordOperation();
            topologicalOrder.add(u);

            for (Graph.Edge edge : graph.getAdjacent(u)) {
                metrics.recordEdgeProcessed();
                inDegree[edge.to]--;
                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        metrics.stopTimer();

        return topologicalOrder.size() == n;
    }

    public List<Integer> getTopologicalOrder() { return topologicalOrder; }
    public Metrics getMetrics() { return metrics; }
}