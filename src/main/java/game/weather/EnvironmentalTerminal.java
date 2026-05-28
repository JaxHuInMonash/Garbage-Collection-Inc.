package game.weather;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.function.Supplier;

/**
 * An environmental monitoring terminal that allows actors to scan the current
 * weather and apply its effects to the game world. Displays as {@code ¶}.
 *
 * <p>The terminal offers a {@link WeatherScanAction} to any adjacent actor.
 * It depends strictly on the {@link WeatherDataPoint} and {@link WeatherEffect}
 * abstractions (Dependency Inversion Principle) — concrete implementations are
 * wired at the composition root.
 */
public class EnvironmentalTerminal extends Ground {

    private final List<Supplier<WeatherDataPoint>> dataPointSuppliers;
    private final List<WeatherEffect> effects;

    /**
     * Constructor.
     *
     * @param dataPointSuppliers suppliers for creating weather data point
     *                           instances (abstraction, never concrete)
     * @param effects            weather effects to apply (abstraction, never concrete)
     */
    public EnvironmentalTerminal(List<Supplier<WeatherDataPoint>> dataPointSuppliers,
                                 List<WeatherEffect> effects) {
        super('¶', "Environmental Terminal");
        this.dataPointSuppliers = dataPointSuppliers;
        this.effects = effects;
    }

    /**
     * Actors cannot enter the terminal tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Offers the {@link WeatherScanAction} when an actor is adjacent to the
     * terminal (direction is non-empty). The action receives the abstractions,
     * keeping this class also DIP-compliant.
     *
     * @param actor     the Actor standing nearby
     * @param location  the location of this terminal
     * @param direction the direction of the terminal from the actor ("" if on it)
     * @return list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty()) {
            actions.add(new WeatherScanAction(dataPointSuppliers, effects));
        }
        return actions;
    }
}
