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

package de.bitbrain.craft.events;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;

import com.badlogic.gdx.Gdx;

import de.bitbrain.craft.events.Event.MessageType;
import de.bitbrain.craft.util.Identifiable;

/**
 * Event bus implementation
 *
 * @author Miguel Gonzalez <miguel-gonzalez@gmx.de>
 * @since 1.0
 * @version 1.0
 */
public final class MBassadorEventBus implements EventBus {

	@SuppressWarnings("deprecation")
	private MBassador<Event<?> > bus = new MBassador<Event<?> >(BusConfiguration.SyncAsync());	
	
	public MBassadorEventBus() { }
	
	@Override
	public void subscribe(Object obj) {
		bus.subscribe(obj);
	}
	
	@Override	
	public void unsubscribe(Object obj) {
		bus.unsubscribe(obj);
	}
	
	@Override
	public <T extends Identifiable> void fireElementEvent(MessageType type, T item, int amount) {
		bus.publish(new ElementEvent<T>(type, item, amount));
	}

	/* (non-Javadoc)
	 * @see de.bitbrain.craft.events.EventBus#fireEvent(de.bitbrain.craft.events.EventMessage.MessageType, java.lang.Object)
	 */
	@Override
	public <T> void fireEvent(MessageType type, T item) {
		bus.publish(new Event<T>(type, item));
	}

	/* (non-Javadoc)
	 * @see de.bitbrain.craft.events.EventBus#fireMouseEvent(de.bitbrain.craft.events.Event.MessageType, java.lang.Object, float, float)
	 */
	@Override
	public <T> void fireMouseEvent(MessageType type, T item, float x, float y) {
		bus.publish(new MouseEvent<T>(type, item, x, y));
	}

	/* (non-Javadoc)
	 * @see de.bitbrain.craft.events.EventBus#fireKeyEvent(de.bitbrain.craft.events.Event.MessageType, java.lang.Object, int)
	 */
	@Override
	public void fireKeyEvent(MessageType type, int key) {
		bus.publish(new KeyEvent(type, Gdx.input, key));
	}
}
