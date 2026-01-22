package io.github.remain.world;

/**
 * Générateur de bruit de Perlin simplifié.
 * 
 * Crée du "bruit" (valeurs aléatoires lisses) utilisé pour générer
 * des terrains naturels. Le même seed produit toujours le même bruit.
 * 
 * Concept : Au lieu de placer chaque bloc aléatoirement, on utilise
 * des fonctions mathématiques pour créer des collines et vallées lisses.
 */
public class NoiseGenerator {
    private final long seed;

    public NoiseGenerator(long seed) {
        this.seed = seed;
    }

    /**
     * Génère une valeur de bruit 2D entre 0 et 1
     */
    public float noise(float x, float z) {
        // Combinaison de plusieurs octaves pour un terrain plus naturel
        float total = 0;
        float frequency = 1;
        float amplitude = 1;
        float maxValue = 0;

        // 3 octaves de bruit
        for (int i = 0; i < 3; i++) {
            total += smoothNoise(x * frequency, z * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5f;
            frequency *= 2;
        }

        return total / maxValue;
    }

    /**
     * Bruit lissé
     */
    private float smoothNoise(float x, float z) {
        // Interpolation bilinéaire
        int intX = (int) Math.floor(x);
        int intZ = (int) Math.floor(z);

        float fracX = x - intX;
        float fracZ = z - intZ;

        // Valeurs aux coins
        float v1 = rawNoise(intX, intZ);
        float v2 = rawNoise(intX + 1, intZ);
        float v3 = rawNoise(intX, intZ + 1);
        float v4 = rawNoise(intX + 1, intZ + 1);

        // Interpolation avec fonction de lissage (smoothstep)
        float smoothX = smoothstep(fracX);
        float smoothZ = smoothstep(fracZ);

        float i1 = interpolate(v1, v2, smoothX);
        float i2 = interpolate(v3, v4, smoothX);

        return interpolate(i1, i2, smoothZ);
    }

    /**
     * Bruit brut basé sur les coordonnées
     */
    private float rawNoise(int x, int z) {
        // Hash simple pour générer un pseudo-random basé sur position et seed
        int n = x + z * 57 + (int)(seed * 131);
        n = (n << 13) ^ n;
        int nn = (n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff;
        return 1.0f - ((float)nn / 1073741824.0f);
    }

    /**
     * Interpolation linéaire
     */
    private float interpolate(float a, float b, float t) {
        return a * (1 - t) + b * t;
    }

    /**
     * Fonction de lissage (smoothstep)
     */
    private float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }
}
