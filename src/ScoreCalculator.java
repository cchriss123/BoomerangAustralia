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

    public static int calculateAnimalScore(List<Card> draft) {
        String[] animals = {"Kangaroos", "Emus", "Wombats", "Koalas", "Platypuses"};
        int[] points = {3, 4, 5, 7, 9};

        int total = 0;

        for (int i = 0; i < animals.length; i++) {
            if (countMatchingCards(draft, animals[i], Card::animals) == 2) {
                total += points[i];
            }
        }

        return total;
    }

    public static int calculateCollectionScore(List<Card> draft) {
        String[] collections = {"Leaves", "Wildflowers", "Shells", "Souvenirs"};
        int total = 0;

        for (String collection : collections) {
            int matchingCardCount = countMatchingCards(draft, collection, Card::collections);
            int points = (matchingCardCount < 8) ? matchingCardCount * 2 : matchingCardCount;

            total += points;
        }
        return total;
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




}