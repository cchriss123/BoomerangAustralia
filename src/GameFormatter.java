import model.Card;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GameFormatter {

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

        String[] act = {"Indigenous Culture", "Bushwalking", "Swimming", "Sightseeing"};

        //Requirement 12
        StringBuilder scoreboard = new StringBuilder();

        scoreboard.append(String.format("%-25s %8s %8s %8s %8s %8s%n",
                "Category", "Round 1", "Round 2", "Round 3", "Round 4", "Total"));

        for (String category : categories) {
            scoreboard.append(String.format("%-25s", category));

            for (Map<String, Integer> round : rScore) {
                scoreboard.append(String.format(" %8d", round.get(category)));
            }

            int categoryTotal = ScoreCalculator.calculateTotal(rScore, category);
            scoreboard.append(String.format(" %8d%n", categoryTotal));
        }

        scoreboard.append(String.format("%-25s %8s %8s %8s %8s %8s%n",
                "", "IndC", "Bushw", "Swim", "Sights", "Total"));
        scoreboard.append(String.format("%-25s", "Activities score"));

        int totalAct = 0;
        for (String activity : act) {
            int points = activitiesScore.getOrDefault(activity, 0);
            scoreboard.append(String.format(" %8d", points));
            totalAct += points;
        }

        scoreboard.append(String.format(" %8d%n", totalAct));
        scoreboard.append(String.format("%-25s %8d%n", "Region bonuses", regionBonus));

        int totalScore = ScoreCalculator.calculateFinalScore(rScore, activitiesScore, regionBonus);

        scoreboard.append(String.format("%-25s %8d points%n", "Total score", totalScore));

        return scoreboard.toString();
    }

    public static String formatCards(List<Card> cards) {
        StringBuilder sb = new StringBuilder();

        String[] labels = {
                "Site [letter] (nr):",
                "Region:",
                "CollectionTypes:",
                "Animals:",
                "Activities:"
        };

        for (int row = 0; row < labels.length; row++) {
            sb.append(String.format("%27s", labels[row]));

            for (Card card : cards) {
                String value = switch (row) {
                    case 0 -> "%s [%s] (%d)".formatted(card.name(), card.letter(), card.number());
                    case 1 -> card.region();
                    case 2 -> card.collections();
                    case 3 -> card.animals();
                    case 4 -> card.activities();
                    default -> "";
                };

                sb.append(String.format("%-35s", value));
            }

            if (row < labels.length - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public static String formatActivities(Map<String, Integer> activities) {
        return activities.entrySet()
                .stream()
                .map(entry -> "%s(# %d)\t".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
    }
}