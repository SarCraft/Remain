package io.github.remain.service.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Service interface for input handling and event dispatching.
 * This service provides a high-level abstraction over libGDX's input system,
 * allowing systems and screens to register for input events without directly
 * coupling to libGDX's InputProcessor.
 * Design Rationale:
 * - Decouples input handling from screens/systems
 * - Enables input event recording/playback for replays
 * - Allows input remapping without changing game code
 * - Testable without actual input devices
 * Usage Pattern:
 * {@code
 * InputService input = serviceRegistry.get(InputService.class);
 * input.registerProcessor(myInputProcessor);
 * input.unregisterProcessor(myInputProcessor);
 * }
 * @author SarCraft
 * @since 1.0
 */
public interface InputService {
    
    /**
     * Registers an input processor to receive input events.
     * Multiple processors can be registered. Events are dispatched in
     * registration order until one processor handles the event.
     * @param processor The input processor to register
     */
    void registerProcessor(InputProcessor processor);
    
    /**
     * Unregisters an input processor.
     * Should be called when the processor is no longer needed (e.g., when
     * leaving a screen).
     * @param processor The input processor to unregister
     */
    void unregisterProcessor(InputProcessor processor);
    
    /**
     * Clears all registered input processors.
     * Use this when transitioning between screens to ensure clean state.
     */
    void clearProcessors();
    
    /**
     * Checks if the specified key is currently pressed.
     * @param keycode The key code (from {@link com.badlogic.gdx.Input.Keys})
     * @return true if pressed, false otherwise
     */
    boolean isKeyPressed(int keycode);
    
    /**
     * Checks if the specified button is currently pressed.
     * @param button The button code (from {@link com.badlogic.gdx.Input.Buttons})
     * @return true if pressed, false otherwise
     */
    boolean isButtonPressed(int button);
    
    /**
     * Gets the current mouse/touch X coordinate in screen space.
     * @return X coordinate in pixels
     */
    int getX();
    
    /**
     * Gets the current mouse/touch Y coordinate in screen space.
     * @return Y coordinate in pixels
     */
    int getY();
    
    /**
     * Sets whether input should be processed.
     * Use this to temporarily disable input (e.g., during cutscenes).
     * @param enabled true to enable input, false to disable
     */
    void setInputEnabled(boolean enabled);
    
    /**
     * Checks if input processing is enabled.
     * @return true if enabled, false otherwise
     */
    boolean isInputEnabled();
}
