/**
 * @author agata.koziol
 */
package model;

import model.exceptions.*;

import java.util.*;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

/**
 * class World
 */
public class World {


    /**
     * name of the world
     */
    private String name;

    /**
     * Size of the world in the (x,z) plane.
     */
    private int worldSize;

    /**
     * World seed for procedural world generation
     */
    private long seed;

    /**
     * bloques de este mundo
     */
    private Map<Location, Block> blocks;

    /**
     * Items depositados en algĂşn lugar de este mundo.
     */
    private Map<Location, ItemStack> items;

    /**
     * El jugador
     */
    private Player player;

    /**
     * <p>Constructor; creates an instance of the world class.</p>
     *
     * @param name represents a name of the world to create
     * @deprecated do not use this constructor starting from assignment 2
     */
    public World(String name) {
        this.name = name;
    }

    /**
     * Esta clase interna representa un mapa de alturas bidimiensional
     * que nos servirĂĄ para guardar la altura del terreno (coordenada 'y')
     * en un array bidimensional, e indexarlo con valores 'x' y 'z' positivos o negativos.
     * <p>
     * la localizaciĂłn x=0,z=0 queda en el centro del mundo.
     * Por ejemplo, un mundo de tamaĂąo 51 tiene su extremo noroeste a nivel del mar en la posiciĂłn (-25,63,-25)
     * y su extremo sureste, tambiĂŠn a nivel del mar, en la posiciĂłn (25,63,25).
     * Para un mundo de tamaĂąo 50, estos extremos serĂĄn (-24,63,-24) y (25,63,25), respectivamente.
     * <p>
     * Por ejemplo, para obtener la altura del terreno en estas posiciones, invocarĂ­amos al mĂŠtodo get() de esta  clase:
     * get(-24,24) y get(25,25)
     * <p>
     * de forma anĂĄloga, si queremos modificar el valor 'y' almacenado, haremos
     * set(-24,24,70)
     */
    class HeightMap {
        double[][] heightMap;

        int positiveWorldLimit;
        int negativeWorldLimit;

        HeightMap(int worldsize) {
            heightMap = new double[worldsize][worldsize];
            positiveWorldLimit = worldsize / 2;
            negativeWorldLimit = (worldsize % 2 == 0) ? -(positiveWorldLimit - 1) : -positiveWorldLimit;
        }

        /**
         * obtiene la atura del  terreno en la posiciĂłn (x,z)
         *
         * @param x coordenada 'x' entre 'positiveWorldLimit' y 'negativeWorldLimit'
         * @param z coordenada 'z' entre 'positiveWorldLimit' y 'negativeWorldLimit'
         */
        double get(double x, double z) {
            return heightMap[(int) x - negativeWorldLimit][(int) z - negativeWorldLimit];
        }

        void set(double x, double z, double y) {
            heightMap[(int) x - negativeWorldLimit][(int) z - negativeWorldLimit] = y;
        }

    }


    /**
     * Coordenadas 'y' de la superficie del mundo. Se inicializa en generate() y debe actualizarse
     * cada vez que el jugador coloca un nuevo bloque en una posiciĂłn vacĂ­a
     * Puedes usarlo para localizar el bloque de la superficie de tu mundo.
     */
    private HeightMap heightMap;

    /**
     * It creates a world of size size*size in the plane (x,z).
     * It invokes the land generator (method generate()).
     *
     * @param seed is the seed for the land generator
     * @param size seize of the world to be generated.
     * @param name name of the world
     * @throws IllegalArgumentException if the given size is not greater than zero
     */
    public World(long seed, int size, String name) throws IllegalArgumentException {

        if (size > 0) {
            this.seed = seed;
            this.worldSize = size;
            this.name = name;
            blocks = new HashMap<>();
            items = new HashMap<>();
            generate(seed, size);
        } else throw new IllegalArgumentException();

    }

