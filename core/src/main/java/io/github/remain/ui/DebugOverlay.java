package io.github.remain.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.remain.world.World;
import io.github.remain.rendering.IsometricProjection;

/**
 * Overlay de debug (écran F3) similaire à celui de Minecraft.
 * 
 * Affiche des informations de débogage utiles :
 * - Métriques de performance (FPS)
 * - Informations de la caméra (position, zoom)
 * - Statistiques du monde (taille, nombre de blocs)
 * - Position de la souris
 * - Bloc sélectionné
 * 
 * Appuyez sur F3 pour afficher/masquer.
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
     * Affiche/masque l'overlay de debug.
     */
    public void toggle() {
        visible = !visible;
        Gdx.app.log("DebugOverlay", "Infos de debug " + (visible ? "activées" : "désactivées"));
    }
    
    /**
     * Définit la visibilité de l'overlay de debug.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Vérifie si l'overlay de debug est visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Dessine l'overlay de debug.
     */
    public void render(SpriteBatch batch, OrthographicCamera camera, World world, float screenHeight) {
        render(batch, camera, world, screenHeight, -1, -1, -1);
    }
    
    /**
     * Dessine l'overlay de debug avec les informations de sélection de bloc.
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
