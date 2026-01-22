package io.github.remain.service.asset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import io.github.remain.domain.world.BlockType;

/**
 * Service de gestion des ressources graphiques (images, sons, etc.).
 * 
 * Ce service charge et donne accès aux ressources du jeu :
 * - Textures (images) des blocs
 * - Sons
 * - Polices de caractères
 * - Autres fichiers du jeu
 * 
 * Exemple d'utilisation :
 *   // Récupérer le service
 *   AssetService assets = serviceRegistry.get(AssetService.class);
 *   // Charger les ressources (au démarrage)
 *   assets.loadAssets();
 *   // Récupérer une texture (pendant le jeu)
 *   TextureRegion texture = assets.getBlockTexture(BlockType.GRASS);
 */
public interface AssetService extends Disposable {
    
    /**
     * Charge toutes les ressources du jeu (de manière synchrone).
     * 
     * Cette méthode attend que tout soit chargé avant de continuer.
     * Bien pour les petits jeux ou pendant un écran de chargement.
     */
    void loadAssets();
    
    /**
     * Démarre le chargement des ressources (de manière asynchrone).
     * 
     * Appeler updateLoading() à chaque frame pour continuer le chargement.
     * Vérifier isLoadingComplete() pour savoir quand c'est fini.
     */
    void loadAssetsAsync();
    
    /**
     * Met à jour le processus de chargement.
     * 
     * À appeler une fois par frame lors du chargement asynchrone.
     * 
     * @return true si le chargement est terminé, false sinon
     */
    boolean updateLoading();
    
    /**
     * Vérifie si le chargement est terminé.
     */
    boolean isLoadingComplete();
    
    /**
     * Récupère la progression du chargement en pourcentage.
     * 
     * @return Progression de 0.0 (0%) à 1.0 (100%)
     */
    float getLoadingProgress();
    
    /**
     * Récupère la texture d'un type de bloc spécifique.
     * 
     * Cette méthode est rapide (recherche instantanée).
     * Peut être appelée à chaque frame sans problème.
     * 
     * @param blockType Le type de bloc
     * @return La texture, ou null si non trouvée
     */
    TextureRegion getBlockTexture(BlockType blockType);
    
    /**
     * Récupère la texture de la grille utilisée pour le sol.
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
