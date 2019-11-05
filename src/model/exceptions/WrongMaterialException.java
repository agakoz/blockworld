/**
 * @author agata.koziol
 */
package model.exceptions;

import model.Material;

public class WrongMaterialException  extends Exception {
    public WrongMaterialException(Material type) {
        super(type +" is not a proper type of material.");
    }
}
