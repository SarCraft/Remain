package io.github.remain.service.asset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import io.github.remain.domain.world.BlockType;

/**
 * Service interface for asset management and loading.
 * This service abstracts libGDX's AssetManager and provides a clean API for
 * loading and accessing game assets (textures, sounds, fonts, etc.).
 * Design Rationale: By using an interface, we can:
 *    - Mock asset loading in tests
 *    - Swap implementations (e.g., for different platforms)
 *    - Add caching layers without changing client code
 *    - Track asset lifecycle centrally
 * 
 * Thread Safety: Implementations should be thread-safe for asset loading,
 * but texture retrieval should happen on the render thread only.
 * Usage Pattern:
 * {@code
 * // Get service from registry
 * AssetService assets = serviceRegistry.get(AssetService.class);
 * // Load assets (typically in loading screen)
 * assets.loadAssets();
 * // Get textures (in render loop)
 * TextureRegion texture = assets.getBlockTexture(BlockType.GRASS);
 * }
 * @author SarCraft
 * @since 1.0
 */
public interface AssetService extends Disposable {
    
    /**
     * Loads all game assets synchronously.
     * This method blocks until all assets are loaded. Use this for small games
     * or during a loading screen.
     * Performance: For large games, consider asynchronous loading
     * with {@link #loadAssetsAsync()} and {@link #updateLoading()}.
     */
    void loadAssets();
    
    /**
     * Starts loading assets asynchronously.
     * Call {@link #updateLoading()} each frame to continue loading.
     * Check {@link #isLoadingComplete()} to know when loading finishes.
     */
    void loadAssetsAsync();
    
    /**
     * Updates the asset loading process.
     * Call this once per frame when loading asynchronously.
     * Returns true when loading is complete.
     * @return true if loading is complete, false otherwise
     */
    boolean updateLoading();
    
    /**
     * Checks if asset loading is complete.
     * @return true if all assets are loaded, false otherwise
     */
    boolean isLoadingComplete();
    
    /**
     * Gets the loading progress as a percentage.
     * @return Loading progress (0.0 to 1.0)
     */
    float getLoadingProgress();
    
    /**
     * Gets the texture region for a specific block type.
     * Performance: This method should be fast (O(1) lookup from cache).
     * Safe to call every frame.
     * @param blockType The block type
     * @return The texture region, or null if not found
     */
    TextureRegion getBlockTexture(BlockType blockType);
    
    /**
     * Gets the grid/tile texture used for floor rendering.
     * @return The grid texture region, or null if not loaded
     */
    TextureRegion getGridTexture();
    
    /**
     * Checks if an asset is loaded.
     * @param assetPath The asset file path
     * @return true if loaded, false otherwise
     */
    boolean isLoaded(String assetPath);
    
    /**
     * Unloads a specific asset to free memory.
     * Use this for streaming large worlds or managing memory on mobile.
     * @param assetPath The asset file path to unload
     */
    void unload(String assetPath);
}
