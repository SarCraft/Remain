package io.github.remain.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.remain.domain.world.Block;
import io.github.remain.system.isometric.IsometricProjection;

/**
 * Renders a visual highlight for the selected tile.
 * Draws an overlay on top of the selected block to provide visual feedback.
 * 
 * Design Rationale:
 *    - Separated from main render system for clarity
 *    - Uses ShapeRenderer for simple geometric overlay
 *    - Customizable color and animation
 * 
 * @author SarCraft
 * @since 1.0
 */
public class TileSelectionRenderer {
    
    private final ShapeRenderer shapeRenderer;
    private final Color selectionColor;
    private float animationTime;
    
    // Selection highlight settings
    private static final float PULSE_SPEED = 2.0f;
    private static final float MIN_ALPHA = 0.3f;
    private static final float MAX_ALPHA = 0.7f;
    
    /**
     * Creates a new TileSelectionRenderer.
     */
    public TileSelectionRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.selectionColor = new Color(1.0f, 1.0f, 0.0f, 0.5f); // Yellow, semi-transparent
        this.animationTime = 0.0f;
    }
    
    /**
     * Updates the animation state.
     * @param delta Time since last update in seconds
     */
    public void update(float delta) {
        animationTime += delta;
    }
    
    /**
     * Renders the selection highlight for the given block.
     * Must be called between batch.end() and batch.begin() to avoid conflicts.
     * 
     * @param batch The sprite batch (must be ended before calling this)
     * @param selectedBlock The block to highlight, or null for no highlight
     */
    public void render(SpriteBatch batch, Block selectedBlock) {
        if (selectedBlock == null) {
            return;
        }
        
        // Calculate pulsing alpha
        float pulseAlpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * 
            (0.5f + 0.5f * (float) Math.sin(animationTime * PULSE_SPEED));
        
        selectionColor.a = pulseAlpha;
        
        // Get tile position
        float[] drawPos = IsometricProjection.getTileDrawPosition(
            selectedBlock.getX(), selectedBlock.getY(), selectedBlock.getZ()
        );
        
        float tileWidth = IsometricProjection.getTileWidth();
        float tileHeight = IsometricProjection.getTileHeight();
        
        // Draw diamond outline
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(selectionColor);
        
        // Diamond shape (isometric tile outline)
        float centerX = drawPos[0] + tileWidth / 2;
        float centerY = drawPos[1] + tileHeight / 2;
        float halfWidth = tileWidth / 2;
        float halfHeight = tileHeight / 4; // Diamond shape is half height visually
        
        // Draw the four edges of the diamond
        shapeRenderer.line(centerX, centerY + halfHeight, centerX + halfWidth, centerY);
        shapeRenderer.line(centerX + halfWidth, centerY, centerX, centerY - halfHeight);
        shapeRenderer.line(centerX, centerY - halfHeight, centerX - halfWidth, centerY);
        shapeRenderer.line(centerX - halfWidth, centerY, centerX, centerY + halfHeight);
        
        shapeRenderer.end();
        
        // Draw filled diamond overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        selectionColor.a = pulseAlpha * 0.3f; // More transparent for fill
        shapeRenderer.setColor(selectionColor);
        
        shapeRenderer.triangle(
            centerX, centerY + halfHeight,
            centerX + halfWidth, centerY,
            centerX, centerY
        );
        shapeRenderer.triangle(
            centerX, centerY + halfHeight,
            centerX, centerY,
            centerX - halfWidth, centerY
        );
        shapeRenderer.triangle(
            centerX, centerY,
            centerX + halfWidth, centerY,
            centerX, centerY - halfHeight
        );
        shapeRenderer.triangle(
            centerX, centerY,
            centerX, centerY - halfHeight,
            centerX - halfWidth, centerY
        );
        
        shapeRenderer.end();
    }
    
    /**
     * Disposes of resources.
     */
    public void dispose() {
        shapeRenderer.dispose();
    }
    
    /**
     * Sets the selection highlight color.
     * @param color The color to use
     */
    public void setColor(Color color) {
        this.selectionColor.set(color);
    }
}
