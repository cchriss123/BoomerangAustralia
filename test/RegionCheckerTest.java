import model.Card;
import model.Regions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegionCheckerTest {

    // Requirement 10b - Tourist sites score:
    // A region is completed when all four unique tourist sites
    // within that region have been visited.

    @ParameterizedTest
    @MethodSource("regionArguments")
    void regionRequiresFourUniqueSites(String selectionLetter, String region, boolean expectedResult) {

        Map<String, String> visitedSites = Map.of(
                "A", Regions.NEW_SOUTH_WALES,
                "B", Regions.NEW_SOUTH_WALES,
                "C", Regions.NEW_SOUTH_WALES
        );

        Card card = new Card("Test site", selectionLetter, region, 1, "", "", "");

        boolean result = RegionChecker.isRegionComplete(Regions.NEW_SOUTH_WALES, visitedSites, List.of(card));

        assertEquals(expectedResult, result);
    }

    static Stream<Arguments> regionArguments() {
        return Stream.of(
                Arguments.of("D", Regions.NEW_SOUTH_WALES, true),
                Arguments.of("A", Regions.NEW_SOUTH_WALES, false),
                Arguments.of("E", Regions.QUEENSLAND, false)
        );
    }
}