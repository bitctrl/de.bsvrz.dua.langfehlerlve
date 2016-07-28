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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.bsvrz.dav.daf.main.*;
import org.junit.Before;
import org.junit.Test;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Testet den Modul DE Langzeit-Fehlererkennung nach PruefSpez.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class TestLangFehlerLve extends DuaLangFehlerLveTestBase implements ClientSenderInterface {

	/**
	 * Alle hier betrachteten Systemobjekte.
	 */
	static final String[] OBJEKTE_KURZ = new String[] { "Q1", "QZ11",
			"QA11", "QA12", "QZ12", "Q2", "QZ2", "QA2", "Q3", "Q4", "QZ4",
			"QA4" };
	/**
	 * Alle hier betrachteten Messstellen.
	 */
	static final String[] MS_OBJEKTE_KURZ = new String[] { "Q1", "Q2", "Q3",
			"Q4" };
	/**
	 * Pfad zu Testdaten.
	 */
	private static final String DATEN_QUELLE_LANG = Verbindung.TEST_DATEN_VERZEICHNIS
			+ "Langzeitdat_3.0.csv"; //$NON-NLS-1$
	
	/**
	 * Alle hier betrachteten Systemobjekte.
	 */
	private static final String[] OBJEKTE_LANG = new String[] { "Q1", "QZ11",
			"QA11", "QA12", "QZ12", "Q2", "QZ2", "QA2", "Q3", "Q4", "QZ4",
			"QA4" };

	/**
	 * Alle hier betrachteten Messstellen.
	 */
	private static final String[] MS_OBJEKTE_LANG = new String[] { "Q1", "Q2", "Q3",
			"Q4" };
	/**
	 * Pfad zu Testdaten.
	 */
	private static final String DATEN_QUELLE_KURZ = Verbindung.TEST_DATEN_VERZEICHNIS
			+ "Kurzzeitdat_3.0.csv"; //$NON-NLS-1$

	/**
	 * Testet nach PruefSpez.
	 * 
	 * @throws Exception
	 *             wird weitergereicht.
	 */
	public void testLang() throws Exception {
		DatenImporterPrSpezLang daten = new DatenImporterPrSpezLang();
		daten.init(DATEN_QUELLE_LANG);

		ArrayList<AbstraktAtgUeberwacher> ueberwacher = new ArrayList<AbstraktAtgUeberwacher>();
		for (String ms : MS_OBJEKTE_LANG) {
			SystemObject objJa = _connection.getDataModel()
					.getObject("ms.sys.ja." + ms.substring(1));
			SystemObject objNein = _connection.getDataModel().getObject(
					"ms.sys.nein." + ms.substring(1));

			/**
			 * MQ-Intervall
			 */
			AbstraktAtgUeberwacher dummy = null;
			dummy = new AtgIntervallUeberwacherMqLang();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeIntervallMq(ms));
			ueberwacher.add(dummy);
			dummy = new AtgIntervallUeberwacherMsLang();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeIntervallMs(ms));
			ueberwacher.add(dummy);
			dummy = new AtgBilanzUeberwacherLang();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab().getAusgabeBilanz(
					ms));
			ueberwacher.add(dummy);
			dummy = new AtgAbweichungUeberwacherLang();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeAbweichungMs(ms));
			ueberwacher.add(dummy);

			dummy = new AtgIntervallUeberwacherMqLang();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeIntervallMq(ms));
			ueberwacher.add(dummy);
			dummy = new AtgIntervallUeberwacherMsLang();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeIntervallMs(ms));
			ueberwacher.add(dummy);
			dummy = new AtgBilanzUeberwacherLang();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeBilanz(ms));
			ueberwacher.add(dummy);
			dummy = new AtgAbweichungUeberwacherLangAlle();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeAbweichungMs(ms));
			ueberwacher.add(dummy);
		}

		long jetzt = System.currentTimeMillis();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(jetzt);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DAY_OF_YEAR, -1);

		System.out.println(DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(cal
				.getTimeInMillis())));

		DataDescription ddMq = new DataDescription(_connection.getDataModel()
				.getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ), _connection
				.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		for (int i = 0; i < daten.getKnotenpunkteTab().getAnzahlZeilen() + 1; i++) {
			for (String objPidEnd : OBJEKTE_LANG) {
				Data dataJa = DatenImporterPrSpezKurz.getDatensatz(_connection,
				                                                   daten.getKnotenpunkteTab().get(i % 4, objPidEnd));
				Data dataNein = DatenImporterPrSpezKurz.getDatensatz(_connection,
				                                                     daten.getFreieStreckeTab().get(i % 4, objPidEnd));
				ResultData resultatJa = new ResultData(_connection.getDataModel()
						.getObject("ms.sys.ja." + objPidEnd.toLowerCase()),
						ddMq, cal.getTimeInMillis(), dataJa);
				ResultData resultatNein = new ResultData(_connection.getDataModel()
						.getObject("ms.sys.nein." + objPidEnd.toLowerCase()),
						ddMq, cal.getTimeInMillis(), dataNein);
				_connection.sendData(resultatJa);
				_connection.sendData(resultatNein);
			}

			cal.add(Calendar.HOUR_OF_DAY, 1);
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
			Thread.sleep(2000L);
		} catch (InterruptedException ex) {
			//
		}

		for (AbstraktAtgUeberwacher uw : ueberwacher) {
			uw.ueberpruefe();
		}
	}

	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// 		
	}

	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

	/**
	 * Fuehrt Datenverteilerverbindung durch.
	 * 
	 * @throws Exception
	 *             wird weitergereicht
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		DataDescription ddMq = new DataDescription(_connection.getDataModel()
				.getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ), _connection
				.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		for (String objPidEnd : OBJEKTE_KURZ) {
			System.out.println("Anmeldung: " + "ms.sys.ja."
					+ objPidEnd.toLowerCase() + ", " + "ms.sys.nein."
					+ objPidEnd.toLowerCase());
			_connection.subscribeSender(this, _connection.getDataModel().getObject(
					"ms.sys.ja." + objPidEnd.toLowerCase()), ddMq, SenderRole
					.source());
			_connection.subscribeSender(this, _connection.getDataModel().getObject(
					"ms.sys.nein." + objPidEnd.toLowerCase()), ddMq, SenderRole
					.source());
		}

		Util.parametriere(_connection, _connection.getDataModel().getObject("gruppe.sys.ja"),
				5, 4, 114, 114, 90, 90);
		Util.parametriere(_connection, _connection.getDataModel().getObject("gruppe.sys.nein"),
				5, 4, 103, 103, 111, 111);

		Thread.sleep(5000L);
	}

	/**
	 * Testet nach PruefSpez.
	 * 
	 * @throws Exception
	 *             wird weitergereicht.
	 */
	public void testKurz() throws Exception {
		DatenImporterPrSpezKurz daten = new DatenImporterPrSpezKurz();
		daten.init(DATEN_QUELLE_KURZ);

		ArrayList<AbstraktAtgUeberwacher> ueberwacher = new ArrayList<AbstraktAtgUeberwacher>();
		for (String ms : MS_OBJEKTE_KURZ) {
			SystemObject objJa = _connection.getDataModel()
					.getObject("ms.sys.ja." + ms.substring(1));
			SystemObject objNein = _connection.getDataModel().getObject(
					"ms.sys.nein." + ms.substring(1));

			/**
			 * MQ-Intervall
			 */
			AbstraktAtgUeberwacher dummy = null;
			dummy = new AtgIntervallUeberwacherMqKurz();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeIntervallMq(ms));
			ueberwacher.add(dummy);
			dummy = new AtgIntervallUeberwacherMsKurz();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeIntervallMs(ms));
			ueberwacher.add(dummy);
			dummy = new AtgBilanzUeberwacherKurz();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab().getAusgabeBilanz(
					ms));
			ueberwacher.add(dummy);
			dummy = new AtgAbweichungUeberwacherKurz();
			dummy.init(_connection, objJa, daten.getKnotenpunkteTab()
					.getAusgabeAbweichungMs(ms));
			ueberwacher.add(dummy);

			dummy = new AtgIntervallUeberwacherMqKurz();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeIntervallMq(ms));
			ueberwacher.add(dummy);
			dummy = new AtgIntervallUeberwacherMsKurz();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeIntervallMs(ms));
			ueberwacher.add(dummy);
			dummy = new AtgBilanzUeberwacherKurz();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeBilanz(ms));
			ueberwacher.add(dummy);
			dummy = new AtgAbweichungUeberwacherKurzAlle();
			dummy.init(_connection, objNein, daten.getFreieStreckeTab()
					.getAusgabeAbweichungMs(ms));
			ueberwacher.add(dummy);
		}

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

		DataDescription ddMq = new DataDescription(_connection.getDataModel()
				.getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ), _connection
				.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		for (int i = 0; i < daten.getKnotenpunkteTab().getAnzahlZeilen() + 1; i++) {
			for (String objPidEnd : OBJEKTE_KURZ) {
				Data dataJa = DatenImporterPrSpezKurz.getDatensatz(_connection,
				                                                   daten.getKnotenpunkteTab().get(i % 5, objPidEnd));
				Data dataNein = DatenImporterPrSpezKurz.getDatensatz(_connection,
				                                                     daten.getFreieStreckeTab().get(i % 5, objPidEnd));
				ResultData resultatJa = new ResultData(_connection.getDataModel()
						.getObject("ms.sys.ja." + objPidEnd.toLowerCase()),
				                                       ddMq, cal.getTimeInMillis(), dataJa);
				ResultData resultatNein = new ResultData(_connection.getDataModel()
						.getObject("ms.sys.nein." + objPidEnd.toLowerCase()),
						ddMq, cal.getTimeInMillis(), dataNein);
				_connection.sendData(resultatJa);
				_connection.sendData(resultatNein);
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
			Thread.sleep(2000L);
		} catch (InterruptedException ex) {
			//
		}

		for (AbstraktAtgUeberwacher uw : ueberwacher) {
			uw.ueberpruefe();
		}
	}

	@Test
	public void test() throws Exception {
		testLang();
		testKurz();
	}
}
