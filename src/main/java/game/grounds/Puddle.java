package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.capabilities.Ability;
import game.capabilities.Consumable;
import game.statuses.PoisonStatus;
import game.capabilities.WeatherAffected;
import game.weather.WeatherApiResponse;
import game.weather.effect.TerrainEffect;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * In a standard video game, this would just be water. On a deprecated moon
 * in the Eclipse Nebula, it could be anything from spilled engine coolant to
 * highly corrosive alien saliva.
 * <p>
 * Workers standing on a puddle can attempt to drink from it. The outcome
 * depends on whether they are carrying a SterilisationBox.
 *
 * @author Jax
 */
public class Puddle extends Ground implements Consumable, WeatherAffected {
    private static final int CONSUMED_HEALING = 1;
    private static final int CONSUMED_POISON_DAMAGE_PER_TURN = 1;
    private static final int CONSUMED_POISON_DURATION = 3;
    private static final int HUMIDITY_EVAPORATE_THRESHOLD = 30;

    /**
     * Constructor
     */
    public Puddle() {
        super('~', "Puddle");
        this.enableAbility(Ability.EVAPORATABLE);
    }

    /**
     * In low humidity, evaporates into Dirt.
     * Called by {@link TerrainEffect} after a weather scan —
     * no polling in {@code tick()}.
     */
    @Override
    public boolean onWeatherChange(Location location, WeatherApiResponse weather) {
        if (weather.humidity() <= HUMIDITY_EVAPORATE_THRESHOLD) {
            location.setGround(new Dirt());

            return true;
        }

        return false;
    }

    @Override
    public String consumedBy(Actor actor, GameMap map) {
        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(CONSUMED_HEALING);
            return actor + " drinks purified water from the puddle and heals 1 HP.";
        } else {
            actor.addStatus(new PoisonStatus(CONSUMED_POISON_DAMAGE_PER_TURN, CONSUMED_POISON_DURATION));
            return actor + " drinks toxic water from the puddle and is poisoned! (" + CONSUMED_POISON_DAMAGE_PER_TURN + " dmg/turn for " + CONSUMED_POISON_DURATION + " turns)";
        }
    }

    @Override
    public void removeFromGround(Location location) {
        return;
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " drinks from puddle";
    }
    /**
     * When an actor is standing on this puddle (direction is empty string),
     * offer the option to drink from it.
     *
     * @param actor     the Actor standing on or near this puddle
     * @param location  the location of this puddle
     * @param direction the direction of the puddle from the actor ("" if standing on it)
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
