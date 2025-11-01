package graph.dagsp;
import java.util.*;

public class PathResult {
    private List<Integer> path;
    private double length;

    public PathResult(List<Integer> path, double length) {
        this.path = path;
        this.length = length;
    }

    public List<Integer> getPath() { return path; }
    public double getLength() { return length; }

    @Override
    public String toString() {
        return String.format("PathResult{path=%s, length=%.2f}", path, length);
    }
}