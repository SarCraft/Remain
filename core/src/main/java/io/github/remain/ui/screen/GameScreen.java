package io.github.remain.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import io.github.remain.core.GameContext;
import io.github.remain.core.ServiceRegistry;
import io.github.remain.domain.world.World;
import io.github.remain.service.asset.AssetService;
import io.github.remain.service.input.InputService;
import io.github.remain.service.rendering.RenderingService;
import io.github.remain.system.isometric.IsometricProjection;
import io.github.remain.systems.CameraControlSystem;
import io.github.remain.systems.IsometricRenderSystem;
import io.github.remain.systems.TileSelectionSystem;
import io.github.remain.systems.TileSelectionRenderer;
import io.github.remain.ui.DebugOverlay;

/**
 * Main gameplay screen - refactored with clean architecture.
 * This screen demonstrates the new architecture in action:
 *    - Services accessed via ServiceRegistry (no static dependencies)
 *    - Rendering delegated to RenderingService
 *    - Input handled via InputService
 *    - Pure domain logic in World model
 *    - Coordinate transformations via IsometricProjection
 *    - Tile selection system for interactive gameplay
 * 
 * @author SarCraft
 * @since 1.0
 */
public final class GameScreen extends BaseScreen {
    
    // Services (injected via constructor)
    private final AssetService assetService;
    private final InputService inputService;
    private final RenderingService renderingService;
    
    // Game state
    private World world;
    private DebugOverlay debugOverlay;
    
    // Systems
    private CameraControlSystem cameraControlSystem;
    private IsometricRenderSystem renderSystem;
    private TileSelectionSystem tileSelectionSystem;
    private TileSelectionRenderer selectionRenderer;
    
    /**
     * Creates a new GameScreen.
     * @param services Service registry
     * @param context Game context
     */
    public GameScreen(ServiceRegistry services, GameContext context) {
        super(services, context);
        
        // Get services from registry
        this.assetService = services.get(AssetService.class);
        this.inputService = services.get(InputService.class);
        this.renderingService = services.get(RenderingService.class);
    }
    
    @Override
    protected void onShow() {
        Gdx.app.log("GameScreen", "Initializing game screen...");
        
        // Load assets
        assetService.loadAssets();
        
        // Create debug overlay
        debugOverlay = new DebugOverlay();
        
        // Create world (25x25x8 with fixed seed for demo)
        world = new World(25, 25, 8, 12345L);
        
        // Initialize systems
        cameraControlSystem = new CameraControlSystem(inputService, context.worldCamera());
        renderSystem = new IsometricRenderSystem(assetService, world);
        tileSelectionSystem = new TileSelectionSystem(world);
        selectionRenderer = new TileSelectionRenderer();
        
        // Center camera on world
        float centerX = (world.getWidth() * IsometricProjection.getGridWidth()) / 2f;
        float centerY = (world.getDepth() * IsometricProjection.getGridHeight()) / 2f + 100;
        context.worldCamera().position.set(centerX, centerY, 0);
        context.worldCamera().update();
        
        // Register input processor
        inputService.registerProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cameraControlSystem.handleZoom(amountY);
                return true;
            }
            
            @Override
            public boolean keyDown(int keycode) {
                // Toggle debug menu with F3
                if (keycode == Input.Keys.F3) {
                    debugOverlay.toggle();
                    return true;
                }
                
                // Rotation handling
                if (keycode == Input.Keys.Q) {
                    cameraControlSystem.rotateCounterClockwise();
                    return true;
                } else if (keycode == Input.Keys.E) {
                    cameraControlSystem.rotateClockwise();
                    return true;
                }
                return false;
            }
            
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Handle left click for tile selection
                if (button == Input.Buttons.LEFT) {
                    boolean selected = tileSelectionSystem.handleClick(
                        screenX, screenY, context.worldCamera()
                    );
                    
                    if (selected) {
                        Gdx.app.log("TileSelection", String.format(
                            "Selected tile: X=%d Y=%d Z=%d",
                            tileSelectionSystem.getSelectedX(),
                            tileSelectionSystem.getSelectedY(),
                            tileSelectionSystem.getSelectedZ()
                        ));
                    }
                    
                    return true;
                }
                return false;
            }
        });
        
        Gdx.app.log("GameScreen", "Game screen initialized");
    }
    
    @Override
    protected void update(float delta) {
        // Update camera system
        cameraControlSystem.update(delta);
        
        // Update selection animation
        selectionRenderer.update(delta);
    }
    
    @Override
    protected void renderFrame(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Render world
        renderWorld();
        
        // Render UI
        renderUI();
    }
    
    /**
     * Renders the game world.
     */
    private void renderWorld() {
        renderingService.beginWorld();
        
        // Render world blocks
        renderSystem.render(renderingService.getBatch());
        
        // End batch before drawing selection (uses ShapeRenderer)
        renderingService.end();
        
        // Render selection highlight
        selectionRenderer.render(
            renderingService.getBatch(),
            tileSelectionSystem.getSelectedBlock()
        );
        
        // Note: We don't call begin again because renderUI will do it
    }
    
    /**
     * Renders UI elements.
     */
    private void renderUI() {
        renderingService.beginUi();
        
        debugOverlay.render(
            renderingService.getBatch(),
            context.worldCamera(),
            world,
            context.uiViewport().getWorldHeight(),
            tileSelectionSystem.getSelectedX(),
            tileSelectionSystem.getSelectedY(),
            tileSelectionSystem.getSelectedZ()
        );
        
        renderingService.end();
    }
    
    @Override
    protected void onHide() {
        // Unregister input processors
        inputService.clearProcessors();
    }
    
    @Override
    protected void onDispose() {
        if (debugOverlay != null) {
            debugOverlay.dispose();
        }
        if (selectionRenderer != null) {
            selectionRenderer.dispose();
        }
    }
}