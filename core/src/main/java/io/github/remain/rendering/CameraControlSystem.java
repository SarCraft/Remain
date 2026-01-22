package io.github.remain.rendering;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.remain.input.InputService;

/**
 * Système de contrôle de la caméra (déplacement, rotation, zoom).
 * 
 * Ce système gère tous les contrôles de la caméra :
 * - Déplacement avec WASD ou les flèches
 * - Zoom avec la molette de la souris
 * - Rotation de la vue avec Q/E
 */
public class CameraControlSystem {
    
    private final InputService inputService;
    private final OrthographicCamera camera;
    
    private boolean enabled = true;
    
    private static final float CAMERA_SPEED = 500f;
    private static final float ZOOM_MIN = 0.3f;
    private static final float ZOOM_MAX = 3.0f;
    private static final float ZOOM_STEP = 0.1f;
    
    public CameraControlSystem(InputService inputService, OrthographicCamera camera) {
        this.inputService = inputService;
        this.camera = camera;
    }
    
    public void update(float delta) {
        if (!enabled) return;
        handleCameraMovement(delta);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Gère le déplacement de la caméra au clavier.
     */
    private void handleCameraMovement(float delta) {
        float moveSpeed = CAMERA_SPEED * delta;
        
        if (inputService.isKeyPressed(Input.Keys.W) || inputService.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += moveSpeed;
        }
        if (inputService.isKeyPressed(Input.Keys.S) || inputService.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= moveSpeed;
        }
        if (inputService.isKeyPressed(Input.Keys.A) || inputService.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= moveSpeed;
        }
        if (inputService.isKeyPressed(Input.Keys.D) || inputService.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += moveSpeed;
        }
        
        camera.update();
    }
    
    /**
     * Gère le zoom avec la molette de la souris.
     */
    public void handleZoom(float scrollAmount) {
        camera.zoom = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, camera.zoom + scrollAmount * ZOOM_STEP));
    }
    
    /**
     * Fait tourner la caméra dans le sens inverse des aiguilles d'une montre.
     */
    public void rotateCounterClockwise() {
        IsometricProjection.rotateCameraCounterClockwise();
    }
    
    /**
     * Fait tourner la caméra dans le sens des aiguilles d'une montre.
     */
    public void rotateClockwise() {
        IsometricProjection.rotateCameraClockwise();
    }
}
