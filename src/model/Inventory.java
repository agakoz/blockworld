/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

import java.util.*;

/**
 * The player has an inventory of infinite capacity; in each position of the inventory he or she can keep a stack of items (ItemStack).
 * A special position (called ‘inHand’) corresponds to the item that the player holds in his or her hand.
 * The player is created with an item in his hand that is a wooden sword (WOOD_SWORD material).
 */
public class Inventory {
    private ArrayList<ItemStack> inventory;
    private ItemStack inHand;

    /**
     * It creates an empty inventory.
     */
    public Inventory() {
            inventory = new ArrayList<>();
            inHand = null;

    }


    /**
     * It adds a stack of items to the inventory in a new position.
     * Items of the same material can exist in different inventory positions.
     *
     * @param items instance of ItemStack class to be added to the inventory.
     * @return the number of items added.
     */
    public int addItem(ItemStack items) {
        inventory.add(items);
        return items.getAmount();
    }

    /**
     * It empties the inventory, including the item in the player’s hand.
     */
    public void clear() {
        inventory.clear();
        inHand = null;
    }

    /**
     * It deletes the items from the given position.
     *
     * @param slot position of the item
     * @throws BadInventoryPositionException if the given position does not exist.
     */
    public void clear(int slot) throws BadInventoryPositionException {
        if (0<=slot && slot<getSize()) {
            inventory.remove(slot);

        } else {
            throw new BadInventoryPositionException(slot);
        }
    }

    /**
     * Finds the index of the first position in the inventory that contains items of the given type
     * (zero being the first position).
     *
     * @param material tpe of material to be found.
     * @return the index of the first position or -1 if there are no items of such type.
     */
    public int first(Material material) {
        for (ItemStack it : inventory) {
            if (it.getType() == material)
                return inventory.indexOf(it);
        }
        return -1;
    }

    /**
     * Getter that finds the item at the given position.
     *
     * @param slot position of the desired item.
     * @return the item in the given position or null if the position does not exist.
     */
    public ItemStack getItem(int slot) {
        if (slot < inventory.size() && slot >= 0) {
            return inventory.get(slot);
        } else return null;
    }

    /**
     * simple getter.
     *
     * @return item player has in hand or null if he/she has nothing.
     */
    public ItemStack getItemInHand() {
        if (inHand != null) {
            return inHand;
        } else return null;
    }

    /**
     * simple size getter.
     *
     * @return inventory size, excluding the item inHand.
     */
    public int getSize() {
        return inventory.size();
    }

    /**
     * It stores the items in the given inventory position.
     * If there were other items in that position, they would be replaced by the new ones.
     *
     * @param pos   position in the inventory to be used to store the item.
     * @param items item to be stored in ineventory.
     * @throws BadInventoryPositionException if the position does not exist (counting from zero).
     */
    public void setItem(int pos, ItemStack items) throws BadInventoryPositionException {
        if (pos >= 0 && inventory.size() > pos)
            inventory.set(pos, items);
        else
            throw new BadInventoryPositionException(pos);
    }

    /**
     * It assigns the items to carry in the hand by the player. A null value for ‘items’ means that the player carries nothing.
     * If the player had something in his or her hand before, they will be replaced by the new items.
     *
     * @param item ItemStack instance to be set as a inHand item of the player.
     */
    public void setItemInHand(ItemStack item) {
        inHand = item;
    }

    /**
     * Creates a string that represents the contents of the inventory.
     *
     * @return string that contains info about the inventory content.
     */
    public String toString() {

        return "(inHand="+inHand+","+ inventory+")";
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
        result = prime * result + ((inHand == null) ? 0 : inHand.hashCode());
        result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
        return result;
    }

    /**
     * Compares this object to another indicated.
     *
     * @return true if objects are equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Inventory other = (Inventory) obj;
        if (inHand == null) {
            if (other.inHand != null)
                return false;
        } else if (!inHand.equals(other.inHand))
            return false;
        if (inventory == null) {
            if (other.inventory != null)
                return false;
        } else if (!inventory.equals(other.inventory))
            return false;
        return true;
    }
}
