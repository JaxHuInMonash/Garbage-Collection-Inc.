package game.states.meeple;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.capabilities.Ability;
import game.states.CreatureState;

import java.util.ArrayList;
import java.util.List;

/**
 * The Meeple's overloaded shutdown state.
 * <p>
 * <strong>Immediate effect (onEnter):</strong> Static Discharge — all actors
 * within 2 tiles have every item in their inventory dropped onto their current
 * tile. Workers lose their carried equipment; hostile creatures lose weapons.
 * <p>
 * <strong>Transitions:</strong>
 * <ul>
 *   <li>→ {@link NomadState}: after 3 turns (automatic reboot).</li>
 *   <li>→ {@link GuardianState}: a Worker is adjacent (they repair the Meeple
 *       before the timer expires).</li>
 * </ul>
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class OverloadedState implements CreatureState {

    private static final int DISCHARGE_RADIUS = 2;
    private static final int REBOOT_TURNS = 3;

    private int turnsRemaining = REBOOT_TURNS;

    @Override
    public void onEnter(StatefulCreature creature, GameMap map) {
        Location loc = map.locationOf(creature);
        for (Location nearby : loc.getNearbyLocations(DISCHARGE_RADIUS)) {
            if (!nearby.containsAnActor()) continue;
            Actor actor = nearby.getActor();
            List<Item> items = new ArrayList<>(actor.getInventory().getItems());
            for (Item item : items) {
                actor.getInventory().remove(item);
                nearby.addItem(item);
            }
        }
    }

    @Override
    public CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map) {
        // → Guardian if an adjacent Worker is present (they repair the Meeple)
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor() && dest.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                return new GuardianState();
            }
        }

        // → Nomad after REBOOT_TURNS turns
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            return new NomadState();
        }

        return this;
    }

    @Override
    public Action getAction(StatefulCreature creature, Location location) {
        return new DoNothingAction(); // offline — no action this turn
    }

    @Override
    public String getStateName() {
        return "Overloaded";
    }
}
