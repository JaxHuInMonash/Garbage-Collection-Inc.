package game.statuses;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RetaliationStatus} — Combat T1 post-hit feedback.
 * <p>
 * The status uses a private {@link java.util.Random} so we exercise the
 * <em>boundary</em> chance values: 0% (never triggers) and 100% (always
 * triggers). These are deterministic by construction and avoid having to
 * inject or seed the RNG.
 */
class RetaliationStatusTest {

    @Test
    void hundredPercentChanceAlwaysAppliesBurnToAttacker() {
        RetaliationStatus status = new RetaliationStatus(100, 2, 4);
        Actor attacker = mock(Actor.class);
        Actor victim = mock(Actor.class);
        GameMap map = mock(GameMap.class);

        String result = status.onTargetHit(attacker, victim, map);

        ArgumentCaptor<BurnStatus> captor = ArgumentCaptor.forClass(BurnStatus.class);
        verify(attacker).addStatus(captor.capture());
        assertNotNull(captor.getValue());
        assertFalse(result.isEmpty(), "non-empty description when retaliation fires");
    }

    @Test
    void zeroPercentChanceNeverAppliesBurn() {
        RetaliationStatus status = new RetaliationStatus(0, 2, 4);
        Actor attacker = mock(Actor.class);
        Actor victim = mock(Actor.class);
        GameMap map = mock(GameMap.class);

        String result = status.onTargetHit(attacker, victim, map);

        verify(attacker, never()).addStatus(any());
        assertEquals("", result);
    }

    @Test
    void statusIsAlwaysActive() {
        RetaliationStatus status = new RetaliationStatus(40, 1, 3);
        // Permanent passive — should remain active regardless of ticks.
        assertTrue(status.isStatusActive());
        status.tickStatus(null, null);
        assertTrue(status.isStatusActive());
    }
}
