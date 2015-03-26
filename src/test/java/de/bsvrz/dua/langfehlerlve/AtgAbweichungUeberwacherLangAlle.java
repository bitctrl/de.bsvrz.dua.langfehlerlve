/*
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.langfehlerlve;

/**
 * Ueberprueft den ersten echten (Nutz-)Wert von
 * <code>atg.abweichungVerkehrsSt�rke</code>,
 * <code>asp.messQuerschnittDerMessStellenGruppeLangZeit</code>.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id: AtgAbweichungUeberwacherLangAlle.java 53825 2015-03-18
 *          09:36:42Z peuker $
 */
class AtgAbweichungUeberwacherLangAlle extends AtgAbweichungUeberwacherKurz {

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getAspPid() {
		return "asp.messQuerschnittDerMessStellenGruppeLangZeit";
	}

}