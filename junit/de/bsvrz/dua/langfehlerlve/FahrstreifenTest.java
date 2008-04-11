/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007 BitCtrl Systems GmbH 
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


package de.bsvrz.dua.langfehlerlve;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.FahrStreifen;

/**
 * 
 * Ermoeglicht die statische instanzen der Klasse Fahrstreifen neustarten
 * 
 * @author BitCtrl Systems GmbH, Bachraty
 *
 */
public class FahrstreifenTest extends FahrStreifen {

	/**
	 * Standardkontruktor
	 * 
	 * @param fsObjekt ein Systemobjekt vom Typ <code>typ.fahrStreifen</code>
	 * @throws DUAInitialisierungsException wenn der Fahrstreifen nicht 
	 * initialisiert werden konnte
	 */
	private FahrstreifenTest(final SystemObject fsObjekt) throws DUAInitialisierungsException {
		super(fsObjekt);
	}
	
	/**
	 * Setzt den initial-Status der Klasse
	 */
	public static void Reset() {
		sDav = null;
		sysObjFsObjMap.clear();
	}
}
