package io.github.remain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.remain.assets.AssetLoader;
import io.github.remain.systems.WorldRenderer;
import io.github.remain.domain.world.World;
import io.github.remain.domain.world.Block;
import io.github.remain.system.isometric.IsometricProjection;

public class GameScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private AssetLoader assetLoader;
    private World world;
    private WorldRenderer worldRenderer;
    private BitmapFont debugFont;

    private static final float VIRTUAL_WIDTH = 1024;
    private static final float VIRTUAL_HEIGHT = 600;
    private static final float CAMERA_SPEED = 500f;

    // Système de zoom
    private float targetZoom = 1.0f;
    private static final float MIN_ZOOM = 0.3f;
    private static final float MAX_ZOOM = 3.0f;
    private static final float ZOOM_SPEED = 0.1f;        // Vitesse zoom clavier
    private static final float ZOOM_LERP_SPEED = 8.0f;   // Vitesse interpolation
    private static final float SCROLL_ZOOM_FACTOR = 0.1f; // Sensibilité molette
    
    // Sélection de tile
    private int selectedTileX = -1;
    private int selectedTileY = -1;
    private int selectedTileZ = -1;
    
    // Mode debug
    private boolean debugMode = false;

    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        // Caméra séparée pour l'UI (fixe)
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        assetLoader = new AssetLoader();

        // Font pour debug
        debugFont = new BitmapFont();

        // Créer le monde (largeur, profondeur, hauteur) avec seed fixe pour la démo
        world = new World(25, 25, 8, 12345L);
        worldRenderer = new WorldRenderer(world, assetLoader);

        // Centrer la caméra sur le monde
        float[] center = worldRenderer.getWorldCenter();
        camera.position.set(center[0], center[1] + 100, 0);
        camera.update();
        
        // Créer un curseur personnalisé (losange isométrique)
        createCustomCursor();

        // Configurer le gestionnaire d'entrée pour la molette de souris et les clics
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                // amountY > 0 = scroll vers le bas (zoom out)
                // amountY < 0 = scroll vers le haut (zoom in)
                targetZoom += amountY * SCROLL_ZOOM_FACTOR;
                targetZoom = MathUtils.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
                return true;
            }
            
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    handleTileClick(screenX, screenY);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        // Gestion des déplacements de la caméra et du zoom
        handleInput(delta);

        // Interpolation fluide du zoom
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, ZOOM_LERP_SPEED * delta);

        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1); // Couleur du ciel
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Dessiner la grille isométrique avec lignes
        worldRenderer.renderGridLines(camera);

        batch.begin();
        worldRenderer.render(batch, camera.position.x, camera.position.y, selectedTileX, selectedTileY, selectedTileZ);
        batch.end();

        // Afficher les infos de debug en haut à gauche (HUD fixe)
        if (debugMode) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            int rotation = IsometricProjection.getCameraRotation();
            String[] rotationNames = {"Nord", "Est", "Sud", "Ouest"};
            debugFont.draw(batch, "Rotation: " + rotation + " (" + rotationNames[rotation % 4] + ")", 10, VIRTUAL_HEIGHT - 10);
            
            // Afficher les coordonnées du curseur
            int mouseScreenX = Gdx.input.getX();
            int mouseScreenY = Gdx.input.getY();
            debugFont.draw(batch, "Curseur écran: (" + mouseScreenX + ", " + mouseScreenY + ")", 10, VIRTUAL_HEIGHT - 50);
            
            // Convertir en coordonnées monde
            float worldX = mouseScreenX + camera.position.x - VIRTUAL_WIDTH / 2;
            float worldY = (VIRTUAL_HEIGHT - mouseScreenY) + camera.position.y - VIRTUAL_HEIGHT / 2;
            debugFont.draw(batch, "Curseur monde: (" + (int)worldX + ", " + (int)worldY + ")", 10, VIRTUAL_HEIGHT - 70);
            
            // Afficher les coordonnées de la tile sélectionnée
            if (selectedTileX >= 0) {
                debugFont.draw(batch, "Tile sélectionnée: X=" + selectedTileX + " Y=" + selectedTileY + " Z=" + selectedTileZ, 10, VIRTUAL_HEIGHT - 90);
            }
            
            batch.end();
        }
    }

    private void handleInput(float delta) {
        // Vitesse de caméra adaptée au niveau de zoom (plus on est zoomé, plus on se déplace lentement)
        float adjustedSpeed = CAMERA_SPEED * camera.zoom * delta;

        // Déplacement de la caméra avec les touches fléchées ou WASD
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= adjustedSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += adjustedSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += adjustedSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= adjustedSpeed;
        }

        // Zoom avec les touches + et - (ou = et -)
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            targetZoom -= ZOOM_SPEED * delta;
            targetZoom = MathUtils.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            targetZoom += ZOOM_SPEED * delta;
            targetZoom = MathUtils.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
        }

        // Reset du zoom avec la touche R
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            targetZoom = 1.0f;
        }
        
        // Toggle du mode debug avec F3
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
        }

        // Rotation de la vue isométrique (change l'angle de vue)
        // Q ou flèche gauche + Shift = rotation anti-horaire
        // E ou flèche droite + Shift = rotation horaire
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) ||
            (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            IsometricProjection.rotateCameraCounterClockwise();
            System.out.println("Rotation caméra: " + IsometricProjection.getCameraRotation());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) ||
            (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            IsometricProjection.rotateCameraClockwise();
            System.out.println("Rotation caméra: " + IsometricProjection.getCameraRotation());
        }
    }
    
    private void handleTileClick(int screenX, int screenY) {
        // Utiliser la caméra pour transformer les coordonnées écran en coordonnées monde
        com.badlogic.gdx.math.Vector3 worldCoords = camera.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0));
        
        // Trouver la tile la plus proche
        Block clickedBlock = worldRenderer.getBlockAtScreenPosition(worldCoords.x, worldCoords.y);
        
        if (clickedBlock != null) {
            selectedTileX = clickedBlock.getX();
            selectedTileY = clickedBlock.getY();
            selectedTileZ = clickedBlock.getZ();
            System.out.println("Tile sélectionnée: X=" + selectedTileX + " Y=" + selectedTileY + " Z=" + selectedTileZ);
        } else {
            System.out.println("Aucune tile détectée à la position: (" + (int)worldCoords.x + ", " + (int)worldCoords.y + ")");
        }
    }
    
    private void createCustomCursor() {
        // Créer un curseur personnalisé en forme de losange isométrique
        int size = 16;
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(size, size, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        
        // Dessiner un losange avec bordure
        pixmap.setColor(1, 1, 1, 1); // Blanc
        
        // Dessiner le contour du losange
        for (int i = 0; i < size / 2; i++) {
            // Haut du losange
            pixmap.drawPixel(size / 2 - i, i);
            pixmap.drawPixel(size / 2 + i, i);
            // Bas du losange
            pixmap.drawPixel(size / 2 - i, size - 1 - i);
            pixmap.drawPixel(size / 2 + i, size - 1 - i);
        }
        
        // Remplir le losange (optionnel, pour un curseur plus visible)
        pixmap.setColor(1, 1, 1, 0.5f); // Blanc semi-transparent
        for (int y = 1; y < size - 1; y++) {
            for (int x = 1; x < size - 1; x++) {
                int distFromCenter = Math.abs(x - size / 2) + Math.abs(y - size / 2);
                if (distFromCenter < size / 2) {
                    pixmap.drawPixel(x, y);
                }
            }
        }
        
        // Définir le curseur avec le point chaud au centre
        com.badlogic.gdx.graphics.Cursor cursor = Gdx.graphics.newCursor(pixmap, size / 2, size / 2);
        Gdx.graphics.setCursor(cursor);
        pixmap.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetLoader.dispose();
        worldRenderer.dispose();
        debugFont.dispose();
        Gdx.input.setInputProcessor(null);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
