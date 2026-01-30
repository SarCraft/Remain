package io.github.remain.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Conteneur des ressources partagées du jeu.
 */
public record GameContext(
        SpriteBatch batch,
        OrthographicCamera worldCamera,
        OrthographicCamera uiCamera,
        Viewport worldViewport,
        Viewport uiViewport
) {
    public GameContext {
        if (batch == null) throw new NullPointerException("batch cannot be null");
        if (worldCamera == null) throw new NullPointerException("worldCamera cannot be null");
        if (uiCamera == null) throw new NullPointerException("uiCamera cannot be null");
        if (worldViewport == null) throw new NullPointerException("worldViewport cannot be null");
        if (uiViewport == null) throw new NullPointerException("uiViewport cannot be null");
    }

    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    public void updateCameras() {
        worldCamera.update();
        uiCamera.update();
    }

    public void applyWorldProjection() {
        worldViewport.apply();
        batch.setProjectionMatrix(worldCamera.combined);
    }

    public void applyUiProjection() {
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
    }
}
