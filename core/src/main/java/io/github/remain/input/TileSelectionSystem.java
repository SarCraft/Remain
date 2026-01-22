package io.github.remain.input;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import io.github.remain.world.Block;
import io.github.remain.world.World;
import io.github.remain.rendering.IsometricProjection;

/**
 * Handles tile selection via mouse/touch input.
 * This system converts screen coordinates to world coordinates and
 * determines which tile (if any) is selected.
 * 
 * Design Rationale:
 *    - Centralized tile picking logic
 *    - Uses IsometricProjection for coordinate conversion
 *    - Prioritizes higher tiles (Y coordinate) for better UX
 *    - Maintains selected tile state
 * 
 * @author SarCraft
 * @since 1.0
 */
public class TileSelectionSystem {
    
    private final World world;
    private Block selectedBlock;
    private boolean enabled = true;
    
    // Temporary vector for coordinate conversion (reused to avoid allocations)
    private final Vector3 tempVector = new Vector3();
    
    /**
     * Creates a new TileSelectionSystem.
     * @param world The game world
     */
    public TileSelectionSystem(World world) {
        this.world = world;
    }
    
    public void update(float delta) {
        // Selection is event-driven (via handleClick), no per-frame update needed
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Handles a click at the specified screen coordinates.
     * Converts screen coordinates to world coordinates and selects the appropriate tile.
     * 
     * @param screenX Screen X coordinate (pixels)
     * @param screenY Screen Y coordinate (pixels)
     * @param camera The world camera for unprojecting coordinates
     * @return true if a tile was selected, false otherwise
     */
    public boolean handleClick(int screenX, int screenY, Camera camera) {
        if (!enabled) {
            return false;
        }
        
        // Convert screen coordinates to world coordinates
        tempVector.set(screenX, screenY, 0);
        camera.unproject(tempVector);
        
        // Find the block at this position
        Block clicked = getBlockAtScreenPosition(tempVector.x, tempVector.y);
        
        if (clicked != null) {
            selectedBlock = clicked;
            return true;
        } else {
            selectedBlock = null;
            return false;
        }
    }
    
    /**
     * Finds the block at the specified screen position.
     * This method checks all visible blocks and finds the closest one,
     * prioritizing higher blocks (greater Y coordinate).
     * 
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return The block at this position, or null if none found
     */
    private Block getBlockAtScreenPosition(float worldX, float worldY) {
        var blocks = world.getVisibleBlocks();
        Block closestBlock = null;
        float minDistance = Float.MAX_VALUE;
        
        // Search radius in pixels (how close the click must be to tile center)
        final float SELECTION_RADIUS = 20.0f;
        
        for (Block block : blocks) {
            // Get the tile's draw position
            float[] drawPos = IsometricProjection.getTileDrawPosition(
                block.getX(), block.getY(), block.getZ()
            );
            
            // Calculate tile center
            float tileCenterX = drawPos[0] + IsometricProjection.getTileWidth() / 2f;
            float tileCenterY = drawPos[1] + IsometricProjection.getTileHeight() / 2f;
            
            // Calculate distance from click to tile center
            float dx = worldX - tileCenterX;
            float dy = worldY - tileCenterY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Check if within selection radius
            if (distance < SELECTION_RADIUS) {
                // Prioritize higher tiles (greater Y coordinate)
                if (closestBlock == null) {
                    closestBlock = block;
                    minDistance = distance;
                } else {
                    // If this block is higher, select it
                    if (block.getY() > closestBlock.getY()) {
                        closestBlock = block;
                        minDistance = distance;
                    } 
                    // If same height, select closer one
                    else if (block.getY() == closestBlock.getY() && distance < minDistance) {
                        closestBlock = block;
                        minDistance = distance;
                    }
                }
            }
        }
        
        return closestBlock;
    }
    
    /**
     * Gets the currently selected block.
     * @return The selected block, or null if none selected
     */
    public Block getSelectedBlock() {
        return selectedBlock;
    }
    
    /**
     * Checks if a block is currently selected.
     * @return true if a block is selected, false otherwise
     */
    public boolean hasSelection() {
        return selectedBlock != null;
    }
    
    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        selectedBlock = null;
    }
    
    /**
     * Gets the X coordinate of the selected tile.
     * @return X coordinate, or -1 if no selection
     */
    public int getSelectedX() {
        return selectedBlock != null ? selectedBlock.getX() : -1;
    }
    
    /**
     * Gets the Y coordinate of the selected tile.
     * @return Y coordinate, or -1 if no selection
     */
    public int getSelectedY() {
        return selectedBlock != null ? selectedBlock.getY() : -1;
    }
    
    /**
     * Gets the Z coordinate of the selected tile.
     * @return Z coordinate, or -1 if no selection
     */
    public int getSelectedZ() {
        return selectedBlock != null ? selectedBlock.getZ() : -1;
    }
}
