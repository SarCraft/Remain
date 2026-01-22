package io.github.remain.system.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.remain.assets.AssetLoader;
import io.github.remain.domain.world.Block;
import io.github.remain.domain.world.World;
import io.github.remain.system.isometric.IsometricProjection;

import java.util.Comparator;
import java.util.List;

public class WorldRenderer {
    private final World world;
    private final AssetLoader assetLoader;
    private final ShapeRenderer shapeRenderer;
    
    // Animation de chargement
    private float loadingProgress = 0f;
    private static final float LOADING_SPEED = 2.0f;
    private boolean loadingComplete = false;

    public WorldRenderer(World world, AssetLoader assetLoader) {
        this.world = world;
        this.assetLoader = assetLoader;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, float cameraX, float cameraY, int selectedX, int selectedY, int selectedZ) {
        // Mettre à jour l'animation de chargement
        if (!loadingComplete) {
            loadingProgress += LOADING_SPEED * com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (loadingProgress >= 1f) {
                loadingProgress = 1f;
                loadingComplete = true;
            }
        }
        
        // Grid rendering (optional)
        // renderGrid(batch);

        // Collecter uniquement les blocs visibles (optimisation)
        List<Block> visibleBlocks = world.getVisibleBlocks();
        
        // Calculer le centre pour l'animation
        int worldWidth = world.getWidth();
        int worldDepth = world.getDepth();
        float centerX = worldWidth / 2f;
        float centerZ = worldDepth / 2f;
        float maxDistance = (float)Math.sqrt(centerX * centerX + centerZ * centerZ);

        // Tri isométrique adaptatif selon la rotation de caméra
        sortBlocksForIsometricRendering(visibleBlocks);

        // Rendu de chaque bloc avec animation individuelle
        for (Block block : visibleBlocks) {
            TextureRegion texture = assetLoader.getBlockTexture(block.getType());
            if (texture != null) {
                // Calculer le facteur d'animation pour ce bloc
                float dx = block.getX() - centerX;
                float dz = block.getZ() - centerZ;
                float distance = (float)Math.sqrt(dx * dx + dz * dz);
                float normalizedDistance = distance / maxDistance;
                
                // Calculer le scale basé sur la distance et la progression
                float tileProgress = Math.max(0, Math.min(1, (loadingProgress - normalizedDistance * 0.5f) * 2f));
                float scale = tileProgress; // Scale de 0 à 1
                
                if (scale > 0.01f) { // Ne dessiner que si visible
                    // Utiliser le système de grille pour obtenir la position correcte
                    float[] drawPos = IsometricProjection.getTileDrawPosition(
                        block.getX(),
                        block.getY(),
                        block.getZ()
                    );
                    
                    float tileWidth = IsometricProjection.getTileWidth();
                    float tileHeight = IsometricProjection.getTileHeight();
                    
                    // Calculer le centre de la tile pour le scale
                    float tileCenterX = drawPos[0] + tileWidth / 2f;
                    float tileCenterY = drawPos[1] + tileHeight / 2f;
                    
                    // Appliquer le scale autour du centre
                    float scaledWidth = tileWidth * scale;
                    float scaledHeight = tileHeight * scale;
                    float scaledX = tileCenterX - scaledWidth / 2f;
                    float scaledY = tileCenterY - scaledHeight / 2f;
                    
                    // Effet de surbrillance si la tile est sélectionnée
                    boolean isSelected = (block.getX() == selectedX && block.getY() == selectedY && block.getZ() == selectedZ);
                    if (isSelected) {
                        // Légère teinte jaune pour la sélection
                        batch.setColor(1.0f, 1.0f, 0.7f, 1.0f);
                    }
                    
                    batch.draw(
                        texture,
                        scaledX,
                        scaledY,
                        scaledWidth,
                        scaledHeight
                    );
                    
                    // Réinitialiser la couleur
                    if (isSelected) {
                        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
    }

    /**
     * Trie les blocs pour un rendu isométrique correct selon la rotation de caméra
     */
    private void sortBlocksForIsometricRendering(List<Block> blocks) {
        blocks.sort((b1, b2) -> {
            // Step 1: Always sort by Y height first (bottom to top)
            int yCompare = Integer.compare(b1.getY(), b2.getY());
            if (yCompare != 0) return yCompare;

            // Step 2: Apply same rotation transformation as rendering
            int[] rot1 = IsometricProjection.rotateCoordinates(b1.getX(), b1.getZ());
            int[] rot2 = IsometricProjection.rotateCoordinates(b2.getX(), b2.getZ());
            
            // Step 3: INVERTED isometric sort on transformed coordinates
            // After transformation, sort from front to back (inverted)
            int sum1 = rot1[0] + rot1[1];
            int sum2 = rot2[0] + rot2[1];
            if (sum1 != sum2) return Integer.compare(sum2, sum1); // INVERSÉ
            
            return Integer.compare(rot2[0], rot1[0]); // INVERSÉ
        });
    }

    /**
     * Rendu de la grille isométrique pour visualiser l'espace du monde
     */
    private void renderGrid(SpriteBatch batch) {
        TextureRegion gridTexture = assetLoader.getGridTexture();
        if (gridTexture == null) {
            return; // Pas de texture de grille disponible
        }

        int width = world.getWidth();
        int depth = world.getDepth();

        // Dessiner la grille au niveau Y=0 pour toute la surface du monde
        // Tri isométrique: selon Y croissant, puis (X+Z) croissant
        for (int sum = 0; sum < width + depth; sum++) {
            for (int x = 0; x < width; x++) {
                int z = sum - x;
                if (z >= 0 && z < depth) {
                    float[] drawPos = IsometricProjection.getTileDrawPosition(x, 0, z);

                    batch.draw(
                        gridTexture,
                        drawPos[0],
                        drawPos[1],
                        IsometricProjection.getTileWidth(),
                        IsometricProjection.getTileHeight()
                    );
                }
            }
        }
    }

    /**
     * Rendu de la grille isométrique avec des lignes (pour debug/visualisation)
     */
    public void renderGridLines(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.3f, 0.8f, 0.3f, 0.5f)); // Vert semi-transparent

        int width = world.getWidth();
        int depth = world.getDepth();

        // Dessiner les losanges de la grille isométrique
        // Chaque losange a 4 coins: haut, droite, bas, gauche
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                // Position centrale de cette case
                float[] center = IsometricProjection.gridToScreen(x, 0, z);

                // Calculer les 4 coins du losange autour du centre
                // Pour des tiles 32x24, le losange fait 32x16
                float halfWidth = 16f;   // GRID_WIDTH / 2 = 32 / 2
                float halfHeight = 8f;   // GRID_HEIGHT / 2 = 16 / 2

                // Coin haut
                float topX = center[0];
                float topY = center[1] + halfHeight;

                // Coin droit
                float rightX = center[0] + halfWidth;
                float rightY = center[1];

                // Coin bas
                float bottomX = center[0];
                float bottomY = center[1] - halfHeight;

                // Coin gauche
                float leftX = center[0] - halfWidth;
                float leftY = center[1];

                // Dessiner les 4 côtés du losange
                shapeRenderer.line(topX, topY, rightX, rightY);      // Haut -> Droite
                shapeRenderer.line(rightX, rightY, bottomX, bottomY); // Droite -> Bas
                shapeRenderer.line(bottomX, bottomY, leftX, leftY);   // Bas -> Gauche
                shapeRenderer.line(leftX, leftY, topX, topY);         // Gauche -> Haut
            }
        }

        shapeRenderer.end();
    }

