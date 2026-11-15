import java.util.Map;


public class ActivityFormatter {

    public static String format(Map<String, Integer> activities) {
        StringBuilder newActivities = new StringBuilder();

        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            newActivities.append(entry.getKey())
                    .append("(# ")
                    .append(entry.getValue())
                    .append(")\t");
        }

        return newActivities.toString();
    }
}