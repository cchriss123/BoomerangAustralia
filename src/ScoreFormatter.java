import java.util.List;
import java.util.Map;

public final class ScoreFormatter {

    private ScoreFormatter() {}

    public static String format(
            List<Map<String, Integer>> rScore,
            Map<String, Integer> activitiesScore,
            int regionBonus) {

        List<String> categories = List.of(
                "Throw and Catch score",
                "Tourist sites score",
                "Collections score",
                "Animals score"
        );

        String[] act = {
                "Indigenous Culture", "Bushwalking",
                "Swimming", "Sightseeing"
        };

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
        scoreboard.append(String.format("%-25s %8d%n",
                "Region bonuses", regionBonus));

        int totalScore = ScoreCalculator.calculateFinalScore(
                rScore, activitiesScore, regionBonus);

        scoreboard.append(String.format("%-25s %8d points%n",
                "Total score", totalScore));

        return scoreboard.toString();
    }
}