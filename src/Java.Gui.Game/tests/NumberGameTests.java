import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberGameTests {

    private NumberGame numberGame;

    @BeforeEach
    public void setUp() {
        numberGame = new NumberGame();
        numberGame.buttons = new javafx.scene.control.Button[20];
        numberGame.placedNumbers = new int[20];
        for (int i = 0; i < 20; i++) {
            numberGame.placedNumbers[i] = -1;
        }
    }

    //POSITIVE TESTS
    @Test
    public void testValidPlacementInEmptyGrid()
    {
        if (!numberGame.isValidPlacement(10, 500))
        {
            throw new IllegalArgumentException("Expected valid placement in empty grid at index 10 with number 500.");
        }
    }

    @Test
    public void testValidPlacementWithCorrectLeftRightContext()
    {
        numberGame.placedNumbers[5] = 200;
        numberGame.placedNumbers[15] = 800;

        if (!numberGame.isValidPlacement(10, 500))
        {
            throw new IllegalArgumentException("Expected valid placement of 500 between 200 and 800.");
        }
    }

    @Test
    public void testHasValidMoveReturnsTrueWhenOneSlotIsValid()
    {
        numberGame.placedNumbers[0] = 200;
        numberGame.placedNumbers[1] = -1;
        numberGame.placedNumbers[2] = 800;
        numberGame.currentNumber = 500;

        if (!numberGame.hasValidMove())
        {
            throw new IllegalArgumentException("Expected valid move to exist for number 500.");
        }
    }

    @Test
    public void testValidPlacementWithOnlyLeftOccupied()
    {
        numberGame.placedNumbers[0] = 100;

        if (!numberGame.isValidPlacement(5, 200))
        {
            throw new IllegalArgumentException("Expected valid placement of 200 with left side having 100.");
        }
    }

    // NEGATIVE TESTS

    @Test
    public void testRejectPlacementInUsedSlot()
    {
        numberGame.placedNumbers[3] = 999;
        boolean valid = numberGame.isValidPlacement(3, 500);

        if (valid) {
            throw new IllegalArgumentException("Should NOT allow placement in occupied slot index 3.");
        }
    }

    @Test
    public void testRejectPlacementWhenLeftNumberTooLarge()
    {
        numberGame.placedNumbers[2] = 700;
        boolean valid = numberGame.isValidPlacement(5, 500);

        if (valid)
        {
            throw new IllegalArgumentException("Should NOT allow placing 500 to the right of 700.");
        }
    }

    @Test
    public void testRejectPlacementWhenRightNumberTooSmall()
    {
        numberGame.placedNumbers[10] = 400;
        boolean valid = numberGame.isValidPlacement(5, 500);

        if (valid) {
            throw new IllegalArgumentException("Should NOT allow placing 500 to the left of 400.");
        }
    }

    @Test
    public void testNoValidMoveAvailable()
    {
        for (int i = 0; i < 20; i++)
        {
            numberGame.placedNumbers[i] = 1000 - i;
        }

        numberGame.currentNumber = 1;
        boolean hasMove = numberGame.hasValidMove();

        if (hasMove)
        {
            throw new IllegalArgumentException("Should NOT detect valid move for number 1 when grid is reverse-filled.");
        }
    }
}
