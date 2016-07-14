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

import java.io.File;

/**
 * Haelt alle Startparameter, die zur Durchfuehrung des Tests notwendig sind
 * (Datenverteiler-Verbindung, Basisverzeichnis fuer Quelldaten).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class Verbindung {
	
	/**
	 * Standardkonstruktor.
	 */
	private Verbindung() {
	}
	
	/**
	 * Konfigurationsbereichs-PID fuer normalen Test.
	 */
	private static final String KB1 = "kb.deLzFhTest1";
	
	/**
	 * Konfigurationsbereichs-PID fuer Test nach PruefSpez.
	 */
	private static final String KB2 = "kb.deLzFhTest2";
	
//	 /**
//	 * Verbindungsdaten1.
//	 */
//	 public static final String[] CON_DATA_NORMAL = new String[] {
//	 "-datenverteiler=localhost:8083", //$NON-NLS-1$
//	 "-benutzer=Tester", //$NON-NLS-1$
//	 "-authentifizierung=c:\\passwd", //$NON-NLS-1$
//	 "-KonfigurationsBereichsPid=kb.deLzFhTest1", //$NON-NLS-1$
//	 "-debugLevelStdErrText=ERROR", //$NON-NLS-1$
//	 "-debugLevelFileText=ERROR" }; //$NON-NLS-1$
//
//	 /**
//	 * Verbindungsdaten2.
//	 */
//	 public static final String[] CON_DATA_PR_SPEZ = new String[] {
//	 "-datenverteiler=localhost:8083", //$NON-NLS-1$
//	 "-benutzer=Tester", //$NON-NLS-1$
//	 "-authentifizierung=c:\\passwd", //$NON-NLS-1$
//	 "-KonfigurationsBereichsPid=kb.deLzFhTest2", //$NON-NLS-1$
//	 "-debugLevelStdErrText=ERROR", //$NON-NLS-1$
//	 "-debugLevelFileText=ERROR" }; //$NON-NLS-1$
//
//	/**
//	 * Verzeichnis, in dem sich die CSV-Dateien mit den Testdaten befinden.
//	 */
//	public static final String TEST_DATEN_VERZEICHNIS = "extra\\testDaten" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
	 
	 /**
	  * TODO:
	  */
	/**
	 * Verbindungsdaten1.
	 */
	public static final String[] CON_DATA_NORMAL = new String[] {
			"-datenverteiler=localhost:8083", //$NON-NLS-1$
			"-benutzer=Tester", //$NON-NLS-1$
			"-authentifizierung=passwd", //$NON-NLS-1$
			"-KonfigurationsBereichsPid=" + KB1, //$NON-NLS-1$
			"-debugLevelStdErrText=OFF", //$NON-NLS-1$
			"-debugLevelFileText=ERROR" }; //$NON-NLS-1$

	/**
	 * Verbindungsdaten2.
	 */
	public static final String[] CON_DATA_PR_SPEZ = new String[] {
			"-datenverteiler=localhost:8083", //$NON-NLS-1$
			"-benutzer=Tester", //$NON-NLS-1$
			"-authentifizierung=passwd", //$NON-NLS-1$
			"-KonfigurationsBereichsPid=" + KB2, //$NON-NLS-1$
			"-debugLevelStdErrText=OFF", //$NON-NLS-1$
			"-debugLevelFileText=ERROR" }; //$NON-NLS-1$

	/**
	 * Verzeichnis, in dem sich die CSV-Dateien mit den Testdaten befinden.
	 */
	public static final String TEST_DATEN_VERZEICHNIS = ""; //$NON-NLS-1$ //$NON-NLS-2$

}
