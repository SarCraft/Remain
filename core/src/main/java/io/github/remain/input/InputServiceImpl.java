package io.github.remain.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

/**
 * Implémentation du service de gestion des entrées.
 * 
 * Cette classe utilise InputMultiplexer de libGDX pour gérer
 * plusieurs processeurs d'entrée et délègue les requêtes d'état
 * à Gdx.input.
 */
public final class InputServiceImpl implements InputService {
    
    private final InputMultiplexer multiplexer;
    private boolean inputEnabled;
    
    /**
     * Crée un nouveau service d'entrée et l'enregistre dans libGDX.
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
