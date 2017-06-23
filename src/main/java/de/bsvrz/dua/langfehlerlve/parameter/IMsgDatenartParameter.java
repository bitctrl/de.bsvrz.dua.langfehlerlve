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

package de.bsvrz.dua.langfehlerlve.parameter;

/**
 * Schnittstelle zu den Informationen der Attributgruppe
 * (<code>atg.parameterMessStellenGruppe</code>) fuer sowohl LZ-Vergleich- wie
 * KZ-Vergleichswerte.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public interface IMsgDatenartParameter {

	/**
	 * Erfragt Vergleichsintervall für die Langzeitfehlererkennung von
	 * Verkehrsdaten in ms.
	 * 
	 * @return Vergleichsintervall für die Langzeitfehlererkennung von
	 *         Verkehrsdaten in ms
	 */
	long getVergleichsIntervall();

	/**
	 * Erfragt die maximal zulässige Toleranz für die Abweichung von Messwerten
	 * beim Vergleich mit dem Vorgänger beim Kurzzeitintervall für die
	 * Langzeitfehlererkennung von Verkehrsdaten.
	 * 
	 * @return maximal zulässige Toleranz für die Abweichung von Messwerten beim
	 *         Vergleich mit dem Vorgänger beim Kurzzeitintervall für die
	 *         Langzeitfehlererkennung von Verkehrsdaten
	 */
	int getMaxAbweichungVorgaenger();

	/**
	 * Erfragt die maximal zulässige Toleranz für die Abweichung von Messwerten
	 * beim Vergleich mit den Werten der MessStellenGruppe beim
	 * Kurzzeitintervall für die Langzeitfehlererkennung von Verkehrsdaten.
	 * 
	 * @return maximal zulässige Toleranz für die Abweichung von Messwerten beim
	 *         Vergleich mit den Werten der MessStellenGruppe beim
	 *         Kurzzeitintervall für die Langzeitfehlererkennung von
	 *         Verkehrsdaten
	 */
	int getMaxAbweichungMessStellenGruppe();

}
