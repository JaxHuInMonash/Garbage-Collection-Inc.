package game.statuses;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.testfixtures.TestActors.TestActor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReinforcedPlatesStatus} — Combat T2 absorption + AoE
 * shockwave. Deterministic (no random). Real {@link TestActor} is used for the
 * shockwave target because {@code Actor.hasAbility} is final and Mockito can't
 * stub it; HP delta is asserted to confirm the discharge.
 */
class ReinforcedPlatesStatusTest {

    @Test
    void absorbReturnsFiftyPercentOfDamage() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();
        assertEquals(5, plates.absorb(10), "50% of 10");
        assertEquals(50, plates.absorb(100), "50% of 100");
    }

    @Test
    void edge_oddDamageRoundsDown() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();
        assertEquals(1, plates.absorb(3), "50% of 3 → 1 (integer rounding)");
    }

    @Test
    void counterIncrementsOnEachAbsorb() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();
        plates.absorb(10);
        plates.absorb(10);
        assertEquals(2, plates.getHitsAbsorbed());
    }

    @Test
    void belowThresholdNoDischarge() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();
        Location location = mock(Location.class);

        plates.absorb(10);
        plates.absorb(10);
        plates.absorb(10);

        plates.tickStatus(null, location);

        // The AoE never queried the location's exits because the discharge wasn't armed.
        verify(location, never()).getExits();
    }

    @Test
    void thresholdReachedFiresShockwaveOnAdjacentHostile() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();

        // Absorb 5 hits — arms the discharge.
        for (int i = 0; i < 5; i++) plates.absorb(10);

        TestActor hostile = new TestActor("Hostile", 'H', Ability.HOSTILE_TO_WORKERS);
        int hostileHpBefore = hostile.getStatistic(ActorStatistics.HEALTH);

        Location adjacent = mock(Location.class);
        when(adjacent.containsAnActor()).thenReturn(true);
        when(adjacent.getActor()).thenReturn(hostile);

        Exit exit = mock(Exit.class);
        when(exit.getDestination()).thenReturn(adjacent);

        Location centre = mock(Location.class);
        when(centre.getExits()).thenReturn(List.of(exit));

        plates.tickStatus(null, centre);

        assertTrue(hostile.getStatistic(ActorStatistics.HEALTH) < hostileHpBefore,
                "the shockwave should damage the adjacent hostile");
    }

    @Test
    void counterResetsAfterDischarge() {
        ReinforcedPlatesStatus plates = new ReinforcedPlatesStatus();
        for (int i = 0; i < 5; i++) plates.absorb(10);

        assertEquals(0, plates.getHitsAbsorbed(),
                "counter resets immediately after the 5th absorb arms a discharge");
    }
}
