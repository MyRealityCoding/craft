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

package de.bitbrain.craft.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.google.inject.Inject;

import de.bitbrain.craft.Assets;
import de.bitbrain.craft.SharedAssetManager;
import de.bitbrain.craft.Sizes;
import de.bitbrain.craft.Styles;
import de.bitbrain.craft.audio.SoundUtils;
import de.bitbrain.craft.core.Icon;
import de.bitbrain.craft.core.IconManager;
import de.bitbrain.craft.core.IconManager.IconDrawable;
import de.bitbrain.craft.inject.StateScoped;

/**
 * Responsive tab view which can be extendable
 *
 * @author Miguel Gonzalez <miguel-gonzalez@gmx.de>
 * @since 1.0
 * @version 1.0
 */
@StateScoped
public class TabView extends Table {
	
	public static final int TAB_SIZE = 70;
	
	private Cell<Container<Actor>> left;
	
	private Cell<Group> right;
	
	private Group tabGroup;
	
	private Map<String, Tab> tabs;
	
	private String activeTabId = "";
	
	@Inject
	private IconManager iconManager;
	
	public TabView() {
		tabs = new HashMap<String, Tab>();
		generateLeft();
		generateRight();
		bottom();
		left();
	}
	
	/**
	 * Switches to an existing tab. Does nothing if tab can not be found.
	 *  
	 * @param tab
	 */
	public void setTab(String tab) {
		if (tabs.containsKey(tab) && !tab.equals(activeTabId)) {			
			if (!activeTabId.isEmpty()) {
				Tab oldTab = tabs.get(activeTabId);
				oldTab.setActive(false);
			}			
			Tab newTab = tabs.get(tab);
			left.getActor().setActor(newTab.getContent());
			newTab.getContent().setWidth(left.getActorWidth());
			activeTabId = tab;
		}
	}
	
	/**
	 * Registers a new tab with the given icon and the target actor
	 * 
	 * @param id tab id
	 * @param icon tab icon to show
	 * @param actor Content actor
	 */
	public void addTab(String id, Icon icon, Actor content) {
		if (!id.isEmpty() && !tabs.containsKey(id)) {
			final Tab tab = new Tab(id, content, generateTabStyle(icon, false), generateTabStyle(icon, true));
			tabs.put(id,  tab);
			tabGroup.addActor(tab);
			setTab(id);
			tab.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if (activeTabId != tab.getId()) {
						SoundUtils.play(Assets.SND_TAB, 0.7f, 0.9f);
					}
					setTab(tab.getId());
				}
			});
		}
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		left.width(width - TAB_SIZE);
		left.getActor().setWidth(width - TAB_SIZE);
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		left.height(height);
		left.getActor().setHeight(height);
		right.height(height);
		right.getActor().setHeight(height);
	}
	
	private void generateLeft() {
		Container<Actor> c = new Container();
		c.setBackground(new NinePatchDrawable(Styles.ninePatch(Assets.TEX_PANEL_9patch, Sizes.panelRadius())));
		left = add(c);

	}
	
	private void generateRight() {
		tabGroup = new VerticalGroup();
		tabGroup.setWidth(TAB_SIZE);
		right = add(tabGroup);
	}
	
	private ImageButtonStyle generateTabStyle(Icon icon, boolean active) {
		ImageButtonStyle origin = Styles.BTN_TAB;
		if (active) {
			origin = Styles.BTN_TAB_ACTIVE;
		}
		ImageButtonStyle style = new ImageButtonStyle(origin);
		IconDrawable iconDrawable = iconManager.fetch(icon);
		iconDrawable.setOffsetX(-Sizes.panelRadius() - 1f);
		iconDrawable.color.a = 0.8f;
		style.imageUp = iconDrawable;
		style.imageOver = iconDrawable;
		style.imageUp.setMinHeight(70);
		style.imageUp.setMinWidth(70);
		style.imageOver.setMinHeight(70);
		style.imageOver.setMinWidth(70);
		return style;
	}
	
	public static class Tab extends ImageButton {
		
		private static final float OFFSET = Sizes.panelRadius() - 1f;
		
		private Actor content;
		
		private ImageButtonStyle otherStyle;
		
		private String id;

		public Tab(String id, Actor content, ImageButtonStyle style, ImageButtonStyle active) {
			super(style);
			this.id = id;
			this.content = content;
			otherStyle = active;
		}
		
		@Override
		public Image getImage() {
			Image img = super.getImage();
			return img;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public float getX() {
			return super.getX() - OFFSET;
		}
		
		public void setActive(boolean active) {
			
		}
		
		public Actor getContent() {
			return content;
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.setColor(1f, 1f, 1f, parentAlpha);
			Texture texture = SharedAssetManager.get(Assets.TEX_TAB_GRADIENT, Texture.class);
			batch.draw(texture, getX() + OFFSET - 1, getY(), getWidth() - OFFSET + 1, getHeight() - 1);
		}
		
	}
}