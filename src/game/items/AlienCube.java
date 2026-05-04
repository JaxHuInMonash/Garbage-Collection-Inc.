package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.SellAlienCubeAction;
import game.actions.UseAlienCubeAction;
import game.capabilities.Tradeable;

public class AlienCube extends Item implements Tradeable {

    private static final int CREDIT_VALUE = 25;

    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.makePortable();
    }

    @Override
    public int getCreditValue() {
        return CREDIT_VALUE;
    }

    @Override
    public String soldBy(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " sold the Alien Cube for " + CREDIT_VALUE + " credits — an Undead materialises nearby!";
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseAlienCubeAction(this, 0));
        actions.add(new UseAlienCubeAction(this, 1));
        actions.add(new UseAlienCubeAction(this, 2));
        actions.add(new SellAlienCubeAction(this));
        return actions;
    }
}
