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

/**
 * Hoert auf Aktualisierungen der DELzFh-Intervalldaten eines Messquerschnitts
 * bzw. einer Messstelle. Dies sind:<br>
 *  - <code>QKfz(MessQuerschnitt)</code><br> -
 * <code>QLkw(MessQuerschnitt)</code><br> -
 * <code>QPkw(MessQuerschnitt)</code><br>
 * 
 * berechnet analog DUA-BW-C1C2-2
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public interface IDELzFhDatenListener {

	/**
	 * Aktualisiert die DELzFh-Intervalldaten eines Messquerschnitts bzw. einer
	 * Messstelle immer wenn ein Intervall als abgeschlossen wargenommen wurde
	 * bzw. wenn keine Daten empfangen wurden
	 * 
	 * @param objekt
	 *            ein Systemobjekt eines Messquerschnitts fuer das die Daten
	 *            sind
	 * @param intervallDatum
	 *            ein Intervalldatum <code>!= null</code>
	 */
	void aktualisiereDatum(SystemObject objekt, Intervall intervallDatum);

}
