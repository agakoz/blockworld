/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;

public class StackSizeException extends Exception {
    public StackSizeException() {
        super("The amount of items is not correct/ out of range.");
    }
}
