package dataset;
import java.util.Random;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateAllDatasets {

    public static void main(String[] args) throws IOException {
        String dataDir = "src/main/resources/data";
        new File(dataDir).mkdirs();
        System.out.println("\n All 9 datasets created in src/main/resources/data/\n");
    }

    private static void generateSmall1(String dir) throws IOException {
        System.out.print("  small1.json (8 nodes, DAG) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 8,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0},\n" +
                "    {\"u\": 0, \"v\": 2, \"w\": 1.5},\n" +
                "    {\"u\": 1, \"v\": 3, \"w\": 2.0},\n" +
                "    {\"u\": 2, \"v\": 3, \"w\": 1.0},\n" +
                "    {\"u\": 2, \"v\": 4, \"w\": 1.5},\n" +
                "    {\"u\": 3, \"v\": 5, \"w\": 2.0},\n" +
                "    {\"u\": 4, \"v\": 5, \"w\": 1.0},\n" +
                "    {\"u\": 4, \"v\": 6, \"w\": 1.5},\n" +
                "    {\"u\": 5, \"v\": 7, \"w\": 1.0},\n" +
                "    {\"u\": 6, \"v\": 7, \"w\": 2.0}\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Pure DAG: 8 nodes, 10 edges. Multiple paths from 0 to 7. Tests basic DAG algorithms.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/small1.json"), json.getBytes());
        System.out.println("✓");
    }

    private static void generateSmall2(String dir) throws IOException {
        System.out.print("  small2.json (6 nodes, cyclic) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 6,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0},\n" +
                "    {\"u\": 1, \"v\": 2, \"w\": 1.0},\n" +
                "    {\"u\": 2, \"v\": 3, \"w\": 1.0},\n" +
                "    {\"u\": 3, \"v\": 0, \"w\": 1.0},\n" +
                "    {\"u\": 3, \"v\": 4, \"w\": 2.0},\n" +
                "    {\"u\": 4, \"v\": 5, \"w\": 1.5}\n" +
                "  ],\n" +
                "  \"source\": 3,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Cyclic: nodes 0,1,2,3 form cycle. Then 3->4->5. Tests SCC detection.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/small2.json"), json.getBytes());
        System.out.println("✓");
    }

    private static void generateSmall3(String dir) throws IOException {
        System.out.print("  small3.json (7 nodes, mixed) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 7,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0},\n" +
                "    {\"u\": 1, \"v\": 2, \"w\": 1.0},\n" +
                "    {\"u\": 2, \"v\": 0, \"w\": 1.0},\n" +
                "    {\"u\": 2, \"v\": 3, \"w\": 2.0},\n" +
                "    {\"u\": 3, \"v\": 4, \"w\": 1.0},\n" +
                "    {\"u\": 4, \"v\": 5, \"w\": 1.5},\n" +
                "    {\"u\": 5, \"v\": 6, \"w\": 1.0}\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Mixed: cycle 0->1->2->0, then DAG 2->3->4->5->6. Tests SCC compression.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/small3.json"), json.getBytes());
        System.out.println("✓");
    }

    // ===== MEDIUM DATASETS =====

    private static void generateMedium1(String dir) throws IOException {
        System.out.print("  medium1.json (15 nodes, sparse) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 15,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0}, {\"u\": 0, \"v\": 2, \"w\": 1.5},\n" +
                "    {\"u\": 1, \"v\": 3, \"w\": 2.0}, {\"u\": 2, \"v\": 3, \"w\": 1.0},\n" +
                "    {\"u\": 2, \"v\": 4, \"w\": 1.5}, {\"u\": 3, \"v\": 5, \"w\": 2.0},\n" +
                "    {\"u\": 4, \"v\": 6, \"w\": 1.0}, {\"u\": 5, \"v\": 7, \"w\": 1.5},\n" +
                "    {\"u\": 6, \"v\": 7, \"w\": 1.0}, {\"u\": 7, \"v\": 8, \"w\": 2.0},\n" +
                "    {\"u\": 8, \"v\": 9, \"w\": 1.3}, {\"u\": 9, \"v\": 10, \"w\": 1.7},\n" +
                "    {\"u\": 10, \"v\": 11, \"w\": 1.2}, {\"u\": 11, \"v\": 12, \"w\": 1.8},\n" +
                "    {\"u\": 12, \"v\": 13, \"w\": 1.5}, {\"u\": 12, \"v\": 14, \"w\": 2.0},\n" +
                "    {\"u\": 11, \"v\": 14, \"w\": 1.4}\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Sparse DAG: 15 nodes, 17 edges (≈1.1 E/V). Tests scalability on sparse graphs.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/medium1.json"), json.getBytes());
        System.out.println("✓");
    }

    private static void generateMedium2(String dir) throws IOException {
        System.out.print("  medium2.json (12 nodes, dense) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 12,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0}, {\"u\": 0, \"v\": 2, \"w\": 1.5}, {\"u\": 0, \"v\": 3, \"w\": 1.2},\n" +
                "    {\"u\": 1, \"v\": 2, \"w\": 1.3}, {\"u\": 1, \"v\": 4, \"w\": 1.1}, {\"u\": 2, \"v\": 3, \"w\": 1.4},\n" +
                "    {\"u\": 2, \"v\": 5, \"w\": 1.2}, {\"u\": 3, \"v\": 4, \"w\": 1.0}, {\"u\": 3, \"v\": 6, \"w\": 1.5},\n" +
                "    {\"u\": 4, \"v\": 5, \"w\": 1.3}, {\"u\": 4, \"v\": 7, \"w\": 1.2}, {\"u\": 5, \"v\": 6, \"w\": 1.1},\n" +
                "    {\"u\": 5, \"v\": 8, \"w\": 1.4}, {\"u\": 6, \"v\": 7, \"w\": 1.0}, {\"u\": 6, \"v\": 9, \"w\": 1.3},\n" +
                "    {\"u\": 7, \"v\": 8, \"w\": 1.2}, {\"u\": 7, \"v\": 10, \"w\": 1.1}, {\"u\": 8, \"v\": 9, \"w\": 1.5},\n" +
                "    {\"u\": 8, \"v\": 11, \"w\": 1.0}, {\"u\": 9, \"v\": 10, \"w\": 1.2}, {\"u\": 10, \"v\": 11, \"w\": 1.3}\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Dense DAG: 12 nodes, 21 edges (≈1.75 E/V). Tests performance on dense structures.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/medium2.json"), json.getBytes());
        System.out.println("✓");
    }

    private static void generateMedium3(String dir) throws IOException {
        System.out.print("  medium3.json (14 nodes, multiple SCCs) ... ");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 14,\n" +
                "  \"edges\": [\n" +
                "    {\"u\": 0, \"v\": 1, \"w\": 1.0}, {\"u\": 1, \"v\": 2, \"w\": 1.0}, {\"u\": 2, \"v\": 0, \"w\": 1.0},\n" +
                "    {\"u\": 1, \"v\": 3, \"w\": 2.0}, {\"u\": 3, \"v\": 4, \"w\": 1.0}, {\"u\": 4, \"v\": 5, \"w\": 1.5},\n" +
                "    {\"u\": 5, \"v\": 3, \"w\": 1.0}, {\"u\": 5, \"v\": 6, \"w\": 2.0}, {\"u\": 6, \"v\": 7, \"w\": 1.0},\n" +
                "    {\"u\": 7, \"v\": 8, \"w\": 1.5}, {\"u\": 8, \"v\": 9, \"w\": 1.2}, {\"u\": 9, \"v\": 8, \"w\": 1.0},\n" +
                "    {\"u\": 9, \"v\": 10, \"w\": 2.0}, {\"u\": 10, \"v\": 11, \"w\": 1.0}, {\"u\": 11, \"v\": 12, \"w\": 1.5},\n" +
                "    {\"u\": 12, \"v\": 13, \"w\": 1.0}\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "  \"description\": \"Multiple SCCs: SCC1(0,1,2), SCC2(3,4,5), SCC3(8,9), then DAG parts. Tests SCC detection and compression.\"\n" +
                "}";

        Files.write(Paths.get(dir + "/medium3.json"), json.getBytes());
        System.out.println("✓");
    }

    // ===== LARGE DATASETS =====

    private static void generateLarge1(String dir) throws IOException {
        System.out.print("  large1.json (35 nodes, sparse) ... ");

        StringBuilder edges = new StringBuilder();
        for (int i = 0; i < 34; i++) {
            if (i > 0) edges.append(",\n    ");
            edges.append(String.format("{\"u\": %d, \"v\": %d, \"w\": 1.0}", i, i + 1));
        }
        edges.append(",\n    {\"u\": 10, \"v\": 20, \"w\": 1.5}");
        edges.append(",\n    {\"u\": 15, \"v\": 25, \"w\": 1.2}");
        edges.append(",\n    {\"u\": 20, \"v\": 30, \"w\": 1.3}");

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 35,\n" +
                "  \"edges\": [\n" +
                "    " + edges.toString() + "\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "}";

        Files.write(Paths.get(dir + "/large1.json"), json.getBytes());
        System.out.println("✓");
    }

    private static void generateLarge2(String dir) throws IOException {
        System.out.print("  large2.json (25 nodes, dense) ... ");

        StringBuilder edges = new StringBuilder();
        int edgeCount = 0;
        int n = 25;

        Random r = new Random(45);

        for (int i = 0; i < n && edgeCount < 50; i++) {
            for (int j = i + 1; j < Math.min(i + 4, n) && edgeCount < 50; j++) {
                if (r.nextDouble() < 0.7) {
                    if (edgeCount > 0) edges.append(",\n    ");
                    double w = 0.5 + r.nextDouble() * 1.5;
                    edges.append(String.format("{\"from\": %d, \"to\": %d, \"weight\": %.2f}", i, j, w));
                    edgeCount++;
                }
            }
        }

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 25,\n" +
                "  \"edges\": [\n" +
                "    " + edges.toString() + "\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\"\n" +
                "}";

        Files.write(Paths.get(dir + "/large2.json"), json.getBytes());
        System.out.println("✓");
    }


    private static void generateLarge3(String dir) throws IOException {
        System.out.print("  large3.json (40 nodes, complex) ... ");

        StringBuilder edges = new StringBuilder();
        int edgeCount = 0;

        for (int i = 0; i < 39; i++) {
            if (edgeCount > 0) edges.append(",\n    ");
            edges.append(String.format("{\"u\": %d, \"v\": %d, \"w\": 1.0}", i, i + 1));
            edgeCount++;
        }

        int[][] crossLinks = {{5, 15}, {10, 20}, {15, 25}, {20, 30}, {3, 12}, {8, 18}, {25, 35}};
        for (int[] link : crossLinks) {
            edges.append(",\n    ");
            edges.append(String.format("{\"u\": %d, \"v\": %d, \"w\": 1.5}", link[0], link[1]));
        }

        String json = "{\n" +
                "  \"directed\": true,\n" +
                "  \"n\": 40,\n" +
                "  \"edges\": [\n" +
                "    " + edges.toString() + "\n" +
                "  ],\n" +
                "  \"source\": 0,\n" +
                "  \"weight_model\": \"edge\",\n" +
                "}";

        Files.write(Paths.get(dir + "/large3.json"), json.getBytes());
        System.out.println("✓");
    }
}