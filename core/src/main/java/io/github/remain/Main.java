package io.github.remain;

import io.github.remain.core.GameApplication;

/**
 * Legacy main entry point - redirects to {@link GameApplication}.
 * This class is kept for backwards compatibility with the existing launcher.
 * The actual application logic is now in {@link GameApplication}.
 * @deprecated Use {@link GameApplication} directly
 * @author SarCraft
 * @since 1.0
 */
@Deprecated(since = "1.0", forRemoval = false)
public class Main extends GameApplication {
    // Inherits all behavior from GameApplication
}
