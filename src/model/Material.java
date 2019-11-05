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
     * constants, indicating type of material and assigned to them values and symbols.
     */
    BEDROCK(-1, '*'),
    CHEST(0.1, 'C'),
    SAND(0.5, 'a'),
    DIRT(0.5, 'd'),
    GRASS(0.6, 'g'),
    STONE(1.5, 's'),
    GRANITE(1.5, 'r'),
    OBSIDIAN(5, 'o'),
    WATER_BUCKET(1, 'W'),
    APPLE(4, 'A'),
    BREAD(5, 'B'),
    BEEF(8, 'F'),
    IRON_SHOVEL(0.2, '>'),
    IRON_PICKAXE(0.5, '^'),
    WOOD_SWORD(1, 'i'),
    IRON_SWORD(2, 'I');

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
        if (symbol == '*' || symbol == 'C' || symbol == 'a' || symbol == 'd' || symbol == 'g' || symbol == 's' || symbol == 'r' || symbol == 'o')
            return true;
        else
            return false;
    }

    /**
     * It indicates whether the material is food or not by comparing assigned unique symbols.
     *
     * @return true if the material is food, false if not.
     */
    public boolean isEdible() {
        if (symbol == 'W' || symbol == 'A' || symbol == 'B' || symbol == 'F')
            return true;
        else
            return false;
    }

    /**
     * It indicates whether the material is a tool or not by comparing assigned unique symbols..
     *
     * @return true if the material is a tool, false if not.
     */
    public boolean isTool() {
        if (symbol == '^' || symbol == '>')
            return true;
        else
            return false;
    }

    /**
     * It indicates whether the material is a weapon or not by comparing assigned unique symbols..
     *
     * @return true if the material is a weapon, false if not.
     */
    public boolean isWeapon() {
        if (symbol == 'i' || symbol == 'I')
            return true;
        else
            return false;
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
