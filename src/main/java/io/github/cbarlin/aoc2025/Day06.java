package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day06 {

    private static final String testInput = "src/main/resources/day06/test.txt";
    private static final String realInput = "src/main/resources/day06/real.txt";

    private record Result(
            long part1,
            long part2
    ) {}

    private static Result solveDay(final String input) throws IOException {
        // Need to parse this into a List<List<Long>> until it's just additions and multiplications...
        final Path path = Path.of(input);
        final List<String> lines = Files.readAllLines(path);
        final List<List<Long>> numbers = new ArrayList<>(lines.size() - 1);
        // The last line is nothing but +'s and *'s
        for (int i = 0; i < lines.size() - 1; i++) {
            String str = lines.get(i).trim();
            final List<Long> nums = new ArrayList<>();

            while (str.indexOf(' ') > 0) {
                int endIndex = str.indexOf(' ');
                nums.add(Long.parseLong(str.substring(0, endIndex).trim()));
                str = str.substring(endIndex + 1).trim();
            }
            nums.add(Long.parseLong(str));
            numbers.add(nums);
        }
        // OK, last line!
        long sumOfAnswers = getSumOfAnswers(lines, numbers);

        return new Result(sumOfAnswers, 0);
    }

    private static long getSumOfAnswers(List<String> lines, List<List<Long>> numbers) {
        long sumOfAnswers = 0;
        int ix = 0;
        String str = lines.getLast();
        while(str.indexOf(' ') > 0) {
            int endIndex = str.indexOf(' ');
            if (str.charAt(0) == '+') {
                long add = 0;
                for (List<Long> number : numbers) {
                    add += number.get(ix);
                }
                sumOfAnswers += add;
            } else {
                long mult = 1;
                for (List<Long> number : numbers) {
                    mult *= number.get(ix);
                }
                sumOfAnswers += mult;
            }
            ix++;
            str = str.substring(endIndex + 1).trim();
        }
        if (str.charAt(0) == '+') {
            long add = 0;
            for (List<Long> number : numbers) {
                add += number.get(ix);
            }
            sumOfAnswers += add;
        } else {
            long mult = 1;
            for (List<Long> number : numbers) {
                mult *= number.get(ix);
            }
            sumOfAnswers += mult;
        }
        return sumOfAnswers;
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput);
            System.out.println("Day 06 Test part 1: " + testResult.part1);
            System.out.println("Day 06 Test part 2: " + testResult.part2);
            if (testResult.part1 != 4277556) throw new AssertionError();
            // if (testResult.part2 != 14) throw new AssertionError();
            final Result realResult = solveDay(realInput);
            System.out.println("Day 06 Real part 1: " + realResult.part1);
            System.out.println("Day 06 Real part 2: " + realResult.part2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
