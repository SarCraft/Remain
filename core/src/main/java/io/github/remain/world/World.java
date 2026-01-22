package io.github.remain.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente le monde entier du jeu.
 * 
 * Le monde est une grille 3D qui contient tous les blocs (cubes) du jeu.
 * Il gère la génération du terrain, l'ajout/suppression de blocs,
 * et les requêtes pour trouver des blocs.
 * 
 * Dimensions :
 * - Largeur (X) : nombre de colonnes
 * - Profondeur (Z) : nombre de lignes
 * - Hauteur (Y) : altitude/élévation
 */
public final class World {
    
    private final int width;
    private final int depth;
    private final int height;
    private final Block[][][] blocks;
    private final TerrainGenerator terrainGenerator;
    private final long seed;
    
    /**
     * Crée un nouveau monde avec une graine aléatoire. (système minecraft de seeds)
     * 
     * @param width Largeur du monde (colonnes)
     * @param depth Profondeur du monde (lignes)
     * @param height Hauteur maximale du monde
     */
    public World(int width, int depth, int height) {
        this(width, depth, height, System.currentTimeMillis());
    }
    
    /**
     * Crée un nouveau monde avec une graine spécifique.
     * 
     * La même graine génère toujours le même monde. Utile pour :
     * - Multijoueur (tout le monde a le même monde)
     * - Tests (résultats prévisibles)
     * - Partage de mondes intéressants
     * 
     * @param width Largeur du monde (colonnes)
     * @param depth Profondeur du monde (lignes)
     * @param height Hauteur maximale du monde
     * @param seed Graine pour la génération aléatoire
     */
    public World(int width, int depth, int height, long seed) {
        if (width <= 0 || depth <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive");
        }
        
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.seed = seed;
        this.blocks = new Block[width][depth][height];
        this.terrainGenerator = new TerrainGenerator(seed);
        
        generateWorld();
    }
    
    /**
     * Génère le terrain du monde de manière procédurale.
     * 
     * Remplit le monde avec des blocs en utilisant des fonctions de bruit
     * et des règles de terrain (herbe en surface, terre en dessous, etc.).
     */
    private void generateWorld() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                generateColumn(x, z);
            }
        }
    }
    
    /**
     * Génère une colonne verticale de blocs.
     * 
     * @param x Colonne de la grille
     * @param z Ligne de la grille
     */
    private void generateColumn(int x, int z) {
        // Get terrain height at this position
        int terrainHeight = terrainGenerator.getTerrainHeight(x, z);
        terrainHeight = Math.max(0, Math.min(terrainHeight, height - 2));
        
        // Generate blocks from bottom to terrain height
        for (int y = 0; y <= terrainHeight; y++) {
            BlockType type = getBlockTypeForDepth(x, z, y, terrainHeight);
            
            if (type != null) {
                blocks[x][z][y] = new Block(x, y, z, type);
            }
        }
    }
    
    /**
     * Détermine le type de bloc selon la profondeur sous la surface.
     * 
     * @param y Altitude actuelle
     * @param surfaceHeight Altitude de la surface à cette position
     * @return Type de bloc, ou null pour de l'air
     */
    private BlockType getBlockTypeForDepth(int x, int z, int y, int surfaceHeight) {
        if (y == surfaceHeight) {
            // Surface block
            return terrainGenerator.getSurfaceBlockType(x, z, surfaceHeight);
        } else {
            // Subsurface block
            return terrainGenerator.getSubsurfaceBlockType(y, surfaceHeight);
        }
    }
    
    /**
     * Récupère le bloc à une position donnée.
     * 
     * @return Le bloc, ou null si hors limites ou vide (air)
     */
    public Block getBlock(int x, int z, int y) {
        if (!isInBounds(x, z, y)) {
            return null;
        }
        return blocks[x][z][y];
    }
    
    /**
     * Récupère le bloc à une position donnée.
     */
    public Block getBlock(Position3D position) {
        Objects.requireNonNull(position, "position cannot be null");
        return getBlock(position.x(), position.z(), position.y());
    }
    
    /**
     * Place ou retire un bloc à une position.
     * 
     * Utile pour placer/casser des blocs pendant le jeu.
     * 
     * @param block Le bloc à placer, ou null pour retirer
     * @return true si réussi, false si hors limites
     */
    public boolean setBlock(int x, int z, int y, Block block) {
        if (!isInBounds(x, z, y)) {
            return false;
        }
        
        blocks[x][z][y] = block;
        return true;
    }
    
    /**
     * Place ou retire un bloc à une position.
     */
    public boolean setBlock(Position3D position, Block block) {
        Objects.requireNonNull(position, "position cannot be null");
        return setBlock(position.x(), position.z(), position.y(), block);
    }
    
    /**
     * Vérifie si une position est dans les limites du monde.
     */
    public boolean isInBounds(int x, int z, int y) {
        return x >= 0 && x < width &&
               z >= 0 && z < depth &&
               y >= 0 && y < height;
    }
    
    /**
     * Vérifie si une position est dans les limites du monde.
     */
    public boolean isInBounds(Position3D position) {
        Objects.requireNonNull(position, "position cannot be null");
        return isInBounds(position.x(), position.z(), position.y());
    }
    
    /**
     * Récupère tous les blocs du monde.
     * 
     * Attention : Cette méthode est lente car elle parcourt toutes les positions.
     * Pour le rendu, utilisez plutôt getVisibleBlocks().
     * 
     * @return Liste de tous les blocs
     */
    public List<Block> getAllBlocks() {
        List<Block> allBlocks = new ArrayList<>();
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < height; y++) {
                    Block block = blocks[x][z][y];
                    if (block != null) {
                        allBlocks.add(block);
                    }
                }
            }
        }
        
        return allBlocks;
    }
    
    /**
     * Récupère seulement les blocs visibles (en surface) pour le rendu.
     * 
     * Retourne uniquement le bloc le plus haut de chaque colonne, ce qui
     * suffit pour le rendu isométrique (les blocs du dessous sont cachés).
     * 
     * Performance : Beaucoup plus rapide que getAllBlocks() pour le rendu.
     * 
     * @return Liste des blocs visibles
     */
    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>(width * depth);
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Find the topmost block in this column
                for (int y = height - 1; y >= 0; y--) {
                    Block block = blocks[x][z][y];
                    if (block != null) {
                        visibleBlocks.add(block);
                        break; // Only add the top block
                    }
                }
            }
        }
        
        return visibleBlocks;
    }
    
    /**
     * Retourne la largeur du monde (dimension X, colonnes).
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Retourne la profondeur du monde (dimension Z, lignes).
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Retourne la hauteur du monde (dimension Y, altitude).
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Retourne la graine utilisée pour générer le monde.
     */
    public long getSeed() {
        return seed;
    }
    
    /**
     * Retourne le générateur de terrain utilisé par ce monde.
     */
    public TerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
    }
}
