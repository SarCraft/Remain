package io.github.remain.rendering;

/**
 * Système de projection isométrique (vue en diagonale).
 * 
 * Cette classe convertit les coordonnées 3D du monde (X, Y, Z) en
 * coordonnées 2D à l'écran (X, Y). C'est ce qui crée l'effet de vue
 * en diagonale, comme dans les anciens jeux vidéo.
 * 
 * Système de coordonnées :
 * - Monde 3D : X (droite), Y (haut), Z (avant)
 * - Écran 2D : X (droite), Y (bas)
 * - Ratio isométrique : 2:1 (losange de 32x16 pixels)
 * 
 * Dimensions des tuiles : 32x32 pixels avec 16px de hauteur visuelle
 */
public final class IsometricProjection {
    
    // Tile dimensions in pixels
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    
    // Isometric grid dimensions (the diamond shape on ground)
    private static final float GRID_WIDTH = 32.0f;
    private static final float GRID_HEIGHT = 16.0f;
    
    // Vertical spacing between Y layers
    private static final float LAYER_HEIGHT = 16.0f;
    
    // Tile rendering offsets (to center tile on grid point)
    private static final float TILE_OFFSET_X = TILE_WIDTH / 2.0f;
    private static final float TILE_OFFSET_Y = 8.0f;
    
    // Current camera rotation (0-3 for 90° increments)
    private static int cameraRotation = 0;
    
    /**
     * Constructeur privé - Cette classe contient seulement des méthodes statiques.
     */
    private IsometricProjection() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Convertit des coordonnées 3D de grille en coordonnées 2D à l'écran.
     * 
     * Cette fonction applique la "magie" isométrique : elle prend une position
     * dans le monde (X, Y, Z) et calcule où la dessiner à l'écran (X, Y).
     * 
     * @param gridX Colonne de la grille (X)
     * @param gridY Altitude (Y)
     * @param gridZ Ligne de la grille (Z)
     * @return Tableau [posX écran, posY écran] en pixels
     */
    public static float[] gridToScreen(int gridX, int gridY, int gridZ) {
        // Apply camera rotation
        int[] rotated = rotateCoordinates(gridX, gridZ);
        int rotX = rotated[0];
        int rotZ = rotated[1];
        
        // Isometric projection formula (2:1 ratio)
        float halfGridWidth = GRID_WIDTH / 2.0f;
        float halfGridHeight = GRID_HEIGHT / 2.0f;
        
        float screenX = (rotX - rotZ) * halfGridWidth;
        float screenY = (rotX + rotZ) * halfGridHeight - (gridY * LAYER_HEIGHT);
        
        return new float[]{screenX, screenY};
    }
    
    /**
     * Gets the tile draw position (includes tile offset for proper centering).
     * Use this for actual rendering, as it positions the tile texture
     * correctly relative to its grid position.
     * @param gridX Grid column
     * @param gridY Elevation
     * @param gridZ Grid row
     * @return Array [drawX, drawY] in pixels
     */
    public static float[] getTileDrawPosition(int gridX, int gridY, int gridZ) {
        float[] screenPos = gridToScreen(gridX, gridY, gridZ);
        
        // Apply tile offsets to center the tile sprite
        float drawX = screenPos[0] - TILE_OFFSET_X;
        float drawY = screenPos[1] - TILE_OFFSET_Y;
        
        return new float[]{drawX, drawY};
    }
    
    /**
     * Converts 2D screen coordinates to 3D grid coordinates.
     * Assumes Y=0 (ground level). Use this for mouse picking.
     * @param screenX Screen X in pixels
     * @param screenY Screen Y in pixels
     * @return Array [gridX, gridZ] (Y not included, assume 0)
     */
    public static int[] screenToGrid(float screenX, float screenY) {
        // Reverse the isometric projection
        // This is the inverse of the gridToScreen formula
        
        float halfGridWidth = GRID_WIDTH / 2.0f;
        float halfGridHeight = GRID_HEIGHT / 2.0f;
        
        // Solve the isometric equations for X and Z
        float rotX = (screenX / halfGridWidth + screenY / halfGridHeight) / 2.0f;
        float rotZ = (screenY / halfGridHeight - screenX / halfGridWidth) / 2.0f;
        
        // Round to nearest integer
        int gridX = Math.round(rotX);
        int gridZ = Math.round(rotZ);
        
        // Reverse camera rotation
        int[] unrotated = unrotateCoordinates(gridX, gridZ);
        
        return unrotated;
    }
    
