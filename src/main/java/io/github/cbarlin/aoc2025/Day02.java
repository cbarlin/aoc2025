package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class Day02 {
    private static final Logger TEST = Logger.getLogger("Day 02 Test");
    private static final Logger REAL = Logger.getLogger("Day 02 Real");
    private static final String testInput = "day02/test.txt";
    private static final String realInput = "day02/real.txt";

    private record Result (
            long byHalvesSum,
            long anyRepeatsSum
    ) {}

    private static int countUniqueSubstrings(final String input, final int fixedWindowSize) {
        final Set<String> uniq = HashSet.newHashSet(input.length() / fixedWindowSize);
        for (int i = 0; i <= input.length() - fixedWindowSize; i+=fixedWindowSize) {
            uniq.add(input.substring(i, i + fixedWindowSize));
        }
        return uniq.size();
    }

    private static Result sumNumbersWithRepeating(final long start, final long end) {
        long byHalvesSum = 0;
        long anyRepeatsSum = 0;
        for (long i = start; i <= end; i++) {
            final String str = String.valueOf(i);
            final int mid = str.length() / 2;
            if (str.substring(0, mid).equals(str.substring(mid))) {
                byHalvesSum += i;
                anyRepeatsSum += i;
                continue;
            }
            for (int l = 1; l <= mid; l ++) {
                if (str.length() % l == 0) {
                    final int uniq = countUniqueSubstrings(str, l);
                    if (uniq == 1) {
                        anyRepeatsSum += i;
                        break;
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
        if (testResult.byHalvesSum != 1227775554L) throw new AssertionError();
        if (testResult.anyRepeatsSum != 4174379265L) throw new AssertionError();
        TEST.info("Part 1: " + testResult.byHalvesSum);
        TEST.info("Part 2: " + testResult.anyRepeatsSum);
        final Result realResult = sumNumbersWithRepeating(realInput);
        if (realResult.byHalvesSum != 31000881061L) throw new AssertionError();
        if (realResult.anyRepeatsSum != 46769308485L) throw new AssertionError();
        REAL.info("Part 1: " + realResult.byHalvesSum);
        REAL.info("Part 2: " + realResult.anyRepeatsSum);
    }


}
