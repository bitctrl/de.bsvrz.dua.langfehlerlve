/*
 * Segment Datenübernahme und Aufbereitung (DUA), SWE Langzeit-Fehlererkennung LVE
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.
 * 
 * de.bsvrz.dua.langfehlerlve is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.modell;

import java.util.HashSet;
import java.util.Set;

/**
 * Alle Fahrzeugarten die innerhalb der SWE 4.DELzFh benoetigt werden.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
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
	private FahrzeugArt(String attributName) {
		this.attributName = attributName;
		instanzen.add(this);
	}

	/**
	 * Erfragt alle statische Instanzen dieser Klasse.
	 * 
	 * @return alle statische Instanzen dieser Klasse
	 */
	public static Set<FahrzeugArt> getInstanzen() {
		return instanzen;
	}

	/**
	 * Erfragt den Namen des Attributs Q... dieser Fahrzeugart
	 * 
	 * @return den Namen des Attributs Q... dieser Fahrzeugart
	 */
	public String getAttributName() {
		return this.attributName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Attributname: " + this.attributName; //$NON-NLS-1$
	}

}
