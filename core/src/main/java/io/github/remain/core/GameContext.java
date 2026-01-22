package io.github.remain.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Immutable context holder for shared application-wide resources.
 * This class provides access to core libGDX resources that need to be shared
 * across screens and systems. It follows the immutability principle to prevent
 * accidental modifications.
 * Design Rationale: Instead of passing individual resources around or
 * using global statics, we bundle them in an immutable context object. This makes
 * dependencies explicit and facilitates testing.
 * Thread Safety: This class is immutable and thread-safe after construction.
 * @author SarCraft
 * @since 1.0
 */
public record GameContext(
    SpriteBatch batch,
    OrthographicCamera worldCamera,
    OrthographicCamera uiCamera,
    Viewport worldViewport,
    Viewport uiViewport
) {
    /**
     * Creates a new GameContext with the specified resources.
     * @param batch The main SpriteBatch for rendering (shared across screens)
     * @param worldCamera Camera for world/game rendering with projection
     * @param uiCamera Separate camera for UI rendering (typically orthographic)
     * @param worldViewport Viewport for world rendering (handles resize)
     * @param uiViewport Viewport for UI rendering (handles resize)
     * @throws NullPointerException if any parameter is null
     */
    public GameContext {
        // Compact constructor validation (Java 25 feature)
        if (batch == null) throw new NullPointerException("batch cannot be null");
        if (worldCamera == null) throw new NullPointerException("worldCamera cannot be null");
        if (uiCamera == null) throw new NullPointerException("uiCamera cannot be null");
        if (worldViewport == null) throw new NullPointerException("worldViewport cannot be null");
        if (uiViewport == null) throw new NullPointerException("uiViewport cannot be null");
    }
    
    /**
     * Updates both viewports when the window is resized.
     * This should be called from the application's resize callback.
     * @param width New window width in pixels
     * @param height New window height in pixels
     */
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }
    
    /**
     * Updates the cameras for both viewports.
     * This should be called once per frame before rendering.
     */
    public void updateCameras() {
        worldCamera.update();
        uiCamera.update();
    }
    
    /**
     * Sets the projection matrix for the batch to the world camera.
     * Call this before rendering world objects.
     */
    public void applyWorldProjection() {
        batch.setProjectionMatrix(worldCamera.combined);
    }
    
    /**
     * Sets the projection matrix for the batch to the UI camera.
     * Call this before rendering UI elements.
     */
    public void applyUiProjection() {
        batch.setProjectionMatrix(uiCamera.combined);
    }
}
