package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class Day05 {
    private static final String testInput = "src/main/resources/day05/test.txt";
    private static final String realInput = "src/main/resources/day05/real.txt";

    private record Range(long start, long end) implements Comparable<Range> {
        boolean overlaps(final Range other) {
            return Math.max(start, other.start) <= Math.min(end, other.end);
        }

        Range merge(final Range other) {
            return new Range(Math.min(start, other.start), Math.max(end, other.end));
        }

        long size() {
            return Math.max(0, end - start + 1);
        }

        @Override
        public int compareTo(Range range) {
            if (start > range.start) {
                return 1;
            } else if (range.start > start) {
                return -1;
            }
            return 0;
        }
    }

    private record Result(
            List<Range> ranges,
            long part1,
            long part2
    ) {}

    private static Result parseRange(final Result soFar, final String line, final int index) {
        final long start = Long.parseLong(line.substring(0, index));
        final long end = Long.parseLong(line.substring(index + 1));
        final List<Range> ranges = new ArrayList<>(soFar.ranges.size() + 1);
        ranges.addAll(soFar.ranges);
        ranges.add(new Range(start, end));
        return new Result(ranges, soFar.part1, soFar.part2);
    }

    private static Result parseNumber(final Result soFar, final String line) {
        final long num = Long.parseLong(line);
        for (final Range range : soFar.ranges) {
            if (range.start() <= num && range.end() >= num) {
                return new Result(soFar.ranges, soFar.part1 + 1, soFar.part2);
            }
        }
        return soFar;
    }

    private static Result processLine(final Result soFar, final String line) {
        final int index = line.indexOf('-');
        if (index > 0) {
            return parseRange(soFar, line, index);
        } else {
            return parseNumber(soFar, line);
        }
    }

    private static Result obtainPart2(final Result result) {
        final List<Range> toCollapse = new ArrayList<>(result.ranges);
        Collections.sort(toCollapse);
        Range currRange = toCollapse.getFirst();
        long ans = 0;
        for (final Range range : toCollapse) {
            if (!range.overlaps(currRange)) {
                ans += currRange.size();
                currRange = range;
            } else {
                currRange = range.merge(currRange);
            }
        }
        return new Result(result.ranges, result.part1, ans + currRange.size());
    }

    private static Result solveDay(final String input) throws IOException {
        try (final Stream<String> loading = Files.lines(Path.of(input))) {
            final Result tmp = loading.filter(Predicate.not(String::isBlank))
                    .gather(
                        Gatherers.fold(
                                () -> new Result(List.of(), 0L, 0L),
                                Day05::processLine
                        )
                    ).findFirst()
                    .orElseThrow();
            return obtainPart2(tmp);
        }
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput);
            System.out.println("Day 05 Test part 1: " + testResult.part1);
            System.out.println("Day 05 Test part 2: " + testResult.part2);
            if (testResult.part1 != 3) throw new AssertionError();
            if (testResult.part2 != 14) throw new AssertionError();
            final Result realResult = solveDay(realInput);
            System.out.println("Day 05 Real part 1: " + realResult.part1);
            System.out.println("Day 05 Real part 2: " + realResult.part2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
