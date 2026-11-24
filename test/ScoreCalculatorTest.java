import model.Animals;
import model.Card;
import model.CollectionTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCalculatorTest {

    // Requirement 10a - Throw and catch score:
    // Compare the number on your Throw (first) card and the Catch (final) card and
    // calculate the difference (the absolute value): this is your Throw and catch score.
    //
    // Examples:
    // First card = 2, last card = 7: |2 - 7| = 5 points.
    // First card = 7, last card = 2: |7 - 2| = 5 points.
    // First card = 4, last card = 4: |4 - 4| = 0 points.

    @ParameterizedTest
    @MethodSource("throwCatchArguments")
    void throwCatchScore(int firstNumber, int lastNumber, int expectedScore) {
        Card first = new Card("Test A", "A", "", firstNumber, "", "", "");
        Card last = new Card("Test B", "B", "", lastNumber, "", "", "");

        assertEquals(expectedScore, ScoreCalculator.calculateThrowCatchScore(List.of(first, last)));
    }

    static Stream<Arguments> throwCatchArguments() {
        return Stream.of(
                Arguments.of(2, 7, 5),
                Arguments.of(7, 2, 5),
                Arguments.of(4, 4, 0)
        );
    }


    // Requirement 10c - Collections score:
    // Many cards also have a collection attribute. Each item has a value associated with
    // it: Leaves are 1, Wildflowers are 2, Shells are 3, and Souvenirs are 5.
    // Add up all the values of each item you collected on your cards this round.
    //
    // i. If the total for your collections is 1-7 then you double that number
    //    as your score for that round.
    // ii. If your value is over 7, you only score your collection number as points.
    //
    // Examples:
    // No collections: 0 points.
    // Leaves: 1 * 2 = 2 points.
    // Shells: 3 * 2 = 6 points.
    // Souvenirs + Wildflowers: (5 + 2) * 2 = 14 points.
    // Souvenirs + Shells: 5 + 3 = 8 points (not doubled).

    @ParameterizedTest
    @MethodSource("collectionScoreArguments")
    void collectionScore(String firstCollection, String secondCollection, int expectedScore) {
        Card first = new Card("Test A", "A", "", 1, firstCollection, "", "");
        Card second = new Card("Test B", "B", "", 2, secondCollection, "", "");

        assertEquals(expectedScore, ScoreCalculator.calculateCollectionScore(List.of(first, second)));
    }

    static Stream<Arguments> collectionScoreArguments() {
        return Stream.of(
                Arguments.of("", "", 0),
                Arguments.of(CollectionTypes.LEAVES, "", 2),
                Arguments.of(CollectionTypes.SHELLS, "", 6),
                Arguments.of(CollectionTypes.SOUVENIRS, CollectionTypes.WILDFLOWERS, 14),
                Arguments.of(CollectionTypes.SOUVENIRS, CollectionTypes.SHELLS, 8)
        );
    }


    // Requirement 10d - Animals score:
    // For each pair of matching animals on the cards you drafted this round, you score
    // points marked on that animal (Kangaroos are 3, Emus are 4, Wombats are 5,
    // Koalas are 7, and Platypuses are 9).
    // Total what you score for all the matching animal pairs you collected this round.
    //
    // Examples:
    // One Koala and one Emu: 0 points (no matching pair).
    // One Koala: 0 points (no pair).
    // Two Koalas: 1 pair * 7 = 7 points.
    // Three Koalas: 1 pair * 7 = 7 points (one unpaired Koala).
    // Four Koalas: 2 pairs * 7 = 14 points.
    // Two Kangaroos: 3 points.
    // Two Emus: 4 points.
    // Two Wombats: 5 points.
    // Two Platypuses: 9 points.

    @Test
    void animalScoreWithoutPair() {
        Card first = new Card("Test A", "A", "", 1, "", Animals.KOALAS, "");
        Card second = new Card("Test B", "B", "", 2, "", Animals.EMUS, "");

        assertEquals(0, ScoreCalculator.calculateAnimalScore(List.of(first, second)));
    }

    @ParameterizedTest
    @MethodSource("matchingAnimalsArguments")
    void animalScoreWithMatchingAnimals(int count, int expectedScore) {
        List<Card> draft = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            draft.add(new Card("Test", "", "", 1, "", Animals.KOALAS, ""));
        }

        assertEquals(expectedScore, ScoreCalculator.calculateAnimalScore(draft));
    }

    static Stream<Arguments> matchingAnimalsArguments() {
        return Stream.of(
                Arguments.of(1, 0),
                Arguments.of(2, 7),
                Arguments.of(3, 7),
                Arguments.of(4, 14)
        );
    }

    @ParameterizedTest
    @MethodSource("animalScoreArguments")
    void animalScoreForEachPair(String animal, int expectedScore) {
        Card first = new Card("Test A", "A", "", 1, "", animal, "");
        Card second = new Card("Test B", "B", "", 2, "", animal, "");

        assertEquals(expectedScore, ScoreCalculator.calculateAnimalScore(List.of(first, second)));
    }

    static Stream<Arguments> animalScoreArguments() {
        return Stream.of(
                Arguments.of(Animals.KANGAROOS, 3),
                Arguments.of(Animals.EMUS, 4),
                Arguments.of(Animals.WOMBATS, 5),
                Arguments.of(Animals.KOALAS, 7),
                Arguments.of(Animals.PLATYPUSES, 9)
        );
    }


    // Requirement 10e - Activities score:
    // Scoring activities is optional. For any single activity you would like to score each
    // round, count how many of that activity you have on your seven cards and score
    // the corresponding points:
    //
    // Activity cards: 1  2  3  4  5   6
    // Score:          0  2  4  7  10  15
    //
    // Examples:
    // No cards with the selected activity: 0 points.
    // One card with the selected activity: 0 points.
    // Two cards with the selected activity: 2 points.
    // Three cards with the selected activity: 4 points.
    // Four cards with the selected activity: 7 points.
    // Five cards with the selected activity: 10 points.
    // Six cards with the selected activity: 15 points.

    @ParameterizedTest
    @MethodSource("activityScoreArguments")
    void activityScoreForEachFrequency(int frequency, int expectedScore) {
        assertEquals(expectedScore, ScoreCalculator.calculateActivityScore(frequency));
    }

    static Stream<Arguments> activityScoreArguments() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(1, 0),
                Arguments.of(2, 2),
                Arguments.of(3, 4),
                Arguments.of(4, 7),
                Arguments.of(5, 10),
                Arguments.of(6, 15)
        );
    }
}