package game;

import game.WeightLimitedInventory;
import game.actors.ContractedWorker;

/**
 * Factory helpers for creating actors inside tests.
 * Mirrors the {@link TestMaps} pattern.
 * @author Mary
 */
public final class TestActors {

    private TestActors() {}

    /**
     * Creates a ContractedWorker with a WeightLimitedInventory(50) and 100 HP.
     */
    public static ContractedWorker worker() {
        WeightLimitedInventory inventory = new WeightLimitedInventory(50);
        return new ContractedWorker("Test Worker", 'ඞ', 100, inventory);
    }
}