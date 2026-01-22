package io.github.remain.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.remain.service.asset.AssetService;
import io.github.remain.service.asset.impl.AssetServiceImpl;
import io.github.remain.service.input.InputService;
import io.github.remain.service.input.impl.InputServiceImpl;
import io.github.remain.service.rendering.RenderingService;
import io.github.remain.service.rendering.impl.RenderingServiceImpl;
import io.github.remain.service.screen.ScreenManager;
import io.github.remain.ui.screen.GameScreen;

/**
 * Main application entry point for the Remain game.
 * This class is responsible for:
 *    - Initializing core libGDX resources (SpriteBatch, Cameras, Viewports)
 *    - Registering all application services in the ServiceRegistry
 *    - Managing the application lifecycle (create, resize, dispose)
 *    - Delegating screen management to ScreenManager
 * 
 * Design Rationale: This class is intentionally thin. It focuses solely
 * on application bootstrap and lifecycle. All game logic is delegated to services
 * and screens, following the Single Responsibility Principle.
 * 
 * <p><b>Lifecycle Order:</b></p>
 * <ul>
 *   <li>create() - Initialize resources, register services, set initial screen</li>
 *   <li>resize() - Handle window resize events</li>
 *   <li>render() - Delegate to current screen (via ScreenManager)</li>
 *   <li>dispose() - Clean up all resources</li>
 * </ul>
 * 
 * @author SarCraft
 * @since 1.0
 */
public class GameApplication extends Game {
    
    // Virtual screen dimensions (independent of window size)
    private static final float VIRTUAL_WIDTH = 1920f;
    private static final float VIRTUAL_HEIGHT = 1080f;
    
    // Core services and context
    private ServiceRegistry serviceRegistry;
    private GameContext gameContext;
    private ScreenManager screenManager;
    
    // Core resources (owned by this class)
    private SpriteBatch batch;
    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;
    private FitViewport worldViewport;
    private ScreenViewport uiViewport;
    
    /**
     * Creates a new GameApplication.
     * Constructor is intentionally empty. All initialization happens in create()
     * to follow libGDX lifecycle conventions.
     */
    public GameApplication() {
        super();
    }
    
    /**
     * Called once when the application is created.
     * This is the main initialization method. It:
     * <ul>
     *   <li>Creates core libGDX resources</li>
     *   <li>Initializes the service registry</li>
     *   <li>Registers all services</li>
     *   <li>Creates the game context</li>
     *   <li>Sets the initial screen</li>
     * </ul>
     * 
     * <p><b>Performance Note:</b> This method may take time. Consider showing
     * a loading screen for production builds.</p>
     */
    @Override
    public void create() {
        Gdx.app.log("GameApplication", "Initializing application...");
        
        // Initialize core libGDX resources
        initializeResources();
        
        // Create service registry
        serviceRegistry = new ServiceRegistry();
        
        // Create game context
        gameContext = new GameContext(
            batch,
            worldCamera,
            uiCamera,
            worldViewport,
            uiViewport
        );
        
        // Register services (order matters for dependencies)
        registerServices();
        
        // Initialize screen manager
        screenManager = serviceRegistry.get(ScreenManager.class);
        
        // Set initial screen (GameScreen for now, MenuScreen in production)
        screenManager.setScreen(new GameScreen(serviceRegistry, gameContext));
        
        Gdx.app.log("GameApplication", "Application initialized successfully");
        Gdx.app.log("GameApplication", "Registered services: " + serviceRegistry.size());
    }
    
    /**
     * Initializes core libGDX resources.
     * Resources created here:
     *    - SpriteBatch - Shared batch for all rendering
     *    - WorldCamera - Camera for game world (uses FitViewport for scaling)
     *    - UICamera - Separate camera for UI (uses ScreenViewport for pixel-perfect)
     *    - Viewports - Handle different screen sizes and aspect ratios
     */
    private void initializeResources() {
        Gdx.app.log("GameApplication", "Creating core resources...");
        
        // Create shared SpriteBatch (reused across all screens for performance)
        batch = new SpriteBatch();
        
        // World camera with FitViewport (maintains aspect ratio, scales content)
        worldCamera = new OrthographicCamera();
        worldViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, worldCamera);
        worldCamera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        worldCamera.update();
        
