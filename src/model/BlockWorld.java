/**
 * @author agata.koziol
 */
package model;

import model.entities.*;
import model.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
            info = player.toString() + "\n"
                    + player.getLocation().getWorld().getNeighbourhoodString(player.getLocation());

        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        return info;
    }

    /**
     * It moves the player to the adjacent location (x+dx,y+dy,z+dz) and makes him or her collect the items in that location, if any.
     * It take sinto account that the player can go through liquid blocks and that they can damage the payer when going through them.
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
        if (world.getBlockAt(p.getLocation()) != null) {
            if (world.getBlockAt(p.getLocation()).getType().isLiquid())
                p.damage(world.getBlockAt(p.getLocation()).getType().getValue());
        }
        System.out.println(world.getItemsAt(p.getLocation()));
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
        try {
            ItemStack usedItem = p.useItemInHand(times);
            if (usedItem != null) {
                Location location = p.getOrientation();
                if (!usedItem.getType().isEdible() && Location.check(location)) {
                    double damage;
                    if (usedItem.getType().isBlock()) {
                        if (world.isFree(location)) {
                            try {
                                world.addBlock(location, new SolidBlock(usedItem.getType()));
                            } catch (BadLocationException e) {
                            } catch (WrongMaterialException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                            return;
                        }
                        damage = 0.1 * times;
                    } else {
                        damage = usedItem.getType().getValue() * times;
                    }


                    Block block;
                    Creature creature;
                    if ((block = world.getBlockAt(location)) != null) {
                        if (block.getClass() == SolidBlock.class) {
                            SolidBlock solidBlock = (SolidBlock) block;
                            if (solidBlock.breaks(damage)) {
                                world.destroyBlockAt(location);
                                if (solidBlock.getDrops() != null) {
                                    world.addItems(location, solidBlock.getDrops());
                                }
                            }
                        }
                    } else if ((creature = world.getCreatureAt(location)) != null) {
                        if (creature.getClass() == Monster.class) {
                            Monster monster = (Monster) creature;
                            monster.damage(damage);
                            if (!monster.isDead()) {
                                p.damage(0.5 * times);
                            } else {
                                world.killCreature(location);
                            }
                        } else if (creature.getClass() == Animal.class) {
                            Animal animal = (Animal) creature;
                            animal.damage(damage);
                            if (animal.isDead()) {
                                world.killCreature(location);
                                world.addItems(location, animal.getDrops());
                            }
                        }
                    }
                }
            }
        } catch (BadLocationException ex) {
            throw new RuntimeException();
        }
    }

    /**
     * Calls the player’s orientate() method in order to orientate the player.
     *
     * @param p  player
     * @param dx x-axis coordinate
     * @param dy y-axis coordinate
     * @param dz z-axis coordinate
     * @throws BadLocationException  If dx==dy==dz==0 (a player cannot be oriented towards himself) or
     *                               the orientation is not towards an adjacent location
     * @throws EntityIsDeadException Exception if the player to be orientated is dead.
     */
    public void orientatePlayer(Player p, int dx, int dy, int dz) throws BadLocationException, EntityIsDeadException {
        p.orientate(dx, dy, dz);
    }

    /**
     * Opens the given input file and executes each one of its commands (by calling play(Scanner)).
     *
     * @param path path to the document
     * @throws FileNotFoundException if the file is not found.
     */
    public void playFile(String path) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(path));
        play(fileScanner);

    }

    /**
     * Creates the scanner that reads from the standard input the commands to be executed.
     */
    public void playFromConsole() {
        Scanner consoleScanner = new Scanner(System.in);
        play(consoleScanner);

    }

    /**
     * Executes the commands it reads, line by line, from the Scanner object passed as argument.
     * It catches the exceptions that may occur during the execution of these commands,
     * printing the exception message to the error output.
     * If an unknown command is found, it displays an appropriate error message through the error output
     * and continues reading the next command. It stops reading from the Scanner object if
     * there is nothing left to read or the player has died.
     *
     * @param sc scanner
     */
    public void play(Scanner sc) {

        String[] worldInfo = sc.nextLine().split(" ", 3);
        createWorld(Long.parseLong(worldInfo[0]), Integer.parseInt(worldInfo[1]), worldInfo[2]);

        while (sc.hasNextLine() && !world.getPlayer().isDead()) {
            try {
                String line = sc.nextLine();
                Scanner lineScanner = new Scanner(line);
                String command = lineScanner.next();

                switch (command) {
                    case "move":
                        movePlayer(world.getPlayer(), lineScanner.nextInt(), lineScanner.nextInt(), lineScanner.nextInt());
                        break;
                    case "orientate":
                        orientatePlayer(world.getPlayer(), lineScanner.nextInt(), lineScanner.nextInt(), lineScanner.nextInt());
                        break;
                    case "useItem":
                        useItem(world.getPlayer(), lineScanner.nextInt());
                        break;
                    case "show":
                        System.out.println(showPlayerInfo(world.getPlayer()));
                        break;
                    case "selectItem":
                        selectItem(world.getPlayer(), lineScanner.nextInt());
                        break;
                    default:
                        throw new UnknownGameCommandException(command);

                }


            } catch (Exception ex) {
                //  ex.printStackTrace();
                System.err.println(ex.getMessage());
            }

        }
    }
}

