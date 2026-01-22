package io.github.remain.service.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Service interface for rendering operations and batch management.
 * This service centralizes rendering concerns and provides utilities for
 * efficient batch rendering, camera management, and render order.
 * Design Rationale:
 *    - Centralizes SpriteBatch lifecycle (begin/end)
 *    - Manages render layers and sorting
 *    - Provides utilities for common rendering tasks
 *    - Abstracts rendering pipeline for future changes (e.g., 3D, post-processing)
 * 
 * Usage Pattern:
 * {@code
 * RenderingService rendering = serviceRegistry.get(RenderingService.class);
 * // Begin rendering to world camera
 * rendering.beginWorld();
 * // ... draw world objects ...
 * rendering.end();
 * // Begin rendering to UI camera
 * rendering.beginUi();
 * // ... draw UI elements ...
 * rendering.end();
 * }
 * @author SarCraft
 * @since 1.0
 */
public interface RenderingService extends Disposable {
    
    /**
     * Begins a rendering batch with the world camera projection.
     * Must be matched with a call to {@link #end()}.
     */
    void beginWorld();
    
    /**
     * Begins a rendering batch with the UI camera projection.
     * Must be matched with a call to {@link #end()}.
     */
    void beginUi();
    
    /**
     * Begins a rendering batch with a custom camera projection.
     * Must be matched with a call to {@link #end()}.
     * @param camera The camera to use for projection
     */
    void begin(Camera camera);
    
    /**
     * Ends the current rendering batch and flushes all draw calls.
     * Must be called after {@link #beginWorld()}, {@link #beginUi()},
     * or {@link #begin(Camera)}.
     */
    void end();
    
    /**
     * Gets the sprite batch for manual rendering.
     * Warning: Only use this for advanced cases where you need direct
     * batch access. Prefer using begin/end methods.
     * @return The sprite batch
     */
    SpriteBatch getBatch();
    
    /**
     * Checks if the batch is currently rendering (between begin/end).
     * @return true if rendering, false otherwise
     */
    boolean isRendering();
}
