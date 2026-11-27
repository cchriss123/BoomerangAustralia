import model.Card;

import java.util.*;

public final class RegionChecker {

    private RegionChecker() {}

    public static boolean isRegionComplete(String region, Map<String, String> sites, List<Card> draft) {

        Set<String> collectedSites = new HashSet<>();

        for (Map.Entry<String, String> entry : sites.entrySet()) {
            if (entry.getValue().equals(region)) {
                collectedSites.add(entry.getKey());
            }
        }

        for (Card card : draft) {
            if (card.region().equals(region)) {
                collectedSites.add(card.selectionLetter());
            }
        }

        return collectedSites.size() == 4;
    }
// TODO EU - Passport Bonus
// public static boolean hasVisitedAllRegions(Map<String, String> visitedSites, List<Card> draft,Set<String> allRegions) {
//
//     Set<String> visitedRegions = new HashSet<>(visitedSites.values());
//
//     for (Card card : draft) {
//         visitedRegions.add(card.region());
//     }
//
//     return visitedRegions.containsAll(allRegions);
// }



}