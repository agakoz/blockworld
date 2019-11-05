/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

/**
 * It represents the BlockWorld player. The player has an inventory of items and knows his own location in the world.
 * He or she has a level of health and a level of food that will vary during the development of the game.
 *
 * @author agata.koziol
 */
public class Player {
    /**
     * Represents user's name.
     */
    private String name;
    /**
     * Represents user's health.
     */
    private double health;
    /**
     * Represents user's food level.
     */
    private double foodLevel;
    /**
     * Represent the maximum levels of food that a player can have.
     */
    public static final double MAX_FOODLEVEL = 20;
    /**
     * represent the maximum levels of health that a player can have.
     */
    public static final double MAX_HEALTH = 20;
    /**
     * Represents location of the Player.
     */
    private Location location;
    /**
     * Player's inventory in which he/she holds the items.
     */
    private Inventory inventory;

    /**
     * Simple constructor that assign values of name given as the parameter and health as 20.
     *
     * @param name will be assigned as the value of player's name.
     */
    public Player(String name, World world) {
        try {
            this.name = name;

            health = MAX_HEALTH;
            foodLevel = MAX_FOODLEVEL;
            location = world.getHighestLocationAt(new Location(world, 0, 0, 0));
            location.add(new Location(world, 0, 1, 0));
            inventory = new Inventory();
            inventory.setItemInHand(new ItemStack(Material.WOOD_SWORD, 1));
        } catch (StackSizeException ex) {
            System.out.println(ex.getMessage());
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

    /**
     * Simple getter.
     *
     * @return a copy of the player's location.
     */
    public Location getLocation() {
        return new Location(location);
    }

    /**
     * It checks if the player has a health level equal to or less than zero.
     *
     * @return false if the player's health is greater than 0, true otherwise.
     */
    public boolean isDead() {
        return (health <= 0);
    }

    /**
     * simple getter.
     *
     * @return player's health points.
     */
    public double getHealth() {

        return health;
    }

    /**
     * simple setter. It sets the health level, which saturates in MAX_HEALTH.
     *
     * @param health health points to be set as a player's health.
     */
    public void setHealth(double health) {
        if (health > MAX_HEALTH) this.health = MAX_HEALTH;
        else this.health = health;
    }

    /**
     * simple getter.
     *
     * @return player's food level.
     */
    public double getFoodLevel() {

        return foodLevel;
    }

    /**
     * It sets the player’s current food level, which saturates in MAX_FOODLEVEL.
     *
     * @param foodLevel food level to be set as a player's foodlevel.
     */
    public void setFoodLevel(double foodLevel) {
        if (foodLevel > MAX_FOODLEVEL) this.foodLevel = MAX_FOODLEVEL;
        //else if (foodLevel < 0) this.foodLevel = 0;
        else this.foodLevel = foodLevel;
    }

    /**
     * simple getter to obtaint player's name.
     *
     * @return player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * simple getter to obtaint the size of the inventory, it uses the method from inventory class.
     *
     * @return size of the player's inventory, excluding the item that user has in hand.
     */
    public int getInventorySize() {
        return inventory.getSize();
    }

    /**
     * If (x,y,z) is the player’s current location, it ‘moves’ the player to location (x+dx,y+dy,z+dz).
     * The target location must be a location adjacent to the current one
     * and must not be occupied by a block or by the player himself.
     * <p>
     * The player does not move if the method throws any of these exceptions.
     * If he is able to move, his food/health level gets decreased by 0.05 points.
     *
     * @param dx movement in x axis
     * @param dy movement in y axis
     * @param dz movement in z axis
     * @return a copy of the location after movement.
     * @throws EntityIsDeadException if the player to be moved is dead.
     * @throws BadLocationException  if the target location is not adjacent to the current one, is occupied or is not valid.
     */
    public Location move(int dx, int dy, int dz) throws EntityIsDeadException, BadLocationException {
        Location newLocation = new Location(location).add(new Location(location.getWorld(), dx, dy, dz));

        if (isDead()) {
            throw new EntityIsDeadException();
        } else if (!location.getNeighborhood().contains(newLocation)) {
            throw new BadLocationException("Location is not adjacent to the current one.");
        } else if (!newLocation.isFree()) {
            throw new BadLocationException("Location is occupied.");
        } else if (!Location.check(newLocation)) {
            throw new BadLocationException("Location is not valid.");
        }
        decreaseFoodLevel(0.05);
        location = newLocation;
        return new Location(location);
    }

    /**
     * Using item inHand by the Player.
     * If what is in the player’s hand is food, it increases the player’s food/health level.
     * Each food unit restores the corresponding food or health level points, as appropriate.
     * If the player has no items in his or her hand, it does nothing. But, if what is in the player’s hand is not food,
     * it decreases the player’s food/health level by 0.1*times points.
     *
     * @param times indicates the number of uses of the item
     * @throws EntityIsDeadException    if the player is dead before using the item.
     * @throws IllegalArgumentException if the argument ‘times’ is less than or equal to zero.
     */
    public void useItemInHand(int times) throws EntityIsDeadException, IllegalArgumentException {
        if (!isDead()) {
            if (times > 0) {
                if (inventory.getItemInHand() != null) {
                    if (inventory.getItemInHand().getType().isEdible()) {
                        while (times >= 1 && inventory.getItemInHand().getAmount() > 0) {
                            increaseFoodLevel(inventory.getItemInHand().getType().getValue());
                            int amount = inventory.getItemInHand().getAmount() - 1;
                            try {
                                if (amount > 0)
                                    inventory.getItemInHand().setAmount(amount);
                                else {
                                    inventory.setItemInHand(null);
                                    break;
                                }
                            } catch (StackSizeException ex) { throw new RuntimeException();
                            }

                            times--;

                        }
                    } else {
                        decreaseFoodLevel(0.1 * times);
                    }
                }
            } else throw new IllegalArgumentException("The item has to be used the positive number of times.");
        } else throw new EntityIsDeadException();

    }

    /**
     * It swaps the item in the hand for the one in position ‘pos’.
     * If the player does not have an item in his or her hand,
     * it selects the one in position ‘pos’ of the inventory and removes it from the inventory list.
     *
     * @param pos position in the inventory
     * @throws BadInventoryPositionException if the given position does not exist.
     */
    public void selectItem(int pos) throws BadInventoryPositionException {
        if (inventory.getItem(pos) != null) {
            if(inventory.getItemInHand()==null || inventory.getItemInHand().getAmount()==0){
                inventory.setItemInHand(inventory.getItem(pos));
                inventory.clear(pos);
            }
            else{
                ItemStack swap = inventory.getItemInHand();
                inventory.setItemInHand(inventory.getItem(pos));
                inventory.setItem(pos, swap);
            }

        } else throw new BadInventoryPositionException(pos);
    }

    /**
     * It adds the items to the player’s inventory; they are stored in a new inventory position.
     *
     * @param item item to be added to the inventory.
     */
    public void addItemsToInventory(ItemStack item) {
        inventory.addItem(item);
    }

    /**
     * It decreases the food/health level by indicated units.
     * First it decreases from food level, then if foodlevel equals to 0, it decreases life.
     *
     * @param d indicates the units by with the food/health level will be decreased.
     */
    private void decreaseFoodLevel(double d) {
        if (foodLevel >= d) foodLevel -= d;
        else {
            double difference = d - foodLevel;
            foodLevel = 0;
            health -= difference;
        }
    }

    /**
     * It increases the food/health level by indicated units.
     * First it increases from food level, then if food level equals to 0, it increases life.
     *
     * @param d indicates the units by with the food/health level will be increased.
     */
    private void increaseFoodLevel(double d) {
        if (foodLevel + d <= MAX_FOODLEVEL) foodLevel += d;
        else {
            double difference = d - (MAX_FOODLEVEL - foodLevel);
            foodLevel = MAX_FOODLEVEL;
            if (health + difference <= MAX_HEALTH) health += difference;
            else health = MAX_HEALTH;
        }
    }

    /**
     * Creates the string with user infromation.
     *
     * @return the player information.
     */
    public String toString() {
        return "Name=" + getName() + "\n" +
                location.toString() + "\n" +
                "Health=" + getHealth() + "\n" +
                "Food level=" + foodLevel + "\n" +
                "Inventory=" + inventory.toString();
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
        long temp;
        temp = Double.doubleToLongBits(foodLevel);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(health);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Player other = (Player) obj;
        if (Double.doubleToLongBits(foodLevel) != Double.doubleToLongBits(other.foodLevel))
            return false;
        if (Double.doubleToLongBits(health) != Double.doubleToLongBits(other.health))
            return false;
        if (inventory == null) {
            if (other.inventory != null)
                return false;
        } else if (!inventory.equals(other.inventory))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
