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

/**
 * Liest die Testdaten ein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
final class DatenImporterPrSpezLang extends DatenImporterPrSpezKurz {

	/**
	 * Liest die Tabelle ein.
	 * 
	 * @param csvDateiName
	 *            Name der CSV-Datei (mit oder ohne Suffix)
	 * @throws Exception
	 *             wenn die Datei nicht geoeffnet werden kann
	 */
	void init(String csvDateiName) throws Exception {
		CSVImporter importer = new CSVImporter(csvDateiName);
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
