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

package de.bsvrz.dua.langfehlerlve;

import de.bsvrz.sys.funclib.bitctrl.dua.test.CSVImporter;

/**
 * Liest die Testdaten ein.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
final class TestDatenImporterPrSpezLang extends TestDatenImporterPrSpezKurz {

	/**
	 * Liest die Tabelle ein.
	 *
	 * @param csvDateiName
	 *            Name der CSV-Datei (mit oder ohne Suffix)
	 * @throws Exception
	 *             wenn die Datei nicht geoeffnet werden kann
	 */
	@Override
	void init(final String csvDateiName) throws Exception {
		final CSVImporter importer = new CSVImporter(csvDateiName);
		importer.getNaechsteZeile();
		knotenpunkteTab = new MSGDaten(importer.getNaechsteZeile());
		for (int i = 0; i < 4; i++) {
			knotenpunkteTab.addEingabe(importer.getNaechsteZeile());
		}
		for (int i = 0; i < 4; i++) {
			importer.getNaechsteZeile();
		}
		knotenpunkteTab.addAusgabe("Q1", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		knotenpunkteTab.addAusgabe("Q2", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		knotenpunkteTab.addAusgabe("Q3", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		knotenpunkteTab.addAusgabe("Q4", importer.getNaechsteZeile());

		importer.reset();
		for (int i = 0; i < 23; i++) {
			importer.getNaechsteZeile();
		}
		freieStreckeTab = new MSGDaten(importer.getNaechsteZeile());
		for (int i = 0; i < 4; i++) {
			freieStreckeTab.addEingabe(importer.getNaechsteZeile());
		}
		for (int i = 0; i < 4; i++) {
			importer.getNaechsteZeile();
		}
		freieStreckeTab.addAusgabe("Q1", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		freieStreckeTab.addAusgabe("Q2", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		freieStreckeTab.addAusgabe("Q3", importer.getNaechsteZeile());
		importer.getNaechsteZeile();
		freieStreckeTab.addAusgabe("Q4", importer.getNaechsteZeile());
	}

}
