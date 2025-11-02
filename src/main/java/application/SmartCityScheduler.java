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
        System.out.println("\nAnalysis complete! Report generated at: report/analysis_results.txt\n");
    }

    private static AnalysisResult processDatasetOptimized(String datasetName) throws IOException {
        AnalysisResult result = new AnalysisResult();
        result.datasetName = datasetName;

        Graph g;
        boolean loadedFromFile = false;
        try {
            g = DataLoader.loadDataset(datasetName);
            loadedFromFile = true;
            System.out.print("[JSON] ");
        } catch (Exception e) {
            System.out.print("[Fallback: " + e.getMessage() + "] ");
            String baseName = datasetName.replace(".json", "");
            g = DataLoader.createSampleDataset(baseName);
        }

        result.vertices = g.getNumVertices();
        result.edges = countEdges(g);

        // DEBUG: Show what we actually loaded
        if (!loadedFromFile) {
            System.err.println("\n⚠ WARNING: " + datasetName + ".json failed to load!");
            System.err.println("   Using fallback sample data instead.");
        }

        // Rest of the code remains the same...
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

            // Bottleneck analysis
            sb.append(bottleneckBlock(results));

            // Structure analysis
            sb.append(structureBlock(results));

            // Complexity verification
            sb.append(complexityAnalysisBlock(results));

            // Critical path analysis
            sb.append(criticalPathBlock(results));

            // Conclusions
            sb.append(conclusionsBlock(results));

            bw.write(sb.toString());
            bw.flush();
        }

        System.out.println("Report generated: " + filepath);
    }

    private static String modelDescriptionBlock() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    private static String dataSummaryBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("DATASET SUMMARY\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append(String.format("%-15s | %5s | %5s | %6s | %8s | %5s | %10s\n",
                "Dataset", "Nodes", "Edges", "E/V", "Type", "SCCs", "Crit.Path"));
        sb.append("-".repeat(90)).append('\n');

        for (AnalysisResult r : results) {
            double evRatio = (r.vertices > 0) ? (double) r.edges / r.vertices : 0.0;
            sb.append(String.format("%-15s | %5d | %5d | %6.2f | %8s | %5d | %10.2f\n",
                    r.datasetName, r.vertices, r.edges, evRatio, r.graphType, r.numSCCs,
                    r.criticalPathLength));
        }

        int smallCount = (int) results.stream().filter(r -> r.vertices >= 6 && r.vertices <= 10).count();
        int mediumCount = (int) results.stream().filter(r -> r.vertices > 10 && r.vertices <= 20).count();
        int largeCount = (int) results.stream().filter(r -> r.vertices > 20 && r.vertices <= 50).count();

        sb.append("\nDATASET CATEGORIES:\n");
        sb.append(String.format("  • Small (6-10 nodes): %d datasets\n", smallCount));
        sb.append(String.format("  • Medium (10-20 nodes): %d datasets\n", mediumCount));
        sb.append(String.format("  • Large (20-50 nodes): %d datasets\n\n", largeCount));

        long cyclicCount = results.stream().filter(r -> "Cyclic".equals(r.graphType)).count();
        long dagCount = results.stream().filter(r -> "DAG".equals(r.graphType)).count();

        sb.append("GRAPH TYPES:\n");
        sb.append(String.format("  • Pure DAGs: %d (no cycles, direct topological sort)\n", dagCount));
        sb.append(String.format("  • Cyclic graphs: %d (require SCC compression)\n\n", cyclicCount));

        double avgDensity = results.stream()
                .mapToDouble(r -> r.vertices > 0 ? (double) r.edges / r.vertices : 0)
                .average().orElse(0);
        double minDensity = results.stream()
                .mapToDouble(r -> r.vertices > 0 ? (double) r.edges / r.vertices : 0)
                .min().orElse(0);
        double maxDensity = results.stream()
                .mapToDouble(r -> r.vertices > 0 ? (double) r.edges / r.vertices : 0)
                .max().orElse(0);

        sb.append("DENSITY ANALYSIS (E/V ratio):\n");
        sb.append(String.format("  • Average: %.2f\n", avgDensity));
        sb.append(String.format("  • Range: %.2f - %.2f\n", minDensity, maxDensity));
        sb.append("  • Interpretation:\n");
        sb.append("    - Sparse (E/V < 1.3): Few dependencies, faster processing\n");
        sb.append("    - Medium (1.3 ≤ E/V < 2.0): Moderate complexity\n");
        sb.append("    - Dense (E/V ≥ 2.0): Many dependencies, more edge operations\n\n");

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
        return sb.toString();
    }

    private static String structureBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("EFFECT OF GRAPH STRUCTURE ON PERFORMANCE\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("DENSITY IMPACT (E/V ratio vs SCC time):\n");
        for (AnalysisResult r : results) {
            double evRatio = (r.vertices > 0) ? (double) r.edges / r.vertices : 0.0;
            String densityClass = evRatio < 1.3 ? "Sparse" :
                    evRatio < 2.0 ? "Medium" : "Dense";
            sb.append(String.format("  • %-15s E/V=%.2f (%s) → SCC: %6.2f µs, Topo: %6.2f µs\n",
                    r.datasetName, evRatio, densityClass,
                    r.sccTimeMs * 1000, r.topoTimeMs * 1000));
        }

        long cyclicCount = results.stream().filter(r -> "Cyclic".equals(r.graphType)).count();
        sb.append("\nCYCLE IMPACT:\n");
        sb.append(String.format("  • Cyclic graphs: %d/%d datasets\n", cyclicCount, results.size()));

        if (cyclicCount > 0) {
            double avgCyclicSCC = results.stream()
                    .filter(r -> "Cyclic".equals(r.graphType))
                    .mapToDouble(r -> r.sccTimeMs)
                    .average().orElse(0);
            double avgDAGSCC = results.stream()
                    .filter(r -> "DAG".equals(r.graphType))
                    .mapToDouble(r -> r.sccTimeMs)
                    .average().orElse(0);

            sb.append(String.format("  • Avg SCC time (Cyclic): %.4f ms\n", avgCyclicSCC));
            sb.append(String.format("  • Avg SCC time (DAG):    %.4f ms\n", avgDAGSCC));

            if (avgDAGSCC > 0) {
                double overhead = ((avgCyclicSCC - avgDAGSCC) / avgDAGSCC) * 100;
                sb.append(String.format("  • Cyclic overhead: %.1f%%\n", overhead));
            }
        }

        sb.append("\nSCC SIZE DISTRIBUTION:\n");
        for (AnalysisResult r : results) {
            if (r.numSCCs < r.vertices) {
                sb.append(String.format("  • %-15s %d SCCs for %d nodes (compression ratio: %.2f)\n",
                        r.datasetName, r.numSCCs, r.vertices,
                        (double) r.vertices / r.numSCCs));
            }
        }

        sb.append('\n');
        return sb.toString();
    }

    private static String complexityAnalysisBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("COMPLEXITY VERIFICATION: O(V + E)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("Expected: All algorithms should scale linearly with (V + E)\n\n");

        sb.append(String.format("%-15s | %6s | %6s | %8s | %12s | %10s\n",
                "Dataset", "V", "E", "V+E", "SCC (µs)", "µs/(V+E)"));
        sb.append("-".repeat(85)).append('\n');

        for (AnalysisResult r : results) {
            int vPlusE = r.vertices + r.edges;
            double timePerUnit = vPlusE > 0 ? (r.sccTimeMs * 1000) / vPlusE : 0;
            sb.append(String.format("%-15s | %6d | %6d | %8d | %12.2f | %10.2f\n",
                    r.datasetName, r.vertices, r.edges, vPlusE,
                    r.sccTimeMs * 1000, timePerUnit));
        }

        return sb.toString();
    }

    private static String criticalPathBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    private static String conclusionsBlock(List<AnalysisResult> results) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}