/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;

/**
 * Class extends the exception class, that is used when some actions regard the entity which is dead.
 */
public class EntityIsDeadException extends Exception {
    /**
     * Exception constructor. Calls parent constructor passing the message saying that the user is dead.
     */
    public EntityIsDeadException() {
        super("The player is dead");
    }
}
