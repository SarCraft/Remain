package io.github.remain;

import io.github.remain.game.GameApplication;

/**
 * Point d'entrée historique - redirige vers GameApplication.
 * 
 * Cette classe est conservée pour la compatibilité avec le lanceur existant.
 * La vraie logique du jeu est maintenant dans GameApplication.
 */
@Deprecated(since = "1.0", forRemoval = false)
public class Main extends GameApplication {
    // Hérite tout le comportement de GameApplication
}
