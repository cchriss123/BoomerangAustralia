import model.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ScoreCalculator {

    private ScoreCalculator() {}

    public static int countMatchingCards(List<Card> draft, String targetValue, Function<Card, String> category) {
        return (int) draft.stream()
                .filter(card -> category.apply(card).equals(targetValue))
                .count();
    }

    private static final Map<String, Integer> ANIMAL_POINTS = Map.of(
            Animals.KANGAROOS, 3,
            Animals.EMUS, 4,
            Animals.WOMBATS, 5,
            Animals.KOALAS, 7,
            Animals.PLATYPUSES, 9
    );


    public static int calculateAnimalScore(List<Card> draft) {
        int total = 0;

        for (Map.Entry<String, Integer> animal : ANIMAL_POINTS.entrySet()) {
            int count = countMatchingCards(draft, animal.getKey(), Card::animals);
            int pairs = count / 2;

            total += pairs * animal.getValue();
        }

        return total;
    }


    public static int calculateCollectionScore(List<Card> draft) {
        int total = draft.stream()
                .mapToInt(card -> CollectionTypes.POINTS.getOrDefault(card.collectionType(), 0))
                .sum();

        return (total > 0 && total < 8) ? total * 2 : total;
    }



    public static int calculateTotal(List<Map<String, Integer>> roundScores, String category) {

        return roundScores.stream()
                .mapToInt(round -> round.get(category))
                .sum();
    }

    public static int calculateFinalScore(List<Map<String, Integer>> rScore, Map<String, Integer> activitiesScore, int regionBonus) {

        int totalScore = 0;

        for (String category : rScore.getFirst().keySet()) {
            totalScore += calculateTotal(rScore, category);
        }

        for (int points : activitiesScore.values()) {
            totalScore += points;
        }

        totalScore += regionBonus;

        return totalScore;
    }

    private static final Map<Integer, Integer> ACTIVITY_POINTS_BY_FREQUENCY = Map.of(
            1, 0,
            2, 2,
            3, 4,
            4, 7,
            5, 10,
            6, 15
    );

    public static int calculateActivityScore(int frequency) {
        return ACTIVITY_POINTS_BY_FREQUENCY.getOrDefault(frequency, 0);
    }


    public static Player getHighestScoringPlayer(
            List<Player> players, RuleSet ruleSet) {

        Player winner = players.getFirst();

        for (Player player : players) {
            if (player.getFinalScore() > winner.getFinalScore()) {
                winner = player;

            } else if (player.getFinalScore() == winner.getFinalScore()) {

                // TODO Europe-specific tie-breaker
//                if (ruleSet == RuleSet.EUROPE) {
//                    if (player.getPassportBonus() > winner.getPassportBonus()) {
//                        winner = player;
//                        continue;
//                    }
//
//                    if (player.getPassportBonus() < winner.getPassportBonus()) {
//                        continue;
//                    }
//                }

                // Common Throw and Catch tie-breaker
                int playerThrowCatch = calculateTotal(player.getRScore(), "Throw and Catch score");
                int winnerThrowCatch = calculateTotal(winner.getRScore(), "Throw and Catch score");

                if (playerThrowCatch > winnerThrowCatch) {
                    winner = player;
                }
            }
        }

        return winner;
    }


    public static Player getHighestScoringPlayer(List<Player> players) {
        Player winner = players.getFirst();

        for (Player player : players) {
            if (player.getFinalScore() > winner.getFinalScore()) {
                winner = player;
            } else if (player.getFinalScore() == winner.getFinalScore()) {

                int playerThrowCatch = calculateTotal(player.getRScore(), "Throw and Catch score");
                int winnerThrowCatch = calculateTotal(winner.getRScore(), "Throw and Catch score");

                if (playerThrowCatch > winnerThrowCatch) {
                    winner = player;
                }
            }
        }
        return winner;
    }
    public static int calculateThrowCatchScore(List<Card> draft) {
        int firstCardNumber = draft.getFirst().throwCatchValue();
        int lastCardNumber = draft.getLast().throwCatchValue();

        return Math.abs(firstCardNumber - lastCardNumber);
    }

    public static int calculatePlayerFinalScore(Player player) {
        int regionBonus = calculateRegionBonus(player.getCompletedRegions().size());
        // Requirement 12

        return calculateFinalScore(
                player.getRScore(),
                player.getActivitiesScore(),
                regionBonus)
                // +  player.getPassportBonus();
                ;
    }

    public static int calculateRegionBonus(int completedRegions) {
        return completedRegions * 3;
    }


}