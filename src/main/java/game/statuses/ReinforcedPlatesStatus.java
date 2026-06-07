package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;

/**
 * Combat Tier 2 — Reinforced Plates. Installed permanently on a worker when
 * they take {@code ReinforcedPlates}.
 * <p>
 * <strong>Two-phase mechanism:</strong>
 * <ol>
 *   <li><strong>Absorption (in {@code hurt})</strong> — each incoming hit has
 *       {@value #DAMAGE_REDUCTION_PERCENT}% of its damage absorbed. The bearer's
 *       {@code hurt()} layer calls {@link #absorb(int)} once per hit. A
 *       {@code hitsAbsorbed} counter ticks up on every absorption.</li>
 *   <li><strong>Discharge (in {@link #tickStatus})</strong> — once the counter
 *       reaches {@value #HITS_TO_DISCHARGE}, a shockwave is armed; on the next
 *       turn the bearer emits an AoE blast against every {@link Ability#HOSTILE_TO_WORKERS}
 *       actor on an adjacent tile, then the counter resets. The cycle repeats.</li>
 * </ol>
 * Discharge is deferred to {@link #tickStatus} because {@code Actor.hurt(int)}
 * doesn't know the bearer's location; the engine passes it to status ticks.
 *
 * @author Jax
 */
public class ReinforcedPlatesStatus implements Status {

    private static final int HITS_TO_DISCHARGE = 5;
    private static final int DAMAGE_REDUCTION_PERCENT = 50;
    private static final int SHOCKWAVE_DAMAGE = 3;

    private int hitsAbsorbed = 0;
    private boolean dischargePending = false;

    /**
     * Absorbs a portion of an incoming hit. Called from {@code ContractedWorker.hurt}.
     *
     * @param incoming the raw damage of this hit
     * @return how much damage to subtract from the worker's HP-bound amount
     */
    public int absorb(int incoming) {
        int absorbed = incoming * DAMAGE_REDUCTION_PERCENT / 100;
        hitsAbsorbed++;
        if (hitsAbsorbed >= HITS_TO_DISCHARGE) {
            dischargePending = true;
            hitsAbsorbed = 0; // reset for the next cycle
        }
        return absorbed;
    }

    /**
     * On the turn after the {@value #HITS_TO_DISCHARGE}th absorbed hit, deal
     * {@value #SHOCKWAVE_DAMAGE} damage to every hostile on an adjacent tile.
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (!dischargePending) return;
        dischargePending = false;
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor()) continue;
            Actor neighbour = adjacent.getActor();
            if (neighbour.hasAbility(Ability.HOSTILE_TO_WORKERS)) {
                neighbour.hurt(SHOCKWAVE_DAMAGE);
            }
        }
    }

    @Override
    public boolean isStatusActive() {
        return true; // permanent — installed by a permanent upgrade
    }

    /** Exposed for tests / display. */
    public int getHitsAbsorbed() {
        return hitsAbsorbed;
    }

    @Override
    public String toString() {
        return "Reinforced Plates (" + DAMAGE_REDUCTION_PERCENT + "% reduce, "
                + hitsAbsorbed + "/" + HITS_TO_DISCHARGE + " charge)";
    }
}
