package application;

import graph.core.Graph;
import graph.core.DataLoader;
import graph.scc.TarjanSCC;
import graph.scc.CondensationGraph;
import graph.topo.TopoSortKahn;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.PathResult;
import java.io.*;
import java.util.*;

public class SmartCityScheduler {

    private static class AnalysisResult {
        String datasetName;
        int vertices;
        int edges;
        String graphType;
        List<Integer> sccSizes;

        int numSCCs;
        double sccTimeMs;
        long sccDFSVisits;
        long sccEdgesProcessed;

        boolean isDAG;
        double topoTimeMs;
        long topoOperations;

        double lpTimeMs;
        long lpRelaxations;
        double criticalPathLength;
        List<Integer> criticalPath;

        @Override
        public String toString() {
            return String.format("%s: V=%d E=%d Type=%s SCCs=%d CritPath=%.2f",
                    datasetName, vertices, edges, graphType, numSCCs, criticalPathLength);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> datasets = DataLoader.getAvailableDatasets();

        if (datasets.isEmpty()) {
            System.out.println("No JSON datasets found. Using programmatic datasets...\n");
            datasets = Arrays.asList(
                    "small1", "small2", "small3",
                    "medium1", "medium2", "medium3",
                    "large1", "large2", "large3"
            );
        } else {
            System.out.println("Found " + datasets.size() + " datasets\n");
        }

        List<AnalysisResult> results = new ArrayList<>(datasets.size());

        for (String dataset : datasets) {
            System.out.print("▶ Processing: " + String.format("%-15s", dataset) + " ... ");
            try {
                AnalysisResult result = processDatasetOptimized(dataset);
                results.add(result);
                System.out.println("✓");
            } catch (Exception e) {
                System.out.println("✗ " + e.getMessage());
            }
        }

        System.out.println();
        generateReport(results);
        System.out.println("\n Analysis complete!\n");
    }

    private static AnalysisResult processDatasetOptimized(String datasetName) throws IOException {
        AnalysisResult result = new AnalysisResult();
        result.datasetName = datasetName;

        Graph g;
        try {
            g = DataLoader.loadDataset(datasetName + ".json");
        } catch (Exception e) {
            g = DataLoader.createSampleDataset(datasetName);
        }

        result.vertices = g.getNumVertices();
        result.edges = countEdges(g);

        TarjanSCC tarjan = new TarjanSCC(g);
        tarjan.detectSCCs();

        result.numSCCs = tarjan.getComponents().size();
        result.sccTimeMs = tarjan.getMetrics().getExecutionTimeMillis();
        result.sccDFSVisits = tarjan.getMetrics().getDFSVisits();
        result.sccEdgesProcessed = tarjan.getMetrics().getEdgesProcessed();
        result.graphType = (result.numSCCs == result.vertices) ? "DAG" : "Cyclic";

        result.sccSizes = new ArrayList<>(result.numSCCs);
        for (var comp : tarjan.getComponents()) result.sccSizes.add(comp.getSize());

        Graph graphForTopoAndLP;
        CondensationGraph condGraph = null;
        if (result.graphType.equals("DAG")) {
            graphForTopoAndLP = g;
        } else {
            condGraph = tarjan.getCondensationGraph();
            graphForTopoAndLP = condGraph;
        }

        TopoSortKahn topoKahn = new TopoSortKahn(graphForTopoAndLP);
        boolean topoValid = topoKahn.computeTopologicalOrder();
        result.isDAG = topoValid;
        result.topoTimeMs = topoKahn.getMetrics().getExecutionTimeMillis();
        result.topoOperations = topoKahn.getMetrics().getOperationCount();

        int nForLP = graphForTopoAndLP.getNumVertices();
        int source = (nForLP > 0) ? 0 : -1;

        if (source >= 0) {
            DAGLongestPath lp = new DAGLongestPath(graphForTopoAndLP);
            lp.computeLongestPaths(source);

            result.lpTimeMs = lp.getMetrics().getExecutionTimeMillis();
            result.lpRelaxations = lp.getMetrics().getEdgesProcessed();

            double[] longest = lp.getLongestPaths();
            // find index of maximum longest path
            double maxVal = Double.NEGATIVE_INFINITY;
            int maxIdx = -1;
            for (int i = 0; i < longest.length; i++) {
                if (longest[i] > maxVal) {
                    maxVal = longest[i];
                    maxIdx = i;
                }
            }

            if (maxIdx >= 0 && maxVal != Double.NEGATIVE_INFINITY) {
                PathResult path = lp.reconstructPath(maxIdx);
                result.criticalPathLength = path.getLength();
                result.criticalPath = path.getPath();
            } else {
                result.criticalPathLength = 0.0;
                result.criticalPath = Collections.emptyList();
            }
        } else {
            result.lpTimeMs = 0.0;
            result.lpRelaxations = 0;
            result.criticalPathLength = 0.0;
            result.criticalPath = Collections.emptyList();
        }

        return result;
    }

    private static int countEdges(Graph g) {
        int count = 0;
        int n = g.getNumVertices();
        for (int i = 0; i < n; i++) {
            count += g.getAdjacent(i).size();
        }
        return count;
    }


