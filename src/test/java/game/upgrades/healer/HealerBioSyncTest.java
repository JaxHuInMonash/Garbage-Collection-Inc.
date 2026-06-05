package game.upgrades.healer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
import game.testfixtures.TestActors.TestActor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HealerBioSync} — Tier 1 Healer per-turn AoE heal.
 * <p>
 * Uses real {@link TestActor} instances because {@code Actor.hasAbility} is
 * final (Mockito 3.2.4 can't stub final methods). Hit-points are pre-damaged
 * so the heal-effect is observable as a delta.
 */
class HealerBioSyncTest {

    private static final int DAMAGE_BEFORE_HEAL = 10;

    @Test
    void healsAdjacentWorkerAlly() {
        HealerBioSync upgrade = new HealerBioSync();

        TestActor self = new TestActor("self", 's', Ability.ATTACKABLE_BY_CREATURES);
        TestActor ally = new TestActor("ally", 'a', Ability.ATTACKABLE_BY_CREATURES);
        ally.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_BEFORE_HEAL);
        int allyHpBeforeHeal = ally.getStatistic(ActorStatistics.HEALTH);

        Location selfLoc = mock(Location.class);
        Location allyLoc = mock(Location.class);
        when(allyLoc.containsAnActor()).thenReturn(true);
        when(allyLoc.getActor()).thenReturn(ally);
        when(selfLoc.getNearbyLocations(anyInt())).thenReturn(List.of(selfLoc, allyLoc));

        upgrade.tick(self, selfLoc);

        assertTrue(ally.getStatistic(ActorStatistics.HEALTH) > allyHpBeforeHeal,
                "ally should be healed by the bio-sync pulse");
    }

    @Test
    void doesNotHealSelf() {
        HealerBioSync upgrade = new HealerBioSync();

        TestActor self = new TestActor("self", 's', Ability.ATTACKABLE_BY_CREATURES);
        self.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_BEFORE_HEAL);
        int selfHpBefore = self.getStatistic(ActorStatistics.HEALTH);

        Location selfLoc = mock(Location.class);
        when(selfLoc.containsAnActor()).thenReturn(true);
        when(selfLoc.getActor()).thenReturn(self);
        when(selfLoc.getNearbyLocations(anyInt())).thenReturn(List.of(selfLoc));

        upgrade.tick(self, selfLoc);

        assertEquals(selfHpBefore, self.getStatistic(ActorStatistics.HEALTH),
                "self should not be healed by the bio-sync pulse");
    }

    @Test
    void invalid_ignoresNonWorkerActors() {
        HealerBioSync upgrade = new HealerBioSync();

        TestActor self = new TestActor("self", 's', Ability.ATTACKABLE_BY_CREATURES);
        TestActor nonWorker = new TestActor("creature", 'c'); // no ATTACKABLE_BY_CREATURES
        nonWorker.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_BEFORE_HEAL);
        int nonWorkerHpBefore = nonWorker.getStatistic(ActorStatistics.HEALTH);

        Location selfLoc = mock(Location.class);
        Location otherLoc = mock(Location.class);
        when(otherLoc.containsAnActor()).thenReturn(true);
        when(otherLoc.getActor()).thenReturn(nonWorker);
        when(selfLoc.getNearbyLocations(anyInt())).thenReturn(List.of(selfLoc, otherLoc));

        upgrade.tick(self, selfLoc);

        assertEquals(nonWorkerHpBefore, nonWorker.getStatistic(ActorStatistics.HEALTH),
                "non-workers should be skipped by the bio-sync pulse");
    }

    @Test
    void skipsEmptyTiles() {
        HealerBioSync upgrade = new HealerBioSync();
        Actor self = new TestActor("self", 's', Ability.ATTACKABLE_BY_CREATURES);

        Location selfLoc = mock(Location.class);
        Location emptyLoc = mock(Location.class);
        when(emptyLoc.containsAnActor()).thenReturn(false);
        when(selfLoc.getNearbyLocations(anyInt())).thenReturn(List.of(selfLoc, emptyLoc));

        // Should complete without throwing and without calling getActor on the empty tile.
        upgrade.tick(self, selfLoc);

        verify(emptyLoc, never()).getActor();
    }
}
