package io.github.remain.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.remain.domain.world.World;
import io.github.remain.system.isometric.IsometricProjection;

/**
 * Debug overlay (F3 menu) similar to Minecraft's debug screen.
 * Displays performance metrics, camera info, and world statistics.
 * 
 * @author SarCraft
 * @since 1.0
 */
public class DebugOverlay implements Disposable {
    
    private final BitmapFont font;
    private boolean visible = false;
    
    private static final float LINE_HEIGHT = 20f;
    private static final float PADDING_X = 10f;
    private static final float PADDING_Y = 10f;
    
    public DebugOverlay() {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
    }
    
    /**
     * Toggle the visibility of the debug overlay.
     */
    public void toggle() {
        visible = !visible;
        Gdx.app.log("DebugOverlay", "Debug info " + (visible ? "enabled" : "disabled"));
    }
    
    /**
     * Set the visibility of the debug overlay.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Check if the debug overlay is visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Render the debug overlay.
     * @param batch SpriteBatch to draw with
     * @param camera World camera
     * @param world Game world
     * @param screenHeight Screen height for positioning
     */
    public void render(SpriteBatch batch, OrthographicCamera camera, World world, float screenHeight) {
        render(batch, camera, world, screenHeight, -1, -1, -1);
    }
    
    /**
     * Render the debug overlay with tile selection info.
     * @param batch SpriteBatch to draw with
     * @param camera World camera
     * @param world Game world
     * @param screenHeight Screen height for positioning
     * @param selectedX Selected tile X coordinate (-1 if none)
     * @param selectedY Selected tile Y coordinate (-1 if none)
     * @param selectedZ Selected tile Z coordinate (-1 if none)
     */
    public void render(SpriteBatch batch, OrthographicCamera camera, World world, 
                      float screenHeight, int selectedX, int selectedY, int selectedZ) {
        if (!visible) {
            return;
        }
        
        float y = screenHeight - PADDING_Y;
        
        // Title
        font.setColor(Color.YELLOW);
        font.draw(batch, "Remain - Debug Screen (F3)", PADDING_X, y);
        y -= LINE_HEIGHT;
        
        // Separator
        font.setColor(Color.WHITE);
        font.draw(batch, "================================", PADDING_X, y);
        y -= LINE_HEIGHT;
        
        // FPS info
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), PADDING_X, y);
        y -= LINE_HEIGHT;
        
        // Camera info
        float camX = camera.position.x;
        float camY = camera.position.y;
        font.draw(batch, 
            String.format("Camera: X=%.1f Y=%.1f Zoom=%.2f", camX, camY, camera.zoom),
            PADDING_X, y
        );
        y -= LINE_HEIGHT;
        
        // Rotation
        int rotation = IsometricProjection.getCameraRotation();
        String[] rotationNames = {"North", "East", "South", "West"};
        font.draw(batch,
            "Rotation: " + rotation + " (" + rotationNames[rotation % 4] + ")",
            PADDING_X, y
        );
        y -= LINE_HEIGHT;
        
        // World info
        font.draw(batch,
            "World: " + world.getWidth() + "x" + world.getDepth() + "x" + world.getHeight(),
            PADDING_X, y
        );
        y -= LINE_HEIGHT;
        
        // Visible blocks
        font.draw(batch,
            "Visible blocks: " + world.getVisibleBlocks().size(),
            PADDING_X, y
        );
        y -= LINE_HEIGHT;
        
        // Selected tile info
        if (selectedX >= 0 && selectedY >= 0 && selectedZ >= 0) {
            font.setColor(Color.CYAN);
            font.draw(batch,
                String.format("Selected Tile: X=%d Y=%d Z=%d", selectedX, selectedY, selectedZ),
                PADDING_X, y
            );
        } else {
            font.setColor(Color.GRAY);
            font.draw(batch, "Selected Tile: None", PADDING_X, y);
        }
        
        // Reset color
        font.setColor(Color.WHITE);
    }
    
    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
