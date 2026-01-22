package io.github.remain.domain.world;

import io.github.remain.domain.common.Position3D;

import java.util.Objects;

/**
 * Represents a single block in the game world.
 * A block is the fundamental building element of the world. Each block has a
 * position and a type that determines its appearance and behavior.
 * Design Rationale:
 *    - Immutable position (blocks don't move)
 *    - Mutable type (allows changing terrain, e.g., digging)
 *    - Position as value object (Position3D) for type safety
 *    - Lightweight design for memory efficiency (millions of blocks)
 * 
 * Thread Safety: This class is not thread-safe due to mutable type.
 * Synchronize externally if accessed from multiple threads.
 * @author SarCraft
 * @since 1.0
 */
public final class Block {
    
    private final Position3D position;
    private BlockType type;
    
    /**
     * Creates a new Block at the specified position with the given type.
     * @param x Grid column coordinate
     * @param y Elevation coordinate
     * @param z Grid row coordinate
     * @param type Block type
     * @throws NullPointerException if type is null
     */
    public Block(int x, int y, int z, BlockType type) {
        this(new Position3D(x, y, z), type);
    }
    
    /**
     * Creates a new Block at the specified position with the given type.
     * @param position Block position
     * @param type Block type
     * @throws NullPointerException if position or type is null
     */
    public Block(Position3D position, BlockType type) {
        this.position = Objects.requireNonNull(position, "position cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }
    
    /**
     * Gets the block's position.
     * @return Immutable position
     */
    public Position3D getPosition() {
        return position;
    }
    
    /**
     * Gets the block's X coordinate (grid column).
     * @return X coordinate
     */
    public int getX() {
        return position.x();
    }
    
    /**
     * Gets the block's Y coordinate (elevation/height).
     * @return Y coordinate
     */
    public int getY() {
        return position.y();
    }
    
    /**
     * Gets the block's Z coordinate (grid row).
     * @return Z coordinate
     */
    public int getZ() {
        return position.z();
    }
    
    /**
     * Gets the block type.
     * @return Block type
     */
    public BlockType getType() {
        return type;
    }
    
    /**
     * Sets the block type.
     * Use this for terrain modifications (e.g., digging, placing blocks).
     * @param type New block type
     * @throws NullPointerException if type is null
     */
    public void setType(BlockType type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }
    
    /**
     * Checks if this block is walkable (based on its type).
     * @return true if walkable, false otherwise
     */
    public boolean isWalkable() {
        return type.isWalkable();
    }
    
    /**
     * Checks if this block is solid (based on its type).
     * @return true if solid, false otherwise
     */
    public boolean isSolid() {
        return type.isSolid();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Block other)) return false;
        return position.equals(other.position) && type == other.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(position, type);
    }
    
    @Override
    public String toString() {
        return String.format("Block[position=%s, type=%s]", position, type);
    }
}
