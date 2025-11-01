package graph.core;
import java.util.*;

public class Graph {
    private int numVertices;
    private Map<Integer, List<Edge>> adjList;
    private Map<Integer, Double> vertexWeights;

    public static class Edge {
        public int to;
        public double weight;
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public Graph(int vertices) {
        this.numVertices = vertices;
        this.adjList = new HashMap<>();
        this.vertexWeights = new HashMap<>();
        for (int i = 0; i < vertices; i++) {
            adjList.put(i, new ArrayList<>());
            vertexWeights.put(i, 1.0);
        }
    }

    public void addEdge(int from, int to, double weight) {
        adjList.get(from).add(new Edge(to, weight));
    }

    public void setVertexWeight(int vertex, double weight) {
        vertexWeights.put(vertex, weight);
    }

    public int getNumVertices() { return numVertices; }
    public List<Edge> getAdjacent(int vertex) { return adjList.get(vertex); }
    public double getVertexWeight(int vertex) { return vertexWeights.get(vertex); }

    public Graph reverse() {
        Graph reversed = new Graph(numVertices);
        for (int v = 0; v < numVertices; v++) {
            reversed.vertexWeights.put(v, vertexWeights.get(v));
            for (Edge e : adjList.get(v)) {
                reversed.addEdge(e.to, v, e.weight);
            }
        }
        return reversed;
    }
}