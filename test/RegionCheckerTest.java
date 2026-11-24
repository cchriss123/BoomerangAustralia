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
    //
    // Examples:
    // Three visited sites: region incomplete.
    // Three previously visited sites + one new site: region complete.
    // Three previously visited sites + one duplicate site: region incomplete.
    // Three previously visited sites + a site from another region: incomplete.

    @ParameterizedTest
    @MethodSource("regionArguments")
    void regionCompletion(Map<String, String> sites, List<Card> draft,
                          boolean expectedResult) {

        assertEquals(
                expectedResult,
                RegionChecker.isRegionComplete(Regions.NEW_SOUTH_WALES, sites, draft)
        );
    }

    static Stream<Arguments> regionArguments() {
        Map<String, String> sites = Map.of(
                "A", Regions.NEW_SOUTH_WALES,
                "B", Regions.NEW_SOUTH_WALES,
                "C", Regions.NEW_SOUTH_WALES
        );

        Card newSite = new Card("Test D", "D", Regions.NEW_SOUTH_WALES, 1, "", "", "");
        Card duplicate = new Card("Test A", "A", Regions.NEW_SOUTH_WALES, 1, "", "", "");
        Card otherRegion = new Card("Test E", "E", Regions.QUEENSLAND, 1, "", "", "");

        return Stream.of(
                Arguments.of(sites, List.of(), false),
                Arguments.of(sites, List.of(newSite), true),
                Arguments.of(sites, List.of(duplicate), false),
                Arguments.of(sites, List.of(otherRegion), false)
        );
    }
}