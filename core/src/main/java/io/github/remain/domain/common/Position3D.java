package io.github.remain.domain.common;

/**
 * Immutable value object representing a 3D position in the game world.
 * This record encapsulates x, y, z coordinates following the value object pattern.
 * Being immutable, it can be safely shared across threads and used as map keys.
 * Design Rationale:
 *    - Immutability prevents bugs from unexpected modifications
 *    - Records provide automatic equals/hashCode/toString
 *    - Type safety vs using raw int[] or Vector3
 *    - Domain-specific meaning vs generic Point3D
 * 
 * Coordinate System:
 *    - X: Grid column (increases right)
 *    - Y: Elevation/height (increases up)
 *    - Z: Grid row (increases forward)
 * 
 * @param x Grid column coordinate
 * @param y Elevation coordinate (height)
 * @param z Grid row coordinate
 * @author SarCraft
 * @since 1.0
 */
public record Position3D(int x, int y, int z) {
    
    /**
     * Creates a new Position3D with validation.
     * Currently allows any integer values. Add validation if needed
     * (e.g., non-negative coordinates).
     */
    public Position3D {
        // Compact constructor for validation
        // Add validation here if needed, e.g.:
        // if (y < 0) throw new IllegalArgumentException("y must be non-negative");
    }
    
    /**
     * Creates a new position offset by the specified amounts.
     * @param dx Delta X
     * @param dy Delta Y
     * @param dz Delta Z
     * @return New position
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
