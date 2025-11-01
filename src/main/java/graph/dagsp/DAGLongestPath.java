package graph.dagsp;
import graph.core.Graph;
import graph.core.Metrics;
import graph.topo.TopoSortKahn;
import java.util.*;

public class DAGLongestPath {
    private Graph graph;
    private Metrics metrics;
    private double[] longest;
    private int[] predecessor;
    private int source;

    public DAGLongestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public void computeLongestPaths(int source) {
        metrics.reset();
        metrics.startTimer();

        this.source = source;
        int n = graph.getNumVertices();
        longest = new double[n];
        predecessor = new int[n];

        Arrays.fill(longest, Double.NEGATIVE_INFINITY);
        Arrays.fill(predecessor, -1);
        longest[source] = graph.getVertexWeight(source);

        TopoSortKahn sorter = new TopoSortKahn(graph);
        sorter.computeTopologicalOrder();
        List<Integer> topoOrder = sorter.getTopologicalOrder();

        for (int u : topoOrder) {
            if (longest[u] != Double.NEGATIVE_INFINITY) {
                for (Graph.Edge edge : graph.getAdjacent(u)) {
                    metrics.recordEdgeProcessed();
                    metrics.recordOperation();

                    double newDist = longest[u] + edge.weight + graph.getVertexWeight(edge.to);
                    if (newDist > longest[edge.to]) {
                        longest[edge.to] = newDist;
                        predecessor[edge.to] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
    }


    public PathResult reconstructPath(int destination) {
        List<Integer> path = new ArrayList<>();
        int current = destination;
        while (current != -1) {
            path.add(0, current);
            current = predecessor[current];
        }
        return new PathResult(path, longest[destination]);
    }

    public double[] getLongestPaths() { return longest; }
    public Metrics getMetrics() { return metrics; }
}
