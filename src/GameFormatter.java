
import model.Activities;
import model.Card;

import java.util.List;
import java.util.Map;

public final class GameFormatter {

    private GameFormatter() {}

    public static String formatScore(List<Map<String, Integer>> rScore, Map<String, Integer> activitiesScore, int regionBonus) {

        List<String> categories = List.of(
                "Throw and Catch score",
                "Tourist sites score",
                "Collection score",
                "Animals score"
        );

        String categoryHeader = "Category";
        int categoryWidth = categoryHeader.length();

        for (String category : categories) {
            categoryWidth = Math.max(categoryWidth, category.length());
        }

        StringBuilder sb = new StringBuilder();

        // Same width for every section divider
        int lineWidth = categoryWidth + 29;
        String divider = "-".repeat(lineWidth);

        sb.append("\n=================== SCOREBOARD ===================\n\n");

        sb.append(categoryHeader);
        sb.append(" ".repeat(categoryWidth - categoryHeader.length()));
        sb.append(" | R1 | R2 | R3 | R4 | Total\n");
        sb.append(divider).append("\n");

        for (String category : categories) {
            sb.append(category);
            sb.append(" ".repeat(categoryWidth - category.length()));

            for (Map<String, Integer> round : rScore) {
                String points = String.valueOf(round.get(category));
                sb.append(" | ").append(points);
                sb.append(" ".repeat(Math.max(0, 2 - points.length())));
            }

            int total = ScoreCalculator.calculateTotal(rScore, category);
            sb.append(" | ").append(total).append("\n");
        }

        sb.append("\nACTIVITIES\n");
        sb.append(divider).append("\n");

        int totalActivities = 0;

        for (String activity : Activities.ALL) {
            int points = activitiesScore.getOrDefault(activity, 0);
            sb.append(activity).append(": ").append(points).append("\n");
            totalActivities += points;
        }

        sb.append("Activities total: ").append(totalActivities).append("\n");

        sb.append("\nBONUSES & TOTAL\n");
        sb.append(divider).append("\n");

        sb.append("Region bonuses: ").append(regionBonus).append("\n");

        int totalScore = ScoreCalculator.calculateFinalScore(
                rScore, activitiesScore, regionBonus
        );

        sb.append("FINAL SCORE: ").append(totalScore).append(" points\n");

        return sb.toString();
    }


    public static String formatCards(List<Card> cards) {
        if (cards.isEmpty()) {
            return "No cards.\n";
        }

        StringBuilder sb = new StringBuilder();

        for (Card card : cards) {
            sb.append("\n");
            sb.append("[").append(card.letter()).append("] ");
            sb.append(card.name()).append(" (").append(card.number()).append(")\n");

            sb.append("    Region: ").append(card.region()).append("\n");
            sb.append("    Collections: ").append(card.collections()).append("\n");
            sb.append("    Animals: ").append(card.animals()).append("\n");
            sb.append("    Activities: ").append(card.activities()).append("\n");
        }

        return sb.toString();
    }

    public static String formatActivities(Map<String, Integer> activities) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }

            sb.append(entry.getKey());
            sb.append(" (").append(entry.getValue()).append(")");
        }

        return sb.toString();
    }
}
