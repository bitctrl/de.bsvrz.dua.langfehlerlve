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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.Verbindung;
import de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Testet den Modul DE Langzeit-Fehlererkennung nach PruefSpez.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DELzFhTesterPrProzKurz implements ClientSenderInterface {

	/**
	 * Pfad zu Testdaten.
	 */
	public static final String DATEN_QUELLE1 = Verbindung.TEST_DATEN_VERZEICHNIS
			+ "Kurzzeitdat_2.0.csv"; //$NON-NLS-1$

	/**
	 * Alle hier betrachteten Systemobjekte.
	 */
	private static final String[] OBJEKTE = new String[] { "Q1", "QZ11",
			"QA11", "QA12", "QZ12", "Q2", "QZ2", "QA2", "Q3", "Q4", "QZ4",
			"QA4" };
	
	/**
	 * Alle hier betrachteten Messstellen.
	 */
	private static final String[] MS_OBJEKTE = new String[] { "Q1", "Q2", "Q3", "Q4" };

	/**
	 * Datenverteiler-Verbindung.
	 */
	protected static ClientDavInterface dav = null;

	/**
	 * Fuehrt Datenverteilerverbindung durch.
	 * 
	 * @throws Exception
	 *             wird weitergereicht
	 */
	@Before
	public void setUp() throws Exception {
		StandardApplicationRunner.run(new StandardApplication() {

			public void initialize(ClientDavInterface connection)
					throws Exception {
				dav = connection;
			}

			public void parseArguments(ArgumentList argumentList)
					throws Exception {
				argumentList.fetchUnusedArguments();
			}

		}, Verbindung.CON_DATA_PR_SPEZ.clone());

		DataDescription ddMq = new DataDescription(dav.getDataModel()
				.getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ), dav
				.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		for (String objPidEnd : OBJEKTE) {
			System.out.println("Anmeldung: " + "ms.sys.ja."
					+ objPidEnd.toLowerCase() + ", " + "ms.sys.nein."
					+ objPidEnd.toLowerCase());
			dav.subscribeSender(this, dav.getDataModel().getObject(
					"ms.sys.ja." + objPidEnd.toLowerCase()), ddMq, SenderRole
					.source());
			dav.subscribeSender(this, dav.getDataModel().getObject(
					"ms.sys.nein." + objPidEnd.toLowerCase()), ddMq, SenderRole
					.source());
		}

		StandardApplicationRunner.run(new DELangZeitFehlerErkennung(),
				Verbindung.CON_DATA_PR_SPEZ.clone());

		Thread.sleep(5000L);
	}

	/**
	 * Testet nach PruefSpez.
	 * 
	 * @throws Exception
	 *             wird weitergereicht.
	 */
	@Test
	public void test() throws Exception {
		TestDatenImporterPrSpezKurz daten = new TestDatenImporterPrSpezKurz();
		daten.init(DATEN_QUELLE1);
		
		ArrayList<AbstraktAtgUeberwacher> ueberwacher = new ArrayList<AbstraktAtgUeberwacher>();
//		for(String mq:)
//		/**
//		 * MQ-Intervall
//		 */
//		AtgIntervallUeberwacherMqKurz dummy = null;
//		dummy = new AtgIntervallUeberwacherMqKurz()

		long jetzt = System.currentTimeMillis();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(jetzt);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int aktuelleMinute = cal.get(Calendar.MINUTE);
		int naechsterStart = (((int) (aktuelleMinute / 5)) + 1) * 5;
		cal.set(Calendar.MINUTE, naechsterStart);

		System.out.println(DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(cal
				.getTimeInMillis())));

		DataDescription ddMq = new DataDescription(dav.getDataModel()
				.getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ), dav
				.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		for (int i = 0; i < daten.getKnotenpunkteTab().getAnzahlZeilen() + 1; i++) {
			for (String objPidEnd : OBJEKTE) {
				Data dataJa = TestDatenImporterPrSpezKurz.getDatensatz(dav,
						daten.getKnotenpunkteTab().get(i % 5, objPidEnd));
				Data dataNein = TestDatenImporterPrSpezKurz.getDatensatz(dav,
						daten.getFreieStreckeTab().get(i % 5, objPidEnd));
				ResultData resultatJa = new ResultData(dav.getDataModel()
						.getObject("ms.sys.ja." + objPidEnd.toLowerCase()),
						ddMq, cal.getTimeInMillis(), dataJa);
				ResultData resultatNein = new ResultData(dav.getDataModel()
						.getObject("ms.sys.nein." + objPidEnd.toLowerCase()),
						ddMq, cal.getTimeInMillis(), dataNein);
				dav.sendData(resultatJa);
				dav.sendData(resultatNein);
			}

			cal.add(Calendar.MINUTE, 1);
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ex) {
				//
			}
		}
		
		/**
		 * Warte bis alle Daten verarbeitet sind
		 */
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException ex) {
			//
		}
		
		for (AbstraktAtgUeberwacher uw : ueberwacher) {
			uw.ueberpruefe();
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
