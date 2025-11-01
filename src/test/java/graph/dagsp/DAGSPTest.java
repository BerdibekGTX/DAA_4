package graph.dagsp;
import graph.core.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DAGSPTest {

    @Test
    public void testLongestPath() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1.0);
        g.addEdge(0, 2, 4.0);
        g.addEdge(1, 3, 2.0);
        g.addEdge(2, 3, 1.0);

        DAGLongestPath lp = new DAGLongestPath(g);
        lp.computeLongestPaths(0);

        double[] longest = lp.getLongestPaths();
        assertTrue(longest[3] > Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testShortestPath() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1.0);
        g.addEdge(0, 2, 4.0);
        g.addEdge(1, 3, 2.0);
        g.addEdge(2, 3, 1.0);

        DAGShortestPaths sp = new DAGShortestPaths(g);
        sp.computeShortestPaths(0);

        double[] dist = sp.getDistances();
        assertTrue(dist[3] < Double.POSITIVE_INFINITY);
    }
}