package graph.scc;

import graph.core.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 0, 1.0);

        TarjanSCC scc = new TarjanSCC(g);
        scc.detectSCCs();

        assertEquals(1, scc.getComponents().size());
        assertEquals(3, scc.getComponents().get(0).getSize());
    }

    @Test
    public void testMultipleSCCs() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 0, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 3, 1.0);
        g.addEdge(3, 2, 1.0);
        g.addEdge(3, 4, 1.0);

        TarjanSCC scc = new TarjanSCC(g);
        scc.detectSCCs();

        assertEquals(3, scc.getComponents().size());
    }

    @Test
    public void testDAG() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1.0);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 3, 1.0);

        TarjanSCC scc = new TarjanSCC(g);
        scc.detectSCCs();

        assertEquals(4, scc.getComponents().size());
    }
}