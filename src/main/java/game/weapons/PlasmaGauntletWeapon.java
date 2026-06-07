package game.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.Ability;
import game.grounds.Fire;
import game.statuses.BurnStatus;

/**
 * Combat Tier 3 — Plasma Gauntlet. Replaces the worker's intrinsic weapon
 * when the {@code PlasmaGauntlet} upgrade is installed.
 * <p>
 * On every successful attack the gauntlet:
 * <ul>
 *   <li>deals base damage via {@link IntrinsicWeapon#attack(Actor, Actor, GameMap)};</li>
 *   <li>ignites the target's tile and the tile one step further away from the
 *       attacker (so a plasma jet "punches through" in a straight line);</li>
 *   <li>if the hit was a killing blow, applies {@link BurnStatus} to every
 *       hostile within {@value #KILL_AOE_RADIUS} tiles of the target.</li>
 * </ul>
 * Everything cross-component (terrain change × AoE × kill trigger) lives in the
 * weapon — the AttackAction stays generic and unaware of the gauntlet.
 *
 * @author Jax
 */
public class PlasmaGauntletWeapon extends IntrinsicWeapon {

    private static final int DAMAGE = 4;
    private static final int HIT_RATE = 80;
    private static final int FIRE_DURATION = 3;
    private static final int KILL_AOE_RADIUS = 2;
    private static final int KILL_BURN_DAMAGE = 1;
    private static final int KILL_BURN_DURATION = 3;

    public PlasmaGauntletWeapon() {
        super(DAMAGE, "scorches", HIT_RATE, "plasma gauntlet");
    }

    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        boolean wasConscious = target.isConscious();
        Location targetLoc = map.locationOf(target);
        Location attackerLoc = map.locationOf(attacker);

        String result = super.attack(attacker, target, map);

        // Plasma punch-through: ignite target tile + the tile behind from attacker's line of sight
        igniteTile(targetLoc);
        Location behind = tileBehind(attackerLoc, targetLoc, map);
        if (behind != null) {
            igniteTile(behind);
        }

        // On kill: AoE burn to other hostiles within radius
        if (wasConscious && !target.isConscious()) {
            int affected = 0;
            for (Location nearby : targetLoc.getNearbyLocations(KILL_AOE_RADIUS)) {
                if (!nearby.containsAnActor()) continue;
                Actor other = nearby.getActor();
                if (other == attacker || other == target) continue;
                if (other.hasAbility(Ability.HOSTILE_TO_WORKERS)) {
                    other.addStatus(new BurnStatus(KILL_BURN_DAMAGE, KILL_BURN_DURATION));
                    affected++;
                }
            }
            if (affected > 0) {
                result += "\nPlasma backwash burns " + affected + " nearby hostile" + (affected == 1 ? "" : "s") + ".";
            }
        }

        return result;
    }

    /** Replaces non-wall, non-fire ground with a {@link Fire} that lasts {@value #FIRE_DURATION} turns. */
    private void igniteTile(Location loc) {
        if (!loc.getGround().hasAbility(Ability.BLOCKS_ENTRY)
                && !loc.getGround().hasAbility(Ability.ON_FIRE)) {
            loc.setGround(new Fire(loc.getGround(), FIRE_DURATION));
        }
    }

    /** The tile one step beyond {@code target} along the attacker→target vector, or null if off-map. */
    private Location tileBehind(Location attacker, Location target, GameMap map) {
        int dx = target.x() - attacker.x();
        int dy = target.y() - attacker.y();
        int bx = target.x() + dx;
        int by = target.y() + dy;
        if (bx < map.getXRange().min() || bx > map.getXRange().max()) return null;
        if (by < map.getYRange().min() || by > map.getYRange().max()) return null;
        return map.at(bx, by);
    }
}