    /**
     * Rotates grid coordinates based on current camera rotation.
     * Rotation is applied clockwise in 90° increments.
     * @param gridX Original X
     * @param gridZ Original Z
     * @return Array [rotatedX, rotatedZ]
     */
    public static int[] rotateCoordinates(int gridX, int gridZ) {
        return switch (cameraRotation) {
            case 1 -> new int[]{gridZ, -gridX};   // 90° clockwise
            case 2 -> new int[]{-gridX, -gridZ};  // 180°
            case 3 -> new int[]{-gridZ, gridX};   // 270° clockwise
            default -> new int[]{gridX, gridZ};   // 0° (no rotation)
        };
    }
    
    /**
     * Unrotates grid coordinates (inverse of rotateCoordinates).
     * @param rotX Rotated X
     * @param rotZ Rotated Z
     * @return Array [originalX, originalZ]
     */
    private static int[] unrotateCoordinates(int rotX, int rotZ) {
        return switch (cameraRotation) {
            case 1 -> new int[]{-rotZ, rotX};     // Inverse of 90° clockwise
            case 2 -> new int[]{-rotX, -rotZ};    // Inverse of 180°
            case 3 -> new int[]{rotZ, -rotX};     // Inverse of 270° clockwise
            default -> new int[]{rotX, rotZ};     // No rotation
        };
    }
    
    /**
     * Rotates the camera 90° clockwise.
     */
    public static void rotateCameraClockwise() {
        cameraRotation = (cameraRotation + 1) % 4;
    }
    
    /**
     * Rotates the camera 90° counter-clockwise.
     */
    public static void rotateCameraCounterClockwise() {
        cameraRotation = (cameraRotation - 1 + 4) % 4;
    }
    
    /**
     * Resets camera rotation to 0° (north).
     */
    public static void resetCameraRotation() {
        cameraRotation = 0;
    }
    
    /**
     * Gets the current camera rotation (0-3).
     * @return Rotation value (0=north, 1=east, 2=south, 3=west)
     */
    public static int getCameraRotation() {
        return cameraRotation;
    }
    
    /**
     * Sets the camera rotation directly.
     * @param rotation Rotation value (0-3), will be clamped
     */
    public static void setCameraRotation(int rotation) {
        cameraRotation = Math.floorMod(rotation, 4);
    }
    
    /**
     * Gets the tile width in pixels.
     * @return Tile width
     */
    public static int getTileWidth() {
        return TILE_WIDTH;
    }
    
    /**
     * Gets the tile height in pixels.
     * @return Tile height
     */
    public static int getTileHeight() {
        return TILE_HEIGHT;
    }
    
    /**
     * Gets the grid width (isometric diamond width).
     * @return Grid width
     */
    public static float getGridWidth() {
        return GRID_WIDTH;
    }
    
    /**
     * Gets the grid height (isometric diamond height).
     * @return Grid height
     */
    public static float getGridHeight() {
        return GRID_HEIGHT;
    }
    
    /**
     * Gets the vertical layer height.
     * @return Layer height in pixels
     */
    public static float getLayerHeight() {
        return LAYER_HEIGHT;
    }
    
    /**
     * Calculates the center of the world in screen coordinates.
     * @param worldWidth World width in grid units
     * @param worldDepth World depth in grid units
     * @return Array [screenX, screenY] of world center
     */
    public static float[] getWorldCenter(int worldWidth, int worldDepth) {
        return gridToScreen(worldWidth / 2, 0, worldDepth / 2);
    }
}
