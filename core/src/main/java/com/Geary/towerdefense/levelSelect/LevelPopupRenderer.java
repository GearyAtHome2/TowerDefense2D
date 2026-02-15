package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.world.LevelData;
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
    private final List<LevelData> levels;
    private LevelData hoveredLevel;
    private final Rectangle startButtonBounds = new Rectangle();
    private float popupScale = 1f;

    public LevelPopupRenderer(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font,
                              OrthographicCamera camera, List<LevelData> levels){
        this.batch=batch; this.shapeRenderer=shapeRenderer; this.font=font; this.camera=camera; this.levels=levels;
    }

    public void updateHoveredLevel(LevelData hovered){ this.hoveredLevel=hovered; }

    public void drawPopup(LevelData hovered){
        if(hovered==null){ startButtonBounds.set(0,0,0,0); return; }

        int index = levels.indexOf(hovered);
        float wx = (LevelGridGenerator.GRID_WIDTH/2 + index*3)*LevelGridGenerator.CELL_SIZE;
        float wy = (LevelGridGenerator.GRID_HEIGHT/2)*LevelGridGenerator.CELL_SIZE;

        float scale = getPopupScale();
        float pad = 8f*scale, rowHeight=22f*scale;
        List<String> lines=new ArrayList<>();
        lines.add(hovered.getDisplayName()); lines.add(""); lines.add("Resources:");
        hovered.getResourceAllocation().forEach((t,a)->lines.add(t.name()+": "+a));

        GlyphLayout layout=new GlyphLayout();
        float widest=0f;
        for(String line:lines){ layout.setText(font,line); widest=Math.max(widest, layout.width*scale); }

        float width = widest+pad*2, height=lines.size()*rowHeight + pad*3 + (20f*scale);
        float halfW=camera.viewportWidth*camera.zoom*0.5f, halfH=camera.viewportHeight*camera.zoom*0.5f;
        float x=MathUtils.clamp(wx+60f,camera.position.x-halfW+5f,camera.position.x+halfW-width-5f);
        float y= MathUtils.clamp(wy+60f,camera.position.y-halfH+5f,camera.position.y+halfH-height-5f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f,0f,0f,0.85f); shapeRenderer.rect(x,y,width,height);
        shapeRenderer.end();

        batch.begin();
        float origX=font.getData().scaleX, origY=font.getData().scaleY;
        font.getData().setScale(origX*scale,origY*scale);
        float cursorY = y+height-pad;
        for(String line:lines){ font.draw(batch,line,x+pad,cursorY); cursorY-=rowHeight; }
        font.getData().setScale(origX,origY);
        batch.end();

        float btnH = 20f*scale, btnW=width-pad*2, btnX=x+pad, btnY=y+pad;
        startButtonBounds.set(btnX,btnY,btnW,btnH);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f,0.6f,0.2f,1f); shapeRenderer.rect(btnX,btnY,btnW,btnH);
        shapeRenderer.end();

        batch.begin(); font.draw(batch,"Start",btnX+4*scale,btnY+btnH-4*scale); batch.end();
    }

    private float getPopupScale(){
        float target = 1f + (camera.zoom-1f)*0.65f;
        popupScale = MathUtils.lerp(popupScale, MathUtils.clamp(target,0.5f,3f),0.1f);
        return popupScale;
    }
}
