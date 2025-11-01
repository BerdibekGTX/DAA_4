package graph.topo;

import graph.core.Graph;
import graph.core.Metrics;
import java.util.*;

public class TopoSortDFS {
    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    private Graph graph;
    private Metrics metrics;
    private int[] color;
    private Stack<Integer> stack;
    private boolean hasCycle;

    public TopoSortDFS(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    public boolean computeTopologicalOrder() {
        metrics.reset();
        metrics.startTimer();

        int n = graph.getNumVertices();
        color = new int[n];
        stack = new Stack<>();
        hasCycle = false;

        for (int i = 0; i < n; i++) {
            color[i] = WHITE;
        }

        for (int i = 0; i < n && !hasCycle; i++) {
            if (color[i] == WHITE) {
                dfsVisit(i);
            }
        }

        metrics.stopTimer();
        return !hasCycle;
    }

    private void dfsVisit(int u) {
        metrics.recordDFSVisit();
        color[u] = GRAY;

        for (Graph.Edge edge : graph.getAdjacent(u)) {
            metrics.recordEdgeProcessed();
            metrics.recordOperation();

            int v = edge.to;

            if (color[v] == WHITE) {
                dfsVisit(v);
            } else if (color[v] == GRAY) {
                hasCycle = true;
                return;
            }
        }

        color[u] = BLACK;
        stack.push(u);
    }

    public List<Integer> getTopologicalOrder() {
        List<Integer> order = new ArrayList<>();
        while (!stack.isEmpty()) {
            order.add(stack.pop());
        }
        return order;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}