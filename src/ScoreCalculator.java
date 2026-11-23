import model.Animals;
import model.Card;
import model.CollectionTypes;
import model.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ScoreCalculator {

    private ScoreCalculator() {}

    public static int countMatchingCards(List<Card> draft, String targetValue, Function<Card, String> category) {
        int count = 0;

        for (Card card : draft) {
            if (category.apply(card).equals(targetValue)) {
                count++;
            }
        }

        return count;
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
            if (countMatchingCards(draft, animal.getKey(), Card::animals) == 2) {
                total += animal.getValue();
            }
        }

        return total;
    }


    public static int calculateCollectionScore(List<Card> draft) {
        int total = draft.stream()
                .mapToInt(card -> CollectionTypes.POINTS.getOrDefault(card.collections(), 0))
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

    public static Player getHighestScoringPlayer(List<Player> players) {
        Player highScore = players.getFirst();

        for (Player player : players) {
            if (player.getFinalScore() > highScore.getFinalScore()) {
                highScore = player;
            }
        }
        return highScore;
    }

    public static int calculateThrowCatchScore(List<Card> draft) {
        int firstCardNumber = draft.getFirst().number();
        int lastCardNumber = draft.getLast().number();

        return Math.abs(firstCardNumber - lastCardNumber);
    }
}