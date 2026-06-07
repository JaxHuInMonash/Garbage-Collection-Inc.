package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
import game.display.EventLog;

/**
 * A stasis pod left behind by a phase-out worker who held the
 * {@link game.upgrades.healer.ResurrectionProtocol} upgrade (Healer T3).
 * Displays as {@code Ω}.
 * <p>
 * Holds a reference to the dead worker plus the ground that was under them
 * before death. Each turn, if another worker steps onto the pod, the pod:
 * <ol>
 *   <li>finds an empty adjacent tile for the revival;</li>
 *   <li>restores the stored worker's HP to {@value #REVIVAL_HP} and deducts
 *       {@value #CREDIT_PENALTY_PERCENT}% of their personal worker credits
 *       as the Company's processing fee;</li>
 *   <li>re-adds the stored worker to the map at that tile;</li>
 *   <li>reverts its own tile to the ground that was underneath the dead worker
 *       (so the pod consumes itself cleanly).</li>
 * </ol>
 * Pre-existing upgrades, statuses, inventory, etc. all persist on the
 * stored worker Actor object — Java's reference holds them in memory across
 * the death/revive transition.
 *
 * @author Jax
 */
public class StasisPod extends Ground {

    private static final int REVIVAL_HP = 10;
    private static final int CREDIT_PENALTY_PERCENT = 30;

    private final Actor storedWorker;
    private final Ground originalGround;

    /**
     * @param storedWorker   the unconscious worker awaiting revival
     * @param originalGround the ground that was at this tile before the pod
     *                       was placed; restored when the pod is consumed
     */
    public StasisPod(Actor storedWorker, Ground originalGround) {
        super('Ω', "Stasis Pod");
        this.storedWorker = storedWorker;
        this.originalGround = originalGround;
    }

    /** Workers can walk onto the pod — that's the trigger for revival. */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Each turn, if a (different) worker stands on the pod, attempt to revive
     * the stored worker on an empty adjacent tile and consume the pod.
     */
    @Override
    public void tick(Location location) {
        if (!location.containsAnActor()) return;
        Actor walker = location.getActor();
        if (walker == storedWorker) return; // safety — shouldn't happen
        if (!walker.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) return;

        Location revivalSpot = findAdjacentEmpty(location);
        if (revivalSpot == null) {
            // No spot to revive in right now; wait another turn.
            return;
        }

        // Restore HP to a small positive amount, then re-add to the map.
        storedWorker.modifyStatistic(
                ActorStatistics.HEALTH, StatisticOperations.UPDATE, REVIVAL_HP);
        try {
            revivalSpot.addActor(storedWorker);
        } catch (GameEngineException e) {
            // Spot became occupied between check and add; bail and try again next tick.
            return;
        }

        // Processing fee — 30% of the revived worker's own credits.
        if (storedWorker.hasStatistic(game.ActorStatistics.CREDITS)) {
            int credits = storedWorker.getStatistic(game.ActorStatistics.CREDITS);
            int penalty = credits * CREDIT_PENALTY_PERCENT / 100;
            if (penalty > 0) {
                storedWorker.modifyStatistic(
                        game.ActorStatistics.CREDITS, StatisticOperations.DECREASE, penalty);
            }
            EventLog.getInstance().log(
                    walker + " consumes the Stasis Pod — " + storedWorker
                            + " is revived at (" + revivalSpot.x() + ", " + revivalSpot.y()
                            + ") with " + REVIVAL_HP + " HP. Processing fee: " + penalty + " credits.");
        } else {
            EventLog.getInstance().log(
                    walker + " consumes the Stasis Pod — " + storedWorker
                            + " is revived at (" + revivalSpot.x() + ", " + revivalSpot.y() + ").");
        }

        // Pod consumed — restore the ground that was under the dead worker.
        location.setGround(originalGround);
    }

    /**
     * Returns the first empty (no actor) tile adjacent to {@code from} that
     * the stored worker can enter, or null if none exists.
     */
    private Location findAdjacentEmpty(Location from) {
        for (Exit exit : from.getExits()) {
            Location candidate = exit.getDestination();
            if (!candidate.containsAnActor() && candidate.canActorEnter(storedWorker)) {
                return candidate;
            }
        }
        return null;
    }
}
