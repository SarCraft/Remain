package io.github.remain.domain.common;

/**
 * Représente une position 3D dans le monde du jeu.
 * 
 * Contient trois coordonnées :
 * - X : Colonne de la grille (augmente vers la droite)
 * - Y : Altitude/hauteur (augmente vers le haut)
 * - Z : Ligne de la grille (augmente vers l'avant)
 * 
 * Cette position est immutable (ne peut pas être modifiée après création).
 */
public record Position3D(int x, int y, int z) {
    
    /**
     * Crée une nouvelle position 3D.
     * (On peut ajouter de la validation ici si nécessaire)
     */
    public Position3D {
        // Compact constructor for validation
        // Add validation here if needed, e.g.:
        // if (y < 0) throw new IllegalArgumentException("y must be non-negative");
    }
    
    /**
     * Crée une nouvelle position décalée par rapport à celle-ci.
     * 
     * @param dx Décalage en X
     * @param dy Décalage en Y
     * @param dz Décalage en Z
     * @return Nouvelle position
     */
    public Position3D offset(int dx, int dy, int dz) {
        return new Position3D(x + dx, y + dy, z + dz);
    }
    
    /**
     * Creates a new position with the same x and z, but different y.
     * @param newY New Y coordinate
     * @return New position
     */
    public Position3D withY(int newY) {
        return new Position3D(x, newY, z);
    }
    
    /**
     * Calculates Manhattan distance to another position.
     * @param other The other position
     * @return Manhattan distance (sum of absolute coordinate differences)
     */
    public int manhattanDistance(Position3D other) {
        return Math.abs(x - other.x) + 
               Math.abs(y - other.y) + 
               Math.abs(z - other.z);
    }
    
    /**
     * Calculates Euclidean distance to another position.
     * @param other The other position
     * @return Euclidean distance
     */
    public double euclideanDistance(Position3D other) {
        int dx = x - other.x;
        int dy = y - other.y;
        int dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Checks if this position is adjacent to another (including diagonals).
     * @param other The other position
     * @return true if adjacent (distance <= 1 in all dimensions)
     */
    public boolean isAdjacentTo(Position3D other) {
        return Math.abs(x - other.x) <= 1 &&
               Math.abs(y - other.y) <= 1 &&
               Math.abs(z - other.z) <= 1 &&
               !equals(other);
    }
    
    /**
     * Zero position constant (0, 0, 0).
     */
    public static final Position3D ZERO = new Position3D(0, 0, 0);
}
