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

import java.util.Date;
import java.util.Objects;

import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Zeitintervall. Zwei Intervalle sind dann gleich, wenn sie den gleichen Anfang
 * und das gleiche Ende besitzen (unabhaengig von den im Intervall gespeicherten
 * Daten)
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class Intervall {

	/**
	 * Intervallbegin (absolute Zeit in ms).
	 */
	private final long start;

	/**
	 * Intervallende (absolute Zeit in ms).
	 */
	private final long ende;

	/**
	 * Das Datum, das zu diesem Intervall gehoert.
	 */
	private IDELzFhDatum datum = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param start
	 *            Intervallbegin (absolute Zeit in ms)
	 * @param ende
	 *            Intervallende (absolute Zeit in ms)
	 * @param datum
	 *            das Datum, das zu diesem Intervall gehoert
	 */
	public Intervall(final long start, final long ende, IDELzFhDatum datum) {
		if (ende < start) {
			throw new RuntimeException("Intervallende (" + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(ende))
					+ ") liegt vor Intervallbegin (" + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(start)) + ")");
		}
		this.start = start;
		this.ende = ende;

		if (datum == null) {
			throw new NullPointerException("Es wurde kein Datum uebergeben");
		}

		this.datum = datum;
	}

	/**
	 * Erfragt Intervallbegin (absolute Zeit in ms).
	 * 
	 * @return Intervallbegin (absolute Zeit in ms)
	 */
	public final long getStart() {
		return this.start;
	}

	/**
	 * Erfragt Intervallende (absolute Zeit in ms).
	 * 
	 * @return Intervallende (absolute Zeit in ms)
	 */
	public final long getEnde() {
		return this.ende;
	}

	/**
	 * Erfragt das Datum, das zu diesem Intervall gehoert.
	 * 
	 * @return das Datum, das zu diesem Intervall gehoert
	 */
	public final IDELzFhDatum getDatum() {
		return this.datum;
	}

	/**
	 * Erfragt, ob der uebergebene Wert im Intervall [begin, ende) liegt.
	 * 
	 * @param wert
	 *            ein Wert
	 * @return ob der uebergebene Wert im Intervall [begin, ende) liegt
	 */
	public final boolean isInIntervall(final long wert) {
		return this.start <= wert && wert < this.ende;
	}

	@Override
	public boolean equals(Object obj) {
		boolean gleich = false;

		if (obj != null && obj instanceof Intervall) {
			Intervall that = (Intervall) obj;
			gleich = this.start == that.start && this.ende == that.ende;
		}

		return gleich;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, ende);
	}

	@Override
	public String toString() {
		String s = "Datum:\n" + this.datum + "\n";

		return "Intervallbegin: " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(start)) + ", Intervallende: "
				+ DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(ende)) + " --> " + s;
	}

}
