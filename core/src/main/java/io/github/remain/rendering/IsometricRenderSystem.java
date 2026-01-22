package io.github.remain.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.remain.world.Block;
import io.github.remain.world.World;
import io.github.remain.assets.AssetService;

import java.util.List;

/**
 * Handles isometric rendering of the game world.
 * Responsible for sorting blocks and rendering them in the correct order.
 * 
 * @author SarCraft
 * @since 1.0
 */
public class IsometricRenderSystem {
    
    private final AssetService assetService;
    private final World world;
    
    private boolean enabled = true;
    
    public IsometricRenderSystem(AssetService assetService, World world) {
        this.assetService = assetService;
        this.world = world;
    }
    
    public void update(float delta) {
        // No update logic needed for rendering system
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Renders the game world with isometric projection.
     */
    public void render(SpriteBatch batch) {
        if (!enabled) return;
        
        // Get visible blocks (already optimized by World)
        List<Block> visibleBlocks = world.getVisibleBlocks();
        
        // Sort for isometric rendering
        sortBlocksIsometric(visibleBlocks);
        
        // Render each block
        for (Block block : visibleBlocks) {
            TextureRegion texture = assetService.getBlockTexture(block.getType());
            if (texture != null) {
                float[] drawPos = IsometricProjection.getTileDrawPosition(
                    block.getX(), block.getY(), block.getZ()
                );
                
                batch.draw(
                    texture,
                    drawPos[0], drawPos[1],
                    IsometricProjection.getTileWidth(),
                    IsometricProjection.getTileHeight()
                );
            }
        }
    }
    
    /**
     * Sorts blocks for correct isometric rendering order.
     * @param blocks Blocks to sort (modified in-place)
     */
    private void sortBlocksIsometric(List<Block> blocks) {
        blocks.sort((b1, b2) -> {
            // Sort by Y first (bottom to top)
            int yCompare = Integer.compare(b1.getY(), b2.getY());
            if (yCompare != 0) return yCompare;
            
            // Then by rotated coordinates
            int[] rot1 = IsometricProjection.rotateCoordinates(b1.getX(), b1.getZ());
            int[] rot2 = IsometricProjection.rotateCoordinates(b2.getX(), b2.getZ());
            
            // Isometric sort: back to front
            int sum1 = rot1[0] + rot1[1];
            int sum2 = rot2[0] + rot2[1];
            if (sum1 != sum2) return Integer.compare(sum2, sum1);
            
            return Integer.compare(rot2[0], rot1[0]);
        });
    }
}
