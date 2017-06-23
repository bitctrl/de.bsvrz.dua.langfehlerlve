/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.tests.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.tests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import de.kappich.sys.funclib.csv.CsvParseException;
import de.kappich.sys.funclib.csv.CsvReader;
import de.kappich.sys.funclib.csv.IterableCsvData;

/**
 * TBD Dokumentation
 *
 * @author Kappich Systemberatung
 */
public class CSVImporter {

	private final String _csvDateiName;
	private Iterator<IterableCsvData.CsvRow> data;

	/**
	 * Standardkonstruktor
	 *
	 * @param csvDateiName
	 *            Name der CSV-Datei (mit oder ohne Suffix)
	 * @throws Exception
	 *             wenn die Datei nicht geöffnet werden kann
	 */
	public CSVImporter(final String csvDateiName) throws Exception {
		_csvDateiName = csvDateiName;
		InputStream inputStream = getClass().getResourceAsStream("testdata/" + csvDateiName);
		CsvReader csvReader = new CsvReader(Charset.forName("UTF-8"), inputStream, ';', '"');
		data = csvReader.read(null).iterator();
	}

	/**
	 * Gibt alle Spalten einer Zeile der Tabelle als String-Array zurück.
	 *
	 * @return ein String-Array mit den Spalten einer Zeile oder
	 *         <code>null</code>, wenn das Dateiende erreicht ist
	 */
	public final String[] getNaechsteZeile() {
		if (!data.hasNext()) {
			return null;
		}
		try {
			return data.next().asList().toArray(new String[0]);
		} catch (CsvParseException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Setzt den Dateizeiger wieder auf Anfang.
	 */
	public final void reset() {
		InputStream inputStream = getClass().getResourceAsStream("testdata/" + _csvDateiName);
		CsvReader csvReader = new CsvReader(Charset.forName("UTF-8"), inputStream, ';', '"');
		try {
			data = csvReader.read(null).iterator();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