    /**
     * Genera un mundo nuevo del tamaĂąo size*size en el plano (x,z). Si existĂ­an elementos anteriores en el mundo,
     * serĂĄn eliminados. Usando la misma semilla y el mismo tamaĂąo podemos generar mundos iguales
     *
     * @param seed semilla para el algoritmo de generaciĂłn.
     * @param size tamaĂąo del mundo para las dimensiones x y z
     */
    private void generate(long seed, int size) {

        Random rng = new Random(getSeed());

        blocks.clear();
        items.clear();

        // Paso 1: generar nuevo mapa de alturas del terreno
        heightMap = new HeightMap(size);
        CombinedNoiseGenerator noise1 = new CombinedNoiseGenerator(this);
        CombinedNoiseGenerator noise2 = new CombinedNoiseGenerator(this);
        OctaveGenerator noise3 = new PerlinOctaveGenerator(this, 6);

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                double heightLow = noise1.noise(x * 1.3, z * 1.3) / 6.0 - 4.0;
                double heightHigh = noise2.noise(x * 1.3, z * 1.3) / 5.0 + 6.0;
                double heightResult = 0.0;
                if (noise3.noise(x, z, 0.5, 2) / 8.0 > 0.0)
                    heightResult = heightLow;
                else
                    heightResult = Math.max(heightHigh, heightLow);
                heightResult /= 2.0;
                if (heightResult < 0.0)
                    heightResult = heightResult * 8.0 / 10.0;
                heightMap.heightMap[x][z] = Math.floor(heightResult + Location.SEA_LEVEL);
            }
        }

        // Paso 2: generar estratos
        Block block = null;
        Location location = null;
        Material material = null;
        OctaveGenerator noise = new PerlinOctaveGenerator(this, 8);
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                double dirtThickness = noise.noise(x, z, 0.5, 2.0) / 24 - 4;
                double dirtTransition = heightMap.heightMap[x][z];
                double stoneTransition = dirtTransition + dirtThickness;
                for (int y = 0; y <= dirtTransition; y++) {
                    if (y == 0) material = Material.BEDROCK;
                    else if (y <= stoneTransition)
                        material = Material.STONE;
                    else // if (y <= dirtTransition)
                        material = Material.DIRT;
                    try {
                        location = new Location(this, x + heightMap.negativeWorldLimit, y, z + heightMap.negativeWorldLimit);
                        block = new Block(material);
                        if (rng.nextDouble() < 0.5) // los bloques contendrĂĄn item con un 50% de probabilidad
                            block.setDrops(block.getType(), 1);
                        blocks.put(location, block);
                    } catch (WrongMaterialException | StackSizeException e) {
                        // Should never happen
                        e.printStackTrace();
                    }
                }

            }
        }

        // Paso 3: Crear cuevas
        int numCuevas = size * size * 256 / 8192;
        // TODO: Crear varias cuevas (numCuevas)
        Location cavePos = new Location(this, rng.nextInt(size), rng.nextInt((int) Location.UPPER_Y_VALUE), rng.nextInt(size));
        double caveLength = rng.nextDouble() * rng.nextDouble() * 200;
        //cave direction is given by two angles and corresponding rate of change in those angles,
        //spherical coordinates perhaps?
        double theta = rng.nextDouble() * Math.PI * 2;
        double deltaTheta = 0.0;
        double phi = rng.nextDouble() * Math.PI * 2;
        double deltaPhi = 0.0;
        double caveRadius = rng.nextDouble() * rng.nextDouble();

        for (int i = 0; i < (int) caveLength; i++) {
            cavePos.setX(cavePos.getX() + Math.sin(theta) * Math.cos(phi));
            cavePos.setY(cavePos.getY() + Math.cos(theta) * Math.cos(phi));
            cavePos.setZ(cavePos.getZ() + Math.sin(phi));
            theta += deltaTheta * 0.2;
            deltaTheta *= 0.9;
            deltaTheta += rng.nextDouble();
            deltaTheta -= rng.nextDouble();
            phi /= 2.0;
            phi += deltaPhi / 4.0;
            deltaPhi *= 0.75;
            deltaPhi += rng.nextDouble();
            deltaPhi -= rng.nextDouble();
            if (rng.nextDouble() >= 0.25) {
                Location centerPos = new Location(cavePos);
                centerPos.setX(centerPos.getX() + (rng.nextDouble() * 4.0 - 2.0) * 0.2);
                centerPos.setY(centerPos.getY() + (rng.nextDouble() * 4.0 - 2.0) * 0.2);
                centerPos.setZ(centerPos.getZ() + (rng.nextDouble() * 4.0 - 2.0) * 0.2);
                double radius = (Location.UPPER_Y_VALUE - centerPos.getY()) / Location.UPPER_Y_VALUE;
                radius = 1.2 + (radius * 3.5 + 1) * caveRadius;
                radius *= Math.sin(i * Math.PI / caveLength);
                try {
                    fillOblateSpheroid(centerPos, radius, null);
                } catch (WrongMaterialException e) {
                    // Should not occur
                    e.printStackTrace();
                }
            }

        }

        // Paso 4: crear vetas de minerales
        // Abundancia de cada mineral
        double abundance[] = new double[2];
        abundance[0] = 0.9; // GRANITE
        abundance[1] = 0.5; // OBSIDIAN
        int numVeins[] = new int[2];
        numVeins[0] = size * size * 256 * (int) abundance[0] / 16384; // GRANITE
        numVeins[1] = size * size * 256 * (int) abundance[1] / 16384; // OBSIDIAN


        Material vein = Material.GRANITE;
        for (int numVein = 0; numVein < 2; numVein++, vein = Material.OBSIDIAN) {
            Location veinPos = new Location(this, rng.nextInt(size), rng.nextInt((int) Location.UPPER_Y_VALUE), rng.nextInt(size));
            double veinLength = rng.nextDouble() * rng.nextDouble() * 75 * abundance[numVein];
            //cave direction is given by two angles and corresponding rate of change in those angles,
            //spherical coordinates perhaps?
            theta = rng.nextDouble() * Math.PI * 2;
            deltaTheta = 0.0;
            phi = rng.nextDouble() * Math.PI * 2;
            deltaPhi = 0.0;
            //double caveRadius = rng.nextDouble() * rng.nextDouble();
            for (int len = 0; len < (int) veinLength; len++) {
                veinPos.setX(veinPos.getX() + Math.sin(theta) * Math.cos(phi));
                veinPos.setY(veinPos.getY() + Math.cos(theta) * Math.cos(phi));
                veinPos.setZ(veinPos.getZ() + Math.sin(phi));
                theta += deltaTheta * 0.2;
                deltaTheta *= 0.9;
                deltaTheta += rng.nextDouble();
                deltaTheta -= rng.nextDouble();
                phi /= 2.0;
                phi += deltaPhi / 4.0;
                deltaPhi *= 0.9; // 0.9 for veins
                deltaPhi += rng.nextDouble();
                deltaPhi -= rng.nextDouble();
                double radius = abundance[numVein] * Math.sin(len * Math.PI / veinLength) + 1;

                try {
                    fillOblateSpheroid(veinPos, radius, vein);
                } catch (WrongMaterialException ex) {
                    // should not ocuur
                    ex.printStackTrace();
                }
            }
        }

        // We obviate flood-fill water and lava

        // Paso 5. crear superficie y entidades
        // Las entidades aparecen sĂłlo en superficie (no en cuevas, por ejemplo)

        OctaveGenerator onoise1 = new PerlinOctaveGenerator(this, 8);
        OctaveGenerator onoise2 = new PerlinOctaveGenerator(this, 8);
        boolean sandChance = false;
        double y = 0.0;
        double entitySpawnChance = 0.05;
        double itemsSpawnChance = 0.10;
        double foodChance = 0.8;
        double toolChance = 0.1;
        double weaponChance = 0.1;

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                sandChance = onoise1.noise(x, z, 0.5, 2.0) > 8.0;
                y = heightMap.heightMap[x][z];
                Location surface = new Location(this, x + heightMap.negativeWorldLimit, y, z + heightMap.negativeWorldLimit); // la posiciĂłn (x,y+1,z) no estĂĄ ocupada (es AIR)
                try {
                    if (sandChance) {
                        Block sand = new Block(Material.SAND);
                        if (rng.nextDouble() < 0.5)
                            sand.setDrops(Material.SAND, 1);
                        blocks.put(surface, sand);
                    } else {
                        Block grass = new Block(Material.GRASS);
                        if (rng.nextDouble() < 0.5)
                            grass.setDrops(Material.GRASS, 1);
                        blocks.put(surface, grass);
                    }
                } catch (WrongMaterialException | StackSizeException ex) {
                    // will never happen
                    ex.printStackTrace();
                }
                try {
                    Location aboveSurface = surface.above();
                    // intentamos crear unos items de varios tipos (comida, armas, herramientas)
                    Material itemMaterial = null;
                    int amount = 1; // p. def. para herramientas y armas
                    if (rng.nextDouble() < itemsSpawnChance) {
                        double rand = rng.nextDouble();
                        if (rand < foodChance) { // crear comida
                            // hay cuatro tipos de item de comida, en las posiciones 8 a 11 del array 'materiales'
                            itemMaterial = Material.getRandomItem(8, 11);
                            amount = rng.nextInt(5) + 1;
                        } else if (rand < foodChance + toolChance)
                            // hay dos tipos de item herramienta, en las posiciones 12 a 13 del array 'materiales'
                            itemMaterial = Material.getRandomItem(12, 13);
                        else
                            // hay dos tipos de item arma, en las posiciones 14 a 15 del array 'materiales'
                            itemMaterial = Material.getRandomItem(14, 15);

                        items.put(aboveSurface, new ItemStack(itemMaterial, amount));
                    }
                } catch (BadLocationException | StackSizeException e) {
                    // BadLocationException : no hay posiciones mĂĄs arriba, ignoramos creaciĂłn de entidad/item sin hacer nada
                    // StackSizeException : no se producirĂĄ
                    // WrongMaterialException : al crear el cofre, no se producirĂĄ
                    throw new RuntimeException(e);
                }

            }
        }

        // TODO: Crear plantas

        // Generar jugador
        player = new Player("Steve", this);
        // El jugador se crea en la superficie (posiciĂłn (0,*,0)). AsegurĂŠmonos de que no hay nada mĂĄs ahĂ­
        Location playerLocation = player.getLocation();
        items.remove(playerLocation);

    }

    /**
     * Trivial getter, to obtain size of the world
     *
     * @return size of the world.
     */
    public int getSize() {
        return worldSize;
    }

    /**
     * Trivial getters to obtaint seed.
     *
     * @return seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Trivial getter to obtain name of the world.
     *
     * @return name of the world.
     */
    public String getName() {
        return name;
    }

    /**
     * Trivial getter, to obtain the player from the world/
     *
     * @return player class instance assigned to the world.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Getter, that returns the block in the given location.
     *
     * @param loc location from which we want to get the block.
     * @return It returns a copy of the block in the given location or null if there is no block there.
     * @throws BadLocationException It returns the block in the given location or null if there is no block there.
     */
    public Block getBlockAt(Location loc) throws BadLocationException {
        if (this.equals(loc.getWorld())) {
            Block copyBlock = blocks.getOrDefault(loc, null);
            return copyBlock;
        } else throw new BadLocationException("Location does not belong to this world.");

    }

    /**
     * It returns the ground location at the location (x,*,z)
     *
     * @param ground location from which we want to obtain ground level.
     * @return location instance with y parameter set as the height of the highest block in that locaion.
     * @throws BadLocationException if location ‘loc’ does not belong to this world.
     */
    public Location getHighestLocationAt(Location ground) throws BadLocationException {
        if (this.equals(ground.getWorld())) {
            return new Location(ground.getWorld(), ground.getX(), heightMap.get(ground.getX(), ground.getZ()), ground.getZ());
        } else throw new BadLocationException("Location does not belong to this world.");
    }

    /**
     * It returns the items that are in the given location, or null if there are none.
     *
     * @param loc location from which we want to obtain the item.
     * @return ItemStack class instance that is allocated in the given locaion or null if none item is in the location.
     * @throws BadLocationException if the location does not belong to this world.
     */
    public ItemStack getItemsAt(Location loc) throws BadLocationException {
        if (this.equals(loc.getWorld())) {
            return items.getOrDefault(loc, null);
        } else throw new BadLocationException("Location does not belong to this world.");

    }


    /**
     * It returns a string representing the locations adjacent to the given location.
     * Each location is represented by a character associated with the object that occupies that location,
     * or a dot (‘.’) if it is empty.
     * For blocks, tools, food or weapons, the corresponding symbol will be used.
     * For the player, the letter ‘P’ is used. For locations beyond the limits of the world,
     * the letter ‘X’ (uppercase) is used.
     *
     * @param loc location of which the neighbourhood we want to obtain.
     * @return a string representing the locations adjacent to the given location.
     * @throws BadLocationException if the location does not belong to this world.
     */
    public String getNeighbourhoodString(Location loc) throws BadLocationException {
        StringBuilder neighbourhoodString = new StringBuilder();
        Location temploc;
        if (this.equals(loc.getWorld())) {
            for (int i = -1; i <= 1; i++) {
                for (int j = 1; j >= -1; j--) {
                    for (int k = -1; k <= 1; k++) {
                        temploc = new Location(this, loc.getX() + k, loc.getY() + j, loc.getZ() + i);
                            if (getBlockAt(temploc) != null)
                                neighbourhoodString.append(getBlockAt(temploc).getType().getSymbol());
                            if (getItemsAt(temploc) != null)
                                neighbourhoodString.append(getItemsAt(temploc).getType().getSymbol());
                            else if (temploc.equals(getPlayer().getLocation())) neighbourhoodString.append("P");
                            else if (!Location.check(temploc)) neighbourhoodString.append("X");
                            else if (temploc.isFree()) neighbourhoodString.append(".");


                    }
                    neighbourhoodString.append(" ");
                }
                neighbourhoodString.append("\n");

            }
        } else throw new BadLocationException("Location does not belong to this world.");
        return neighbourhoodString.toString();
    }

    /**
     * It checks if the given location is free, i.e. if it is not occupied by a block or by the player.
     *
     * @param loc location we want to check.
     * @return true if location is free, false otherwise.
     * @throws BadLocationException if the location does not belong to this world.
     */
    public boolean isFree(Location loc) throws BadLocationException {
        if (this.equals(loc.getWorld())) {
            return getBlockAt(loc) == null && !loc.equals(getPlayer().getLocation());
        } else throw new BadLocationException("Location does not belong to this world.");
    }

    /**
     * It removes items from the given location.
     *
     * @param loc location from which we want to remove the item.
     * @throws BadLocationException if the location does not beong to this world.
     */
    public void removeItemsAt(Location loc) throws BadLocationException {
        if (this.equals(loc.getWorld()) && items.containsKey(loc)) {
            items.remove(loc);
        } else throw new BadLocationException("Location does not belong to this world.");
    }

    /**
     * It returns a string with the name of the world.
     *
     * @return string that contains the name of the world.
     */
    public String toString() {
        return name;
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
        World other = (World) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (seed != other.seed)
            return false;
        if (worldSize != other.worldSize)
            return false;
        return true;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (seed ^ (seed >>> 32));
        result = prime * result + worldSize;
        return result;
    }
    /**
     * Where fillOblateSpheroid() is a method which takes a central point, a radius and a material to fill to use on the block array.
     *
     * @param centerPos central point
     * @param radius    radius around central point
     * @param material  material to fill with
     * @throws WrongMaterialException if 'material' is not a block material
     */
    private void fillOblateSpheroid(Location centerPos, double radius, Material material) throws WrongMaterialException {

        for (double x = centerPos.getX() - radius; x < centerPos.getX() + radius; x += 1.0) {
            for (double y = centerPos.getY() - radius; y < centerPos.getY() + radius; y += 1.0) {
                for (double z = centerPos.getZ() - radius; z < centerPos.getZ() + radius; z += 1.0) {
                    double dx = x - centerPos.getX();
                    double dy = y - centerPos.getY();
                    double dz = z - centerPos.getZ();

                    if ((dx * dx + 2 * dy * dy + dz * dz) < radius * radius) {
                        // point (x,y,z) falls within level bounds ?
                        // we don't need to check it, just remove or replace that location from the blocks map.
                        Location loc = new Location(this, x + heightMap.negativeWorldLimit, y, z + heightMap.negativeWorldLimit);
                        if (material == null)
                            blocks.remove(loc);
                        else
                            try { //if ((Math.abs(x) < worldSize/2.0-1.0) && (Math.abs(z) < worldSize/2.0-1.0) && y>0.0 && y<=Location.UPPER_Y_VALUE)
                                Block veinBlock = new Block(material);
                                // los bloques de veta siempre contienen material
                                veinBlock.setDrops(material, 1);
                                blocks.replace(loc, veinBlock);
                            } catch (StackSizeException ex) {
                                // will never happen
                                ex.printStackTrace();
                            }
                    }
                }
            }
        }
    }

}