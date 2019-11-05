/**
 * @author agata.koziol
 */
package model;

import java.lang.*;

import model.exceptions.*;


/**
 * It represents a block, the building unit of BlockWorld. A block is made of a material and may contain a stack of items.
 */
public class Block {
    /**
     * Instance of the Material class, indicates the type of material of which the block is created.
     */
    private Material type;
    /**
     * Items contained in a block.
     */
    private ItemStack drops;

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
        this.drops = block.getDrops();
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
     * Simple getter.
     *
     * @return instance of the itemStack contained by the block.
     */
    public ItemStack getDrops() {
        return drops;
    }

    /**
     * It replaces the items contained in a block by creating a new ItemStack. Any previous content is lost.
     * Blocks can only contain an ItemStack with a single item, except if the block is of type CHEST.
     * In this case, the ItemStack can carry several items.
     *
     * @param type   type of item to be held by the block.
     * @param amount amount of the item to be held.
     * @throws StackSizeException if the amount of items is not correct
     *                            or the amount is out of range for a stack of items.
     */
    public void setDrops(Material type, int amount) throws StackSizeException {
        if (this.type == Material.CHEST){
            drops = new ItemStack(type, amount);
        } else {
            if (amount == 1) {
                this.drops = new ItemStack(type, amount);
            } else throw new StackSizeException();
        }
    }

    /**
     * created the string that shows the type of material of the block.
     *
     * @return string with the format [type].
     */
    public String toString() {
        return "[" + type + "]";
    }

    /**
     * Function generated automatically that creates hashCode.
     *
     * @return hashCode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * Compares this object to another indicated.
     *
     * @return true if objects are equal, false if not..
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
