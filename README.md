# Assignment 4: Smart City / Smart Campus Scheduling Case
### Student: Amir Berdibek

### Group: SE-2429

### Teacher: Sayakulova Zarina
## Goal:
In a smart campus scheduling system, courses and classroom locations are modeled as a directed graph. First, we detect Strongly Connected Components to group courses that depend on each other. Then, we apply Topological Ordering to determine the correct sequence of these course modules. After forming a DAG, we use Shortest Path in DAG algorithms to find the fastest student routes between buildings and minimize travel time across campus. This combined approach helps optimize both academic scheduling and campus movement efficiently.

## Project Structure
```text
DAA_4/
├── pom.xml
├── README.md
├── .gitignore
│
├── src/
│   ├── main/java/graph/
│   │   ├── core/
│   │   │   ├── Graph.java
│   │   │   ├── Metrics.java
│   │   │   └── DataLoader.java
│   │   ├── scc/
│   │   │   ├── TarjanSCC.java
│   │   │   ├── Component.java
│   │   │   └── CondensationGraph.java
│   │   ├── topo/
│   │   │   ├── TopoSortKahn.java
│   │   │   └── TopoSortDFS.java
│   │   ├── dagsp/
│   │   │   ├── DAGLongestPath.java
│   │   │   ├── DAGShortestPaths.java
│   │   │   └── PathResult.java
│   │   ├── dataset/
│   │   │   └── GenerateAllDatasets.java
│   │   └── application/
│   │       └── SmartCityScheduler.java
│   ├── main/resources/data/
│   │   ├── small1.json
│   │   ├── small2.json
│   │   ├── small3.json
│   │   ├── medium1.json
│   │   ├── medium2.json
│   │   ├── medium3.json
│   │   ├── large1.json
│   │   ├── large2.json
│   │   └── large3.json
│   └── test/java/graph/
│       ├── scc/TarjanSCCTest.java
│       ├── topo/TopoSortTest.java
│       └── dagsp/DAGSPTest.java
│
└── report/
    └── analysis_results.txt
```
---
## Dataset Summary

| Dataset  | Nodes | Edges | E/V  | Type   | SCCs | Crit.Path |
|----------|------:|------:|-----:|--------|-----:|----------:|
| large1   |    35 |    34 | 0.97 | DAG    |   35 |     69.00 |
| large2   |    25 |    39 | 1.56 | DAG    |   25 |      3.00 |
| large3   |    40 |    46 | 1.15 | DAG    |   40 |     79.00 |
| medium1  |    15 |    17 | 1.13 | DAG    |   15 |     27.50 |
| medium2  |    12 |    21 | 1.75 | DAG    |   12 |     25.30 |
| medium3  |    14 |    16 | 1.14 | Cyclic |    9 |      1.00 |
| small1   |     6 |     6 | 1.00 | Cyclic |    3 |      1.00 |
| small2   |     8 |     9 | 1.13 | DAG    |    8 |     11.00 |
| small3   |     7 |     7 | 1.00 | Cyclic |    5 |      1.00 |

| Category | Node Range | Count |
|---------|-----------|-------|
| Small  | 6–10 nodes  | 3 |
| Medium | 10–20 nodes | 3 |
| Large  | 20–50 nodes | 3 |

| Type | Description | Count |
|------|-------------|-------|
| **DAGs** | Directed Acyclic Graphs (no cycles) | 6 |
| **Cyclic Graphs** | Contain at least one cycle | 3 |

**Total datasets:** 9 graphs
---
## More details are available in analysis_results.txt.

---

## Key Observations

Higher edge density increases work, but performance remains linear.

Cycle-heavy graphs compress effectively via SCC.

Handles real‑world scheduling workloads efficiently.

## Conclusion

The system successfully evaluates campus task dependencies, resolves cycles, computes valid execution ordering, and finds optimal timing via critical path analysis.

## References
- Astana IT University — DAA Course Materials. https://lms.astanait.edu.kz
- MIT OpenCourseWare — (Algorithms).
 https://ocw.mit.edu/
(Course material for DFS, SCCs, DAG algorithms)
- Cloude-AI(Used only for understanding and clarifying information, not for computation or algorithm execution). https://claude.ai/
