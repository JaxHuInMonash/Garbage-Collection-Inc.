package game.statuses;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public class LockedOutStatus implements Status {
    private int turnsRemaining;

    public LockedOutStatus(int turns) { this.turnsRemaining = turns; }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        turnsRemaining--;
    }

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    @Override
    public String toString() {
        return "LockedOut (" + turnsRemaining + " turns remaining)";
    }
}

