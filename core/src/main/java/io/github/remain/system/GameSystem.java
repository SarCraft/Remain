package io.github.remain.system;

/**
 * Interface de base pour tous les systèmes du jeu.
 * 
 * Les systèmes représentent des morceaux de logique de jeu qui sont
 * mis à jour à chaque frame. Ils suivent une architecture ECS
 * (Entity-Component-System).
 * 
 * Avantages :
 * - Sépare les responsabilités (chaque système a un rôle)
 * - Peut activer/désactiver les systèmes indépendamment
 * - Contrôle clair de l'ordre de mise à jour
 * - Testable isolément
 * 
 * Exemple d'utilisation :
 *   public class MonSysteme implements GameSystem {
 *       private boolean enabled = true;
 *       
 *       @Override
 *       public void update(float delta) {
 *           if (!enabled) return;
 *           // Logique de mise à jour ici
 *       }
 *       
 *       @Override
 *       public boolean isEnabled() { return enabled; }
 *       
 *       @Override
 *       public void setEnabled(boolean enabled) { this.enabled = enabled; }
 *   }
 */
public interface GameSystem {
    
    /**
     * Met à jour la logique du système.
     * 
     * Appelée une fois par frame. Le système doit vérifier isEnabled()
     * et s'arrêter immédiatement si désactivé.
     * 
     * @param delta Temps en secondes depuis la dernière frame
     */
    void update(float delta);
    
    /**
     * Vérifie si le système est activé.
     */
    boolean isEnabled();
    
    /**
     * Active ou désactive le système.
     * 
     * Les systèmes désactivés doivent ignorer toute la logique de mise à jour.
     */
    void setEnabled(boolean enabled);
    
    /**
     * Récupère la priorité du système pour l'ordre de mise à jour.
     * 
     * Les nombres plus bas sont mis à jour en premier. Par défaut = 0.
     * Utilisez des nombres négatifs pour les systèmes précoces (entrées),
     * et des nombres positifs pour les systèmes tardifs (rendu).
     */
    default int getPriority() {
        return 0;
    }
}
