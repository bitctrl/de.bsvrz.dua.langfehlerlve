/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
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
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.langfehlerlve.modell.online;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	private long start = -1;

	/**
	 * Intervallende (absolute Zeit in ms).
	 */
	private long ende = -1;

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
	public Intervall(final long start, final long ende, final IDELzFhDatum datum) {
		if (ende < start) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					DUAKonstanten.ZEIT_FORMAT_GENAU_STR);
			throw new RuntimeException("Intervallende (" + //$NON-NLS-1$
					dateFormat.format(new Date(ende))
					+ ") liegt vor Intervallbegin (" + //$NON-NLS-1$
					dateFormat.format(new Date(start)) + ")"); //$NON-NLS-1$
		}
		this.start = start;
		this.ende = ende;

		if (datum == null) {
			throw new NullPointerException("Es wurde kein Datum uebergeben"); //$NON-NLS-1$
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
		return (this.start <= wert) && (wert < this.ende);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean gleich = false;

		if ((obj != null) && (obj instanceof Intervall)) {
			final Intervall that = (Intervall) obj;
			gleich = (this.start == that.start) && (this.ende == that.ende);
		}

		return gleich;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String s = "Datum:\n" + this.datum + "\n";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				DUAKonstanten.ZEIT_FORMAT_GENAU_STR);

		return "Intervallbegin: " + //$NON-NLS-1$
		dateFormat.format(new Date(start)) + ", Intervallende: " + //$NON-NLS-1$
		dateFormat.format(new Date(ende)) + " --> " + s; //$NON-NLS-1$
	}

}
