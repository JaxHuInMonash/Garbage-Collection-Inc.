package game.upgrades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InMemoryCompanyAccount} — the REQ3 stub that stands in
 * for REQ1's eventual quota ledger. All cases are deterministic; no engine
 * objects are needed since the account is pure arithmetic.
 */
class InMemoryCompanyAccountTest {

    @Test
    @DisplayName("canAfford returns true when balance equals the amount")
    void canAffordExactBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(100);
        assertTrue(account.canAfford(100));
    }

    @Test
    @DisplayName("canAfford returns true when balance exceeds the amount")
    void canAffordAboveBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(100);
        assertTrue(account.canAfford(50));
    }

    @Test
    @DisplayName("canAfford returns false when amount exceeds balance")
    void canAffordRejectsOverdraft() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(50);
        assertFalse(account.canAfford(100));
    }

    @Test
    @DisplayName("edge — zero balance can afford zero")
    void canAffordZeroOnZeroBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(0);
        assertTrue(account.canAfford(0));
    }

    @Test
    @DisplayName("deduct reduces the balance")
    void deductReducesBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(100);
        account.deduct(30);
        assertEquals(70, account.getBalance());
    }

    @Test
    @DisplayName("invalid — deduct throws when balance is insufficient")
    void deductThrowsOnInsufficientBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(50);
        assertThrows(IllegalStateException.class, () -> account.deduct(100));
    }

    @Test
    @DisplayName("deposit increases the balance")
    void depositIncreasesBalance() {
        InMemoryCompanyAccount account = new InMemoryCompanyAccount(50);
        account.deposit(25);
        assertEquals(75, account.getBalance());
    }
}
