import model.Card;
import model.Player;
import model.Regions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    // Requirement 10b - Tourist sites score:
    // Each tourist site is only scored the first time it is visited.
    //
    // New site = 1 point.
    // Previously visited site = 0 points.

    @ParameterizedTest
    @MethodSource("touristSiteArguments")
    void countsOnlyNewTouristSites(boolean alreadyVisited, int expectedNewSites) {
        Player player = new Player(0, false);

        Card cardA = new Card("Sydney Opera House", "A", Regions.NEW_SOUTH_WALES, 1, "", "", "");

        if (alreadyVisited) {
            player.getSites().put("A", Regions.NEW_SOUTH_WALES);
        }

        player.getDraft().add(cardA);

        assertEquals(expectedNewSites, player.countNewSites());
    }

    static Stream<Arguments> touristSiteArguments() {
        return Stream.of(
                Arguments.of(false, 1),
                Arguments.of(true, 0)
        );
    }
}