/**
 * @author agata.koziol
 */
package model;

import java.lang.*;

import model.exceptions.*;


/**
 * It represents a block, the building unit of BlockWorld. A block is made of a material and may contain a stack of items.
 */
public abstract class Block {
    /**
     * Instance of the Material class, indicates the type of material of which the block is created.
     */
    protected Material type;

    /**
     * Constructor. It creates an instance of a block.
     *
     * @param type type of material.
     * @throws WrongMaterialException if the material is not a block material.
     */
    public Block(Material type) throws WrongMaterialException {
        if (type.isBlock()) {
            this.type = type;
        } else {
            throw new WrongMaterialException(type);
        }
    }

    /**
     * Copy constructor. Creates a block with the exact parameter like the one indicated.
     *
     * @param block instance of the block, which parameters will be copied.
     */
    public Block(Block block) {
        this.type = block.getType();
    }

    /**
     * Simple getter.
     *
     * @return type of the material from which the block is created.
     */
    public Material getType() {
        return type;
    }

    /**
     * Abstract method. Its objective is to return a copy of the object ‘this’ when invoked
     * (it is implemented in the subclasses).
     *
     * @return a copy of any block (SolidBlock or LiquidBlock) when needed.
     */
    public abstract Block clone();


    /**
     * created the string that shows the type of material of the block.
     *
     * @return string with the format [type].
     */
    public String toString() {
        return "[" + type + "]";
    }

    /**
     * Creates a hashcode using only the type
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * Determines identity by comparing only the type.
     * @param obj The object which should be checked
     * @return whether the objects are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Block other = (Block) obj;
        if (type != other.type)
            return false;
        return true;
    }
}
