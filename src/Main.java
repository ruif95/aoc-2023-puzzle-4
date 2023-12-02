import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Main {

    public static final String WRONG_INPUT_FILE_MESSAGE = "Erm... excuse me little elf, but those don't look like cubes.";

    public static void main(String[] args) throws IOException {
        final int result = extractInputLines().stream()
                .map(Main::toObject)
                .mapToInt(Game::getPowerOfSetsOfCubes)
                .sum();

        System.out.println("A-ha! The answer to your little game is: " + result);
    }

    /**
     * Reads a number of not yet parsed cube withdrawals... This looks fun!
     *
     * @return many, many uninterpreted games!
     */
    private static List<String> extractInputLines() throws IOException {
        try (InputStream resource = Main.class.getResourceAsStream("input")) {
            if (resource == null) {
                throw new RuntimeException(WRONG_INPUT_FILE_MESSAGE);
            }

            return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                    .lines()
                    .toList();
        }
    }

    /**
     * Turns a raw input line into a {@link Game} object.
     *
     * @param inputLine raw string with game input.
     * @return {@link Game} object with utilities for all your elvish needs.
     */
    private static Game toObject(final String inputLine) {
        final String[] gameInfoAndWithdrawals = inputLine.split(": ");
        final String[] withdrawalCombinations = gameInfoAndWithdrawals[1].split("; ");

        final int gameId = Integer.parseInt(gameInfoAndWithdrawals[0].split(" ")[1]);
        return new Game(gameId,
                Stream.of(withdrawalCombinations)
                        .map(CubeWithdrawalCombination::of)
                        .toList());
    }

    /**
     * Game object.
     */
    static class Game {
        private final int id;
        private final List<CubeWithdrawalCombination> withdrawalCombinations;

        public Game(int id, List<CubeWithdrawalCombination> withdrawalCombinations) {
            this.id = id;
            this.withdrawalCombinations = withdrawalCombinations;
        }

        public int getPowerOfSetsOfCubes() {
            return getMinimumRequiredForEachColor().values().stream().reduce(1, (a, b) -> a * b);
        }

        /**
         * Gets the minimum required plays for each color. This is crucial to getting the water back!!1!
         * @return the saving grace for the elves of sky island.
         */
        private Map<CubeColor, Integer> getMinimumRequiredForEachColor() {
            Map<CubeColor, Integer> colorToMinimumAmount = new HashMap<>() {{
               put(CubeColor.RED, 0);
               put(CubeColor.GREEN, 0);
               put(CubeColor.BLUE, 0);
            }};

            for (CubeWithdrawalCombination withdrawalCombination : withdrawalCombinations) {
                for (CubeWithdrawal withdrawal : withdrawalCombination.getCombinationWithdrawals()) {
                    if (withdrawal.getAmount() > colorToMinimumAmount.get(withdrawal.getColor())) {
                        colorToMinimumAmount.put(withdrawal.getColor(), withdrawal.getAmount());
                    }
                }
            }

            return colorToMinimumAmount;
        }
    }

    /**
     * Represents a combination of different colored cube withdrawals from the elf's pocket.
     */
    static class CubeWithdrawalCombination {
        private final List<CubeWithdrawal> combinationWithdrawals;

        public static CubeWithdrawalCombination of(String combination) {
            return new CubeWithdrawalCombination(Stream.of(combination.split(", "))
                    .map(CubeWithdrawal::new)
                    .toList());
        }

        private CubeWithdrawalCombination(List<CubeWithdrawal> withdrawals) {
            this.combinationWithdrawals = withdrawals;
        }

        public List<CubeWithdrawal> getCombinationWithdrawals() {
            return combinationWithdrawals;
        }
    }

    /**
     * Represents a single withdrawal of a cube's color and the amount of cubes from that color.
     */
    static class CubeWithdrawal {
        CubeColor color;
        int amount;

        public CubeWithdrawal(String inputWithdrawal) {
            String[] splitInfo = inputWithdrawal.split(" ");

            this.color = CubeColor.valueOf(splitInfo[1].toUpperCase());
            this.amount = Integer.parseInt(splitInfo[0]);
        }

        public CubeColor getColor() {
            return color;
        }

        public int getAmount() {
            return amount;
        }
    }

    enum CubeColor {
        RED,
        GREEN,
        BLUE;
    }
}
