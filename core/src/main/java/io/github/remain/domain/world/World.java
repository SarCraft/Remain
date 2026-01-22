package io.github.remain.domain.world;

import io.github.remain.domain.common.Position3D;
import io.github.remain.domain.terrain.TerrainGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the entire game world as an aggregate root.
 * The World is the main domain entity that contains all blocks and manages
 * world generation, querying, and modification. It follows the aggregate root
 * pattern from Domain-Driven Design.
 * Design Rationale:
 *    - Encapsulates all world state (blocks)
 *    - Provides controlled access to blocks (no direct array access)
 *    - Manages world generation lifecycle
 *    - Optimizes rendering with visible block queries
 *    - Coordinates with TerrainGenerator for procedural generation
 * 
 * Performance: For large worlds, consider chunk-based loading and
 * spatial indexing for faster queries.
 * Thread Safety: Not thread-safe. Synchronize externally if accessed
 * from multiple threads.
 * @author SarCraft
 * @since 1.0
 */
public final class World {
    
    private final int width;
    private final int depth;
    private final int height;
    private final Block[][][] blocks;
    private final TerrainGenerator terrainGenerator;
    private final long seed;
    
    /**
     * Creates a new World with the specified dimensions and a random seed.
     * @param width World width (X dimension)
     * @param depth World depth (Z dimension)
     * @param height World height (Y dimension)
     */
    public World(int width, int depth, int height) {
        this(width, depth, height, System.currentTimeMillis());
    }
    
    /**
     * Creates a new World with the specified dimensions and seed.
     * Using the same seed will generate identical worlds, useful for
     * multiplayer, testing, and procedural generation.
     * @param width World width (X dimension, grid columns)
     * @param depth World depth (Z dimension, grid rows)
     * @param height World height (Y dimension, elevation)
     * @param seed Random seed for procedural generation
     * @throws IllegalArgumentException if any dimension is <= 0
     */
    public World(int width, int depth, int height, long seed) {
        if (width <= 0 || depth <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive");
        }
        
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.seed = seed;
        this.blocks = new Block[width][depth][height];
        this.terrainGenerator = new TerrainGenerator(seed);
        
        generateWorld();
    }
    
    /**
     * Generates the world terrain procedurally.
     * This method fills the world with blocks based on noise functions
     * and terrain rules from TerrainGenerator.
     */
    private void generateWorld() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                generateColumn(x, z);
            }
        }
    }
    
    /**
     * Generates a single vertical column of blocks.
     * @param x Grid column
     * @param z Grid row
     */
    private void generateColumn(int x, int z) {
        // Get terrain height at this position
        int terrainHeight = terrainGenerator.getTerrainHeight(x, z);
        terrainHeight = Math.max(0, Math.min(terrainHeight, height - 2));
        
        // Generate blocks from bottom to terrain height
        for (int y = 0; y <= terrainHeight; y++) {
            BlockType type = getBlockTypeForDepth(x, z, y, terrainHeight);
            
            if (type != null) {
                blocks[x][z][y] = new Block(x, y, z, type);
            }
        }
    }
    
    /**
     * Determines the block type based on depth below surface.
     * @param x Grid column
     * @param z Grid row
     * @param y Current elevation
     * @param surfaceHeight Surface elevation at this column
     * @return Block type, or null for air
     */
    private BlockType getBlockTypeForDepth(int x, int z, int y, int surfaceHeight) {
        if (y == surfaceHeight) {
            // Surface block
            return terrainGenerator.getSurfaceBlockType(x, z, surfaceHeight);
        } else {
            // Subsurface block
            return terrainGenerator.getSubsurfaceBlockType(y, surfaceHeight);
        }
    }
    
    /**
     * Gets the block at the specified position.
     * @param x Grid column
     * @param z Grid row
     * @param y Elevation
     * @return The block, or null if out of bounds or air
     */
    public Block getBlock(int x, int z, int y) {
        if (!isInBounds(x, z, y)) {
            return null;
        }
        return blocks[x][z][y];
    }
    
    /**
     * Gets the block at the specified position.
     * @param position The 3D position
     * @return The block, or null if out of bounds or air
     */
    public Block getBlock(Position3D position) {
        Objects.requireNonNull(position, "position cannot be null");
        return getBlock(position.x(), position.z(), position.y());
    }
    
    /**
     * Sets a block at the specified position.
     * Use this for placing/removing blocks during gameplay.
     * @param x Grid column
     * @param z Grid row
     * @param y Elevation
     * @param block The block to set, or null to remove
     * @return true if successful, false if out of bounds
     */
    public boolean setBlock(int x, int z, int y, Block block) {
        if (!isInBounds(x, z, y)) {
            return false;
        }
        
        blocks[x][z][y] = block;
        return true;
    }
    
    /**
     * Sets a block at the specified position.
     * @param position The 3D position
     * @param block The block to set, or null to remove
     * @return true if successful, false if out of bounds
     */
    public boolean setBlock(Position3D position, Block block) {
        Objects.requireNonNull(position, "position cannot be null");
        return setBlock(position.x(), position.z(), position.y(), block);
    }
    
    /**
     * Checks if coordinates are within world bounds.
     * @param x Grid column
     * @param z Grid row
     * @param y Elevation
     * @return true if in bounds, false otherwise
     */
    public boolean isInBounds(int x, int z, int y) {
        return x >= 0 && x < width &&
               z >= 0 && z < depth &&
               y >= 0 && y < height;
    }
    
    /**
     * Checks if a position is within world bounds.
     * @param position The position to check
     * @return true if in bounds, false otherwise
     */
    public boolean isInBounds(Position3D position) {
        Objects.requireNonNull(position, "position cannot be null");
        return isInBounds(position.x(), position.z(), position.y());
    }
    
    /**
     * Gets all non-null blocks in the world.
     * Performance Warning: This creates a new list and iterates all
     * positions. Use {@link #getVisibleBlocks()} for rendering.
     * @return List of all blocks
     */
    public List<Block> getAllBlocks() {
        List<Block> allBlocks = new ArrayList<>();
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < height; y++) {
                    Block block = blocks[x][z][y];
                    if (block != null) {
                        allBlocks.add(block);
                    }
                }
            }
        }
        
        return allBlocks;
    }
    
    /**
     * Gets only the visible (top) blocks for efficient rendering.
     * Returns only the highest non-null block in each column, which is
     * sufficient for isometric rendering where lower blocks are occluded.
     * Performance: Much faster than {@link #getAllBlocks()} for
     * rendering purposes.
     * @return List of visible blocks
     */
    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>(width * depth);
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Find the topmost block in this column
                for (int y = height - 1; y >= 0; y--) {
                    Block block = blocks[x][z][y];
                    if (block != null) {
                        visibleBlocks.add(block);
                        break; // Only add the top block
                    }
                }
            }
        }
        
        return visibleBlocks;
    }
    
    /**
     * Gets the world width (X dimension, grid columns).
     * @return World width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the world depth (Z dimension, grid rows).
     * @return World depth
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Gets the world height (Y dimension, elevation).
     * @return World height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the random seed used for world generation.
     * @return World generation seed
     */
    public long getSeed() {
        return seed;
    }
    
    /**
     * Gets the terrain generator used by this world.
     * @return Terrain generator
     */
    public TerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
    }
}
