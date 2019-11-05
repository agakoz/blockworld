/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;

public class BadInventoryPositionException extends Exception {
    public BadInventoryPositionException(int pos) {

        super("Position [" + pos + "] does not exist in the inventory.");
    }
}
