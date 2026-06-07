package game.upgrades.healer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.upgrades.Upgrade;
import game.upgrades.UpgradePath;

/**
 * Healer path, Tier 1 — <strong>Bio-Sync</strong> (reference implementation).
 * <p>
 * <strong>One-shot:</strong> none (the value is entirely in the passive pulse).
 * <p>
 * <strong>Per-turn passive (cross-component — AoE × inter-actor):</strong> every
 * turn, all <em>other</em> workers within {@value #RADIUS} tiles regenerate
 * {@value #HEAL} HP. This is a genuine area-of-effect that reaches across actors,
 * not a flat self-heal.
 * <p>
 * (Future: when no ally is in range, drop a {@code StasisDroplet} pickup on the
 * worker's tile — left as a TODO until that item exists.)
 *
 * @author Jax
 */
public class HealerBioSync implements Upgrade {

    private static final int TIER = 1;
    private static final int WORKER_COST = 50;
    private static final int COMPANY_COST = 30;
    private static final int RADIUS = 2;
    private static final int HEAL = 2;

    @Override
    public String getName() {
        return "Bio-Sync";
    }

    @Override
    public UpgradePath getPath() {
        return UpgradePath.HEALER;
    }

    @Override
    public int getTier() {
        return TIER;
    }

    @Override
    public int getWorkerCreditCost() {
        return WORKER_COST;
    }

    @Override
    public int getCompanyCreditCost() {
        return COMPANY_COST;
    }

    @Override
    public void applyTo(Actor worker) {
        // No immediate effect; the benefit is the per-turn pulse in tick().
    }

    @Override
    public void tick(Actor worker, Location location) {
        for (Location nearby : location.getNearbyLocations(RADIUS)) {
            if (nearby.equals(location) || !nearby.containsAnActor()) {
                continue;
            }
            Actor other = nearby.getActor();
            if (other != worker && other.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                other.heal(HEAL);
            }
        }
        // TODO(REQ3): if no ally was healed, drop a StasisDroplet on `location`.
    }
}
