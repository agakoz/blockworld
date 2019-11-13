/**
 * @author agata.koziol
 */
package model.exceptions;

import model.Material;

/**
 * Class extendint the exception class, that is used to indicate the wrong type of material
 */
public class WrongMaterialException  extends Exception {
    /**
     * Exception constructor. Calls parent constructor passing the mesasge in case the wrong type of material is used.
     * @param type wrong type of material used.
     */
    public WrongMaterialException(Material type) {
        super(type +" is not a proper type of material.");
    }
}
