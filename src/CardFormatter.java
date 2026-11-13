import java.util.List;

public final class CardFormatter {

    private CardFormatter() {}

    public static String format(List<Card> cards) {
        StringBuilder sb = new StringBuilder();

        String[] labels = {
                "Site [letter] (nr):",
                "Region:",
                "Collections:",
                "Animals:",
                "Activities:"
        };

        for (int row = 0; row < labels.length; row++) {
            sb.append(String.format("%27s", labels[row]));

            for (Card card : cards) {
                String value = switch (row) {
                    case 0 -> card.name() + " [" + card.letter() + "] (" + card.number() + ")";
                    case 1 -> card.region();
                    case 2 -> card.collections();
                    case 3 -> card.animals();
                    case 4 -> card.activities();
                    default -> "";
                };

                sb.append(String.format("%-35s", value));
            }

            if (row < labels.length - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}