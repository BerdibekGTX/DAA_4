package graph.core;

public class Metrics {
    private long dfsVisits = 0;
    private long edgesProcessed = 0;
    private long operationCount = 0;
    private long startTime = 0;
    private long endTime = 0;

    public void recordDFSVisit() { dfsVisits++; }
    public void recordEdgeProcessed() { edgesProcessed++; }
    public void recordOperation() { operationCount++; }

    public void startTimer() { startTime = System.nanoTime(); }
    public void stopTimer() { endTime = System.nanoTime(); }

    public long getDFSVisits() { return dfsVisits; }
    public long getEdgesProcessed() { return edgesProcessed; }
    public long getOperationCount() { return operationCount; }
    public double getExecutionTimeMillis() { return (endTime - startTime) / 1_000_000.0; }

    public void reset() {
        dfsVisits = 0;
        edgesProcessed = 0;
        operationCount = 0;
        startTime = 0;
        endTime = 0;
    }

    @Override
    public String toString() {
        return String.format("Metrics{DFS=%d, Edges=%d, Ops=%d, Time=%.4fms}",
                dfsVisits, edgesProcessed, operationCount, getExecutionTimeMillis());
    }
}