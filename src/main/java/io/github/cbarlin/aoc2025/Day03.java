package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class Day03 {

    private static final String testInput = "day03/test.txt";
    private static final String realInput = "day03/real.txt";

    record Result(long twoBatteries, long twelveBatteries) {}

    private static int highestIndexBetween(final int[] search, final int lowerBound, final int upperBound) {
        int largest = 0;
        int index = 0;
        for (int i = lowerBound; i < upperBound; i++) {
            if (search[i] > largest) {
                largest = search[i];
                index = i;
            }
        }
        return index;
    }

    private static Result processLine(final Result accum, final String line) {
        final int[] search = line.chars().map(i -> i - '0').toArray();
        final int firstIndex = highestIndexBetween(search, 0, search.length - 1);
        final int secondIndex = highestIndexBetween(search, firstIndex + 1, search.length);
        final long twoBatteries = (search[firstIndex] * 10L) + search[secondIndex];
        long twelveBatteries = 0;
        int currIndex = 0;
        for (int i = 12; i > 0; i--) {
            currIndex = highestIndexBetween(search, currIndex, search.length - i + 1) + 1;
            twelveBatteries = (twelveBatteries * 10) + search[currIndex - 1];
        }
        return new Result(twoBatteries + accum.twoBatteries, twelveBatteries + accum.twelveBatteries);
    }

    private static Result solveDay(final String input) throws IOException {
        try (final Stream<String> loading = Files.lines(Path.of("src/main/resources/" + input))) {
            return loading.gather(
                    Gatherers.fold(
                            () -> new Result(0L, 0L),
                            Day03::processLine
                    )
            ).findFirst()
            .orElseThrow();
        }
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput);
            System.out.println("Day 03 Test part 1: " + testResult.twoBatteries);
            System.out.println("Day 03 Test part 2: " + testResult.twelveBatteries);
            if (testResult.twoBatteries != 357) throw new AssertionError();
            if (testResult.twelveBatteries != 3121910778619L) throw new AssertionError();
            final Result realResult = solveDay(realInput);
            System.out.println("Day 03 Real part 1: " + realResult.twoBatteries);
            System.out.println("Day 03 Real part 2: " + realResult.twelveBatteries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
