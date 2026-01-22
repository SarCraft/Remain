package io.github.remain.service.input.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import io.github.remain.service.input.InputService;

/**
 * Default implementation of {@link InputService}.
 * This implementation uses libGDX's {@link InputMultiplexer} to manage
 * multiple input processors and delegates to {@link Gdx#input} for state queries.
 * Thread Safety: Not thread-safe. All methods should be called from
 * the main/render thread.
 * @author SarCraft
 * @since 1.0
 */
public final class InputServiceImpl implements InputService {
    
    private final InputMultiplexer multiplexer;
    private boolean inputEnabled;
    
    /**
     * Creates a new InputServiceImpl and registers it with libGDX.
     */
    public InputServiceImpl() {
        this.multiplexer = new InputMultiplexer();
        this.inputEnabled = true;
        
        // Register the multiplexer as the global input processor
        Gdx.input.setInputProcessor(multiplexer);
        
        Gdx.app.log("InputService", "Input service initialized");
    }
    
    @Override
    public void registerProcessor(InputProcessor processor) {
        if (processor == null) {
            Gdx.app.error("InputService", "Cannot register null processor");
            return;
        }
        
        multiplexer.addProcessor(processor);
        Gdx.app.log("InputService", "Registered input processor: " + processor.getClass().getSimpleName());
    }
    
    @Override
    public void unregisterProcessor(InputProcessor processor) {
        if (processor == null) {
            return;
        }
        
        multiplexer.removeProcessor(processor);
        Gdx.app.log("InputService", "Unregistered input processor: " + processor.getClass().getSimpleName());
    }
    
    @Override
    public void clearProcessors() {
        multiplexer.clear();
        Gdx.app.log("InputService", "Cleared all input processors");
    }
    
    @Override
    public boolean isKeyPressed(int keycode) {
        return inputEnabled && Gdx.input.isKeyPressed(keycode);
    }
    
    @Override
    public boolean isButtonPressed(int button) {
        return inputEnabled && Gdx.input.isButtonPressed(button);
    }
    
    @Override
    public int getX() {
        return Gdx.input.getX();
    }
    
    @Override
    public int getY() {
        return Gdx.input.getY();
    }
    
    @Override
    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
        Gdx.app.log("InputService", "Input " + (enabled ? "enabled" : "disabled"));
    }
    
    @Override
    public boolean isInputEnabled() {
        return inputEnabled;
    }
}