        // UI camera with ScreenViewport (pixel-perfect, no scaling)
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        uiCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        uiCamera.update();
        
        Gdx.app.log("GameApplication", 
            String.format("Virtual resolution: %.0fx%.0f", VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
    }
    
    /**
     * Registers all application services in the service registry.
     * Registration Order: Services with no dependencies first,
     * then services that depend on others.
     * Adding New Services: Register them here following the pattern:
     * {@code
     * ServiceInterface service = new ServiceImplementation(dependencies...);
     * serviceRegistry.register(ServiceInterface.class, service);
     * }
     */
    private void registerServices() {
        Gdx.app.log("GameApplication", "Registering services...");
        
        // Asset service (no dependencies)
        AssetService assetService = new AssetServiceImpl();
        serviceRegistry.register(AssetService.class, assetService);
        
        // Input service (no dependencies)
        InputService inputService = new InputServiceImpl();
        serviceRegistry.register(InputService.class, inputService);
        
        // Rendering service (depends on GameContext)
        RenderingService renderingService = new RenderingServiceImpl(gameContext);
        serviceRegistry.register(RenderingService.class, renderingService);
        
        // Screen manager (depends on Game reference for setScreen)
        ScreenManager screenManager = new ScreenManager(this);
        serviceRegistry.register(ScreenManager.class, screenManager);
        
        Gdx.app.log("GameApplication", "All services registered");
    }
    
    /**
     * Called when the window is resized.
     * Delegates resize to the game context and current screen.
     * @param width New window width in pixels
     * @param height New window height in pixels
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        // Update viewports
        gameContext.resize(width, height);
        
        Gdx.app.log("GameApplication", 
            String.format("Window resized to %dx%d", width, height));
    }
    
    /**
     * Called every frame to render the game.
     * This method:
     * <ul>
     *   <li>Clears the screen</li>
     *   <li>Updates cameras</li>
     *   <li>Delegates rendering to the current screen</li>
     * </ul>
     * 
     * <p><b>Performance:</b> Keep this method as thin as possible. All logic
     * should be in screens and systems.</p>
     */
    @Override
    public void render() {
        // Clear screen with black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update cameras
        gameContext.updateCameras();
        
        // Render current screen (handled by libGDX's Game class)
        super.render();
    }
    
    /**
     * Called when the application is paused (mobile) or minimized (desktop).
     * Override to save game state or pause audio.
     */
    @Override
    public void pause() {
        super.pause();
        Gdx.app.log("GameApplication", "Application paused");
    }
    
    /**
     * Called when the application is resumed after being paused.
     * Override to restore game state or resume audio.
     */
    @Override
    public void resume() {
        super.resume();
        Gdx.app.log("GameApplication", "Application resumed");
    }
    
    /**
     * Called when the application is destroyed.
     * This method ensures all resources are properly disposed in the correct order:
     * <ul>
     *   <li>Dispose current screen (via super.dispose())</li>
     *   <li>Dispose all services (via ServiceRegistry)</li>
     *   <li>Dispose core resources (SpriteBatch, etc.)</li>
     * </ul>
     * 
     * <p><b>Critical:</b> Failure to dispose resources causes memory leaks.</p>
     */
    @Override
    public void dispose() {
        Gdx.app.log("GameApplication", "Disposing application...");
        
        // Dispose current screen
        super.dispose();
        
        // Dispose all registered services
        if (serviceRegistry != null) {
            serviceRegistry.dispose();
        }
        
        // Dispose core resources
        if (batch != null) {
            batch.dispose();
        }
        
        Gdx.app.log("GameApplication", "Application disposed successfully");
    }
    
    /**
     * Gets the service registry for this application.
     * Usage: Generally, you should not need to access this directly.
     * Services are passed to screens via constructor.
     * @return The service registry
     */
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    
    /**
     * Gets the game context for this application.
     * @return The game context
     */
    public GameContext getGameContext() {
        return gameContext;
    }
}
