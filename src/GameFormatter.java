import model.Activities;
import model.Card;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GameFormatter {

    private static final String SCORE_ROW_FORMAT = "%-25s %5d%n";

    private GameFormatter() {}

    public static String formatScore(
            List<Map<String, Integer>> rScore,
            Map<String, Integer> activitiesScore,
            int regionBonus) {

        List<String> categories = List.of(
                "Throw and Catch score",
                "Tourist sites score",
                "CollectionTypes score",
                "Animals score"
        );

        StringBuilder sb = new StringBuilder();

        sb.append("\n========== SCOREBOARD ==========\n\n");

        sb.append("%-25s %8s %8s %8s %8s %8s%n".formatted(
                "Category", "Round 1", "Round 2",
                "Round 3", "Round 4", "Total"
        ));

        sb.append("-".repeat(75)).append("\n");

        for (String category : categories) {
            sb.append("%-25s".formatted(category));

            for (Map<String, Integer> round : rScore) {
                sb.append(" %8d".formatted(round.get(category)));
            }

            int total = ScoreCalculator.calculateTotal(rScore, category);
            sb.append(" %8d%n".formatted(total));
        }

        sb.append("\nACTIVITIES\n");
        sb.append("-".repeat(35)).append("\n");

        int totalActivities = 0;

        for (String activity : Activities.ALL) {
            int points = activitiesScore.getOrDefault(activity, 0);
            sb.append(SCORE_ROW_FORMAT.formatted(activity, points));
            totalActivities += points;
        }

        sb.append(SCORE_ROW_FORMAT.formatted(
                "Activities total", totalActivities
        ));

        sb.append("\nBONUSES & TOTAL\n");
        sb.append("-".repeat(35)).append("\n");

        sb.append(SCORE_ROW_FORMAT.formatted(
                "Region bonuses", regionBonus
        ));

        int totalScore = ScoreCalculator.calculateFinalScore(
                rScore, activitiesScore, regionBonus
        );

        sb.append("%-25s %5d points%n".formatted(
                "FINAL SCORE", totalScore
        ));

        return sb.toString();
    }

    public static String formatCards(List<Card> cards) {
        if (cards.isEmpty()) {
            return "No cards.\n";
        }

        StringBuilder sb = new StringBuilder();

        for (Card card : cards) {
            sb.append("\n");
            sb.append("[%s] %s (%d)%n".formatted(
                    card.letter(), card.name(), card.number()
            ));

            sb.append("    Region:      %s%n".formatted(card.region()));
            sb.append("    Collections: %s%n".formatted(card.collections()));
            sb.append("    Animals:     %s%n".formatted(card.animals()));
            sb.append("    Activities:  %s%n".formatted(card.activities()));
        }

        return sb.toString();
    }

    public static String formatActivities(Map<String, Integer> activities) {
        return activities.entrySet().stream()
                .map(entry -> "%s (%d)".formatted(
                        entry.getKey(), entry.getValue()
                ))
                .collect(Collectors.joining(", "));
    }
}