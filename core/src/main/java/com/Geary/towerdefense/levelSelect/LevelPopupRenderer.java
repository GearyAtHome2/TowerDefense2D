package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.generation.LevelGridGenerator;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class LevelPopupRenderer {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final List<LevelGridCell> levels;
    private LevelData hoveredLevelData;
    private final Rectangle startButtonBounds = new Rectangle();
    private float popupScale = 1f;

    public LevelPopupRenderer(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font,
                              OrthographicCamera camera, List<LevelGridCell> levels) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.camera = camera;
        this.levels = levels;
    }

    public void updateHoveredLevel(LevelData hovered) {
        this.hoveredLevelData = hovered;
    }

    public void drawPopup(LevelGridCell hoveredCell) {
        System.out.println("drawpopup called:" + hoveredCell);
        if (hoveredCell != null) {
            System.out.println("hovered cell is level? " + hoveredCell.isLevel());
            System.out.println("hovered cell has parent? " + hoveredCell.getParentLevelCell());
        }
        if (hoveredCell == null || hoveredCell.getParentLevelCell() == null) {
            startButtonBounds.set(0, 0, 0, 0);
            return;
        }
        if (hoveredCell.getParentLevelCell() != null) {
            hoveredCell = hoveredCell.getParentLevelCell();
        }

        LevelData hovered = hoveredCell.levelData;

        // compute world position from cell indices
        float wx = hoveredCell.xIndex * LevelGridGenerator.CELL_SIZE;
        float wy = hoveredCell.yIndex * LevelGridGenerator.CELL_SIZE;

        float scale = getPopupScale();
        float pad = 8f * scale, rowHeight = 22f * scale;
        List<String> lines = new ArrayList<>();
        lines.add(hovered.getDisplayName());
        lines.add("Order: " + hovered.getPrimaryOrder());
        Entity.Order secondaryOrder = hovered.getSecondaryOrder();
        if (secondaryOrder != Entity.Order.NEUTRAL) {
            lines.add("Secondary Order: " + secondaryOrder);
        }
        lines.add("");
        lines.add("Resources:");
        hovered.getResourceAllocation().forEach((t, a) -> lines.add(t.name() + ": " + a));

        GlyphLayout layout = new GlyphLayout();
        float widest = 0f;
        for (String line : lines) {
            layout.setText(font, line);
            widest = Math.max(widest, layout.width * scale);
        }

        float width = widest + pad * 2, height = lines.size() * rowHeight + pad * 3 + (20f * scale);
        float halfW = camera.viewportWidth * camera.zoom * 0.5f, halfH = camera.viewportHeight * camera.zoom * 0.5f;
        //the 13f is cellsize*1.3 in this case - can use static var eventually maybe
        float x = MathUtils.clamp(wx + LevelGridGenerator.CELL_SIZE * 2, camera.position.x - halfW + 5f, camera.position.x + halfW - width - 5f);
        float y = MathUtils.clamp(wy + LevelGridGenerator.CELL_SIZE * 2, camera.position.y - halfH + 5f, camera.position.y + halfH - height - 5f);

        // background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.85f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        // draw text
        batch.begin();
        float origX = font.getData().scaleX, origY = font.getData().scaleY;
        font.getData().setScale(origX * scale, origY * scale);
        float cursorY = y + height - pad;
        for (String line : lines) {
            font.draw(batch, line, x + pad, cursorY);
            cursorY -= rowHeight;
        }
        font.getData().setScale(origX, origY);
        batch.end();

        // start button
        float btnH = 20f * scale, btnW = width - pad * 2, btnX = x + pad, btnY = y + pad;
        startButtonBounds.set(btnX, btnY, btnW, btnH);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1f);
        shapeRenderer.rect(btnX, btnY, btnW, btnH);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Start", btnX + 4 * scale, btnY + btnH - 4 * scale);
        batch.end();
    }

    private float getPopupScale() {
        float target = 1f + (camera.zoom - 1f) * 0.65f;
        popupScale = MathUtils.lerp(popupScale, MathUtils.clamp(target, 0.5f, 3f), 0.1f);
        return popupScale;
    }
}
