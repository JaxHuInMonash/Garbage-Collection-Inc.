package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for {@link AlienArtifact}.
 *
 * <p>Coverage: correct weight, correct sell value, correct deposit value,
 * implements both Sellable and Depositable, display character.
 * @author Mary
 */
class AlienArtifactTest {

    /**
     * Verifies Alien Artifact has correct weight of 1 as per REQ1.
     */
    @Test
    void testAlienArtifactHasCorrectWeight() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(1, artifact.getStatistic(game.ItemStatistics.WEIGHT));
    }

    /**
     * Verifies Alien Artifact sells for 200 worker credits as per REQ1.
     */
    @Test
    void testAlienArtifactHasCorrectSellValue() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(200, artifact.getSellValue());
    }

    /**
     * Verifies Alien Artifact deposits for 100 company credits as per REQ1.
     */
    @Test
    void testAlienArtifactHasCorrectDepositValue() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals(100, artifact.getDepositValue());
    }

    /**
     * Verifies display character is '?' as per REQ1.
     */
    @Test
    void testAlienArtifactDisplayChar() {
        AlienArtifact artifact = new AlienArtifact();
        assertEquals('?', artifact.getDisplayChar());
    }
}
