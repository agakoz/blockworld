/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

/**
 * This class represents a certain amount of items of the same type, name as stack of items.
 */
public class ItemStack {
    /**
     * the maximum size of a stack.
     */
    public static final int MAX_STACK_SIZE = 64;
    /**
     * type of material
     */
    private Material type;
    private int amount;

    /**
     * Constructor. If the material is a tool or a weapon, only one unit can be created.
     * Stacks of items with zero or a negative amount of units cannot be created.
     *
     * @param type   type of material of the item represented by itemstack.
     * @param amount amount of items.
     * @throws StackSizeException if the amount of items is not between 1 and MAX_STACK_SIZE,
     *                            or if the material is of type tool or weapon and amount is different from 1.
     */
    public ItemStack(Material type, int amount) throws StackSizeException {
        this.type = type;
        setAmount(amount);
    }
    public ItemStack(ItemStack itemStack){
        this.type=itemStack.getType();
        this.amount=itemStack.getAmount();
    }

    /**
     * trivial getter.
     *
     * @return amount of material.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Trivial getter.
     *
     * @return type of material.
     */
    public Material getType() {
        return type;
    }

    /**
     * It assigns an amount of units to the item stack.
     *
     * @param amount
     * @throws StackSizeException if the amount of items is not between 1 and MAX_STACK_SIZE,
     *                            or if the material is of type tool or weapon and amount is different from 1.
     */
    public void setAmount(int amount) throws StackSizeException {
        if ((type.isTool() || type.isWeapon())) {
            if (amount == 1) {
                this.amount = amount;
            } else throw new StackSizeException();

        } else {
            if (amount > 0 && amount <= MAX_STACK_SIZE) {
                this.amount = amount;
            } else {
                throw new StackSizeException();
            }
        }
    }

    /**
     * Creates a string informing about type of material and amount of item.
     *
     * @return a string with the format (type,amount).
     */
    public String toString() {
        return "(" + type + "," + amount + ")";
    }

    /**
     * Generated automatically that creates hashCode.
     *
     * @return hashCode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + amount;
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
        ItemStack other = (ItemStack) obj;
        if (amount != other.amount)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
