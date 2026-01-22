package io.github.remain.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * Gestionnaire d'écrans pour les transitions et le cycle de vie.
 * 
 * Ce service gère le changement d'écrans (menu, jeu, options...)
 * et fournit des fonctionnalités supplémentaires comme :
 * - Le suivi des transitions
 * - L'historique des écrans (pour le bouton retour)
 * - Les effets de transition
 * - Les écrans de chargement
 */
public final class ScreenManager {
    
    private final Game game;
    private Screen currentScreen;
    
    /**
     * Crée un nouveau gestionnaire d'écrans.
     */
    public ScreenManager(Game game) {
        if (game == null) {
            throw new NullPointerException("game cannot be null");
        }
        
        this.game = game;
        
        Gdx.app.log("ScreenManager", "Gestionnaire d'écrans initialisé");
    }
    
    /**
     * Change l'écran actuel avec une transition immédiate.
     * 
     * La méthode hide() de l'écran précédent est appelée,
     * puis la méthode show() du nouvel écran.
     */
    public void setScreen(Screen screen) {
        if (screen == null) {
            Gdx.app.error("ScreenManager", "Impossible de définir un écran null");
            return;
        }
        
        Screen previousScreen = currentScreen;
        String previousName = previousScreen != null ? 
            previousScreen.getClass().getSimpleName() : "aucun";
        String newName = screen.getClass().getSimpleName();
        
        Gdx.app.log("ScreenManager", 
            String.format("Transition de %s vers %s", previousName, newName));
        
        game.setScreen(screen);
        currentScreen = screen;
    }
    
    /**
     * Récupère l'écran actif.
     * @return L'écran actuel, ou null si aucun écran n'est défini
     */
    public Screen getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Ferme l'écran actuel et le met à null.
     * 
     * Utilisez ceci pour nettoyer explicitement un écran sans
     * faire de transition vers un nouvel écran.
     */
    public void disposeCurrentScreen() {
        if (currentScreen != null) {
            Gdx.app.log("ScreenManager", 
                "Fermeture de l'écran : " + currentScreen.getClass().getSimpleName());
            
            currentScreen.dispose();
            currentScreen = null;
        }
    }
}
