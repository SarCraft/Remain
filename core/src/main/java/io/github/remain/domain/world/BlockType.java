package io.github.remain.domain.world;

/**
 * Enumeration of all block types in the game.
 * Each block type represents a different material with unique visual and
 * gameplay properties.
 * Design Rationale: Using an enum provides:
 *    - Type safety (can't pass invalid block types)
 *    - Exhaustiveness checking in switch statements
 *    - Centralized definition of all block types
 *    - Efficient comparison and EnumMap/EnumSet usage
 * 
 * @author SarCraft
 * @since 1.0
 */
public enum BlockType {
    
    /** Grass block - surface vegetation. */
    GRASS(0, "Grass", true),
    
    /** Dirt block - subsurface material. */
    DIRT(1, "Dirt", true),
    
    /** Stone block - hard material, deep underground. */
    STONE(2, "Stone", true),
    
    /** Water block - liquid, not walkable. */
    WATER(3, "Water", false);
    
    private final int tileIndex;
    private final String displayName;
    private final boolean walkable;
    
    /**
     * Creates a new BlockType.
     * @param tileIndex Index in the texture atlas
     * @param displayName Human-readable name
     * @param walkable Whether entities can walk on this block
     */
    BlockType(int tileIndex, String displayName, boolean walkable) {
        this.tileIndex = tileIndex;
        this.displayName = displayName;
        this.walkable = walkable;
    }
    
    /**
     * Gets the tile index in the texture atlas.
     * @return Tile index (used by renderer)
     */
    public int getTileIndex() {
        return tileIndex;
    }
    
    /**
     * Gets the human-readable display name.
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this block type is walkable.
     * @return true if walkable, false otherwise
     */
    public boolean isWalkable() {
        return walkable;
    }
    
    /**
     * Checks if this block type is solid (blocks movement).
     * @return true if solid, false otherwise
     */
    public boolean isSolid() {
        return this != WATER;
    }
}
