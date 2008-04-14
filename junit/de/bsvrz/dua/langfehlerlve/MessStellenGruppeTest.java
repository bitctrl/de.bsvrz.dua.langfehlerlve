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
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStellenGruppe;

/**
 * Ermoeglicht die statische instanzen der Klasse MessStellenGruppe neustarten.
 * 
 * @author BitCtrl Systems GmbH, Bachraty
 * 
 * @version $Id$
 */
public class MessStellenGruppeTest extends MessStellenGruppe {

	/**
	 * Standardkontruktor.
	 * 
	 * @param msgObjekt
	 *            ein Systemobjekt vom Typ <code>typ.messStellenGruppe</code>
	 * @throws DUAInitialisierungsException
	 *             wenn die Messstellengruppe nicht initialisiert werden konnte
	 */
	protected MessStellenGruppeTest(final SystemObject msgObjekt)
			throws DUAInitialisierungsException {
		super(msgObjekt);
	}

	/**
	 * Setzt den initial-Status der Klasse.
	 */
	public static void reset() {
		sDav = null;
		sysObjMsgObjMap.clear();
	}
}
