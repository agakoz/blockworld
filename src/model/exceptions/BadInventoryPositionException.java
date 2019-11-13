/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;
/**
 * Class extends the exception class, that is used when some error with inventory position occures.
 */
public class BadInventoryPositionException extends Exception {
    /**
     * Exception constructor. Calls parent constructos passing the message about the problem.
     * @param pos wrong position.
     */
    public BadInventoryPositionException(int pos) {
        super("Position [" + pos + "] does not exist in the inventory.");
    }
}
