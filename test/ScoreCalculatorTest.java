import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCalculatorTest {

    @Test
    void emptyDraftGivesZeroCollectionPoints() {
        assertEquals(0, ScoreCalculator.calculateCollectionScore(java.util.List.of()));
    }
}