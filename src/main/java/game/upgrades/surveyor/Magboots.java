package game.upgrades.surveyor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.display.EventLog;
import game.grounds.Floor;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Surveyor path, Tier 2 — <strong>Magboots</strong>.
 * <p>
 * <strong>Per-turn passive (cross-component — terrain modification × actor
 * movement tracking):</strong> tracks the worker's previous tile each turn.
 * If the worker has just <em>stepped off</em> a {@link Ability#IS_TOXIC} tile,
 * the magboots' decontamination plating cleanses it — the tile permanently
 * reverts to a plain {@link Floor}. This makes the Surveyor a slow but
 * persistent terrain purifier as they explore.
 * <p>
 * <strong>Not yet implemented (proposal mentions these — flag for scope
 * discussion):</strong>
 * <ul>
 *   <li>"+1 move per turn" — requires action-budget changes the engine doesn't expose cleanly.</li>
 *   <li>"Puddle rides along with the worker for 3 tiles" — would need terrain
 *       state to follow the actor; deferred to a separate work item if we keep
 *       this claim in the proposal.</li>
 * </ul>
 * Each Magboots instance keeps its own {@code lastLocation} — every worker has
 * a separate installed instance via {@link game.upgrades.WorkerProgression},
 * so state per-worker is naturally isolated.
 *
 * @author Jax
 */
public class Magboots implements Upgrade {

    private static final int TIER = 2;
    private static final int WORKER_COST = 150;
    private static final int COMPANY_COST = 100;

    private Location lastLocation;

    @Override public String getName()              { return "Magboots"; }
    @Override public UpgradePath getPath()         { return UpgradePath.SURVEYOR; }
    @Override public int getTier()                 { return TIER; }
    @Override public int getWorkerCreditCost()     { return WORKER_COST; }
    @Override public int getCompanyCreditCost()    { return COMPANY_COST; }

    @Override
    public void applyTo(Actor worker) {
        // No one-shot effect — the value is in the per-turn cleanse below.
    }

    @Override
    public void tick(Actor worker, Location location) {
        if (lastLocation != null && !lastLocation.equals(location)) {
            Ground previousGround = lastLocation.getGround();
            if (previousGround.hasAbility(Ability.IS_TOXIC)) {
                lastLocation.setGround(new Floor());
                EventLog.getInstance().log(
                        worker + "'s magboots cleanse the toxic tile at ("
                                + lastLocation.x() + ", " + lastLocation.y() + ").");
            }
        }
        this.lastLocation = location;
    }
}
