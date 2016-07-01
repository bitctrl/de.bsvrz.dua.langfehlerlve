/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:<br>
 * BitCtrl Systems GmbH<br>
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.langfehlerlve.modell;

import java.util.HashSet;
import java.util.Set;

/**
 * Alle Fahrzeugarten die innerhalb der SWE 4.DELzFh benoetigt werden.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class FahrzeugArt {

	/**
	 * alle statischen Instanzen dieser Klasse.
	 */
	private static Set<FahrzeugArt> instanzen = new HashSet<FahrzeugArt>();

	/**
	 * <code>Kfz</code>.
	 */
	public static final FahrzeugArt KFZ = new FahrzeugArt("QKfz"); //$NON-NLS-1$

	/**
	 * <code>Pkw</code>.
	 */
	public static final FahrzeugArt PKW = new FahrzeugArt("QPkw"); //$NON-NLS-1$

	/**
	 * <code>Lkw</code>.
	 */
	public static final FahrzeugArt LKW = new FahrzeugArt("QLkw"); //$NON-NLS-1$

	/**
	 * der Name des Attributs Q...
	 */
	private String attributName = null;

	/**
	 * Standardkonstruktor.
	 *
	 * @param attributName
	 *            der Name des Attributs Q... dieser Fahrzeugart in den
	 *            Attributgruppen, mit denen die SWE DELzFh arbeitet
	 */
	private FahrzeugArt(final String attributName) {
		this.attributName = attributName;
		FahrzeugArt.instanzen.add(this);
	}

	/**
	 * Erfragt alle statische Instanzen dieser Klasse.
	 *
	 * @return alle statische Instanzen dieser Klasse
	 */
	public static Set<FahrzeugArt> getInstanzen() {
		return FahrzeugArt.instanzen;
	}

	/**
	 * Erfragt den Namen des Attributs Q... dieser Fahrzeugart
	 *
	 * @return den Namen des Attributs Q... dieser Fahrzeugart
	 */
	public String getAttributName() {
		return this.attributName;
	}

	@Override
	public String toString() {
		return "Attributname: " + this.attributName; //$NON-NLS-1$
	}

}
