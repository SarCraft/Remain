package io.github.remain.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import io.github.remain.domain.world.BlockType;

import java.util.HashMap;
import java.util.Map;

/**
 * Chargeur de ressources graphiques (images).
 * 
 * Cette classe charge et stocke toutes les textures (images) du jeu :
 * - Le spritesheet (grande image contenant toutes les tuiles)
 * - Les textures pour chaque type de bloc (herbe, terre, pierre, eau)
 * - La texture de la grille
 * 
 * Les textures sont chargées une seule fois au démarrage et réutilisées
 * tout au long du jeu pour de meilleures performances.
 */
public class AssetLoader implements Disposable {
    private Texture spritesheet;
    private Map<BlockType, TextureRegion> blockTextures;
    private TextureRegion gridTexture;

    // Configuration du spritesheet
    // Dimensions d'une tile isométrique dans le spritesheet
    private static final int TILE_WIDTH = 32;   // Largeur réelle d'une tile (32x32 pixels)
    private static final int TILE_HEIGHT = 32;  // Hauteur réelle d'une tile (32x32 pixels)

    public AssetLoader() {
        loadTextures();
    }

    private void loadTextures() {
        // Charger le spritesheet
        spritesheet = new Texture("blocks/spritesheet.png");
        spritesheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        System.out.println("Spritesheet chargé: " + spritesheet.getWidth() + "x" + spritesheet.getHeight() + " pixels");

        blockTextures = new HashMap<>();

        // Sélection des meilleures tiles pour chaque type de bloc
        blockTextures.put(BlockType.GRASS, getTileAt(4, 1));   // Rangée 1, colonne 4: Herbe verte claire
        blockTextures.put(BlockType.DIRT, getTileAt(2, 0));    // Rangée 0, colonne 2: Terre marron
        blockTextures.put(BlockType.STONE, getTileAt(3, 4));   // Rangée 4, colonne 3: Pierre grise
        blockTextures.put(BlockType.WATER, getTileAt(6, 5));   // Rangée 5, colonne 6: Eau bleue

        System.out.println("Textures de blocs chargées: " + blockTextures.size());

        // Grille: utiliser une tile claire de la rangée 6
        gridTexture = getTileAt(0, 6); // Première tile de la ligne de grille

        System.out.println("Texture de grille chargée");
    }

    /**
     * Récupère une tile à partir de sa position (col, row) dans le spritesheet
     * @param col Colonne (0-15)
     * @param row Ligne (0-15)
     * @return TextureRegion de la tile
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

    public TextureRegion getBlockTexture(BlockType type) {
        return blockTextures.get(type);
    }

    public TextureRegion getGridTexture() {
        return gridTexture;
    }

    @Override
    public void dispose() {
        spritesheet.dispose();
    }
}
