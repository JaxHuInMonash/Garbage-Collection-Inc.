package game.upgrades;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.testfixtures.TestActors.TestActor;
import game.testfixtures.TestActors.TestProgressableWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UpgradeStation} base class — the second REQ3
 * abstraction. Verifies the eligibility filter (path lock + tier sequence)
 * and that the station ignores actors without the
 * {@link game.capabilities.Progressable} capability.
 * <p>
 * Uses real {@link Actor} subclasses (from {@code game.testfixtures.TestActors})
 * because {@code GameEntity.asCapability} is final — Mockito 3.2.4 can't stub
 * final methods, so we need real Java polymorphism to route the lookup.
 */
class UpgradeStationTest {

    private Upgrade tier1Fighter;
    private Upgrade tier2Fighter;
    private TestStation station;

    /** Tiny concrete station that exposes a controlled catalogue for the tests. */
    private static class TestStation extends UpgradeStation {
        private final List<Upgrade> catalogue;
        TestStation(CompanyAccount account, List<Upgrade> catalogue) {
            super('X', "Test Station", account);
            this.catalogue = catalogue;
        }
        @Override public UpgradePath getOfferedPath() { return UpgradePath.FIGHTER; }
        @Override protected List<Upgrade> catalogue() { return catalogue; }
    }

    @BeforeEach
    void setUp() {
        tier1Fighter = mock(Upgrade.class);
        when(tier1Fighter.getPath()).thenReturn(UpgradePath.FIGHTER);
        when(tier1Fighter.getTier()).thenReturn(1);

        tier2Fighter = mock(Upgrade.class);
        when(tier2Fighter.getPath()).thenReturn(UpgradePath.FIGHTER);
        when(tier2Fighter.getTier()).thenReturn(2);

        station = new TestStation(
                new InMemoryCompanyAccount(1000),
                List.of(tier1Fighter, tier2Fighter));
    }

    @Test
    void freshWorkerOnlySeesTier1() {
        TestProgressableWorker worker = new TestProgressableWorker(new WorkerProgression());

        ActionList actions = station.allowableActions(worker, mock(Location.class), "north");

        assertEquals(1, actions.size(),
                "fresh worker can install tier 1 only; tier 2 hidden until tier 1 is bought");
    }

    @Test
    void afterTier1Install_tier2BecomesEligible() {
        WorkerProgression progression = new WorkerProgression();
        TestProgressableWorker worker = new TestProgressableWorker(progression);
        progression.install(tier1Fighter, worker);

        ActionList actions = station.allowableActions(worker, mock(Location.class), "north");

        assertEquals(1, actions.size(),
                "after tier 1, only tier 2 is the next-eligible action");
    }

    @Test
    void invalid_actorWithoutProgressableCapabilityGetsEmptyMenu() {
        // A plain Actor that does NOT implement Progressable.
        TestActor nonWorker = new TestActor("non-worker", 'n');

        ActionList actions = station.allowableActions(nonWorker, mock(Location.class), "north");

        assertEquals(0, actions.size(),
                "actor without Progressable (i.e. not a worker) is offered nothing");
    }

    @Test
    void pathLockedWorker_otherPathStationOffersNothing() {
        WorkerProgression progression = new WorkerProgression();
        TestProgressableWorker worker = new TestProgressableWorker(progression);

        // Lock the worker into the HEALER path.
        Upgrade healerT1 = mock(Upgrade.class);
        when(healerT1.getPath()).thenReturn(UpgradePath.HEALER);
        when(healerT1.getTier()).thenReturn(1);
        progression.install(healerT1, worker);

        // The station is a Fighter station — locked-out worker should see nothing.
        ActionList actions = station.allowableActions(worker, mock(Location.class), "north");

        assertEquals(0, actions.size(),
                "Healer-locked worker should see no actions at a Fighter station");
    }

    @Test
    void edge_stationsAreNotEnterable() {
        // Stations are consoles — workers interact from adjacent tiles, never stand on them.
        assertFalse(station.canActorEnter(new TestActor("anyone", 'a')));
    }
}
