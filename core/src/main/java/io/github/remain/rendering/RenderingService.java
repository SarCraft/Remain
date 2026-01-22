package io.github.remain.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Service de gestion du rendu (dessin à l'écran).
 * 
 * Ce service centralise tout ce qui concerne le dessin et fournit
 * des utilitaires pour dessiner efficacement, gérer les caméras,
 * et l'ordre de rendu.
 * 
 * Avantages :
 * - Centralise la gestion du SpriteBatch (begin/end)
 * - Gère les couches de rendu et le tri
 * - Fournit des utilitaires pour les tâches courantes
 * - Abstrait le pipeline de rendu pour les futurs changements
 * 
 * Exemple d'utilisation :
 *   RenderingService rendering = serviceRegistry.get(RenderingService.class);
 *   
 *   // Dessiner le monde
 *   rendering.beginWorld();
 *   // ... dessiner les objets du monde ...
 *   rendering.end();
 *   
 *   // Dessiner l'interface
 *   rendering.beginUi();
 *   // ... dessiner les menus et textes ...
 *   rendering.end();
 */
public interface RenderingService extends Disposable {
    
    /**
     * Démarre le dessin avec la caméra du monde.
     * Doit être suivi d'un appel à end().
     */
    void beginWorld();
    
    /**
     * Démarre le dessin avec la caméra de l'interface.
     * Doit être suivi d'un appel à end().
     */
    void beginUi();
    
    /**
     * Démarre le dessin avec une caméra personnalisée.
     * Doit être suivi d'un appel à end().
     */
    void begin(Camera camera);
    
    /**
     * Termine le dessin et envoie toutes les commandes de dessin au GPU.
     * Doit être appelé après beginWorld(), beginUi() ou begin().
     */
    void end();
    
    /**
     * Récupère le SpriteBatch pour un dessin manuel.
     * 
     * Attention : Utilisez ceci seulement pour des cas avancés.
     * Préférez utiliser les méthodes begin/end.
     */
    SpriteBatch getBatch();
    
    /**
     * Vérifie si on est en train de dessiner (entre begin/end).
     */
    boolean isRendering();
}
