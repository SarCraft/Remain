package io.github.remain.domain.world;

/**
 * Énumération de tous les types de blocs du jeu.
 * 
 * Chaque type représente un matériau différent avec ses propres
 * propriétés visuelles et de gameplay.
 * 
 * Types disponibles :
 * - GRASS : Herbe (surface)
 * - DIRT : Terre (sous la surface)
 * - STONE : Pierre (profondeur)
 * - WATER : Eau (liquide, non marchable)
 */
public enum BlockType {
    
    /** Herbe - végétation de surface. */
    GRASS(0, "Herbe", true),
    
    /** Terre - matériau sous la surface. */
    DIRT(1, "Terre", true),
    
    /** Pierre - matériau dur, en profondeur. */
    STONE(2, "Pierre", true),
    
    /** Eau - liquide, non marchable. */
    WATER(3, "Eau", false);
    
    private final int tileIndex;
    private final String displayName;
    private final boolean walkable;
    
    /**
     * Crée un nouveau type de bloc.
     * 
     * @param tileIndex Index dans l'atlas de textures
     * @param displayName Nom lisible par l'humain
     * @param walkable Si les entités peuvent marcher sur ce bloc
     */
    BlockType(int tileIndex, String displayName, boolean walkable) {
        this.tileIndex = tileIndex;
        this.displayName = displayName;
        this.walkable = walkable;
    }
    
    /**
     * Retourne l'index de la tuile dans l'atlas de textures.
     * (Utilisé par le moteur de rendu pour trouver la bonne image)
     */
    public int getTileIndex() {
        return tileIndex;
    }
    
    /**
     * Retourne le nom d'affichage lisible.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Vérifie si on peut marcher sur ce type de bloc.
     */
    public boolean isWalkable() {
        return walkable;
    }
    
    /**
     * Vérifie si ce type de bloc est solide (bloque le mouvement).
     */
    public boolean isSolid() {
        return this != WATER;
    }
}
