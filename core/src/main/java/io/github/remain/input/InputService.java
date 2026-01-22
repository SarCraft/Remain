package io.github.remain.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Service de gestion des entrées (clavier, souris, manette).
 * 
 * Ce service permet aux écrans et systèmes de recevoir les événements
 * d'entrée sans dépendre directement de libGDX.
 * 
 * Avantages :
 * - Sépare la gestion des entrées du reste du jeu
 * - Permet d'enregistrer/rejouer les entrées (pour les replays)
 * - Permet de changer les contrôles sans modifier le code du jeu
 * - Testable sans véritables entrées matérielles
 * 
 * Exemple d'utilisation :
 *   InputService input = serviceRegistry.get(InputService.class);
 *   input.registerProcessor(myInputProcessor);
 *   input.unregisterProcessor(myInputProcessor);
 */
public interface InputService {
    
    /**
     * Enregistre un processeur d'entrée pour recevoir les événements.
     * 
     * Plusieurs processeurs peuvent être enregistrés. Les événements sont
     * envoyés dans l'ordre d'enregistrement jusqu'à ce qu'un processeur
     * gère l'événement.
     */
    void registerProcessor(InputProcessor processor);
    
    /**
     * Désenregistre un processeur d'entrée.
     * 
     * À appeler quand le processeur n'est plus nécessaire
     * (par exemple, en quittant un écran).
     */
    void unregisterProcessor(InputProcessor processor);
    
    /**
     * Efface tous les processeurs d'entrée enregistrés.
     * 
     * Utile lors du changement d'écran pour repartir de zéro.
     */
    void clearProcessors();
    
    /**
     * Vérifie si une touche est actuellement enfoncée.
     */
    boolean isKeyPressed(int keycode);
    
    /**
     * Vérifie si un bouton de souris est actuellement enfoncé.
     */
    boolean isButtonPressed(int button);
    
    /**
     * Récupère la position X actuelle de la souris.
     * @return Coordonnée X en pixels
     */
    int getX();
    
    /**
     * Récupère la position Y actuelle de la souris.
     * @return Coordonnée Y en pixels
     */
    int getY();
    
    /**
     * Active ou désactive le traitement des entrées.
     * 
     * Utile pour désactiver temporairement les entrées
     * (par exemple, pendant les cinématiques).
     */
    void setInputEnabled(boolean enabled);
    
    /**
     * Checks if input processing is enabled.
     * @return true if enabled, false otherwise
     */
    boolean isInputEnabled();
}
