package graph.dagsp;

import graph.core.Graph;
import graph.core.Metrics;
import graph.topo.TopoSortKahn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DAGShortestPaths {
    private final Graph graph;
    private final Metrics metrics;
    private double[] distances;
    private int[] predecessor;
    private int source;

    public DAGShortestPaths(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public void computeShortestPaths(int source) {
        if (source < 0 || source >= graph.getNumVertices()) {
            throw new IllegalArgumentException("Source index out of range: " + source);
        }

        metrics.reset();
        metrics.startTimer();

        this.source = source;
        int n = graph.getNumVertices();
        distances = new double[n];
        predecessor = new int[n];

        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessor, -1);
        distances[source] = graph.getVertexWeight(source);

        TopoSortKahn sorter = new TopoSortKahn(graph);
        sorter.computeTopologicalOrder();
        List<Integer> topoOrder = sorter.getTopologicalOrder();

        for (int u : topoOrder) {
            metrics.recordOperation();
            if (distances[u] != Double.POSITIVE_INFINITY) {
                for (Graph.Edge edge : graph.getAdjacent(u)) {
                    metrics.recordEdgeProcessed();
                    metrics.recordOperation();

                    double newDist = distances[u] + edge.weight + graph.getVertexWeight(edge.to);
                    if (newDist < distances[edge.to]) {
                        distances[edge.to] = newDist;
                        predecessor[edge.to] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
    }

    public PathResult reconstructPath(int destination) {
        if (distances == null || predecessor == null) {
            throw new IllegalStateException("Call computeShortestPaths(...) before reconstructPath(...).");
        }
        if (destination < 0 || destination >= graph.getNumVertices()) {
            throw new IllegalArgumentException("Destination index out of range: " + destination);
        }

        List<Integer> path = new ArrayList<>();
        int current = destination;

        if (distances[destination] == Double.POSITIVE_INFINITY && destination != source) {
            path.add(destination);
            return new PathResult(path, distances[destination]);
        }

        while (current != -1) {
            path.add(0, current);
            if (current == source) break; // stop at source
            current = predecessor[current];
        }

        return new PathResult(path, distances[destination]);
    }

    public double[] getDistances() {
        return distances;
    }
}
