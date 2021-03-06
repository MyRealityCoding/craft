/*
 * Craft - Crafting game for Android, PC and Browser.
 * Copyright (C) 2014 Miguel Gonzalez
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package de.bitbrain.craft.graphics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.bitbrain.craft.Assets;
import de.bitbrain.craft.inject.StateScoped;
import de.bitbrain.craft.util.Fadeable;

/**
 * Handles icon management and loads them on time
 *
 * @author Miguel Gonzalez <miguel-gonzalez@gmx.de>
 * @since 1.0
 * @version 1.0
 */
@StateScoped
public class IconManager implements Fadeable {

  public static final int BUFFER = 10;

  private static Sprite loadingSprite;

  private Map<String, IconDrawable> icons;
  private Map<String, Integer> references;
  private Map<String, Texture> textures;

  private Queue<Icon> requests;

  private float alpha = 1.0f;

  public IconManager() {
    icons = new HashMap<String, IconDrawable>();
    references = new HashMap<String, Integer>();
    textures = new HashMap<String, Texture>();
    requests = new LinkedList<Icon>();
  }

  public void update() {
    for (int i = 0; i < BUFFER; ++i) {

      if (requests.isEmpty()) {
        break;
      }

      loadIcon(requests.poll());
    }
  }

  public IconDrawable fetch(Icon icon) {
    String file = icon.getFile();
    if (icons.containsKey(file)) {
      return new IconDrawable(icons.get(file));
    } else {
      references.put(file, 1);
      requests.add(icon);
      IconDrawable iconDrawable = new IconDrawable();
      icons.put(file, iconDrawable);
      return new IconDrawable(iconDrawable);
    }
  }

  public void free(String file) {
    if (references.containsKey(file)) {
      if (references.get(file) > 1) {
        references.put(file, references.get(file) - 1);
      } else {
        references.remove(file);
        textures.get(file).dispose();
        textures.remove(file);
        icons.get(file).setTexture(null);
      }
    }
  }

  @Override
  public void setAlpha(float alpha) {
    for (IconDrawable icon : icons.values()) {
      icon.color.a = alpha;
    }
    this.alpha = alpha;
  }

  @Override
  public float getAlpha() {
    return alpha;

  }

  public void dispose() {

    for (Texture t : textures.values()) {
      t.dispose();
    }

    references.clear();
    icons.clear();
    textures.clear();
    requests.clear();
  }

  private void loadIcon(Icon icon) {
    String file = icon.getFile();
    if (!textures.containsKey(file)) {
      try {
        Texture texture = new Texture(Gdx.files.internal(Assets.DIR_ICONS + file));
        textures.put(file, texture);
        icons.get(file).setTexture(texture);
      } catch (GdxRuntimeException ex) {
        Gdx.app.error("EXCEPTION", ex.getMessage());
      }
    }
  }

  private static class TextureLoader {

    private Sprite sprite;

    public Sprite getSprite() {
      if (sprite == null) {
        setTexture(null);
      }
      return sprite;

    }

    void setTexture(Texture texture) {
      if (texture != null) {
        sprite = new Sprite(texture);
        sprite.flip(false, true);
      } else {
        if (loadingSprite == null) {
          loadingSprite = new Sprite(new Texture(Gdx.files.internal("images/icons/ico_loading.png")));
          loadingSprite.flip(false, true);
        }
        sprite = loadingSprite;
      }
    }
  }

  public static class IconDrawable extends BaseDrawable implements TransformDrawable {

    public float scale = 1.0f;
    public float x, y, width, height, offsetX, offsetY;
    public float rotation;
    public Color color = new Color(Color.WHITE);
    private TextureLoader loader;

    IconDrawable() {
      setTexture(null);
      loader = new TextureLoader();
    }

    public IconDrawable(IconDrawable iconDrawable) {
      this();
      this.scale = iconDrawable.scale;
      this.x = iconDrawable.x;
      this.y = iconDrawable.y;
      this.rotation = iconDrawable.rotation;
      this.width = iconDrawable.width;
      this.height = iconDrawable.height;
      this.offsetX = iconDrawable.offsetX;
      this.offsetY = iconDrawable.offsetY;
      this.loader = iconDrawable.loader;
      this.color = iconDrawable.color;
    }

    void setTexture(Texture texture) {
      if (loader != null) {
        loader.setTexture(texture);
      }
    }

    public void setOffsetX(float x) {
      offsetX = x;
    }

    public void setoffsetY(float y) {
      offsetY = y;
    }

    public Texture getTexture() {
      return loader.getSprite().getTexture();
    }

    public void draw(Batch batch, float alphaModulation) {
      loader.getSprite().setScale(scale);
      loader.getSprite().setBounds(x + offsetX, y + offsetY, width, height);
      loader.getSprite().setColor(color);
      loader.getSprite().setOrigin(width / 2f, height / 2f);
      loader.getSprite().setRotation(rotation);
      loader.getSprite().draw(batch, alphaModulation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable#draw(com. badlogic.gdx.graphics.g2d.Batch, float,
     * float, float, float, float, float, float, float, float)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height,
        float scaleX, float scaleY, float rotation) {
      x += offsetX;
      y += offsetY;
      loader.getSprite().setOrigin(originX, originY);
      loader.getSprite().setRotation(rotation);
      loader.getSprite().setScale(scaleX, scaleY);
      loader.getSprite().setBounds(x, y, width, height);
      Color color = loader.sprite.getColor();
      loader.getSprite().setColor(Color.tmp.set(color).mul(batch.getColor()));
      loader.getSprite().draw(batch);
      loader.getSprite().setColor(color);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable#draw(com.badlogic .gdx.graphics.g2d.Batch, float, float,
     * float, float)
     */
    @Override
    public void draw(Batch batch, float x, float y, float w, float h) {
      if (loader.getSprite().isFlipY()) {
        loader.getSprite().flip(false, true);
      }
      x += offsetX;
      y += offsetY;
      super.draw(batch, x, y, w, h);
      loader.getSprite().setScale(scale);
      loader.getSprite().setBounds(x, y, w, h);
      loader.getSprite().setColor(color);
      loader.getSprite().setOrigin(w / 2f, h / 2f);
      loader.getSprite().setRotation(rotation);
      loader.getSprite().draw(batch, color.a);
    }
  }
}