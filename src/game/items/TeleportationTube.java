package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.UseTeleportationTubeAction;

import java.util.ArrayList;
import java.util.List;

public class TeleportationTube extends Item {

    public record Destination(GameMap map, int x, int y, String label) {}

    private final List<Destination> destinations = new ArrayList<>();

    public TeleportationTube() {
        super("Teleportation Tube", 'Φ');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(5));
        this.makePortable();
    }

    public void addDestination(GameMap map, int x, int y, String label) {
        destinations.add(new Destination(map, x, y, label));
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        for (int i = 0; i < destinations.size(); i++) {
            actions.add(new UseTeleportationTubeAction(this, i));
        }
        return actions;
    }
}
