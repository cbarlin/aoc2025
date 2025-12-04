package io.github.cbarlin.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class Day04 {
    private static final String testInput = "src/main/resources/day04/test.txt";
    private static final String realInput = "src/main/resources/day04/real.txt";

    private record Result(
        long part1,
        long part2
    ) {}

    private static char[][] createGrid(final String input) throws IOException {
        final Path path = Path.of(input);
        final List<String> lines = Files.readAllLines(path);
        final char[][] grid = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            grid[i] = lines.get(i).toCharArray();
        }
        return grid;
    }

    private static int countInstancesInGridAround(
            final char[][] grid,
            final int row,
            final int col
    ) {
        int count = 0;
        if (row - 1 >= 0) {
            count += countThreeLine(col, grid[row - 1]);
        }
        if (row + 1 < grid.length) {
            count += countThreeLine(col, grid[row + 1]);
        }
        final char[] currCol = grid[row];
        if (col - 1 >= 0 && currCol[col - 1] == '@') {
            count++;
        }
        if (col + 1 < currCol.length && currCol[col + 1] == '@') {
            count++;
        }
        return count;
    }

    private static int countThreeLine(int col, char[] currCol) {
        int count = 0;
        if (col - 1 >= 0 && currCol[col - 1] == '@') {
            count++;
        }
        if (currCol[col] == '@') {
            count++;
        }
        if (col + 1 < currCol.length && currCol[col + 1] == '@') {
            count++;
        }
        return count;
    }

    private static void twoDcopy(final char[][] source, final char[][] destination) {
        final int lim = source[0].length;
        for (int i = 0; i < source.length; i++) {
            destination[i] = new char[source[i].length];
            System.arraycopy(source[i], 0, destination[i], 0, lim);
        }
    }

    private static boolean twoDEquals(final char[][] a, final char[][] b) {
        for (int i = 0; i < a.length; i++) {
            if (!Arrays.equals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    private static Result solveDay(final String input) throws IOException {
        final char[][] grid = createGrid(input);
        final char[][] nextGrid = new char[grid.length][];
        // Make the first "next" the same as the original
        twoDcopy(grid, nextGrid);
        final int colLength = grid[0].length;
        long part1rolls = 0;
        long part2rolls = 0;

        do {
            // Overwrite the reference grid
            twoDcopy(nextGrid, grid);
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < colLength; col++) {
                    if (grid[row][col] == '@') {
                        // OK, see if there are less than 4 around it
                        final int count = countInstancesInGridAround(grid, row, col);
                        if (count < 4) {
                            part2rolls++;
                            nextGrid[row][col] = '.';
                        }
                    }
                }
            }
            if (part1rolls == 0) {
                part1rolls = part2rolls;
            }
        } while (!twoDEquals(grid, nextGrid));
        return new Result(part1rolls, part2rolls);
    }

    public static void runDay() {
        try {
            final Result testResult = solveDay(testInput);
            System.out.println("Day 04 Test part 1: " + testResult.part1);
            System.out.println("Day 04 Test part 2: " + testResult.part2);
            if (testResult.part1 != 13) throw new AssertionError();
            if (testResult.part2 != 43) throw new AssertionError();
            final Result realResult = solveDay(realInput);
            System.out.println("Day 04 Real part 1: " + realResult.part1);
            System.out.println("Day 04 Real part 2: " + realResult.part2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
