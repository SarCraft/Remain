package io.github.remain.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import io.github.remain.core.GameContext;
import io.github.remain.core.ServiceRegistry;

/**
 * Abstract base class for all game screens.
 * This class provides a template for screen implementation with common
 * functionality and enforces proper lifecycle management.
 * Design Rationale:
 *    - Provides access to services and context via constructor injection
 *    - Implements default lifecycle methods to avoid boilerplate
 *    - Enforces separation of update and render logic
 *    - Tracks screen lifecycle state for debugging
 * 
 * Lifecycle Order:
 * <ul>
 *   <li>Constructor - Initialize screen-specific state</li>
 *   <li>show() - Screen becomes active, load resources</li>
 *   <li>render(delta) - Called every frame: update(delta) then renderFrame(delta)</li>
 *   <li>resize(width, height) - Window resized</li>
 *   <li>hide() - Screen becomes inactive, release temporary resources</li>
 *   <li>dispose() - Screen destroyed, release all resources</li>
 * </ul>
 * 
 * Usage Pattern:
 * {@code
 * public class MyScreen extends BaseScreen {
 *     public MyScreen(ServiceRegistry services, GameContext context) {
 *         super(services, context);
 *     }
 *     @Override
 *     protected void onShow() {
 *          Load screen-specific resources
 *     }
 *     @Override
 *     protected void update(float delta) {
 *         / Update game logic
 *     }
 *     @Override
 *     protected void renderFrame(float delta) {
 *          Render graphics
 *     }
 * }
 * }
 * @author SarCraft
 * @since 1.0
 */
public abstract class BaseScreen implements Screen {
    
    /** Service registry for accessing application services. */
    protected final ServiceRegistry services;
    
    /** Game context containing shared resources (batch, cameras, viewports). */
    protected final GameContext context;
    
    /** Tracks whether the screen is currently shown. */
    private boolean shown;
    
    /**
     * Creates a new BaseScreen with the specified services and context.
     * @param services The service registry
     * @param context The game context
     * @throws NullPointerException if services or context is null
     */
    protected BaseScreen(ServiceRegistry services, GameContext context) {
        if (services == null) {
            throw new NullPointerException("services cannot be null");
        }
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        
        this.services = services;
        this.context = context;
        this.shown = false;
        
        Gdx.app.log(getClass().getSimpleName(), "Screen created");
    }
    
    /**
     * Called when the screen is shown (becomes active).
     * This is the place to:
     *    - Register input processors
     *    - Start background music
     *    - Initialize screen-specific state
     * 
     * Override {@link #onShow()} to add custom behavior.
     */
    @Override
    public final void show() {
        Gdx.app.log(getClass().getSimpleName(), "Screen shown");
        shown = true;
        onShow();
    }
    
    /**
     * Called every frame to update and render the screen.
     * This method splits rendering into two phases:
     * <ul>
     *   <li>{@link #update(float)} - Update game logic</li>
     *   <li>{@link #renderFrame(float)} - Render graphics</li>
     * </ul>
     * 
     * This separation makes it easier to reason about game logic vs rendering.
     * 
     * @param delta Time in seconds since last frame
     */
    @Override
    public final void render(float delta) {
        // Update phase (game logic)
        update(delta);
        
        // Render phase (graphics)
        renderFrame(delta);
    }
    
    /**
     * Called when the window is resized.
     * The default implementation updates the context viewports.
     * Override {@link #onResize(int, int)} to add custom behavior.
     * @param width New window width in pixels
     * @param height New window height in pixels
     */
    @Override
    public final void resize(int width, int height) {
        Gdx.app.log(getClass().getSimpleName(), 
            String.format("Screen resized to %dx%d", width, height));
        
        context.resize(width, height);
        onResize(width, height);
    }
    
    /**
     * Called when the screen is hidden (no longer active).
     * This is the place to:
     *    - Unregister input processors
     *    - Pause background music
     *    - Save temporary state
     * 
     * Override {@link #onHide()} to add custom behavior.
     */
    @Override
    public final void hide() {
        Gdx.app.log(getClass().getSimpleName(), "Screen hidden");
        shown = false;
        onHide();
    }
    
    /**
     * Called when the game is paused (mobile/minimized).
     * Override {@link #onPause()} to add custom behavior.
     */
    @Override
    public final void pause() {
        Gdx.app.log(getClass().getSimpleName(), "Screen paused");
        onPause();
    }
    
    /**
     * Called when the game is resumed after pause.
     * Override {@link #onResume()} to add custom behavior.
     */
    @Override
    public final void resume() {
        Gdx.app.log(getClass().getSimpleName(), "Screen resumed");
        onResume();
    }
    
    /**
     * Called when the screen is disposed.
     * Override {@link #onDispose()} to release screen-specific resources.
     */
    @Override
    public final void dispose() {
        Gdx.app.log(getClass().getSimpleName(), "Screen disposed");
        onDispose();
    }
    
    // Template methods for subclasses to override
    
    /**
     * Called when the screen is shown.
     * Override to add custom initialization logic.
     */
    protected void onShow() {
        // Default: no-op
    }
    
    /**
     * Update game logic.
     * Called every frame before renderFrame().
     * @param delta Time in seconds since last frame
     */
    protected abstract void update(float delta);
    
    /**
     * Render graphics.
     * Called every frame after update().
     * @param delta Time in seconds since last frame
     */
    protected abstract void renderFrame(float delta);
    
    /**
     * Called when the window is resized.
     * Override to add custom resize behavior.
     * @param width New window width
     * @param height New window height
     */
    protected void onResize(int width, int height) {
        // Default: no-op
    }
    
    /**
     * Called when the screen is hidden.
     * Override to add custom cleanup logic.
     */
    protected void onHide() {
        // Default: no-op
    }
    
    /**
     * Called when the game is paused.
     * Override to add custom pause logic.
     */
    protected void onPause() {
        // Default: no-op
    }
    
    /**
     * Called when the game is resumed.
     * Override to add custom resume logic.
     */
    protected void onResume() {
        // Default: no-op
    }
    
    /**
     * Called when the screen is disposed.
     * Override to release screen-specific resources.
     */
    protected void onDispose() {
        // Default: no-op
    }
    
    /**
     * Checks if the screen is currently shown.
     * @return true if shown, false otherwise
     */
    protected final boolean isShown() {
        return shown;
    }
}
