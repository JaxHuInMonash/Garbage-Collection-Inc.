package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WeatherScanAction;
import game.weather.alerts.WeatherAlert;
import game.weather.effect.WeatherEffect;

import java.util.List;

/**
 * An environmental monitoring terminal that allows actors to scan the current
 * weather and apply its effects to the game world. Displays as {@code ¶}.
 *
 * <p>Offers a {@link WeatherScanAction} to any adjacent actor. Depends strictly
 * on the {@link WeatherEffect} and {@link WeatherAlert} abstractions (DIP) —
 * concrete implementations are wired at the composition root.
 */
public class EnvironmentalTerminal extends Ground {

    private final List<WeatherEffect> effects;
    private final List<WeatherAlert> alerts;

    /**
     * Constructor.
     *
     * @param effects weather effects to apply (abstraction, never concrete)
     * @param alerts  weather alerts to check (abstraction, never concrete)
     */
    public EnvironmentalTerminal(List<WeatherEffect> effects, List<WeatherAlert> alerts) {
        super('¶', "Environmental Terminal");
        this.effects = effects;
        this.alerts = alerts;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (!direction.isEmpty()) {
            actions.add(new WeatherScanAction(effects, alerts));
        }
        return actions;
    }
}
