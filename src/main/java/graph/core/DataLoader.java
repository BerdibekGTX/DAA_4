package graph.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataLoader {

    private static final String DATA_PATH = "src/main/resources/data/";

    public static Graph loadDataset(String filename) throws IOException {
        String content = Files.readString(Paths.get(DATA_PATH + filename));
        return parseJSON(content);
    }

    private static Graph parseJSON(String json) {
        JSONObject obj = new JSONObject(json);

        int n = obj.getInt("n");
        Graph g = new Graph(n);

        if (obj.has("vertices")) {
            JSONArray vtx = obj.getJSONArray("vertices");
            for (int i = 0; i < vtx.length(); i++) {
                JSONObject v = vtx.getJSONObject(i);
                g.setVertexWeight(v.getInt("id"), v.optDouble("weight", 1.0));
            }
        } else {
            for (int i = 0; i < n; i++) g.setVertexWeight(i, 1.0);
        }

        JSONArray edges = obj.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            int from = e.getInt("from");
            int to = e.getInt("to");
            double w = e.optDouble("weight", 1.0);
            g.addEdge(from, to, w);
        }

        return g;
    }

    public static List<String> getAvailableDatasets() {
        File dir = new File(DATA_PATH);
        if (!dir.exists()) return Collections.emptyList();

        File[] files = dir.listFiles((d, n) -> n.endsWith(".json"));
        if (files == null) return Collections.emptyList();

        Arrays.sort(files);
        List<String> result = new ArrayList<>();
        for (File f : files) result.add(f.getName());
        return result;
    }

    public static Graph createSampleDataset(String type) {
        switch (type.toLowerCase()) {
            case "small1": return createSmall1();
            case "small2": return createSmall2();
            case "small3": return createSmall3();
            case "medium1": return createMedium1();
            case "medium2": return createMedium2();
            case "medium3": return createMedium3();
            case "large1": return createLarge1();
            case "large2": return createLarge2();
            case "large3": return createLarge3();
            default: return createSmall1();
        }
    }

    private static Graph createSmall1() {
        Graph g = new Graph(8);
        int[][] edges = {{0,1},{0,2},{1,3},{2,3},{2,4},{3,5},{4,5},{4,6},{5,7},{6,7}};
        double[] weights = {1,1.5,2,1,1.5,2,1,1.5,1,2};
        for (int i = 0; i < edges.length; i++) g.addEdge(edges[i][0], edges[i][1], weights[i]);
        return g;
    }

    private static Graph createSmall2() {
        Graph g = new Graph(6);
        int[][] edges = {{0,1},{1,2},{2,3},{3,0},{3,4},{4,5}};
        double[] w = {1,1,1,1,2,1.5};
        for (int i = 0; i < edges.length; i++) g.addEdge(edges[i][0], edges[i][1], w[i]);
        return g;
    }

    private static Graph createSmall3() {
        Graph g = new Graph(7);
        int[][] edges = {{0,1},{1,2},{2,0},{2,3},{3,4},{4,5},{5,6}};
        double[] w = {1,1,1,2,1,1.5,1};
        for (int i = 0; i < edges.length; i++) g.addEdge(edges[i][0], edges[i][1], w[i]);
        return g;
    }

    private static Graph createMedium1() {
        Graph g = new Graph(15);
        int[][] edges={{0,1},{0,2},{1,3},{2,3},{2,4},{3,5},{4,6},{5,7},{6,7},
                {7,8},{8,9},{9,10},{10,11},{11,12},{12,13},{12,14},{11,14}};
        double[] w={1,1.5,2,1,1.5,2,1,1.5,1,2,1.3,1.7,1.2,1.8,1.5,2,1.4};
        for(int i=0;i<edges.length;i++) g.addEdge(edges[i][0],edges[i][1],w[i]);
        return g;
    }

    private static Graph createMedium2() {
        int n = 12;
        Graph g = new Graph(n);
        Random r = new Random(45);

        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < Math.min(i + 4, n); j++)
                if (r.nextDouble() < 0.7)
                    g.addEdge(i, j, 0.5 + r.nextDouble() * 1.5);

        return g;
    }


    private static Graph createMedium3() {
        Graph g = new Graph(14);
        int[][] edges={{0,1},{1,2},{2,0},{1,3},{3,4},{4,5},{5,3},{5,6},{6,7},{7,8},{8,9},{9,8},{9,10},{10,11},{11,12},{12,13}};
        for (int[] e : edges) g.addEdge(e[0], e[1], 1.0);
        return g;
    }

    private static Graph createLarge1() {
        Graph g = new Graph(35);
        Random rand = new Random(44);
        for (int i = 0; i < 34; i++) g.addEdge(i, i+1, 0.5 + rand.nextDouble()*2);
        g.addEdge(10,20,1.5);
        g.addEdge(15,25,1.2);
        g.addEdge(20,30,1.3);
        return g;
    }

    private static Graph createLarge2() {
        int n = 25;
        Graph g = new Graph(n);
        Random r = new Random(45);

        for (int i = 0; i < n; i++)
            for (int j = i+1; j < Math.min(i+4, n); j++)
                if (r.nextDouble() < 0.7)
                    g.addEdge(i, j, 0.5 + r.nextDouble() * 1.5);

        return g;
    }


    private static Graph createLarge3() {
        Graph g = new Graph(40);
        Random r = new Random(46);
        for (int i=0;i<39;i++) g.addEdge(i,i+1,0.5+r.nextDouble()*2);
        int[][] links={{5,15},{10,20},{15,25},{20,30},{3,12},{8,18},{25,35}};
        for(int[] e:links) g.addEdge(e[0],e[1],1.5);
        return g;
    }
}
