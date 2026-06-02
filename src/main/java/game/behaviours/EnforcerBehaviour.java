package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;

public abstract class EnforcerBehaviour implements Behaviour<Actor, Action> {
    protected final Actor target;

    public EnforcerBehaviour(Actor target) {
        this.target = target;
    }

    @Override
    public abstract Action operate(Actor bot, Location location);
}