package de.suma;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit test for simple App.
 *
 * @author Sascha Szott
 */
public class AppTest {

    @Test
    public void findExistingKey() {
        App app = new App();
        int[] searchArray = new int[] {2, 5, 7};
        assertTrue(app.binarySearch(searchArray, 7));
    }

    @Test
    public void doNotFindMissingKey() {
        App app = new App();
        int[] searchArray = new int[] {2, 5, 7};
        assertFalse(app.binarySearch(searchArray, 6));
    }

}
