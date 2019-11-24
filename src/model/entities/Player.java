/**
 * @author agata.koziol
 */
package model.entities;

import model.*;
import model.exceptions.*;

/**
 * It represents the BlockWorld player. The player has an inventory of items and knows his own location in the world.
 * He or she has a level of health and a level of food that will vary during the development of the game.
 *
 */
public class Player extends LivingEntity {
    /**
     * Represents user's name.
     */
    private String name;
    /**
     * Represents user's food level.
     */
    private double foodLevel;
    /**
     * Represent the maximum levels of food that a player can have.
     */
    public static final double MAX_FOODLEVEL = 20;
    /**
     * User's symbol
     */
    private static char symbol = 'P';
    /**
     * Player's inventory in which he/she holds the items.
     */
    private Inventory inventory;

    /**
     * Simple constructor that assign values of name given as the parameter and health as 20.
     *
     * @param name will be assigned as the value of player's name.
     */
    private Location orientation;

    /**
     * Simple constructor that assignes all the values and defines the user's orientation and inventory
     * @param name name of the player
     * @param world world to which the player will be assigned
     */
    public Player(String name, World world) {
        super(new Location(world, 0,0,0), MAX_HEALTH);
        try {
            this.name = name;
            foodLevel = MAX_FOODLEVEL;
            location = world.getHighestLocationAt(location);
            location = location.above();
            orientation = new Location(getLocation());
            orientate(0,0,1);
            inventory = new Inventory();
            inventory.setItemInHand(new ItemStack(Material.WOOD_SWORD, 1));
        } catch (StackSizeException ex) {
            System.out.println(ex.getMessage());
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex.getMessage());
        } catch (EntityIsDeadException ex){
            System.out.println(ex.getMessage());

        }

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
           else if (foodLevel < 0) this.foodLevel = 0;
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
     * <p>
     * Method also updates the orientation of the player.
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
        location = new Location(newLocation);
        orientation.add(new Location(orientation.getWorld(), dx, dy, dz));
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
     * @return item that player has in hand
     * @throws EntityIsDeadException    if the player is dead before using the item.
     * @throws IllegalArgumentException if the argument ‘times’ is less than or equal to zero.
     */
    public ItemStack useItemInHand(int times) throws EntityIsDeadException, IllegalArgumentException {
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
                            } catch (StackSizeException ex) {
                                throw new RuntimeException();
                            }

                            times--;

                        }
                    } else {
                        decreaseFoodLevel(0.1 * times);
                    }
                }
            } else throw new IllegalArgumentException("The item has to be used the positive number of times.");
        } else throw new EntityIsDeadException();
        return inventory.getItemInHand();
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
            if (inventory.getItemInHand() == null || inventory.getItemInHand().getAmount() == 0) {
                inventory.setItemInHand(inventory.getItem(pos));
                inventory.clear(pos);
            } else {
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
     * simple getter.
     *
     * @return player’s orientation as an absolute location.
     */
    public Location getOrientation() {
        return new Location(orientation);
    }

    /**
     * implementation of the LivingEntity class method.
     *
     * @return the char ‘P’, which represents the player.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * It changes the player’s orientation. If the player is in location (x,y,z), he or she is faced towards location (x+dx,y+dy,z+dz)
     * @param dx x-axis coordinate
     * @param dy y-axis cooridate
     * @param dz z-axis coordinate
     * @return new orienattion of the player
     * @throws EntityIsDeadException if the player is dead
     * @throws BadLocationException  If dx==dy==dz==0 (a player cannot be oriented towards himself)
     * or the orientation is not towards an adjacent location.
     */
    public Location orientate(int dx, int dy, int dz) throws EntityIsDeadException, BadLocationException {
        Location newOrientLoc = new Location(location).add(new Location(location.getWorld(), dx, dy, dz));
        if (isDead()) throw new EntityIsDeadException();
        if (dx == 0 && dy == 0 && dz == 0)
            throw new BadLocationException("a player cannot be oriented towards himself");
        if (dx>1 || dx<-1 || dy>1 || dy<-1 || dz>1 || dz<-1 )
            throw new BadLocationException("the orientation is not torwards an adjacent location.");
        orientation = newOrientLoc;
        return orientation;
    }

    /**
     * calculates the relative location of the player.
     * @return relative location.
     */
    private Location getRelativeLocation(){
        double x= orientation.getX() - location.getX();
        double y= orientation.getY() - location.getY();
        double z= orientation.getZ() - location.getZ();

        return new Location(location.getWorld(), x, y, z);
    }
    /**
     * Creates the string with user infromation.
     *
     * @return the player information.
     */
    public String toString() {
        return "Name=" + getName() + "\n" +
                location.toString() + "\n" +
                "Orientation=" + getRelativeLocation().toString() + "\n" +
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
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(foodLevel);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
        return result;
    }

    /**
     * Compares this object to another indicated.
     * @param obj The object which should be checked
     * @return whether the objects are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (Double.doubleToLongBits(foodLevel) != Double.doubleToLongBits(other.foodLevel))
            return false;
        if (inventory == null) {
            if (other.inventory != null)
                return false;
        } else if (!inventory.equals(other.inventory))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (orientation == null) {
            if (other.orientation != null)
                return false;
        } else if (!orientation.equals(other.orientation))
            return false;
        return true;
    }
}
