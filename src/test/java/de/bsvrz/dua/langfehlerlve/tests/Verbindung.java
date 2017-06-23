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
 * Haelt alle Startparameter, die zur Durchfuehrung des Tests notwendig sind
 * (Datenverteiler-Verbindung, Basisverzeichnis fuer Quelldaten).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class Verbindung {

	/**
	 * Konfigurationsbereichs-PID fuer normalen Test.
	 */
	private static final String KB1 = "kb.deLzFhTest1";

	/**
	 * Konfigurationsbereichs-PID fuer Test nach PruefSpez.
	 */
	private static final String KB2 = "kb.deLzFhTest2";

	/**
	 * TODO:
	 */
	/**
	 * Verbindungsdaten1.
	 */
	static final String[] CON_DATA_NORMAL = new String[] { "-datenverteiler=localhost:8083", "-benutzer=Tester",
			"-authentifizierung=passwd", "-KonfigurationsBereichsPid=" + KB1, "-debugLevelStdErrText=OFF",
			"-debugLevelFileText=ERROR" };

	/**
	 * Verbindungsdaten2.
	 */
	static final String[] CON_DATA_PR_SPEZ = new String[] { "-datenverteiler=localhost:8083", "-benutzer=Tester",
			"-authentifizierung=passwd", "-KonfigurationsBereichsPid=" + KB2, "-debugLevelStdErrText=OFF",
			"-debugLevelFileText=ERROR" };

	/**
	 * Verzeichnis, in dem sich die CSV-Dateien mit den Testdaten befinden.
	 */
	public static final String TEST_DATEN_VERZEICHNIS = ""; // $NON-NLS-2$

	/**
	 * Standardkonstruktor.
	 */
	private Verbindung() {
	}
}
