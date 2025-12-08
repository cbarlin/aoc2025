package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Day08 {
    private static final String testInput = "src/main/resources/day08/test.txt";
    private static final int testIterations = 10;
    private static final String realInput = "src/main/resources/day08/real.txt";
    private static final int realIterations = 1000;

    private record Point(
        long x, long y, long z
    ) {
        public static Point parse(final String line) {
            final String[] split = line.split(",");
            return new Point(
                Long.parseLong(split[0]),
                Long.parseLong(split[1]),
                Long.parseLong(split[2])
            );
        }

        double distanceTo(final Point other) {
            return Math.sqrt(
                Math.pow(x - other.x, 2) +
                Math.pow(y - other.y, 2) +
                Math.pow(z - other.z, 2)
            );
        }
    }

    // Kruskal's Algorithm
    private record Edge (
        int indxA, int indexB, double distance
    )  implements Comparable<Edge> {
        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    static class KruskalsUnionFind {
        private final int[] parent;
        private final int[] rank;

        public KruskalsUnionFind(int numberOfElements) {
            parent = new int[numberOfElements];
            rank = new int[numberOfElements];

            for (int i = 0; i < numberOfElements; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int i) {
            if (parent[i] != i) {
                parent[i] = find(parent[i]);
            }
            return parent[i];
        }

        public boolean union(int indxA, int indexB) {
            int rootA = find(indxA);
            int rootB = find(indexB);

            if (rootA == rootB) {
                return false;
            }

            // Union by rank
            if (rank[rootA] < rank[rootB]) {
                parent[rootA] = rootB;
            } else if (rank[rootA] > rank[rootB]) {
                parent[rootB] = rootA;
            } else {
                parent[rootA] = rootB;
                rank[rootB]++;
            }
            return true;
        }
    }

    private record Result(
        long part1,
        long part2
    ) {}

    private static Result solveDay(final String input, final int iterations) throws IOException {
        // First, let's find all the points
        final List<Point> points;
        try (final Stream<String> loading = Files.lines(Path.of(input))) {
            points = loading.map(Point::parse).toList();
        }
        final List<Edge> allEdges = new ArrayList<>();
        for (int iA = 0; iA < points.size(); iA++) {
            for (int iB = iA + 1; iB < points.size(); iB++) {
                double dist = points.get(iA).distanceTo(points.get(iB));
                allEdges.add(new Edge(iA, iB, dist));
            }
        }
        Collections.sort(allEdges);

        final KruskalsUnionFind kuf = new KruskalsUnionFind(points.size());
        int unionedTimes = 0;
        int remainingIterations = iterations - 1;
        long part1 = 0;
        long part2 = 0;
        for (final Edge edge : allEdges) {
            if(kuf.union(edge.indxA, edge.indexB)) {
                unionedTimes++;
                if (unionedTimes == points.size() - 1) {
                    part2 = points.get(edge.indxA).x * points.get(edge.indexB).x;
                    if (part1 != 0) {
                        break;
                    }
                }
            }

            if (remainingIterations == 0) {
                final List<Integer> lengths = getLongestCircuits(points, kuf);
                part1 = ((long) lengths.getFirst()) * lengths.get(1) * lengths.get(2);
                if (part2 != 0) {
                    break;
                }
                remainingIterations--;
            } else if (remainingIterations > 0) {
                remainingIterations--;
            }
        }
        return new Result(
                part1,
                part2
        );
    }

    private static List<Integer> getLongestCircuits(final List<Point> points, final KruskalsUnionFind uf) {
        final Map<Integer, Integer> circuits = new HashMap<>();
        for(int i = 0; i < points.size(); i++){
            int root = uf.find(i);
            circuits.put(root, circuits.getOrDefault(root, 0) + 1);
        }

        List<Integer> circuitLengths = new ArrayList<>(circuits.values());
        circuitLengths.sort(Collections.reverseOrder());
        return circuitLengths;
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput, testIterations);
            System.out.println("Day 08 Test part 1: " + testResult.part1);
            System.out.println("Day 08 Test part 2: " + testResult.part2);
            if (testResult.part1 != 40) throw new AssertionError();
            if (testResult.part2 != 25272) throw new AssertionError();
            final Result realResult = solveDay(realInput, realIterations);
            System.out.println("Day 08 Real part 1: " + realResult.part1);
            System.out.println("Day 08 Real part 2: " + realResult.part2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
