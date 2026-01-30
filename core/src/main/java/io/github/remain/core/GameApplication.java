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
 * Point d'entrée principal du jeu Remain.
 */
public class GameApplication extends Game {

    // ✅ Astuce : si ta fenêtre est souvent plus petite que 1920x1080,
    // le monde peut paraître "petit". Tu peux mettre 1280x720 plus tard.
    private static final float VIRTUAL_WIDTH = 1920f;
    private static final float VIRTUAL_HEIGHT = 1080f;

    private ServiceRegistry serviceRegistry;
    private GameContext gameContext;

    private SpriteBatch batch;
    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;
    private FitViewport worldViewport;
    private ScreenViewport uiViewport;

    @Override
    public void create() {
        Gdx.app.log("GameApplication", "Démarrage du jeu...");

        initializeResources();

        serviceRegistry = new ServiceRegistry();

        gameContext = new GameContext(
                batch,
                worldCamera,
                uiCamera,
                worldViewport,
                uiViewport
        );

        registerServices();

        ScreenManager screenManager = serviceRegistry.get(ScreenManager.class);
        screenManager.setScreen(new GameScreen(serviceRegistry, gameContext));
    }

    /**
     * Crée les outils de base pour dessiner le jeu.
     */
    private void initializeResources() {
        Gdx.app.log("GameApplication", "Création des ressources...");

        batch = new SpriteBatch();

        // Monde (virtual resolution)
        worldCamera = new OrthographicCamera();
        worldViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, worldCamera);
        worldCamera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        worldCamera.update();

        // ✅ UI : ScreenViewport = UI en pixels -> NE PAS forcer 1920x1080 ici !
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);

        // IMPORTANT : on laisse le viewport définir la taille réelle de la UI
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Gdx.app.log("GameApplication",
                String.format("Résolution virtuelle monde : %.0fx%.0f", VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
    }

    /**
     * Enregistre tous les services du jeu.
     */
    private void registerServices() {
        Gdx.app.log("GameApplication", "Enregistrement des services...");

        // Services
        serviceRegistry.register(AssetService.class, new AssetServiceImpl());
        serviceRegistry.register(InputService.class, new InputServiceImpl());
        serviceRegistry.register(RenderingService.class, new RenderingServiceImpl(gameContext));
        serviceRegistry.register(ScreenManager.class, new ScreenManager(this));

        Gdx.app.log("GameApplication", "Tous les services sont prêts");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (gameContext != null) {
            gameContext.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameApplication", "Fermeture du jeu...");

        super.dispose();

        if (batch != null) batch.dispose();
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public GameContext getGameContext() {
        return gameContext;
    }
}
