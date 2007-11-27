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

package de.bsvrz.dua.langfehlerlve.modell.online;

import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;

/**
 * Schnittstelle zu den MQ- bzw. MS-Daten, die fuer die SWE 4.DELzFh
 * DE Langzeit-Fehlererkennung benoetigt werden
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public interface IDELzFhDatum{

	/**
	 * Erfragt, ob dieses Datum auf <code>keine Daten</code> steht
	 * 
	 * @return ob dieses Datum auf <code>keine Daten</code> steht
	 */
	public boolean isKeineDaten();
	
	/**
	 * Erfragt einen Q-Wert fuer eine bestimmte Fahrzeugart
	 * 
	 * @param fahrzeugArt eine Fahrzeugart
	 * @return der Wert fuer die uebergebene Fahrzeugart
	 */
	public double getQ(FahrzeugArt fahrzeugArt);
	
}
