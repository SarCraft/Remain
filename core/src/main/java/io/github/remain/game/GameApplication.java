package io.github.remain.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.remain.assets.AssetService;
import io.github.remain.assets.AssetServiceImpl;
import io.github.remain.input.InputService;
import io.github.remain.input.InputServiceImpl;
import io.github.remain.rendering.RenderingService;
import io.github.remain.rendering.RenderingServiceImpl;

/**
 * Point d'entrée principal du jeu Remain.
 * 
 * Cette classe démarre le jeu et gère son cycle de vie :
 * 1. create()  → Démarre le jeu et charge les ressources
 * 2. render()  → Dessine le jeu à chaque image (60 fois par seconde)
 * 3. resize()  → Ajuste l'affichage quand la fenêtre change de taille
 * 4. dispose() → Libère la mémoire quand on ferme le jeu
 */
public class GameApplication extends Game {
    
    // Taille de l'écran virtuel du jeu (1920x1080)
    private static final float VIRTUAL_WIDTH = 1920f;
    private static final float VIRTUAL_HEIGHT = 1080f;
    
    // Services du jeu (gestion des ressources, input, rendu, écrans)
    private ServiceRegistry serviceRegistry;
    private GameContext gameContext;
    private ScreenManager screenManager;
    
    // Outils de rendu libGDX
    private SpriteBatch batch;              // Dessine les sprites/images
    private OrthographicCamera worldCamera; // Caméra pour le monde du jeu
    private OrthographicCamera uiCamera;    // Caméra pour l'interface utilisateur
    private FitViewport worldViewport;      // Adapte le monde à la fenêtre
    private ScreenViewport uiViewport;      // Adapte l'UI à la fenêtre
    
    public GameApplication() {
        super();
    }
    
    /**
     * Démarre le jeu - Appelée une seule fois au lancement.
     * 
     * Cette méthode prépare tout ce dont le jeu a besoin :
     * - Crée les outils de dessin (caméras, batch)
     * - Initialise les services (ressources, input, rendu)
     * - Affiche l'écran de jeu
     */
    @Override
    public void create() {
        Gdx.app.log("GameApplication", "Démarrage du jeu...");
        
        // Étape 1 : Créer les outils de dessin
        initializeResources();
        
        // Étape 2 : Créer le registre des services
        serviceRegistry = new ServiceRegistry();
        
        // Étape 3 : Créer le contexte (données partagées entre tous les écrans)
        gameContext = new GameContext(
            batch,
            worldCamera,
            uiCamera,
            worldViewport,
            uiViewport
        );
        
        // Étape 4 : Enregistrer tous les services
        registerServices();
        
        // Étape 5 : Récupérer le gestionnaire d'écrans
        screenManager = serviceRegistry.get(ScreenManager.class);
        
        // Étape 6 : Afficher l'écran de jeu
        screenManager.setScreen(new GameScreen(serviceRegistry, gameContext));
        
        Gdx.app.log("GameApplication", "Jeu démarré avec succès");
        Gdx.app.log("GameApplication", "Services enregistrés : " + serviceRegistry.size());
    }
    
    /**
     * Crée les outils de base pour dessiner le jeu.
     * 
     * - batch : l'outil qui dessine toutes les images
     * - worldCamera : la caméra qui regarde le monde du jeu
     * - uiCamera : la caméra qui regarde l'interface (menu, scores, etc.)
     */
    private void initializeResources() {
        Gdx.app.log("GameApplication", "Création des ressources...");
        
        // Créer le SpriteBatch (dessine toutes les images du jeu)
        batch = new SpriteBatch();
        
        // Créer la caméra du monde (bouge avec le joueur)
        worldCamera = new OrthographicCamera();
        worldViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, worldCamera);
        worldCamera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        worldCamera.update();
        
        // Créer la caméra de l'UI (reste fixe à l'écran)
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        uiCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        uiCamera.update();
        
        Gdx.app.log("GameApplication", 
            String.format("Résolution virtuelle : %.0fx%.0f", VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
    }
    
    /**
     * Enregistre tous les services du jeu.
     * 
     * Les services sont des outils spécialisés :
     * - AssetService : charge les images et les sons
     * - InputService : gère le clavier et la souris
     * - RenderingService : dessine le jeu à l'écran
     * - ScreenManager : change les écrans (menu, jeu, options)
     */
    private void registerServices() {
        Gdx.app.log("GameApplication", "Enregistrement des services...");
        
        // Service de gestion des ressources (images, sons, etc.)
        AssetService assetService = new AssetServiceImpl();
        serviceRegistry.register(AssetService.class, assetService);
        
        // Service de gestion des entrées (clavier, souris)
        InputService inputService = new InputServiceImpl();
        serviceRegistry.register(InputService.class, inputService);
        
        // Service de rendu (dessine le jeu)
        RenderingService renderingService = new RenderingServiceImpl(gameContext);
        serviceRegistry.register(RenderingService.class, renderingService);
        
        // Gestionnaire d'écrans (change entre menu, jeu, options)
        ScreenManager screenManager = new ScreenManager(this);
        serviceRegistry.register(ScreenManager.class, screenManager);
        
        Gdx.app.log("GameApplication", "Tous les services sont prêts");
    }
    
    /**
     * Appelée quand la fenêtre change de taille.
     * Ajuste les caméras pour que le jeu reste bien affiché.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        // Ajuste les viewports pour la nouvelle taille de fenêtre
        gameContext.resize(width, height);
        
        Gdx.app.log("GameApplication", 
            String.format("Fenêtre redimensionnée : %dx%d", width, height));
    }
    
    /**
     * Dessine le jeu - Appelée 60 fois par seconde (60 FPS).
     * 
     * 1. Efface l'écran (fond noir)
     * 2. Met à jour les caméras
     * 3. Dessine l'écran actuel (menu ou jeu)
     */
    @Override
    public void render() {
        // Efface l'écran avec un fond noir
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Met à jour les positions des caméras
        gameContext.updateCameras();
        
        // Dessine l'écran actuel (géré automatiquement par libGDX)
        super.render();
    }
    
    /**
     * Appelée quand le jeu est mis en pause (mobile) ou minimisé (PC).
     */
    @Override
    public void pause() {
        super.pause();
        Gdx.app.log("GameApplication", "Jeu en pause");
    }
    
    /**
     * Appelée quand le jeu reprend après une pause.
     */
    @Override
    public void resume() {
        super.resume();
        Gdx.app.log("GameApplication", "Jeu repris");
    }
    
    /**
     * Ferme le jeu proprement - Appelée à la fermeture.
     * 
     * Libère toute la mémoire utilisée pour éviter les fuites mémoire :
     * 1. Ferme l'écran actuel
     * 2. Ferme tous les services
     * 3. Libère les outils de dessin
     */
    @Override
    public void dispose() {
        Gdx.app.log("GameApplication", "Fermeture du jeu...");
        
        // Ferme l'écran actuel
        super.dispose();
        
        // Ferme tous les services enregistrés
        if (serviceRegistry != null) {
            serviceRegistry.dispose();
        }
        
        // Libère le SpriteBatch
        if (batch != null) {
            batch.dispose();
        }
        
        Gdx.app.log("GameApplication", "Jeu fermé avec succès");
    }
    
    /**
     * Récupère le registre des services.
     * (Rarement utilisé directement - les services sont passés aux écrans)
     */
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    
    /**
     * Récupère le contexte du jeu (caméras, viewports, etc.).
     */
    public GameContext getGameContext() {
        return gameContext;
    }
}
