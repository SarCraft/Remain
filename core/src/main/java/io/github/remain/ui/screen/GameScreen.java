package io.github.remain.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import io.github.remain.core.GameContext;
import io.github.remain.core.ServiceRegistry;
import io.github.remain.domain.world.World;
import io.github.remain.service.asset.AssetService;
import io.github.remain.service.input.InputService;
import io.github.remain.service.player.Player;
import io.github.remain.service.rendering.RenderingService;
import io.github.remain.system.isometric.IsometricProjection;
import io.github.remain.systems.CameraControlSystem;
import io.github.remain.systems.IsometricRenderSystem;
import io.github.remain.systems.TileSelectionRenderer;
import io.github.remain.systems.TileSelectionSystem;
import io.github.remain.ui.DebugOverlay;

public final class GameScreen extends BaseScreen {

    private final AssetService assetService;
    private final InputService inputService;
    private final RenderingService renderingService;

    private World world;
    private DebugOverlay debugOverlay;

    private CameraControlSystem cameraControlSystem;
    private IsometricRenderSystem renderSystem;
    private TileSelectionSystem tileSelectionSystem;
    private TileSelectionRenderer selectionRenderer;

    // ✅ Player + HUD
    private Player player;
    private Texture hpBack, hpFill;
    private Texture xpBack, xpFill;

    public GameScreen(ServiceRegistry services, GameContext context) {
        super(services, context);

        this.assetService = services.get(AssetService.class);
        this.inputService = services.get(InputService.class);
        this.renderingService = services.get(RenderingService.class);
    }

    @Override
    protected void onShow() {
        Gdx.app.log("GameScreen", "Initializing game screen...");

        assetService.loadAssets();

        debugOverlay = new DebugOverlay();

        world = new World(25, 25, 8, 12345L);

        cameraControlSystem = new CameraControlSystem(inputService, context.worldCamera());
        renderSystem = new IsometricRenderSystem(assetService, world);
        tileSelectionSystem = new TileSelectionSystem(world);
        selectionRenderer = new TileSelectionRenderer();

        // Center camera on world
        float centerX = (world.getWidth() * IsometricProjection.getGridWidth()) / 2f;
        float centerY = (world.getDepth() * IsometricProjection.getGridHeight()) / 2f + 100;
        context.worldCamera().position.set(centerX, centerY, 0);
        context.worldCamera().update();

        // ✅ Player + HUD textures
        player = new Player();

        // Tes PNG doivent être dans: Remain/assets/ui/...
        hpBack = new Texture("ui/hp_back.png");
        hpFill = new Texture("ui/hp_fill.png");
        xpBack = new Texture("ui/xp_back.png");
        xpFill = new Texture("ui/xp_fill.png");

        inputService.registerProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cameraControlSystem.handleZoom(amountY);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.F3) {
                    debugOverlay.toggle();
                    return true;
                }
                if (keycode == Input.Keys.Q) {
                    cameraControlSystem.rotateCounterClockwise();
                    return true;
                } else if (keycode == Input.Keys.E) {
                    cameraControlSystem.rotateClockwise();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    boolean selected = tileSelectionSystem.handleClick(
                            screenX, screenY, context.worldCamera()
                    );

                    if (selected) {
                        Gdx.app.log("TileSelection", String.format(
                                "Selected tile: X=%d Y=%d Z=%d",
                                tileSelectionSystem.getSelectedX(),
                                tileSelectionSystem.getSelectedY(),
                                tileSelectionSystem.getSelectedZ()
                        ));
                    }

                    return true;
                }
                return false;
            }
        });

        Gdx.app.log("GameScreen", "Game screen initialized");
    }

    @Override
    protected void update(float delta) {
        cameraControlSystem.update(delta);
        selectionRenderer.update(delta);
    }

    @Override
    protected void renderFrame(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderWorld();
        renderUI();
    }

    private void renderWorld() {
        renderingService.beginWorld();

        renderSystem.render(renderingService.getBatch());

        renderingService.end();

        selectionRenderer.render(
                renderingService.getBatch(),
                tileSelectionSystem.getSelectedBlock()
        );
    }

    private void renderUI() {
        renderingService.beginUi();

        debugOverlay.render(
                renderingService.getBatch(),
                context.worldCamera(),
                world,
                context.uiViewport().getWorldHeight(),
                tileSelectionSystem.getSelectedX(),
                tileSelectionSystem.getSelectedY(),
                tileSelectionSystem.getSelectedZ()
        );

        // ✅ HUD responsive (UI viewport = pixels avec ScreenViewport)
        if (player != null && hpBack != null && hpFill != null && xpBack != null && xpFill != null) {
            float uiW = context.uiViewport().getWorldWidth();
            float uiH = context.uiViewport().getWorldHeight();

            // Taille en % de l'écran (donc jamais minuscule)
            float margin = uiW * 0.012f;
            float barW = uiW * 0.18f;     // 22% largeur écran

            // Hauteur basée sur le ratio du PNG (évite l'écrasement)
            float hpH = barW * ((float) hpBack.getHeight() / (float) hpBack.getWidth());
            float xpH = barW * ((float) xpBack.getHeight() / (float) xpBack.getWidth());

            float x = margin;
            float yTop = uiH - margin;

            drawBar(x, yTop - hpH, barW, hpH,
                    player.getHealth(), player.getMaxHealth(),
                    hpBack, hpFill);

            float gap = uiH * 0.01f;
            drawBar(x, yTop - hpH - gap - xpH, barW, xpH,
                    player.getXp(), player.getXptoNextLevel(),
                    xpBack, xpFill);
        }

        renderingService.end();
    }

    private void drawBar(
            float x, float y,
            float width, float height,
            float value, float max,
            Texture back, Texture fill
    ) {
        if (max <= 0f) return;

        float pct = value / max;
        if (pct < 0f) pct = 0f;
        if (pct > 1f) pct = 1f;

        renderingService.getBatch().draw(back, x, y, width, height);

        int srcW = (int) (fill.getWidth() * pct);
        if (srcW > 0) {
            renderingService.getBatch().draw(
                    fill,
                    x, y,
                    width * pct, height,
                    0, 0,
                    srcW, fill.getHeight(),
                    false, false
            );
        }
    }

    @Override
    protected void onHide() {
        inputService.clearProcessors();
    }

    @Override
    protected void onDispose() {
        if (debugOverlay != null) debugOverlay.dispose();
        if (selectionRenderer != null) selectionRenderer.dispose();

        if (hpBack != null) hpBack.dispose();
        if (hpFill != null) hpFill.dispose();
        if (xpBack != null) xpBack.dispose();
        if (xpFill != null) xpFill.dispose();
    }
}
