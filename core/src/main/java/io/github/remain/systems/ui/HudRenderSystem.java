package io.github.remain.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.remain.service.player.Player;
import io.github.remain.system.GameSystem;

public class HudRenderSystem implements GameSystem {

    private final Player player;
    private final SpriteBatch batch;
    private final OrthographicCamera hudCamera;

    private final Texture hpBack, hpFill;
    private final Texture xpBack, xpFill;

    public HudRenderSystem(Player player, SpriteBatch batch) {
        this.player = player;
        this.batch = batch;

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        hpBack = new Texture("ui/hp_back.png");
        hpFill = new Texture("ui/hp_fill.png");
        xpBack = new Texture("ui/xp_back.png");
        xpFill = new Texture("ui/xp_fill.png");
    }

    @Override
    public void update(float delta) {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();

        drawBar(
                20,
                Gdx.graphics.getHeight() - 30,
                200,
                16,
                player.getHealth(),
                player.getMaxHealth(),
                hpBack,
                hpFill);

        drawBar(
                20,
                Gdx.graphics.getHeight() - 55,
                200,
                12,
                player.getXp(),
                player.getXptoNextLevel(),
                xpBack,
                xpFill);

        batch.end();
    }

    private void drawBar(
            float x, float y, float w, float h,
            float value, float max,
            Texture back, Texture fill) {
        float pct = Math.max(0f, Math.min(1f, value / max));

        batch.draw(back, x, y, w, h);

        if (pct > 0) {
            batch.draw(
                    fill,
                    x, y,
                    w * pct, h,
                    0, 0,
                    (int) (fill.getWidth() * pct),
                    fill.getHeight(),
                    false, false);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public int getPriority() {
        return 100; // 🔥 toujours après le rendu du monde
    }

    public void dispose() {
        hpBack.dispose();
        hpFill.dispose();
        xpBack.dispose();
        xpFill.dispose();
    }
}
