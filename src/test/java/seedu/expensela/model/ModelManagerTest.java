package seedu.expensela.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.expensela.model.Model.PREDICATE_SHOW_ALL_TRANSACTIONS;
import static seedu.expensela.testutil.Assert.assertThrows;
import static seedu.expensela.testutil.TypicalTransactions.GRAB;
import static seedu.expensela.testutil.TypicalTransactions.PIZZA;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.expensela.commons.core.GuiSettings;
import seedu.expensela.model.transaction.CategoryEqualsKeywordPredicate;
import seedu.expensela.model.transaction.DateEqualsKeywordPredicate;
import seedu.expensela.testutil.ExpenseLaBuilder;
import seedu.expensela.testutil.MonthlyDataBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new ExpenseLa(), new ExpenseLa(modelManager.getExpenseLa()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setExpenseLaFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        userPrefs.setTotalBalance(1000.00);
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setExpenseLaFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setExpenseLaFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setExpenseLaFilePath(null));
    }

    @Test
    public void setExpenseLaFilePath_validPath_setsExpenseLaFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setExpenseLaFilePath(path);
        assertEquals(path, modelManager.getExpenseLaFilePath());
    }

    @Test
    public void hasTransaction_nullTransaction_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasTransaction(null));
    }

    @Test
    public void hasTransaction_transactionNotInExpenseLa_returnsFalse() {
        assertFalse(modelManager.hasTransaction(PIZZA));
    }

    @Test
    public void hasTransaction_transactionInExpenseLa_returnsTrue() {
        modelManager.setMonthlyData(new MonthlyDataBuilder().build());
        modelManager.addTransaction(PIZZA);
        assertTrue(modelManager.hasTransaction(PIZZA));
    }

    @Test
    public void getFilteredTransactionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredTransactionList().remove(0));
    }

    @Test
    public void equals() {
        ExpenseLa expenseLa = new ExpenseLaBuilder().withTransaction(PIZZA).withTransaction(GRAB).build();
        ExpenseLa differentExpenseLa = new ExpenseLa();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(expenseLa, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(expenseLa, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different expenseLa -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentExpenseLa, userPrefs)));

        modelManager.updateFilteredTransactionList(new CategoryEqualsKeywordPredicate(Arrays.asList("FOOD")),
                new DateEqualsKeywordPredicate(Arrays.asList("ALL")));
        assertFalse(modelManager.equals(new ModelManager(expenseLa, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS, PREDICATE_SHOW_ALL_TRANSACTIONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setExpenseLaFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(expenseLa, differentUserPrefs)));
    }
}
