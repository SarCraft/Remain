package io.github.remain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.remain.world.BlockType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Implémentation du service de gestion des ressources.
 * 
 * Cette classe charge les textures depuis un spritesheet (grande image
 * contenant toutes les tuiles) et les met en cache pour un accès rapide.
 * 
 * Structure des ressources :
 * - Spritesheet : blocks/spritesheet.png (tuiles de 32x32 pixels)
 * - Texture de grille : Extraite du spritesheet
 * 
 * Optimisations :
 * - Un seul spritesheet pour minimiser les changements de texture
 * - Textures mises en cache (pas d'allocation mémoire à chaque frame)
 * - Filtrage nearest-neighbor pour le style pixel-art
 */
public final class AssetServiceImpl implements AssetService {
    
    // Spritesheet configuration
    private static final String SPRITESHEET_PATH = "blocks/spritesheet.png";
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    
    // Loaded assets
    private Texture spritesheet;
    private final Map<BlockType, TextureRegion> blockTextures;
    private TextureRegion gridTexture;
    
    // Loading state
    private boolean loaded;
    
    /**
     * Crée un nouveau service de ressources.
     * Les ressources ne sont pas chargées avant l'appel à loadAssets().
     */
    public AssetServiceImpl() {
        this.blockTextures = new EnumMap<>(BlockType.class);
        this.loaded = false;
    }
    
    @Override
    public void loadAssets() {
        if (loaded) {
            Gdx.app.log("AssetService", "Ressources déjà chargées");
            return;
        }
        
        Gdx.app.log("AssetService", "Chargement des ressources...");
        
        loadSpritesheet();
        extractTextures();
        
        loaded = true;
        Gdx.app.log("AssetService", "Ressources chargées avec succès");
    }
    
    /**
     * Charge la grande image (spritesheet) contenant toutes les tuiles.
     */
    private void loadSpritesheet() {
        spritesheet = new Texture(Gdx.files.internal(SPRITESHEET_PATH));
        
        // Use nearest-neighbor filtering for crisp pixel art
        spritesheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        Gdx.app.log("AssetService", 
            String.format("Spritesheet loaded: %dx%d", 
                spritesheet.getWidth(), spritesheet.getHeight()));
    }
    
    /**
     * Extracts individual tile textures from the spritesheet.
     * Texture coordinates are hardcoded based on the spritesheet layout.
     * In a production system, consider using a texture atlas descriptor file.
     */
    private void extractTextures() {
        // Extract block textures (coordinates from original AssetLoader)
        blockTextures.put(BlockType.GRASS, getTileAt(4, 1));
        blockTextures.put(BlockType.DIRT, getTileAt(2, 0));
        blockTextures.put(BlockType.STONE, getTileAt(3, 4));
        blockTextures.put(BlockType.WATER, getTileAt(6, 5));
        
        // Extract grid texture
        gridTexture = getTileAt(0, 6);
        
        Gdx.app.log("AssetService", 
            String.format("Extracted %d block textures", blockTextures.size()));
    }
    
    /**
     * Extracts a single tile from the spritesheet at the given grid position.
     * @param col Column index (0-based)
     * @param row Row index (0-based)
     * @return TextureRegion for the tile
     */
    private TextureRegion getTileAt(int col, int row) {
        return new TextureRegion(
            spritesheet,
            col * TILE_WIDTH,
            row * TILE_HEIGHT,
            TILE_WIDTH,
            TILE_HEIGHT
        );
    }
    
    @Override
    public void loadAssetsAsync() {
        // For now, just call synchronous loading
        // In production, this would use AssetManager for async loading
        loadAssets();
    }
    
    @Override
    public boolean updateLoading() {
        // No-op for synchronous loading
        return loaded;
    }
    
    @Override
    public boolean isLoadingComplete() {
        return loaded;
    }
    
    @Override
    public float getLoadingProgress() {
        return loaded ? 1.0f : 0.0f;
    }
    
    @Override
    public TextureRegion getBlockTexture(BlockType blockType) {
        if (!loaded) {
            Gdx.app.error("AssetService", "Attempted to get texture before assets loaded");
            return null;
        }
        
        return blockTextures.get(blockType);
    }
    
    @Override
    public TextureRegion getGridTexture() {
        if (!loaded) {
            Gdx.app.error("AssetService", "Attempted to get grid texture before assets loaded");
            return null;
        }
        
        return gridTexture;
    }
    
    @Override
    public boolean isLoaded(String assetPath) {
        // Simple implementation: only one asset (spritesheet)
        return loaded && SPRITESHEET_PATH.equals(assetPath);
    }
    
    @Override
    public void unload(String assetPath) {
        if (SPRITESHEET_PATH.equals(assetPath)) {
            dispose();
        }
    }
    
    @Override
    public void dispose() {
        Gdx.app.log("AssetService", "Disposing assets...");
        
        if (spritesheet != null) {
            spritesheet.dispose();
            spritesheet = null;
        }
        
        blockTextures.clear();
        gridTexture = null;
        loaded = false;
        
        Gdx.app.log("AssetService", "Assets disposed");
    }
}
