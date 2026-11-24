import model.Card;
import model.Player;
import model.Regions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    // Requirement 10b - Tourist sites score:
    // Each tourist site is only scored the first time it is visited.
    //
    // Example:
    // Round 1: Visit sites A and B = 2 new sites.
    // Round 2: Visit sites A and C = 1 new site,
    // because site A has already been visited.

    @Test
    void countNewSitesAcrossRounds() {
        Player player = new Player(0, false);

        Card first = new Card("Sydney Opera House", "A", Regions.NEW_SOUTH_WALES, 1, "", "", "");
        Card second = new Card("Bondi Beach", "B", Regions.NEW_SOUTH_WALES, 2, "", "", "");
        Card third = new Card("Blue Mountains", "C", Regions.NEW_SOUTH_WALES, 3, "", "", "");

        player.getDraft().addAll(List.of(first, second));

        assertEquals(2, player.countNewSites());

        player.getDraft().clear();
        player.getDraft().addAll(List.of(first, third));

        assertEquals(1, player.countNewSites());
        assertEquals(3, player.getSites().size());
    }


    // Card drafting:
    // The selected card is removed from the player's hand
    // and added to the player's draft.
    // An invalid selection should leave both lists unchanged.
    //
    // Examples:
    // Hand contains A and B. Select A: hand contains B, draft contains A.
    // Hand contains A and B. Select a: same result (case-insensitive).
    // Hand contains A and B. Select X: hand and draft remain unchanged.

    @ParameterizedTest
    @MethodSource("draftArguments")
    void draftCard(String letter, List<Card> expectedHand,
                   List<Card> expectedDraft) {

        Player player = new Player(0, false);

        Card first = new Card("Sydney Opera House", "A", Regions.NEW_SOUTH_WALES, 1, "", "", "");
        Card second = new Card("Bondi Beach", "B", Regions.NEW_SOUTH_WALES, 2, "", "", "");

        player.getHand().addAll(List.of(first, second));

        player.draftCard(letter);

        assertEquals(expectedHand, player.getHand());
        assertEquals(expectedDraft, player.getDraft());
    }

    static Stream<Arguments> draftArguments() {
        Card first = new Card("Sydney Opera House", "A", Regions.NEW_SOUTH_WALES, 1, "", "", "");
        Card second = new Card("Bondi Beach", "B", Regions.NEW_SOUTH_WALES, 2, "", "", "");

        return Stream.of(
                Arguments.of("A", List.of(second), List.of(first)),
                Arguments.of("a", List.of(second), List.of(first)),
                Arguments.of("X", List.of(first, second), List.of())
        );
    }
}