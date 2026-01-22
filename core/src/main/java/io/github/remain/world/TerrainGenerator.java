package io.github.remain.world;

import com.badlogic.gdx.math.MathUtils;

/**
 * Générateur de terrain procédural.
 * 
 * Utilise des fonctions de bruit (noise) pour créer un terrain
 * réaliste avec des variations de hauteur, des biomes, et des détails.
 * 
 * Le même seed génère toujours le même terrain (utile pour le multijoueur).
 */
public class TerrainGenerator {
    private final NoiseGenerator heightNoise;
    private final NoiseGenerator biomeNoise;
    private final NoiseGenerator detailNoise;

    // Paramètres de génération
    private static final float HEIGHT_SCALE = 3.0f;  // Variation de hauteur max (réduit pour mieux voir)
    private static final float HEIGHT_FREQUENCY = 1f;  // Fréquence du terrain (plus lisse)
    private static final int BASE_HEIGHT = 0;  // Hauteur de base du terrain (au niveau 0)

    public TerrainGenerator(long seed) {
        this.heightNoise = new NoiseGenerator(seed);
        this.biomeNoise = new NoiseGenerator(seed + 1000);
        this.detailNoise = new NoiseGenerator(seed + 2000);
    }

    /**
     * Génère la hauteur du terrain à une position donnée
     */
    public int getTerrainHeight(int x, int z) {
        float noise = heightNoise.noise(x * HEIGHT_FREQUENCY, z * HEIGHT_FREQUENCY);
        int height = BASE_HEIGHT + (int)(noise * HEIGHT_SCALE);
        return Math.max(0, height);
    }

    /**
     * Détermine le type de bloc de surface basé sur le biome et la hauteur
     */
    public BlockType getSurfaceBlockType(int x, int z, int height) {
        float biome = biomeNoise.noise(x * 0.05f, z * 0.05f);
        float detail = detailNoise.noise(x * 0.3f, z * 0.3f);

        // Zones au niveau de l'eau ou en dessous
        if (height <= 0) {
            return BlockType.WATER;
        } else if (height == 1 && detail > 0.4f) {
            // Plages
            return BlockType.STONE;
        }

        // Zones de plaine
        if (height <= 2) {
            // Variation avec quelques zones de terre
            if (detail > 0.75f) {
                return BlockType.DIRT;
            }
            return BlockType.GRASS;
        }

        // Zones hautes = pierre/montagne
        if (biome > 0.5f) {
            return BlockType.STONE;
        }

        return BlockType.GRASS;
    }

    /**
     * Détermine le type de bloc en profondeur
     */
    public BlockType getSubsurfaceBlockType(int y, int surfaceHeight) {
        int depth = surfaceHeight - y;

        if (depth == 1) {
            // Juste en dessous de la surface = terre
            return BlockType.DIRT;
        } else if (depth >= 2) {
            // Plus profond = pierre
            return BlockType.STONE;
        }

        return null;
    }

    /**
     * Détermine si un arbre doit être placé à cette position
     */
    public boolean shouldPlaceTree(int x, int z, BlockType surfaceType) {
        if (surfaceType != BlockType.GRASS) {
            return false;
        }

        float treeNoise = detailNoise.noise(x * 0.2f, z * 0.2f);
        // Concentration d'arbres dans certaines zones
        return treeNoise > 0.75f && MathUtils.random() < 0.3f;
    }

    /**
     * Détermine la biome à une position (pour extensions futures)
     */
    public String getBiome(int x, int z) {
        float biome = biomeNoise.noise(x * 0.05f, z * 0.05f);

        if (biome < 0.3f) {
            return "DESERT";
        } else if (biome < 0.6f) {
            return "PLAINS";
        } else {
            return "MOUNTAINS";
        }
    }
}
