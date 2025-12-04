package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public final class Day02 {
    private static final String testInput = "day02/test.txt";
    private static final String realInput = "day02/real.txt";
    // Let's just do this once...
    private static final long[] POWERS_OF_TEN = IntStream.range(0, 19)
            .mapToLong(i -> (long) Math.pow(10, i))
            .filter(l -> l >= 0)
            .toArray();

    private record Result (
            long byHalvesSum,
            long anyRepeatsSum
    ) {}

    private static int numberOfDigits(final long x) {
        int length = 0;
        long temp = 1;
        while (temp <= x) {
            length++;
            temp *= 10;
        }
        return length;
    }

    // Generate possible numbers rather than looping to work it out
    // Basically, for e.g. 123123 it's 123 * (10 ^ 3 + 1)
    // Or 121212 it's 12 * (10^4 + 10^2 + 1)
    // Work out, from possible multipliers, valid "base" numbers to multiply in!
    private static Result sumNumbersWithRepeating(final long start, final long end) {
        long byHalvesSum = 0;
        long anyRepeatsSum = 0;

        final Set<Long> visited = new HashSet<>();
        final int minDigits = numberOfDigits(start);
        final int maxDigits = numberOfDigits(end);

        // Loop between the total digit lengths (e.g the start is 99 and end is 12345 - do 2, 3, 4, 5)
        for (int totalLength = minDigits; totalLength <= maxDigits; totalLength++) {
            // Is the current totalLength halvable?
            final boolean halvable = totalLength % 2 == 0;
            // How much should we try and increment the power of 10 search?
            // e.g. 1111111, 1010101
            for (int powerOfTenIncrement = 1; powerOfTenIncrement <= totalLength / 2; powerOfTenIncrement++) {
                if (totalLength % powerOfTenIncrement != 0) {
                    continue;
                }
                final boolean isHalfPattern = halvable && powerOfTenIncrement == (totalLength / 2);
                // Calculate Multiplier.
                // E.g. totalLength 6, powerOfTenIncrement 2 (121212) -> Multiplier = 10101
                long multiplier = 0;
                for (int k = 0; k < totalLength; k += powerOfTenIncrement) {
                    multiplier += POWERS_OF_TEN[k];
                }
                // Work out the "range" of base numbers we can use (e.g. 1038-9863 should have 11 -> 63 as possible bases)
                final long currentStartBase = Math.max(POWERS_OF_TEN[powerOfTenIncrement - 1], (start + multiplier - 1) / multiplier);
                final long currentEndBase = Math.min(POWERS_OF_TEN[powerOfTenIncrement] - 1, end / multiplier);

                for (long base = currentStartBase; base <= currentEndBase; base++) {
                    long val = base * multiplier;
                    // Only add if not already seen
                    if (visited.add(val)) {
                        anyRepeatsSum += val;
                    }
                    if (isHalfPattern) {
                        byHalvesSum += val;
                    }
                }
            }
        }

        return new Result(byHalvesSum, anyRepeatsSum);
    }

    private static Result sumNumbersWithRepeating(final String input) {
        final Path path = Path.of("src/main/resources/" + input);
        String in;
        try {
            in = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long byHalvesSum = 0;
        long anyRepeatsSum = 0;
        while (!in.isEmpty()) {
            final int chop = in.indexOf(',');
            final String range = chop != -1 ? in.substring(0, chop) : in;
            if (chop != -1) {
                in = in.substring(chop + 1);
            } else {
                in = "";
            }
            final int chop2 = range.indexOf('-');
            final long first = Long.parseLong(range.substring(0, chop2));
            final long second = Long.parseLong(range.substring(chop2 + 1));

            final Result processed = sumNumbersWithRepeating(first, second);
            byHalvesSum += processed.byHalvesSum;
            anyRepeatsSum += processed.anyRepeatsSum;
        }
        return new Result(byHalvesSum, anyRepeatsSum);
    }

    public static void runDay() {
        final Result testResult = sumNumbersWithRepeating(testInput);
        System.out.println("Day 02 Test part 1: " + testResult.byHalvesSum);
        System.out.println("Day 02 Test part 2: " + testResult.anyRepeatsSum);
        if (testResult.byHalvesSum != 1227775554L) throw new AssertionError();
        if (testResult.anyRepeatsSum != 4174379265L) throw new AssertionError();
        final Result realResult = sumNumbersWithRepeating(realInput);
        System.out.println("Day 02 Real part 1: " + realResult.byHalvesSum);
        System.out.println("Day 02 Real part 2: " + realResult.anyRepeatsSum);
        if (realResult.byHalvesSum != 31000881061L) throw new AssertionError();
        if (realResult.anyRepeatsSum != 46769308485L) throw new AssertionError();
    }


}
