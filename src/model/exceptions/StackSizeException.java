/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;

/**
 * Class extends the exception class, that is used to indicate the error in the stack size.
 */
public class StackSizeException extends Exception {
    /**
     * Exception constructor. Calls parent class constructor passing the message in case the amount of item is not correct or out of range..
     */
    public StackSizeException() {
        super("The amount of items is not correct/ out of range.");
    }
}
