import java.util.*;

public final class RegionChecker {

    private RegionChecker() {}

    public static boolean isRegionComplete(
            String region,
            Map<String, String> sites,
            List<Card> draft) {

        Set<String> collectedSites = new HashSet<>();

        for (Map.Entry<String, String> entry : sites.entrySet()) {
            if (entry.getValue().equals(region)) {
                collectedSites.add(entry.getKey());
            }
        }

        for (Card card : draft) {
            if (card.region().equals(region)) {
                collectedSites.add(card.letter());
            }
        }

        return collectedSites.size() == 4;
    }
}