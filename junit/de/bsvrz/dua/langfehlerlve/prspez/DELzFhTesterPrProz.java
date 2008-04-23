/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007 BitCtrl Systems GmbH 
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

package de.bsvrz.dua.langfehlerlve.prspez;

import org.junit.Test;

import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.Verbindung;

/**
 * Testet den Modul DE Langzeit-Fehlererkennung nach PruefSpez.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DELzFhTesterPrProz implements ClientSenderInterface {

	/**
	 * Pfad zu Testdaten.
	 */
	public static final String DATEN_QUELLE1 = Verbindung.TEST_DATEN_VERZEICHNIS
			+ "Kurzzeitdat_2.0.csv"; //$NON-NLS-1$

	
	/**
	 * Testet nach PruefSpez.
	 * 
	 * @throws Exception
	 *             wird weitergereicht.
	 */
	@Test
	public void test() throws Exception {
		TestDatenImporterPrSpez daten = new TestDatenImporterPrSpez();
		daten.init(DATEN_QUELLE1);
		
		System.out.println("\nKnotenpunkte:");
		for (int i = 0; i < daten.getKnotenpunkteTab().getAnzahlZeilen(); i++) {
			System.out.println("Q1=" + daten.getKnotenpunkteTab().get(i, "Q1")
					+ ", QZ11=" + daten.getKnotenpunkteTab().get(i, "QZ11")
					+ ", Q4=" + daten.getKnotenpunkteTab().get(i, "Q4")
					+ ", QZ4=" + daten.getKnotenpunkteTab().get(i, "QZ4")
					+ ", QA4=" + daten.getKnotenpunkteTab().get(i, "QA4"));
		}

		System.out.println("\nfreie Strecke:");
		for (int i = 0; i < daten.getFreieStreckeTab().getAnzahlZeilen(); i++) {
			System.out.println("Q1=" + daten.getFreieStreckeTab().get(i, "Q1")
					+ ", QZ11=" + daten.getFreieStreckeTab().get(i, "QZ11")
					+ ", QZ4=" + daten.getFreieStreckeTab().get(i, "QZ4")
					+ ", QA4=" + daten.getFreieStreckeTab().get(i, "QA4"));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// 		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

}
