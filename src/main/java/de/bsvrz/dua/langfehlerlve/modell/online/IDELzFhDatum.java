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

package de.bsvrz.dua.langfehlerlve.modell.online;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;

/**
 * Schnittstelle zu den MQ-, MS- bzw. sonstigen Daten die fuer die SWE 4.DELzFh
 * DE Langzeit-Fehlererkennung benoetigt werden
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public interface IDELzFhDatum {

	/**
	 * Erfragt das Systemobjekt dieses Datums.
	 * 
	 * @return das Systemobjekt dieses Datums.
	 */
	SystemObject getObjekt();

	/**
	 * Erfragt, ob dieses Datum auf <code>keine Daten</code> steht.
	 * 
	 * @return ob dieses Datum auf <code>keine Daten</code> steht
	 */
	boolean isKeineDaten();

	/**
	 * Indiziert, dass der ueber diese Schnittstelle erfragbare Wert fuer die
	 * uebergebene Fahrzeugart <b>nicht</b> auf einem der folgenden Zustaende
	 * steht:<br>
	 * - <code>nicht ermittelbar</code>,<br>
	 * - <code>fehlerhaft</code>,<br>
	 * - <code>nicht ermittelbar oder fehlerhaft</code>.
	 * 
	 * @param fahrzeugArt
	 *            eine Fahrzeugart
	 * @return ob der ueber diese Schnittstelle erfragbare Wert fuer die
	 *         uebergebene Fahrzeugart auswertbar ist
	 */
	boolean isAuswertbar(FahrzeugArt fahrzeugArt);

	/**
	 * Erfragt einen Q-Wert fuer eine bestimmte Fahrzeugart.
	 * 
	 * @param fahrzeugArt
	 *            eine Fahrzeugart
	 * @return der Wert fuer die uebergebene Fahrzeugart
	 */
	double getQ(FahrzeugArt fahrzeugArt);

}
