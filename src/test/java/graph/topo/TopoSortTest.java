package graph.topo;

import graph.core.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopoSortTest {

    @Test
    public void testValidDAGKahn() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1.0);
        g.addEdge(0, 2, 1.0);
        g.addEdge(1, 3, 1.0);
        g.addEdge(2, 3, 1.0);

        TopoSortKahn sorter = new TopoSortKahn(g);
        boolean isDAG = sorter.computeTopologicalOrder();

        assertTrue(isDAG);
        assertEquals(4, sorter.getTopologicalOrder().size());
    }

    @Test
    public void testCyclicGraphKahn() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 0, 1.0);

        TopoSortKahn sorter = new TopoSortKahn(g);
        boolean isDAG = sorter.computeTopologicalOrder();

        assertFalse(isDAG);
    }

    @Test
    public void testValidDAGDFS() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1.0);
        g.addEdge(0, 2, 1.0);
        g.addEdge(1, 3, 1.0);
        g.addEdge(2, 3, 1.0);

        TopoSortDFS sorter = new TopoSortDFS(g);
        boolean isDAG = sorter.computeTopologicalOrder();

        assertTrue(isDAG);
        assertEquals(4, sorter.getTopologicalOrder().size());
    }

    @Test
    public void testCyclicGraphDFS() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 0, 1.0);

        TopoSortDFS sorter = new TopoSortDFS(g);
        boolean isDAG = sorter.computeTopologicalOrder();

        assertFalse(isDAG);
    }
}