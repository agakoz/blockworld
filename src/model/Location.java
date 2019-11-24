/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

import java.util.*;

import static java.lang.StrictMath.sqrt;

/**
 * <p>
 * Class represents a position in a three-dimensional world formed by blocks of
 * different materials. It allows for creation and manipulation of the location,
 * changing values of the location parameters. Each position (Location) refers
 * to the world it belongs to.
 * </p>
 */

public class Location {
    /**
     * <p>
     * World to which location is associated.
     * </p>
     */
    World world;
    /**
     * <p>
     * Represents x-axis: indicates the distance to the east (positive) or west
     * (negative) from the origin, i.e. the longitude.
     * </p>
     */
    private double x;
    /**
     * <p>
     * Represents z-axis: indicates the distance to the south (positive) or north
     * (negative) from the origin, i.e. the latitude.
     * </p>
     */
    private double y;
    /**
     * <p>
     * Represents y-axis: indicates the height (from 0 to 255, 63 being the sea
     * level) from the origin, i.e. the elevation.
     * </p>
     */
    private double z;
    /**
     * <p>
     * Indicates the maximal location elevation value.
     * </p>
     */
    public static final double UPPER_Y_VALUE = 255;
    /**
     * <p>
     * Indicates the sea level.
     * </p>
     */
    public static final double SEA_LEVEL = 63;

    /**
     * <p>
     * Constructor, creates location with set parameters.
     * </P>
     *
     * @param w instance of world class; will be set as the world of the location.
     * @param x value to be set as the x attribute.
     * @param y value to be set as the Y attribute.
     * @param z value to be set as the Z attribute.
     */
    public Location(World w, double x, double y, double z) {
        world = w;
        setX(x);
        setY(y);
        setZ(z);
    }

    /**
     * Constructor, creates location not associated with any world, with set parameters.
     *
     * @param x value to be set as the x attribute.
     * @param y value to be set as the Y attribute.
     * @param z value to be set as the Z attribute.
     */
    public Location(double x, double y, double z) {
        world = null;
        setX(x);
        setY(y);
        setZ(z);
    }

    /**
     * <p>
     * Constructor, copies values of parameters from one location to the created
     * one.
     * </p>
     *
     * @param loc instance of location class, from this object all the parameters
     *            for the location which is being created are copied.
     */
    public Location(Location loc) {
        world = loc.getWorld();
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();

    }

    /**
     * <p>
     * Adds to x, y, z parameters the values from the parameters from another
     * location.
     * </p>
     *
     * @param loc is an instance of location class, its parameters value will be
     *            used for calculations.
     * @return location with changed parameters values.
     */
    public Location add(Location loc) {
        if (loc.getWorld().equals(world)) {
            setX(x + loc.getX());
            setY(y + loc.getY());
            setZ(z + loc.getZ());

        } else {
            System.err.println("Cannot add Location of differing worlds.");
        }
        return this;

    }


