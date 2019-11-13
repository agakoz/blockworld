/**
 * @author agata.koziol
 */
package model;

import java.util.*;

/**
 * A Java enumerated type that lists the different materials that can be part of the BlockWorld worlds.
 * There are four categories of materials: blocks, food, weapons and tools.
 */
public enum Material {
    /**
     * Material BEDROCK specified.
     */
    BEDROCK(-1, '*'),
    /**
     * Material CHEST specified.
     */
    CHEST(0.1, 'C'),
    /**
     * Material SAND specified.
     */
    SAND(0.5, 'n'),
    /**
     * Material DIRT specified.
     */
    DIRT(0.5, 'd'),
    /**
     * Material GRASS specified.
     */
    GRASS(0.6, 'g'),
    /**
     * Material STONE specified.
     */
    STONE(1.5, 's'),
    /**
     * Material GRANITE specified.
     */
    GRANITE(1.5, 'r'),
    /**
     * Material OBSIDIAN specified.
     */
    OBSIDIAN(5, 'o'),
    /**
     * Material WATER_BUCKET specified.
     */
    WATER_BUCKET(1, 'W'),
    /**
     * Material APPLE specified.
     */
    APPLE(4, 'A'),
    /**
     * Material BREAD specified.
     */
    BREAD(5, 'B'),
    /**
     * Material BEEF specified.
     */
    BEEF(8, 'F'),
    /**
     * Material IRON_SHOVEL specified.
     */
    IRON_SHOVEL(0.2, '>'),
    /**
     * Material IRON_PICKAXE specified.
     */
    IRON_PICKAXE(0.5, '^'),
    /**
     * Material WOOD_SWORD specified.
     */
    WOOD_SWORD(1, 'i'),
    /**
     * Material IRON_SWORD specified.
     */
    IRON_SWORD(2, 'I'),
    /**
     * Material LAVA specified.
     */
    LAVA(1.0, '#'),
    /**
     * Material WATER specified.
     */
    WATER(0.0, '@');


    /**
     * indicates the value of each material.
     */
    private double value;
    /**
     * indicates the symbol of each material.
     */
    private char symbol;
    /**
     * Instance of random, used to get random item.
     */
    public static Random rng = new Random(1L);

    /**
     * Simple constructor, that assigns a value and symbol to each material.
     *
     * @param value  indicates the value to be assigned as value .
     * @param symbol indicates the character to be assigned as a symbol of the material created.
     */
    Material(double value, char symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    /**
     * It indicates whether the material is a block or not by comparing assigned unique symbols.
     *
     * @return true if the material is a block, false if not.
     */
    public boolean isBlock() {
        return (symbol == '#' || symbol == '@' || symbol == '*' || symbol == 'C' || symbol == 'n' || symbol == 'd'
                || symbol == 'g' || symbol == 's' || symbol == 'r' || symbol == 'o');

    }

    public boolean isLiquid() {
        return (symbol == '#' || symbol == '@');
    }


    /**
     * It indicates whether the material is food or not by comparing assigned unique symbols.
     *
     * @return true if the material is food, false if not.
     */
    public boolean isEdible() {
        return (symbol == 'W' || symbol == 'A' || symbol == 'B' || symbol == 'F');

    }

    /**
     * It indicates whether the material is a tool or not by comparing assigned unique symbols..
     *
     * @return true if the material is a tool, false if not.
     */
    public boolean isTool() {
        return (symbol == '^' || symbol == '>');

    }

    /**
     * It indicates whether the material is a weapon or not by comparing assigned unique symbols..
     *
     * @return true if the material is a weapon, false if not.
     */
    public boolean isWeapon() {
        return (symbol == 'i' || symbol == 'I');
    }

    /**
     * Simple getter.
     *
     * @return value of the material.
     */
    public double getValue() {
        return value;
    }

    /**
     * Simple getter.
     *
     * @return value of symbol of the material.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * It returns a random material between the ‘first’ and ‘last’ positions of the enumerated type, both included.
     *
     * @param first first boundry position of the enumerated type to look beetween
     * @param last  last boundry position of the enumerated type to look beetween
     */
    public static Material getRandomItem(int first, int last) {
        int i = rng.nextInt(last - first + 1) + first;
        return values()[i];
    }
}
