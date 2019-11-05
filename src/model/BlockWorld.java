/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

/**
 * The class BlockWorld represents the whole game and its basic functionality;
 * consequently there will only be a single instance of it- singleton design pattern implemented.
 */
public class BlockWorld {
    /**
     * Instance of the world class that belongs to the game.
     */
    private World world;
    /**
     * instance of the class itself, is private in order to prevent creating many instances of this class.
     */
    private static BlockWorld blockWorldInstance;

    /**
     * responsible for creating that instance and saving a reference to it in the ‘instance’ attribute the first time it is invoked.
     *
     * @return a reference to the single instance of this class.
     */
    public static BlockWorld getInstance() {
        if (blockWorldInstance == null) {
            blockWorldInstance = new BlockWorld();

        }
        return blockWorldInstance;
    }

    /**
     * private contructor of the class.
     */
    private BlockWorld() {
        world = null;
    }

    /**
     * It calls the constructor of World to create a new world.
     *
     * @param seed is a seed parameter for world creation.
     * @param size is a size of the world to be created.
     * @param name is a name of the world to be created.
     * @return new world instance with assigned parameters.
     */
    public World createWorld(long seed, int size, String name) {
        World w = new World(seed, size, name);
        this.world = w;
        return w;
    }

    /**
     * Creates a string with the information about the player and the neighbouhood of the player's location,
     * using the methods Player.toString() and World.getNeighbourhoodString().
     *
     * @param player instance of player class from which we want to obtain the info.
     * @return information about the player and what is in his or her adjacent locations.
     */
    public String showPlayerInfo(Player player) {
        String info;
        try {
            info= player.toString() + "\n"
                    + player.getLocation().getWorld().getNeighbourhoodString(player.getLocation());

        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        return info;
    }

    /**
     * It moves the player to the adjacent location (x+dx,y+dy,z+dz) and makes him or her collect the items in that location, if any.
     *
     * @param p  player instance to be moved.
     * @param dx movement in the x axis.
     * @param dy movement in the y axis.
     * @param dz movemeny in the z axis.
     * @throws EntityIsDeadException if the player to be moved is dead.
     * @throws BadLocationException  if the target location is not adjacent to the current one, is occupied or is not valid.
     */
    public void movePlayer(Player p, int dx, int dy, int dz) throws BadLocationException, EntityIsDeadException {
        p.move(dx, dy, dz);
        if (world.getItemsAt(p.getLocation()) != null) {
            p.addItemsToInventory(world.getItemsAt(p.getLocation()));
            world.removeItemsAt(p.getLocation());
        }
    }

    /**
     * It calls a method Player.selectItem() with the argument ‘pos’.
     *
     * @param player player class entity of which item will be reached.
     * @param pos    position of the item in the inventory.
     * @throws BadInventoryPositionException if the given position does not exist.
     */
    public void selectItem(Player player, int pos) throws BadInventoryPositionException {
        player.selectItem(pos);
    }

    /**
     * It makes the player to use the item in his hand ‘times’ times by calling method Player.useItemInHand().
     *
     * @param p     player
     * @param times number of times the item will be used.
     * @throws EntityIsDeadException    if the player is dead before using the item.
     * @throws IllegalArgumentException if the argument ‘times’ is less than or equal to zero.
     */
    public void useItem(Player p, int times) throws EntityIsDeadException, IllegalArgumentException {
        p.useItemInHand(times);
    }

}