    private static void generateReport(List<AnalysisResult> results) throws IOException {
        new File("report").mkdirs();
        String filepath = "report/analysis_results.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            StringBuilder sb = new StringBuilder();

            // Model description
            sb.append(modelDescriptionBlock());

            // Data summary
            sb.append(dataSummaryBlock(results));

            // Metrics detail
            sb.append(metricsDetailBlock(results));

            // Bottleneck analysis + structure analysis + conclusions
            sb.append(bottleneckBlock(results));
            sb.append(structureBlock(results));
            sb.append(conclusionsBlock(results));

            bw.write(sb.toString());
            bw.flush();
        }

        System.out.println("📄 Report generated: " + filepath);
    }

    private static String modelDescriptionBlock() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
    private static String dataSummaryBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("DATA SUMMARY\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append(String.format("%-15s | %5s | %5s | %8s | %5s | %10s\n",
                "Dataset", "Nodes", "Edges", "Type", "SCCs", "Crit.Path"));
        sb.append("-".repeat(85)).append('\n');

        for (AnalysisResult r : results) {
            sb.append(String.format("%-15s | %5d | %5d | %8s | %5d | %10.2f\n",
                    r.datasetName, r.vertices, r.edges, r.graphType, r.numSCCs,
                    r.criticalPathLength));
        }

        int smallCount = (int) results.stream().filter(r -> r.vertices <= 10).count();
        int mediumCount = (int) results.stream().filter(r -> r.vertices > 10 && r.vertices <= 20).count();
        int largeCount = (int) results.stream().filter(r -> r.vertices > 20).count();
        sb.append("\nDataset Categories:\n");
        sb.append(String.format("  • Small (6-10 nodes): %d datasets\n", smallCount));
        sb.append(String.format("  • Medium (10-20 nodes): %d datasets\n", mediumCount));
        sb.append(String.format("  • Large (20-50 nodes): %d datasets\n\n", largeCount));
        return sb.toString();
    }

    private static String metricsDetailBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("ALGORITHM PERFORMANCE METRICS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("1. SCC DETECTION (Tarjan's Algorithm)\n\n");
        sb.append(String.format("%-15s | %12s | %12s | %12s\n",
                "Dataset", "Time (µs)", "DFS Visits", "Edges Proc."));
        sb.append("-".repeat(80)).append('\n');
        for (AnalysisResult r : results) {
            sb.append(String.format("%-15s | %12.3f | %12d | %12d\n",
                    r.datasetName, r.sccTimeMs * 1000, r.sccDFSVisits, r.sccEdgesProcessed));
        }

        sb.append("\n2. TOPOLOGICAL SORT (Kahn's Algorithm)\n\n");
        sb.append(String.format("%-15s | %12s | %12s | %8s\n",
                "Dataset", "Time (µs)", "Operations", "Valid?"));
        sb.append("-".repeat(70)).append('\n');
        for (AnalysisResult r : results) {
            sb.append(String.format("%-15s | %12.3f | %12d | %8s\n",
                    r.datasetName, r.topoTimeMs * 1000, r.topoOperations,
                    r.isDAG ? "Yes" : "No"));
        }

        sb.append("\n3. LONGEST PATH (Critical Path Analysis)\n\n");
        sb.append(String.format("%-15s | %12s | %12s | %12s\n",
                "Dataset", "Time (µs)", "Relaxations", "Max Path"));
        sb.append("-".repeat(80)).append('\n');
        for (AnalysisResult r : results) {
            sb.append(String.format("%-15s | %12.3f | %12d | %12.2f\n",
                    r.datasetName, r.lpTimeMs * 1000, r.lpRelaxations,
                    r.criticalPathLength));
        }
        sb.append('\n');
        return sb.toString();
    }

    private static String bottleneckBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("BOTTLENECK ANALYSIS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        double avgSCCTime = results.stream().mapToDouble(r -> r.sccTimeMs).average().orElse(0);
        double avgTopoTime = results.stream().mapToDouble(r -> r.topoTimeMs).average().orElse(0);
        double avgLPTime = results.stream().mapToDouble(r -> r.lpTimeMs).average().orElse(0);

        sb.append(String.format("Average SCC time: %.4f ms\n", avgSCCTime));
        sb.append(String.format("Average Topo time: %.4f ms\n", avgTopoTime));
        sb.append(String.format("Average LongestPath time: %.4f ms\n\n", avgLPTime));
        return sb.toString();
    }

    private static String structureBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("EFFECT OF GRAPH STRUCTURE ON PERFORMANCE\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        for (AnalysisResult r : results) {
            double evRatio = (r.vertices > 0) ? (double) r.edges / r.vertices : 0.0;
            sb.append(String.format("  • %s: E/V = %.2f, SCC time = %.3f µs\n",
                    r.datasetName, evRatio, r.sccTimeMs * 1000));
        }
        sb.append('\n');

        long cyclicCount = results.stream().filter(r -> "Cyclic".equals(r.graphType)).count();
        sb.append(String.format("  • Cyclic graphs: %d/%d\n", cyclicCount, results.size()));
        sb.append('\n');
        return sb.toString();
    }

    private static String conclusionsBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
