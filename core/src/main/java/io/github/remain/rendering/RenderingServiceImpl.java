package io.github.remain.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.remain.game.GameContext;

/**
 * Implémentation du service de rendu.
 * 
 * Cette classe gère le cycle de vie du SpriteBatch et fournit
 * des méthodes pratiques pour passer entre le rendu du monde
 * et le rendu de l'interface.
 */
public final class RenderingServiceImpl implements RenderingService {
    
    private final GameContext context;
    private boolean rendering;
    
    /**
     * Crée un nouveau service de rendu avec le contexte spécifié.
     */
    public RenderingServiceImpl(GameContext context) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        
        this.context = context;
        this.rendering = false;
        
        Gdx.app.log("RenderingService", "Rendering service initialized");
    }
    
    @Override
    public void beginWorld() {
        if (rendering) {
            Gdx.app.error("RenderingService", "Begin called while already rendering");
            return;
        }
        
        context.applyWorldProjection();
        context.batch().begin();
        rendering = true;
    }
    
    @Override
    public void beginUi() {
        if (rendering) {
            Gdx.app.error("RenderingService", "Begin called while already rendering");
            return;
        }
        
        context.applyUiProjection();
        context.batch().begin();
        rendering = true;
    }
    
    @Override
    public void begin(Camera camera) {
        if (rendering) {
            Gdx.app.error("RenderingService", "Begin called while already rendering");
            return;
        }
        
        if (camera == null) {
            Gdx.app.error("RenderingService", "Camera cannot be null");
            return;
        }
        
        context.batch().setProjectionMatrix(camera.combined);
        context.batch().begin();
        rendering = true;
    }
    
    @Override
    public void end() {
        if (!rendering) {
            Gdx.app.error("RenderingService", "End called without begin");
            return;
        }
        
        context.batch().end();
        rendering = false;
    }
    
    @Override
    public SpriteBatch getBatch() {
        return context.batch();
    }
    
    @Override
    public boolean isRendering() {
        return rendering;
    }
    
    @Override
    public void dispose() {
        if (rendering) {
            Gdx.app.log("RenderingService", "Disposing while rendering, ending batch");
            end();
        }
        
        Gdx.app.log("RenderingService", "Rendering service disposed");
    }
}
