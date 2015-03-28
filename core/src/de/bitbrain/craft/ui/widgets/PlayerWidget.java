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

package de.bitbrain.craft.ui.widgets;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;

import de.bitbrain.craft.Assets;
import de.bitbrain.craft.SharedAssetManager;
import de.bitbrain.craft.Sizes;
import de.bitbrain.craft.events.EventBus;
import de.bitbrain.craft.graphics.GraphicsFactory;
import de.bitbrain.craft.inject.PostConstruct;
import de.bitbrain.craft.inject.StateScoped;
import de.bitbrain.craft.models.Player;
import de.bitbrain.craft.models.Profession;
import de.bitbrain.craft.tweens.FadeableTween;
import de.bitbrain.craft.util.DirectPlayerDataProvider;
import de.bitbrain.craft.util.Fadeable;
import de.bitbrain.craft.util.PlayerDataProvider;
import de.bitbrain.craft.util.ValueProvider;

/**
 * Shows information of a player like level and experience
 * 
 * @author Miguel Gonzalez <miguel-gonzalez@gmx.de>
 * @since 1.0
 * @version 1.0
 */
@StateScoped
public class PlayerWidget extends Actor implements Fadeable {

	@Inject
	private EventBus eventBus;

	@Inject
	private TweenManager tweenManager;

	private NinePatch background;

	private BitmapFont caption;

	private PlayerDataProvider playerData;
	
	private ExperienceProvider expData;
	
	private float progress;

	@PostConstruct
	public void initView() {
		expData = new ExperienceProvider();
		playerData = new DirectPlayerDataProvider(Player.getCurrent().getId());
		progress = 0.4f; // TODO: playerData.getProgress(Profession.current);
		background = GraphicsFactory.createNinePatch(Assets.TEX_PANEL_BAR_9patch,
				5);
		caption = SharedAssetManager.get(Assets.FNT_SMALL, BitmapFont.class);
		getColor().a = 0f;
		Tween.to(this, FadeableTween.DEFAULT, 5f).target(0.95f)
				.ease(TweenEquations.easeOutCubic).start(tweenManager);
		Tween.to(this, FadeableTween.DEFAULT, 2f).target(0.5f).delay(1f)
				.repeatYoyo(Tween.INFINITY, 0f).ease(TweenEquations.easeInOutCubic)
				.start(tweenManager);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		drawBackground(batch);
		drawProgress(batch);
		drawForeground(batch);
	}
	
	private void drawBackground(Batch batch) {
		getColor().a -= 0.3f;
		background.setColor(getColor());
		background.draw(batch, getX(), getY(), getWidth(), getHeight());
		getColor().a += 0.3f;
	}
	
	private void drawProgress(Batch batch) {
		if (progress > 0.1f) {
			float percentage = progress;
			background.draw(batch, getX(), getY(), getWidth() * percentage, getHeight());
		}
	}
	
	private void drawForeground(Batch batch) {
		String text = getText();
		caption.setColor(getColor());
		caption.draw(batch, text,
				getX() + getWidth() / 2f - caption.getBounds(text).width
						/ 2f,
				getY() + getHeight() / 2f + caption.getLineHeight() / 2f);
	}

	@Override
	public float getAlpha() {
		return getColor().a;
	}

	@Override
	public void setAlpha(float alpha) {
		getColor().a = alpha;
	}

	private String getText() {
		String text = "Level ";
		text += playerData.getLevel(Profession.current) + " ";
		text += Profession.current.getName();
		return text;
	}
	
	private class ExperienceProvider implements ValueProvider {
		
		public int xp;

		@Override
		public int getValue() {
			return xp;
		}

		@Override
		public void setValue(int value) {
			this.xp = value;
		}
		
	}
}
