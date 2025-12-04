package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class Day01 {

    //#region Input
    private static final String testInput = "day01/test.txt";

    private static final String realInput = "day01/real.txt";

    //endregion

    private static SafeCodeState processInput(final String inputPath) throws IOException {
        try (final Stream<String> loading = Files.lines(Path.of("src/main/resources/" + inputPath))) {
            return loading.gather(
                    Gatherers.fold(
                            () -> new SafeCodeState(50, 0, 0),
                            (final SafeCodeState s, final String str) -> {
                                if (str.startsWith("L")) {
                                    return s.next(-Integer.parseInt(str.substring(1)));
                                } else {
                                    return s.next(Integer.parseInt(str.substring(1)));
                                }
                            }
                    )
            ).findFirst()
            .orElseThrow();
        }
    }

    public static void runDay() {
        try {
            final SafeCodeState testResult = processInput(testInput);
            System.out.println("Day 01 Test part 1: " + testResult.landedOnZeros);
            System.out.println("Day 01 Test part 2: " + testResult.passedZero);
            if (testResult.landedOnZeros != 3) throw new AssertionError();
            if (testResult.passedZero != 6) throw new AssertionError();
            final SafeCodeState realResult = processInput(realInput);
            System.out.println("Day 01 Real part 1: " + realResult.landedOnZeros);
            System.out.println("Day 01 Real part 2: " + realResult.passedZero);
            if (realResult.landedOnZeros != 1023) throw new AssertionError();
            if (realResult.passedZero != 5899) throw new AssertionError();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private record SafeCodeState(
        int position,
        long landedOnZeros,
        long passedZero
    ) {
        public SafeCodeState next(int move) {
            if (move == 0) {
                return this;
            }
            final int newPos = Math.floorMod(position + move, 100);
            final long newLanded = (newPos == 0) ? landedOnZeros + 1 : landedOnZeros;
            final long newPassed = passedZero + timesSeenZero(move, position);

            return new SafeCodeState(newPos, newLanded, newPassed);
        }

        private static long timesSeenZero(final int move, final int position) {
            if (move > 0) {
                return (position + move) / 100L;
            } else {
                // Shift by -1 so that hitting the boundary counts (e.g. 1 --> 0)
                return Math.floorDiv(position - 1, 100) - Math.floorDiv(position + move - 1, 100);
            }
        }
    }
}
