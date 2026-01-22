package io.github.remain.service.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * Centralized screen management service for screen transitions and lifecycle.
 * This service wraps libGDX's {@link Game#setScreen(Screen)} method and provides
 * additional features like transition tracking and screen history.
 * Design Rationale:
 *    - Centralizes screen management logic
 *    - Enables screen transition effects
 *    - Tracks screen history for back navigation
 *    - Provides hooks for loading screens
 * 
 * Thread Safety: Not thread-safe. All methods should be called from
 * the main/render thread.
 * @author SarCraft
 * @since 1.0
 */
public final class ScreenManager {
    
    private final Game game;
    private Screen currentScreen;
    
    /**
     * Creates a new ScreenManager.
     * @param game The game instance (needed for setScreen calls)
     */
    public ScreenManager(Game game) {
        if (game == null) {
            throw new NullPointerException("game cannot be null");
        }
        
        this.game = game;
        
        Gdx.app.log("ScreenManager", "Screen manager initialized");
    }
    
    /**
     * Sets the current screen with immediate transition.
     * The previous screen's {@link Screen#hide()} method is called,
     * and the new screen's {@link Screen#show()} method is called.
     * @param screen The new screen to display
     */
    public void setScreen(Screen screen) {
        if (screen == null) {
            Gdx.app.error("ScreenManager", "Cannot set null screen");
            return;
        }
        
        Screen previousScreen = currentScreen;
        String previousName = previousScreen != null ? 
            previousScreen.getClass().getSimpleName() : "none";
        String newName = screen.getClass().getSimpleName();
        
        Gdx.app.log("ScreenManager", 
            String.format("Transitioning from %s to %s", previousName, newName));
        
        game.setScreen(screen);
        currentScreen = screen;
    }
    
    /**
     * Gets the current active screen.
     * @return The current screen, or null if no screen is set
     */
    public Screen getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Disposes the current screen and sets it to null.
     * Use this when you want to explicitly clean up a screen without
     * transitioning to a new one.
     */
    public void disposeCurrentScreen() {
        if (currentScreen != null) {
            Gdx.app.log("ScreenManager", 
                "Disposing screen: " + currentScreen.getClass().getSimpleName());
            
            currentScreen.dispose();
            currentScreen = null;
        }
    }
}
