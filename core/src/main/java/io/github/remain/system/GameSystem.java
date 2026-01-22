package io.github.remain.system;

/**
 * Base interface for all game systems.
 * Systems represent discrete pieces of game logic that can be updated each
 * frame. They follow an ECS-inspired (Entity-Component-System) architecture.
 * Design Rationale:
 *    - Separates concerns (each system has one responsibility)
 *    - Enables/disables systems independently
 *    - Clear update order control
 *    - Testable in isolation
 * 
 * Usage Pattern:
 * {@code
 * public class MySystem implements GameSystem {
 *     private boolean enabled = true;
 *     @Override
 *     public void update(float delta) {
 *         if (!enabled) return;
 *         // Update logic here
 *     }
 *     @Override
 *     public boolean isEnabled() {
 *         return enabled;
 *     }
 *     @Override
 *     public void setEnabled(boolean enabled) {
 *         this.enabled = enabled;
 *     }
 * }
 * }
 * @author SarCraft
 * @since 1.0
 */
public interface GameSystem {
    
    /**
     * Updates the system logic.
     * Called once per frame. System should check {@link #isEnabled()}
     * and return early if disabled.
     * @param delta Time in seconds since last frame
     */
    void update(float delta);
    
    /**
     * Checks if the system is enabled.
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
    
    /**
     * Enables or disables the system.
     * Disabled systems should skip all update logic.
     * @param enabled true to enable, false to disable
     */
    void setEnabled(boolean enabled);
    
    /**
     * Gets the system's priority for update order.
     * Lower numbers update first. Default is 0.
     * Use negative numbers for early systems (input),
     * positive for late systems (rendering).
     * @return Priority value
     */
    default int getPriority() {
        return 0;
    }
}
