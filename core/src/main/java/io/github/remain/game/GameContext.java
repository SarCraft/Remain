package io.github.remain.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Conteneur des ressources partagées du jeu.
 * 
 * Cette classe regroupe tous les outils de dessin et caméras utilisés
 * dans tout le jeu. Au lieu de passer chaque outil séparément, on les
 * regroupe ici pour simplifier le code.
 * 
 * Contient :
 * - batch : l'outil qui dessine les images
 * - worldCamera : la caméra qui regarde le monde
 * - uiCamera : la caméra qui regarde l'interface
 * - worldViewport : gère le redimensionnement du monde
 * - uiViewport : gère le redimensionnement de l'interface
 */
public record GameContext(
    SpriteBatch batch,
    OrthographicCamera worldCamera,
    OrthographicCamera uiCamera,
    Viewport worldViewport,
    Viewport uiViewport
) {
    /**
     * Crée un nouveau contexte avec tous les outils de dessin.
     * Vérifie que tous les paramètres sont valides (non null).
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
     * Met à jour les viewports quand la fenêtre change de taille.
     * Appelée automatiquement par GameApplication.resize().
     */
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }
    
    /**
     * Met à jour les deux caméras.
     * À appeler une fois par image avant de dessiner.
     */
    public void updateCameras() {
        worldCamera.update();
        uiCamera.update();
    }
    
    /**
     * Active la caméra du monde pour dessiner le jeu.
     * À appeler avant de dessiner les objets du monde.
     */
    public void applyWorldProjection() {
        batch.setProjectionMatrix(worldCamera.combined);
    }
    
    /**
     * Active la caméra de l'interface pour dessiner l'UI.
     * À appeler avant de dessiner les menus et textes.
     */
    public void applyUiProjection() {
        batch.setProjectionMatrix(uiCamera.combined);
    }
}
