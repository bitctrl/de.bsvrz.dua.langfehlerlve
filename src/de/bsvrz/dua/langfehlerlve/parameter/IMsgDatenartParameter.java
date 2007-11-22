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

package de.bsvrz.dua.langfehlerlve.parameter;

/**
 * Schnittstelle zu den Informationen der Attributgruppe
 * (<code>atg.parameterMessStellenGruppe</code>) fuer sowohl LZ-Vergleich- wie
 * KZ-Vergleichswerte 
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public interface IMsgDatenartParameter {

	/**
	 * Erfragt Vergleichsintervall für die Langzeitfehlererkennung von Verkehrsdaten in ms 
	 * 
	 * @return Vergleichsintervall für die Langzeitfehlererkennung von Verkehrsdaten in ms
	 */
	public long getVergleichsIntervall();
	

	/**
	 * Erfragt die maximal zulässige Toleranz für die Abweichung von Messwerten beim Vergleich mit dem
	 * Vorgänger beim Kurzzeitintervall für die Langzeitfehlererkennung von Verkehrsdaten
	 * 
	 * @return maximal zulässige Toleranz für die Abweichung von Messwerten beim Vergleich mit dem
	 * Vorgänger beim Kurzzeitintervall für die Langzeitfehlererkennung von Verkehrsdaten
	 */
	public int getMaxAbweichungVorgaenger();
	
	
	/**
	 * Erfragt die maximal zulässige Toleranz für die Abweichung von Messwerten beim Vergleich mit den
	 * Werten der MessStellenGruppe beim Kurzzeitintervall für die Langzeitfehlererkennung von Verkehrsdaten
	 * 
	 * @return maximal zulässige Toleranz für die Abweichung von Messwerten beim Vergleich mit den
	 * Werten der MessStellenGruppe beim Kurzzeitintervall für die Langzeitfehlererkennung von Verkehrsdaten
	 */
	public int getMaxAbweichungMessStellenGruppe();
	
}
