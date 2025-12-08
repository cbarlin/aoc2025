package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class Day07 {
    private static final String testInput = "src/main/resources/day07/test.txt";
    private static final String realInput = "src/main/resources/day07/real.txt";

    private record Result(
        Map<Integer, Long> timelines,
        long part1,
        long part2
    ) {}

    private static Result processLine(final Result soFar, final String line) {
        final Map<Integer, Long> timelines = new HashMap<>(soFar.timelines);
        final char[] ln = line.toCharArray();
        long splits = 0;
        for (int i = 0; i < ln.length; i++) {
            final long count = timelines.getOrDefault(i, 0L);
            if (count > 0 && ln[i] == '^') {
                splits++;
                timelines.put(i + 1, timelines.getOrDefault(i + 1, 0L) + count);
                timelines.put(i - 1, timelines.getOrDefault(i - 1, 0L) + count);
                timelines.remove(i);
            }
            if (ln[i] == 'S') {
                timelines.put(i, 1L);
            }
        }

        return new Result(
            Map.copyOf(timelines),
            soFar.part1 + splits,
            timelines.values().stream().reduce(0L, Long::sum)
        );
    }

    private static Result solveDay(final String input) throws IOException {
        try (final Stream<String> loading = Files.lines(Path.of(input))) {
            return loading.gather(
                            Gatherers.fold(
                                    () -> new Result(Map.of(), 0L, 0L),
                                    Day07::processLine
                            )
                    ).findFirst()
                    .orElseThrow();
        }
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput);
            System.out.println("Day 07 Test part 1: " + testResult.part1);
            System.out.println("Day 07 Test part 2: " + testResult.part2);
            if (testResult.part1 != 21) throw new AssertionError();
            if (testResult.part2 != 40) throw new AssertionError();
            final Result realResult = solveDay(realInput);
            System.out.println("Day 07 Real part 1: " + realResult.part1);
            System.out.println("Day 07 Real part 2: " + realResult.part2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
