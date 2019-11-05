/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;

public class EntityIsDeadException extends Exception {
    public EntityIsDeadException() {
        super("The player is dead");
    }
}