    /**
     * <p>
     * Calculates the distance from the location to another location using the
     * mathematical model.
     * </p>
     *
     * @param loc instance of location class to which the distance from the location
     *            will be calculated.
     * @return distance between location.
     */
    public double distance(Location loc) {
        if (loc.getWorld() == null || getWorld() == null) {
            System.err.println("Cannot measure distance to a null world");
            return -1.0;
        } else if (loc.getWorld() != getWorld()) {
            System.err.printf("Cannot measure distance between %s and %s", world.getName(), loc.getWorld().getName());
            return -1.0;
        } else {
            double dx = x - loc.x;
            double dy = y - loc.y;
            double dz = z - loc.z;
            return sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    /**
     * <p>
     * Simple world getter
     * </p>
     *
     * @return world object
     */
    public World getWorld() {
        return world;
    }

    /**
     * <p>
     * Simple x parameter getter.
     * </p>
     *
     * @return x parameter.
     */
    public double getX() {
        return x;
    }

    /**
     * <p>
     * Simple y parameter getter.
     * </p>
     *
     * @return y parameter.
     */
    public double getY() {
        return y;
    }

    /**
     * <p>
     * Simple z parameter getter.
     * </p>
     *
     * @return z parameter.
     */
    public double getZ() {
        return z;
    }

    /**
     * <p>
     * Simple setter. Sets world parameter.
     * </p>
     *
     * @param w instance of World class whis is set as the world parameter.
     */
    public void setWorld(World w) {
        world = w;
    }

    /**
     * <p>
     * Simple setter. Sets x parameter value.
     * </p>
     *
     * @param x value for x parameter
     */
    public void setX(double x) {

        this.x = x;
    }

    /**
     * <p>
     * Setter. Sets y parameter value. Checks if the y value is between allowed
     * limits, if yes: sets y value as the y parameter, if not: sets upper or lower
     * limit.
     * </p>
     *
     * @param y value for y parameter
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * <p>
     * Simple setter. Sets z parameter value.
     * </p>
     *
     * @param z value for z parameter
     */
    public void setZ(double z) {

        this.z = z;
    }

    /**
     * <p>
     * Calculates the length vector of the location using the mathematical formula
     * </p>
     *
     * @return calculated length vector
     */
    public double length() {

        return sqrt(x * x + y * y + z * z);
    }

    /**
     * <p>
     * Changes x, y, z parameters to result of their multiplication with the factor
     * given to the function.
     * </p>
     *
     * @param factor indicates value which will multiply the parameters values.
     * @return location with changed parameters.
     */
    public Location multiply(double factor) {
        x *= factor;
        setY(y * factor);
        z *= factor;
        return this;
    }

    /**
     * <p>
     * Subtracts from x, y, z parameters the values from the parameters from another
     * location.
     * </p>
     *
     * @param loc is an instance of location class, its parameters value will be
     *            used for calculations.
     * @return location with changed parameters values.
     */
    public Location substract(Location loc) {
        if (loc.world != world)
            System.err.println("Cannot substract Locations of differing worlds.");
        else {
            x -= loc.x;
            setY(y - loc.y);
            z -= loc.z;
        }
        return this;
    }

    /**
     * <p>
     * Zeroes the x, y, z parameters of the location.
     * </p>
     *
     * @return location with zeroed x, y, z parameter.
     */
    public Location zero() {
        x = y = z = 0.0;
        return this;
    }

    /**
     * <p>
     * Compares the location with other object in order to assess if they are equal
     * or not.
     * </p>
     *
     * @param  obj object to be assessed equal or not to the world
     * @return true when the equality ofall the parameters is assessed, false if the
     * objects are not equal- either parameters are not equal, or the object
     * is not an instance of the location class.
     */
    public boolean equals(Object obj) {
        // self check
        if (this == obj) return true;
        // null check
        if (obj == null) return false;
        // type check and cast
        if (getClass() != obj.getClass()) return false;
        Location loc = (Location) obj;
        // field comparison
        if (world==null){
            if (loc.world!=null){
                return false;
            }
            return (x == loc.x && y == loc.y && z == loc.z);
        }
        return (x == loc.x && y == loc.y && z == loc.z && world.equals(loc.world));
    }

    /**
     * <p>
     * Calculates hash code by following the formula.
     * </p>
     *
     * @return hashCode calculated.
     */
    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (int) x;
        result = prime * result + (int) y;
        result = prime * result + (int) z;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        return result;
    }

    /**
     * <p>
     * Allows to out print the object by overwriting the toString method.
     * </p>
     *
     * @return name of location's world and values of x, y, z parameters.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("Location{world=");
        if (world == null)
            sb.append("NULL");
        else
            sb.append(world);

        sb.append(",x=").append(x)
                .append(",y=").append(y)
                .append(",z=").append(z).append("}");
        return sb.toString();
    }

    /**
     * Class method that checks that the values ‘x’, ‘y’, ‘z’ are within the limits of the given world.
     *
     * @param w instance of the world class
     * @param x value that represents x parameter.
     * @param y value that represents y parameter.
     * @param z value that represents z parameter.
     * @return true if the parameters are within the limits of the given world, false if not.
     */

    public static boolean check(World w, double x, double y, double z) {
        int worldsize = w.getSize();
        int positiveWorldLimit = worldsize / 2;
        int negativeWorldLimit = (worldsize % 2 == 0) ? -(positiveWorldLimit - 1) : -positiveWorldLimit;

        if (negativeWorldLimit <= x && x <= positiveWorldLimit && 0 <= y && y <= UPPER_Y_VALUE && negativeWorldLimit <= z && z <= positiveWorldLimit) {
            return true;
        }
        return false;
    }

    /**
     * Class method that checks that the location's coordinates are within the limits of the given world.
     *
     * @param loc Location of which coordinates values are to be checked
     * @return true if the parameters are within the limits of the given world, false if not.
     */
    public static boolean check(Location loc) {

        return check(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * It checks if a location is not occupied by a block or by the player.
     * A location without an associated world is never free.
     *
     * @return true if location is free, false otherwise.
     */
    public boolean isFree() {
        try {
            if (world != null)
                return world.isFree(this);
        } catch (BadLocationException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * It returns the location just below it.
     *
     * @return new instance of calculated location below.
     * @throws BadLocationException if the current location belongs to a world and its height is zero.
     */
    public Location below() throws BadLocationException {
        if (y == 0 && world != null) {
            throw new BadLocationException("Current location belongs to a world and its height is zero. " +
                    "Negatives not allowed.");
        } else {
            return new Location(world, x, y - 1, z);
        }
    }

    /**
     * It returns the location just above it.
     *
     * @return ew instance of calculated location above.
     * @throws BadLocationException if the current location belongs to a world and its height is the highest possible.
     */
    public Location above() throws BadLocationException {
        if (y == UPPER_Y_VALUE && world != null) {
            throw new BadLocationException("Current location belongs to a world and its height is already the highest possible.");
        } else {
            return new Location(world, x, y + 1, z);
        }
    }


    /**
     * It returns the locations adjacent to this one.
     *
     * @return valid adjacent locations if the location belong to a world, if not, all adjacent locations.
     */
    public Set<Location> getNeighborhood() {
        Set<Location> adjacentLoc = new HashSet<>();
        Location loc;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if ((i != 0) || (j != 0) || (k != 0)) {
                        if (world != null) {
                            if (check(loc = new Location(world, x + i, y + j, z + k))) {
                                adjacentLoc.add(loc);
                            }
                        } else adjacentLoc.add(new Location(x + i, y + j, z + k));
                    }
                }
            }
        }
        return adjacentLoc;

    }

}