    /**
     * Trouve le bloc le plus proche d'une position écran
     */
    public Block getBlockAtScreenPosition(float screenX, float screenY) {
        List<Block> blocks = world.getVisibleBlocks();
        Block closestBlock = null;
        float minDistance = Float.MAX_VALUE;
        
        for (Block block : blocks) {
            // Utiliser getTileDrawPosition comme dans le rendu pour être cohérent
            float[] pos = IsometricProjection.getTileDrawPosition(block.getX(), block.getY(), block.getZ());
            float tileCenterX = pos[0] + IsometricProjection.getTileWidth() / 2f;
            float tileCenterY = pos[1] + IsometricProjection.getTileHeight() / 2f;
            
            float dx = screenX - tileCenterX;
            float dy = screenY - tileCenterY;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            
            if (distance < 20) {
                // Si la tile est dans le rayon, prioriser par Y (hauteur) puis par distance
                if (closestBlock == null) {
                    closestBlock = block;
                    minDistance = distance;
                } else {
                    // Prioriser les tiles plus hautes (Y plus élevé)
                    if (block.getY() > closestBlock.getY()) {
                        closestBlock = block;
                        minDistance = distance;
                    } else if (block.getY() == closestBlock.getY() && distance < minDistance) {
                        // À même hauteur, prendre la plus proche
                        closestBlock = block;
                        minDistance = distance;
                    }
                }
            }
        }
        
        return closestBlock;
    }

    /**
     * Obtenir le centre du monde en coordonnées écran
     */
    public float[] getWorldCenter() {
        return IsometricProjection.getWorldCenter(world.getWidth(), world.getDepth());
    }

    /**
     * Libérer les ressources
     */
    public void dispose() {
        shapeRenderer.dispose();
    }
}
