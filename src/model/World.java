/**
 * @author agata.koziol
 */
package model;

import org.bukkit.util.noise.CombinedNoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import java.util.*;
import java.util.Set;

import model.entities.*;
import model.exceptions.*;

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
     * Creatures placed in different world locations.
     */
    private Map<Location, Creature> creatures;


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
        /**
         * map of the height in the world.
         */
        double[][] heightMap;
        /**
         * value of the positive limit of the world
         */
        int positiveWorldLimit;
        /**
         * value of the negative limit of the world
         */
        int negativeWorldLimit;

        /**
         * constructor of the heighmap
         * @param worldsize size of the world
         */
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
         * @return the y axis coordinate
         */
        double get(double x, double z) {
            return heightMap[(int) x - negativeWorldLimit][(int) z - negativeWorldLimit];
        }

        /**
         * simple setter
         * @param x xaxis parameter
         * @param z z axis parameter
         * @param y y axis parameter
         */
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
     * inicializes blocks, items and creatures maps.
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
            creatures = new HashMap<>();
            generate(seed, size);
        } else throw new IllegalArgumentException();

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
        if (!this.equals(loc.getWorld())) throw new BadLocationException("Location does not belong to this world.");
        if (loc.getWorld() == null) throw new BadLocationException("Location does not have an associated world.");
        Block copyBlock = blocks.getOrDefault(loc, null);
        return copyBlock;
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
     * For living entities their symbol is used. For locations beyond the limits of the world,
     * the letter ‘X’ (uppercase) is used.
     *
     * @param loc location of which the neighbourhood we want to obtain.
     * @return a string representing the locations adjacent to the given location.
     * @throws BadLocationException if the location does not belong to this world.
     */
    public String getNeighbourhoodString(Location loc) throws BadLocationException {

        if (!this.equals(loc.getWorld())) throw new BadLocationException("Location does not belong to this world.");
        StringBuilder neighbourhoodString = new StringBuilder();
        Location temploc;
        for (int i = -1; i <= 1; i++) {
            for (int j = 1; j >= -1; j--) {
                for (int k = -1; k <= 1; k++) {
                    temploc = new Location(this, loc.getX() + k, loc.getY() + j, loc.getZ() + i);
                    if (getItemsAt(temploc) != null)
                        if (getItemsAt(temploc).getType().isBlock())
                            neighbourhoodString.append(Character.toUpperCase(getItemsAt(temploc).getType().getSymbol()));
                        else neighbourhoodString.append(getItemsAt(temploc).getType().getSymbol());

                    else if (getCreatureAt(temploc) != null)
                        neighbourhoodString.append(getCreatureAt(temploc).getSymbol());
                    else if (temploc.equals(getPlayer().getLocation()))
                        neighbourhoodString.append(player.getSymbol());
                    else if (getBlockAt(temploc) != null)
                        neighbourhoodString.append(getBlockAt(temploc).getType().getSymbol());
                    else if (!Location.check(temploc))
                        neighbourhoodString.append("X");
                    else if (temploc.isFree())
                        neighbourhoodString.append(".");
                }
                if(j>=0)
                neighbourhoodString.append(" ");
            }
            if(i<1)
            neighbourhoodString.append("\n");
        }
        return neighbourhoodString.toString();
    }

    /**
     * It checks if the given location is free, i.e. if it is not occupied by a solid block or by any living creature.
     *
     * @param loc location we want to check.
     * @return true if location is free, false otherwise.
     * @throws BadLocationException if the location does not belong to this world.
     */
    public boolean isFree(Location loc) throws BadLocationException {
        if (!this.equals(loc.getWorld())) throw new BadLocationException("Location does not belong to this world.");
        return ((getBlockAt(loc) == null || getBlockAt(loc).getType().isLiquid()) &&
                !loc.equals(getPlayer().getLocation()) &&
                getCreatureAt(loc) == null);


    }

    /**
     * It removes items from the given location.
     *
     * @param loc location from which we want to remove the item.
     * @throws BadLocationException if the location does not beong to this world.
     */
    public void removeItemsAt(Location loc) throws BadLocationException {
        if (this.equals(loc.getWorld()) ) {
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
     * Genera un mundo nuevo del tamaĂąo size*size en el plano (x,z). Si existĂ­an elementos anteriores en el mundo,
     * serĂĄn eliminados. Usando la misma semilla y el mismo tamaĂąo podemos generar mundos iguales
     *
     * @param seed semilla para el algoritmo de generaciĂłn.
     * @param size tamaĂąo del mundo para las dimensiones x y z
     */
    private void generate(long seed, int size) {

        Random rng = new Random(getSeed());

        blocks.clear();
        creatures.clear();
        items.clear();

        // Paso 1: generar nuevo mapa de alturas del terreno
        heightMap = new HeightMap(size);
        CombinedNoiseGenerator noise1 = new CombinedNoiseGenerator(this);
        CombinedNoiseGenerator noise2 = new CombinedNoiseGenerator(this);
        OctaveGenerator noise3 = new PerlinOctaveGenerator(this, 6);

        System.out.println("Generando superficie del mundo...");
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
        SolidBlock block = null;
        Location location = null;
        Material material = null;
        OctaveGenerator noise = new PerlinOctaveGenerator(this, 8);
        System.out.println("Generando terreno...");
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
                        block = new SolidBlock(material);
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
        double theta = 0.0;
        double deltaTheta = 0.0;
        double phi = 0.0;
        double deltaPhi = 0.0;

        System.out.print("Generando cuevas");
        for (int cueva = 0; cueva < numCuevas; cueva++) {
            System.out.print(".");
            System.out.flush();
            Location cavePos = new Location(this, rng.nextInt(size), rng.nextInt((int) Location.UPPER_Y_VALUE), rng.nextInt(size));
            double caveLength = rng.nextDouble() * rng.nextDouble() * 200;
            //cave direction is given by two angles and corresponding rate of change in those angles,
            //spherical coordinates perhaps?
            theta = rng.nextDouble() * Math.PI * 2;
            deltaTheta = 0.0;
            phi = rng.nextDouble() * Math.PI * 2;
            deltaPhi = 0.0;
            double caveRadius = rng.nextDouble() * rng.nextDouble();

            for (int i = 1; i <= (int) caveLength; i++) {
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
        }
        System.out.println();

        // Paso 4: crear vetas de minerales
        // Abundancia de cada mineral
        double abundance[] = new double[2];
        abundance[0] = 0.5; // GRANITE
        abundance[1] = 0.3; // OBSIDIAN
        int numVeins[] = new int[2];
        numVeins[0] = (int) (size * size * 256 * abundance[0]) / 16384; // GRANITE
        numVeins[1] = (int) (size * size * 256 * abundance[1]) / 16384; // OBSIDIAN

        Material vein = Material.GRANITE;
        for (int numVein = 0; numVein < 2; numVein++, vein = Material.OBSIDIAN) {
            System.out.print("Generando vetas de " + vein);
            for (int v = 0; v < numVeins[numVein]; v++) {
                System.out.print(vein.getSymbol());
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
            System.out.println();
        }

        System.out.println();

        // flood-fill water
        char water = Material.WATER.getSymbol();

        int numWaterSources = size * size / 800;

        System.out.print("Creando fuentes de agua subterrĂĄneas");
        int x = 0;
        int z = 0;
        int y = 0;
        for (int w = 0; w < numWaterSources; w++) {
            System.out.print(water);
            x = rng.nextInt(size) + heightMap.negativeWorldLimit;
            z = rng.nextInt(size) + heightMap.negativeWorldLimit;
            y = (int) Location.SEA_LEVEL - 1 - rng.nextInt(2);
            try {
                floodFill(Material.WATER, new Location(this, x, y, z));
            } catch (WrongMaterialException | BadLocationException e) {
                // no debe suceder
                throw new RuntimeException(e);
            }
        }
        System.out.println();

        System.out.print("Creando erupciones de lava");
        char lava = Material.LAVA.getSymbol();
        // flood-fill lava
        int numLavaSources = size * size / 2000;
        for (int w = 0; w < numLavaSources; w++) {
            System.out.print(lava);
            x = rng.nextInt(size) + heightMap.negativeWorldLimit;
            z = rng.nextInt(size) + heightMap.negativeWorldLimit;
            y = (int) ((Location.SEA_LEVEL - 3) * rng.nextDouble() * rng.nextDouble());
            try {
                floodFill(Material.LAVA, new Location(this, x, y, z));
            } catch (WrongMaterialException | BadLocationException e) {
                // no debe suceder
                throw new RuntimeException(e);
            }
        }
        System.out.println();

        // Paso 5. crear superficie, criaturas e items
        // Las entidades aparecen sĂłlo en superficie (no en cuevas, por ejemplo)

        OctaveGenerator onoise1 = new PerlinOctaveGenerator(this, 8);
        OctaveGenerator onoise2 = new PerlinOctaveGenerator(this, 8);
        boolean sandChance = false;
        double entitySpawnChance = 0.05;
        double itemsSpawnChance = 0.10;
        double foodChance = 0.8;
        double toolChance = 0.1;
        double weaponChance = 0.1;

        System.out.println("Generando superficie del terreno, entidades e items...");
        for (x = 0; x < size; x++) {
            for (z = 0; z < size; z++) {
                sandChance = onoise1.noise(x, z, 0.5, 2.0) > 8.0;
                y = (int) heightMap.heightMap[(int) x][(int) z];
                Location surface = new Location(this, x + heightMap.negativeWorldLimit, y, z + heightMap.negativeWorldLimit); // la posiciĂłn (x,y+1,z) no estĂĄ ocupada (es AIR)
                try {
                    if (sandChance) {
                        SolidBlock sand = new SolidBlock(Material.SAND);
                        if (rng.nextDouble() < 0.5)
                            sand.setDrops(Material.SAND, 1);
                        blocks.put(surface, sand);
                    } else {
                        SolidBlock grass = new SolidBlock(Material.GRASS);
                        if (rng.nextDouble() < 0.5)
                            grass.setDrops(Material.GRASS, 1);
                        blocks.put(surface, grass);
                    }
                } catch (WrongMaterialException | StackSizeException ex) {
                    // will never happen
                    ex.printStackTrace();
                }
                // intenta crear una entidad en superficie
                try {
                    Location aboveSurface = surface.above();

                    if (rng.nextDouble() < entitySpawnChance) {
                        Creature entity = null;
                        double entityHealth = rng.nextInt((int) LivingEntity.MAX_HEALTH) + 1;
                        if (rng.nextDouble() < 0.75) // generamos Monster (75%) o Animal (25%) de las veces
                            entity = new Monster(aboveSurface, entityHealth);
                        else
                            entity = new Animal(aboveSurface, entityHealth);
                        creatures.put(aboveSurface, entity);
                    } else {
                        // si no, intentamos crear unos items de varios tipos (comida, armas, herramientas)
                        // dentro de cofres
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
                    }
                } catch (BadLocationException | StackSizeException e) {
                    // BadLocationException : no hay posiciones mĂĄs arriba, ignoramos creaciĂłn de entidad/item sin hacer nada
                    // StackSizeException : no se producirĂĄ
                    throw new RuntimeException(e);
                }

            }
        }

        // TODO: Crear plantas

        // Generar jugador
        player = new Player("Steve", this);
        // El jugador se crea en la superficie (posiciĂłn (0,*,0)). AsegurĂŠmonos de que no hay nada mĂĄs ahĂ­
        Location playerLocation = player.getLocation();
        creatures.remove(playerLocation);
        items.remove(playerLocation);

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
                        Location loc = new Location(this, Math.floor(x + heightMap.negativeWorldLimit), Math.floor(y), Math.floor(z + heightMap.negativeWorldLimit));
                        if (material == null)
                            blocks.remove(loc);
                        else
                            try { //if ((Math.abs(x) < worldSize/2.0-1.0) && (Math.abs(z) < worldSize/2.0-1.0) && y>0.0 && y<=Location.UPPER_Y_VALUE)
                                SolidBlock veinBlock = new SolidBlock(material);
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

    /**
     * fills the given location with the given liquidblock
     * @param liquid liquid we want to put
     * @param from location to be filled
     * @throws WrongMaterialException  when the block is not of the liquid type.
     * @throws BadLocationException when the location is wrong
     */
    private void floodFill(Material liquid, Location from) throws WrongMaterialException, BadLocationException {
        if (!liquid.isLiquid())
            throw new WrongMaterialException(liquid);
        if (!blocks.containsKey(from)) {
            blocks.put(from, BlockFactory.createBlock(liquid));
            items.remove(from);
            Set<Location> floodArea = getFloodNeighborhood(from);
            for (Location loc : floodArea)
                floodFill(liquid, loc);
        }
    }

    /**
     * Obtiene las posiciones adyacentes a esta que no estĂĄn por encima y estĂĄn libres
     *
     * @param location location considered.
     * @return si esta posiciĂłn pertenece a un mundo, devuelve sĂłlo aquellas posiciones adyacentes vĂĄlidas para ese mundo,  si no, devuelve todas las posiciones adyacentes
     * @throws BadLocationException cuando la posiciĂłn es de otro mundo
     */
    private Set<Location> getFloodNeighborhood(Location location) throws BadLocationException {
        if (location.getWorld() != null && location.getWorld() != this)
            throw new BadLocationException("Esta posiciĂłn no es de este mundo");
        Set<Location> neighborhood = location.getNeighborhood();
        Iterator<Location> iter = neighborhood.iterator();
        while (iter.hasNext()) {
            Location loc = iter.next();
            try {
                if ((loc.getY() > location.getY()) || getBlockAt(loc) != null)
                    iter.remove();
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
                // no sucederĂĄ
            }
        }
        return neighborhood;
    }

    /**
     * It adds a block to this world in the given location
     *
     * @param loc   location where the block will be added
     * @param block bloack that will be added
     * @throws BadLocationException if the location does not belong to this world, it is outside its limits,
     *                              or is occupied by the player.
     */
    public void addBlock(Location loc, Block block) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("the location does not belong to this world ");
        if (!Location.check(loc)){
            throw new BadLocationException("Location "+loc+" is not in the world bounds");
        }
        if (player.getLocation().equals(loc) && !block.getType().isLiquid())
            throw new BadLocationException("Cannot place a block at the players location");
        if (blocks.containsKey(loc)) {
            blocks.remove(loc);
        } else {
            if(heightMap.get(loc.getX(),loc.getZ())<loc.getY()){
                heightMap.set(loc.getX(),loc.getZ(),loc.getY());
            }
        }
        items.remove(loc);
        creatures.remove(loc);
        blocks.put(loc,block);
    }

    /**
     * It adds a creature to this world.
     *
     * @param creature creature to be added.
     * @throws BadLocationException if the location does not belong to this world, it is outside its limits, or is occupied by the player.
     */
    public void addCreature(Creature creature) throws BadLocationException {
        if (this != creature.getLocation().getWorld())
            throw new BadLocationException("creature’s location does not belong to this world");
        if (!Location.check(creature.getLocation()))
            throw new BadLocationException("the location is out of world limits. ");
        if (!creature.getLocation().isFree()) throw new BadLocationException("the location is  occupied.");
        if (getItemsAt(creature.getLocation()) != null) items.remove((creature.getLocation()));
        creatures.put(creature.getLocation(), creature);
    }

    /**
     * It adds a stack of items to this world, in the given location
     *
     * @param loc  location on with the item will be placed
     * @param item item to be added to the world
     * @throws BadLocationException if the location does not belong to this world, it is outside its limits, or is occupied by the player.
     */
    public void addItems(Location loc, ItemStack item) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("location does not belong to this world");
        if (!Location.check(loc)) throw new BadLocationException("the location is out of world limits. ");
        if (!loc.isFree()) throw new BadLocationException("the location is  occupied.");
        if (getItemsAt(loc) == null) items.remove(loc);
        items.put(loc, item);
    }

    /**
     * It destroys the block in the given location, removing it from the world. It places the items that the block
     * could contain in the same location.
     * These items do not ‘fall’ if there is nothing below them.
     *
     * @param loc location
     * @throws BadLocationException if the location does not belong to this world, there is no block in that location,
     *                              or the block is at zero height.
     */
    public void destroyBlockAt(Location loc) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("location does not belong to this world");
        else if (getBlockAt(loc) == null) throw new BadLocationException("No block in this location");
        else if (loc.getY() == 0) throw new BadLocationException("Block is at zero height");
        if (blocks.containsKey(loc)) {
            if (blocks.get(loc).getClass()==SolidBlock.class) {
                if (heightMap.get(loc.getX(), loc.getZ()) == loc.getY()) {
                    Location tempLoc = new Location(loc.below());
                    while (!blocks.containsKey(tempLoc) && tempLoc.getY() >= 0) {
                        tempLoc = tempLoc.below();
                    }
                    heightMap.set(tempLoc.getX(), tempLoc.getZ(), Math.max(tempLoc.getY(), 0));
                }
                SolidBlock block = (SolidBlock) blocks.get(loc);
                if (block.getDrops() != null) {
                    items.put(loc, block.getDrops());
                }
            }
            blocks.remove(loc);
        } else {
            throw new BadLocationException("No block to remove at "+loc);
        }

    }

    /**
     * It returns the creature in the given location, or ‘null’ if there is none in that location
     * or the location does not exists in this world.
     *
     * @param loc location from which we want to get the creature
     * @return creature from the location or null
     * @throws BadLocationException if the location does not belong to this world.
     */
    public Creature getCreatureAt(Location loc) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("location does not belong to this world");
        return creatures.getOrDefault(loc, null);
    }

    /**
     * It returns all living creatures that are occupying locations adjacent to the given one.
     *
     * @param loc location from which neighbourhood we want to get creatures.
     * @return collection of creatures
     * @throws BadLocationException if the location does not belong to this world.
     */
    public Collection<Creature> getNearbyCreatures(Location loc) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("location does not belong to this world");

        Collection<Creature> nearbyCreatures = new ArrayList<>();

        for (Location l : loc.getNeighborhood()) {
            if (getCreatureAt(l) != null) nearbyCreatures.add(getCreatureAt(l));

        }
        return nearbyCreatures;
    }

    /**
     * It remove from the world the creature in the given location.
     *
     * @param loc location from which the creature will be removed
     * @throws BadLocationException if the location does not belong to this world
     *                              or there is no creature in that location.
     */
    public void killCreature(Location loc) throws BadLocationException {
        if (this != loc.getWorld()) throw new BadLocationException("location does not belong to this world");
        if (getCreatureAt(loc) == null) throw new BadLocationException("no creature in that location.");
        creatures.remove(loc);
    }

}