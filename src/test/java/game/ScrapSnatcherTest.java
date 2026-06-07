package game;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import game.actors.Parasite;
import game.actors.ScrapSnatcher;
import game.statuses.InfectedStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScrapSnatcher — initial state and the infection lifecycle.
 */
class ScrapSnatcherTest {

    @Test
    public void snatcherStartsWith25HitPoints() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        assertEquals(25, snatcher.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    public void snatcherDisplaysAsLowercaseS() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        assertEquals('s', snatcher.getDisplayChar());
    }

    @Test
    public void newSnatcherIsConscious() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        assertTrue(snatcher.isConscious());
    }

    @Test
    public void infectionAddsInfectedStatus() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        snatcher.onInfected(new Parasite(), TestMaps.floor5x5());
        assertTrue(snatcher.hasStatus(InfectedStatus.class));
    }

    @Test
    public void infectionEquipsAnIntrinsicWeapon() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        snatcher.onInfected(new Parasite(), TestMaps.floor5x5());
        assertNotNull(snatcher.getIntrinsicWeapon());
    }

    @Test
    public void infectionTickDealsOneDamage() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        snatcher.onInfected(new Parasite(), TestMaps.floor5x5());

        int hpBefore = snatcher.getStatistic(ActorStatistics.HEALTH);
        snatcher.onInfectionTick(null);
        assertEquals(hpBefore - 1, snatcher.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    public void infectionEndsWhenSnatcherDies() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        snatcher.onInfected(new Parasite(), TestMaps.floor5x5());

        snatcher.hurt(1000);            // overkill — definitely dead
        assertFalse(snatcher.isConscious());
        assertFalse(snatcher.isInfectionActive());
    }

    @Test
    public void infectionKillsSnatcherWithin25Ticks() {
        // 25 HP, 1 damage per tick → dead by tick 25.
        ScrapSnatcher snatcher = new ScrapSnatcher();
        snatcher.onInfected(new Parasite(), TestMaps.floor5x5());

        for (int i = 0; i < 25; i++) {
            snatcher.onInfectionTick(null);
        }
        assertFalse(snatcher.isConscious());
    }
}