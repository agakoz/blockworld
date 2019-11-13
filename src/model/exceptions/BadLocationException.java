/**
 * @author agata.koziol
 */
package model.exceptions;

import java.lang.*;
/**
 * Class extends the exception class, that is used when some error with location occures.
 */
public class BadLocationException extends Exception {
    /**
     * Exception constructor. Calls parent constructos passing the message about the problem.
     * @param message message regarding the problem with location.
     */
    public BadLocationException(String message) {
        super(message);
    }
}
