/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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

package de.bsvrz.dua.fehlertls.fehlertls;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.AtgTlsFehlerAnalyse;
import de.bsvrz.dua.fehlertls.DAVTest;
import de.bsvrz.dua.fehlertls.DeStatus;
import de.bsvrz.dua.fehlertls.IAtgTlsFehlerAnalyseListener;
import de.bsvrz.dua.fehlertls.TestKEx;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.BmClient;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.IBmListener;

/**
 * Stellt eine Datenverteiler-Verbindung zur Verfügung.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DeFaApplikationTest implements IBmListener {

	/**
	 * Zeigt an, ob der Test an ist, oder ob nur die Ausgaben mitgeloggt werden
	 * sollen.
	 */
	public static final boolean ASSERTION_AN = true;

	/**
	 * Zeigt an, ob der der Text der DE-Fehleranalyse an allen DE ausgegeben
	 * werden soll.
	 */
	public static final boolean SHOW_DE = true;

	/**
	 * Zeigt an, ob die Betriebsmeldungen an allen DE ausgegeben werden soll.
	 */
	public static final boolean SHOW_BM = true;

	/**
	 * Zeigt an, ob die Nummer des Zeitpunktes mitgeloggt werden soll.
	 */
	public static final boolean SHOW_ZEITPUNKT = true;

	/**
	 * Testet analog Beschreibung in "LiesMich.txt".
	 * 
	 * @throws Exception
	 *             wird weitergereicht
	 */
	@Test
	public void test() throws Exception {
		DAVTest.setTestParameter("kri1");
		ClientDavInterface dav = de.bsvrz.sys.funclib.bitctrl.dua.test.DAVTest.getDav();
		System.out.println(dav);
		init();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long theoretischerEmpfangsZeitStempel = cal.getTimeInMillis();
		while (theoretischerEmpfangsZeitStempel <= System.currentTimeMillis()) {
			theoretischerEmpfangsZeitStempel += 60L * 1000L;
		}
		warteBis(theoretischerEmpfangsZeitStempel + 1000L);

		BmClient.getInstanz(dav).addListener(this);

		TestKEx kex = TestKEx.getInstanz(dav);

		kex.setAnalyseParameter(3000L, 3000L);

		AtgTlsFehlerAnalyse.getInstanz(TestKEx.iB2SM1LVE1DE1).addListener(
				new IAtgTlsFehlerAnalyseListener() {
					private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[] {
							TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
							TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.EAK_AN_SM_DEFEKT };
					private int i = 0;

					public void aktualisiereTlsFehlerAnalyse(
							TlsFehlerAnalyse fehlerAnalyse) {
						if (ASSERTION_AN) {
							if (i < fehler.length) {
								Assert.assertEquals(fehler[i++], fehlerAnalyse);
							}
						}

					}
				});

		AtgTlsFehlerAnalyse.getInstanz(TestKEx.iB2SM2LVE1DE1).addListener(
				new IAtgTlsFehlerAnalyseListener() {
					private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[] {
							TlsFehlerAnalyse.UNBEKANNT,
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.INSELBUS_DEFEKT,
							TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT,
							TlsFehlerAnalyse.KRI_DEFEKT };
					private int i = 0;

					public void aktualisiereTlsFehlerAnalyse(
							TlsFehlerAnalyse fehlerAnalyse) {
						if (ASSERTION_AN) {
							if (i < fehler.length) {
								Assert.assertEquals(fehler[i++], fehlerAnalyse);
							}
						}

					}
				});

		AtgTlsFehlerAnalyse.getInstanz(TestKEx.iB2SM2LVE1DE2).addListener(
				new IAtgTlsFehlerAnalyseListener() {
					private TlsFehlerAnalyse[] fehler = new TlsFehlerAnalyse[] {
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.UNBEKANNT,
							TlsFehlerAnalyse.KRI_DEFEKT,
							TlsFehlerAnalyse.INSELBUS_DEFEKT,
							TlsFehlerAnalyse.SM_MODEM_ODER_SM_DEFEKT };
					private int i = 0;

					public void aktualisiereTlsFehlerAnalyse(
							TlsFehlerAnalyse fehlerAnalyse) {
						if (ASSERTION_AN) {
							if (i < fehler.length) {
								Assert.assertEquals(fehler[i++], fehlerAnalyse);
							}
						}
					}
				});

		/**
		 * Setzte DE-Fehlerstatus auf != 0 fuer alle DE ausser alle LVE DE an
		 * SM2.1 und SM2.2
		 */
		for (SystemObject de : kex.getAlleLveDes()) {
			if (de.equals(TestKEx.iB2SM2LVE1DE1)
					|| de.equals(TestKEx.iB2SM2LVE1DE2)
					|| de.equals(TestKEx.iB2SM1LVE1DE1)) {
				kex.setDeFehlerStatus(de, 0, false);
			} else {
				kex.setDeFehlerStatus(de, 0, true);
			}
			kex.setBetriebsParameter(de, 15L * 1000L);

			final SystemObject obj = de;
			AtgTlsFehlerAnalyse.getInstanz(de).addListener(
					new IAtgTlsFehlerAnalyseListener() {

						public void aktualisiereTlsFehlerAnalyse(
								TlsFehlerAnalyse fehlerAnalyse) {
							if (SHOW_DE) {
								System.out
										.println("+++ " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + //$NON-NLS-1$ //$NON-NLS-2$
												obj
												+ ", " + fehlerAnalyse + " +++"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}

					});
		}

		/**
		 * Alle UFD-DE auf kein zyklischer Abruf setzten
		 */
		for (SystemObject de : kex.getAlleUFDDes()) {
			kex.setBetriebsParameter(de, -1);
			kex.setDeFehlerStatus(de, 0, false);
			final SystemObject obj = de;
			AtgTlsFehlerAnalyse.getInstanz(de).addListener(
					new IAtgTlsFehlerAnalyseListener() {

						public void aktualisiereTlsFehlerAnalyse(
								TlsFehlerAnalyse fehlerAnalyse) {
							if (SHOW_DE) {
								System.out
										.println("+++ " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + //$NON-NLS-1$ //$NON-NLS-2$
												obj
												+ ", " + fehlerAnalyse + " +++"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}

					});
		}
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			//
		}

		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long datenZeitStempel = cal.getTimeInMillis();
		theoretischerEmpfangsZeitStempel = cal.getTimeInMillis() + 15L * 1000L;
		while (theoretischerEmpfangsZeitStempel <= System.currentTimeMillis()) {
			datenZeitStempel += 15L * 1000L;
			theoretischerEmpfangsZeitStempel += 15L * 1000L;
		}

		kex.setDe(TestKEx.iB1SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS);

		int zeitpunkt = 1;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}

		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}

		warteBis(theoretischerEmpfangsZeitStempel + 10000L);
		kex.setDe(TestKEx.iB2SM1UFD1DE1, System.currentTimeMillis(),
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);
		kex.setDe(TestKEx.iB2SM1UFD1DE1, System.currentTimeMillis(),
				DeStatus.ZYKLISCH_AN);
		kex.setDe(TestKEx.iB2SM1UFD1DE2, System.currentTimeMillis(),
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);
		kex.setDe(TestKEx.iB2SM1UFD1DE2, System.currentTimeMillis(),
				DeStatus.ZYKLISCH_AN);
		kex.setDe(TestKEx.iB2SM3LVE1DE1, System.currentTimeMillis(),
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);
		kex.setDe(TestKEx.iB2SM3LVE1DE1, System.currentTimeMillis(),
				DeStatus.ZYKLISCH_AN);

		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}

		/**
		 * 1.3
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 2.0
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 2.1
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}

		/**
		 * 2.2
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 2.3
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 3.0
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM1UFD1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM1UFD1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 3.1
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		warteBis(theoretischerEmpfangsZeitStempel + 10000L);
		kex.setDe(TestKEx.iB2SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AN);

		/**
		 * 3.2
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM3LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		/**
		 * 3.3
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB2SM2LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);
		kex.setDe(TestKEx.iB2SM2LVE1DE2, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		warteBis(theoretischerEmpfangsZeitStempel + 10000L);
		kex.setDe(TestKEx.iB1SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);

		/**
		 * 4.0
		 */
		datenZeitStempel += 15L * 1000L;
		theoretischerEmpfangsZeitStempel += 15L * 1000L;
		warteBis(theoretischerEmpfangsZeitStempel);
		if (SHOW_ZEITPUNKT) {
			System.out.println("***\nZeitpunkt Nr. " + (zeitpunkt / 4) + "."
					+ (zeitpunkt % 4) + "\n***");
			zeitpunkt++;
		}
		kex.setDe(TestKEx.iB1SM1LVE1DE1, theoretischerEmpfangsZeitStempel,
				DeStatus.NUTZ_DATEN);

		warteBis(System.currentTimeMillis() + 10L * 1000L);
	}

	/**
	 * globaler Meldungszaehler.
	 */
	private static int meldungsZeitpunkt = 0;

	/**
	 * Die Meldungen die der Reihe nach erwartet werden (aufgeteilt und sortiert
	 * nach Meldungszeitpunkt und den dazu erwarteten Meldungen (innerhalb eines
	 * Meldungszeitpunktes ist die Reihenfolge der Meldungen egal)).
	 */
	private MeldungsZeitpunkt[] meldungen = null;

	/**
	 * Initialisiert die erwarteten Meldungen.
	 */
	private void init() {
		/**
		 * Die Meldungen die der Reihe nach erwartet werden (aufgeteilt und sortiert
		 * nach Meldungszeitpunkt und den dazu erwarteten Meldungen (innerhalb eines
		 * Meldungszeitpunktes ist die Reihenfolge der Meldungen egal)).
		 */
		this.meldungen = new MeldungsZeitpunkt[] {
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 0.0
						new ErwarteteMeldung[] {
								new ErwarteteMeldung(
										"kri1.ib1.sm1.eaklve1.de1", "kri1.ib1.sm1.eaklve1.de1 (kri1.ib1.sm1.eaklve1.de1): Keine TLS-Fehleranalyse moeglich. DE-Kanal ist passiviert"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib2.sm3.eaklve1.de1", "kri1.ib2.sm3.eaklve1.de1 (kri1.ib2.sm3.eaklve1.de1): Keine TLS-Fehleranalyse moeglich. DE-Kanal ist passiviert"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib1.sm1.eakufd1.de1", "kri1.ib1.sm1.eakufd1.de1 (kri1.ib1.sm1.eakufd1.de1): TLS-Fehlerueberwachung nicht moeglich, da keine zyklische Abgabe von Meldungen eingestellt"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib1.sm1.eakufd1.de2", "kri1.ib1.sm1.eakufd1.de2 (kri1.ib1.sm1.eakufd1.de2): TLS-Fehlerueberwachung nicht moeglich, da keine zyklische Abgabe von Meldungen eingestellt"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib2.sm1.eakufd1.de1", "kri1.ib2.sm1.eakufd1.de1 (kri1.ib2.sm1.eakufd1.de1): TLS-Fehlerueberwachung nicht moeglich, da keine zyklische Abgabe von Meldungen eingestellt"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib2.sm1.eakufd1.de2", "kri1.ib2.sm1.eakufd1.de2 (kri1.ib2.sm1.eakufd1.de2): TLS-Fehlerueberwachung nicht moeglich, da keine zyklische Abgabe von Meldungen eingestellt") }), //$NON-NLS-1$ //$NON-NLS-2$		
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 0.1
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1.ib2.sm1", "Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul defekt. Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 0.2
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1.ib2.sm1", "Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul defekt. Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 0.3
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1", "Verbindung zum KRI kri1 (kri1) oder KRI selbst defekt. Verbindung zum KRI oder KRI instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 1.0
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1.ib2.sm1", "Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul defekt. Modem am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) oder Steuermodul instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 1.1
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1", "Verbindung zum KRI kri1 (kri1) oder KRI selbst defekt. Verbindung zum KRI oder KRI instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 1.2
						new ErwarteteMeldung[] {
								new ErwarteteMeldung(
										"kri1.ib2.sm1.eaklve1", "EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) defekt. EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) instand setzen"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib2", "Inselbus kri1.ib2 (kri1.ib2) gestört: Für die DE der Steuermodule kri1.ib2.sm2 (kri1.ib2.sm2), kri1.ib2.sm3 (kri1.ib2.sm3) sind keine Daten verfügbar. Inselbus kri1.ib2 (kri1.ib2) instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 1.3
						new ErwarteteMeldung[] {
								new ErwarteteMeldung(
										"kri1.ib2.sm2", "Modem am Steuermodul kri1.ib2.sm2 (kri1.ib2.sm2) oder Steuermodul defekt. Modem am Steuermodul kri1.ib2.sm2 (kri1.ib2.sm2) oder Steuermodul instand setzen"), //$NON-NLS-1$ //$NON-NLS-2$
								new ErwarteteMeldung(
										"kri1.ib2.sm1.eaklve1", "EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) defekt. EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) instand setzen") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 2.0
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								null,
								"EAK kri1.ib2.sm1.eakufd1 (kri1.ib2.sm1.eakufd1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) defekt. EAK kri1.ib2.sm1.eakufd1 (kri1.ib2.sm1.eakufd1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) instand setzen") }), //$NON-NLS-1$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 2.1
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								null,
								"Verbindung zum KRI kri1 (kri1) oder KRI selbst defekt. Verbindung zum KRI oder KRI instand setzen") }), //$NON-NLS-1$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 3.0
						new ErwarteteMeldung[] {
								new ErwarteteMeldung(
										null,
										"Inselbus kri1.ib2 (kri1.ib2) gestört: Für die DE der Steuermodule kri1.ib2.sm2 (kri1.ib2.sm2), kri1.ib2.sm3 (kri1.ib2.sm3) sind keine Daten verfügbar. Inselbus kri1.ib2 (kri1.ib2) instand setzen"), //$NON-NLS-1$
								new ErwarteteMeldung(
										null,
										"EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) defekt. EAK kri1.ib2.sm1.eaklve1 (kri1.ib2.sm1.eaklve1) am Steuermodul kri1.ib2.sm1 (kri1.ib2.sm1) instand setzen") }), //$NON-NLS-1$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 3.1
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								"kri1.ib2.sm1.eaklve1.de1", "kri1.ib2.sm1.eaklve1.de1 (kri1.ib2.sm1.eaklve1.de1): Keine TLS-Fehleranalyse moeglich. DE-Fehler(StörEAK): Störung vom E/A-Konzentrator erkannt") }), //$NON-NLS-1$ //$NON-NLS-2$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 3.3
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								null,
								"Modem am Steuermodul kri1.ib2.sm3 (kri1.ib2.sm3) oder Steuermodul defekt. Modem am Steuermodul kri1.ib2.sm3 (kri1.ib2.sm3) oder Steuermodul instand setzen") }), //$NON-NLS-1$
				new MeldungsZeitpunkt(// Zeitpunkt Nr. 4.0
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								null,
								"Modem am Inselbus kri1.ib2 (kri1.ib2) oder Inselbus selbst defekt. Modem oder Inselbus instand setzen") }), //$NON-NLS-1$				

				/**
				 * Ende
				 */
				new MeldungsZeitpunkt(// Zeitpunkt Nr.7
						new ErwarteteMeldung[] { new ErwarteteMeldung(
								Constants.EMPTY_STRING, Constants.EMPTY_STRING) }),

		};
	}

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereBetriebsMeldungen(SystemObject obj, long zeit,
			String text) {
		if (ASSERTION_AN) {
			Assert
					.assertTrue(
							"Falsche Nachricht (Zeitpunkt Nr. " + //$NON-NLS-1$
									(meldungsZeitpunkt + 1 == meldungen.length ? "letzter Zeitpunkt" : meldungsZeitpunkt + 1) + "): " //$NON-NLS-1$ //$NON-NLS-2$
									+ obj.getPid() + ", " + text, //$NON-NLS-1$
							meldungen[meldungsZeitpunkt]
									.isMeldungErwartet(new ErwarteteMeldung(obj
											.getPid(), text)));
			if (meldungen[meldungsZeitpunkt].isNeuerZeitpunkt()) {
				meldungsZeitpunkt++;
			}
		}

		if (SHOW_BM) {
			if (text.length() > 70) {
				System.out
						.println("*** " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + obj + "\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								text.substring(0, 60)
								+ "\n      " + text.substring(60, text.length()) + " ***"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				System.out
						.println("*** " + DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date()) + ":\n" + obj + "\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								text + " ***"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Wartet bis zu einem bestimmten Zeitpunkt.
	 * 
	 * @param zeitStempel
	 *            Zeitpunkt bis zu dem gewartet werden soll
	 */
	public final void warteBis(long zeitStempel) {
		while (System.currentTimeMillis() < zeitStempel) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
				//
			}
		}
	}

	/**
	 * Ein Meldungszeitpunkt (zu diesem Zeitpunkt koennen eine Menge von
	 * Meldungen in unterschiedlicher Reihenfolge erwartet werden).
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 */
	private class MeldungsZeitpunkt {

		/**
		 * alle Meldungen, die zu diesem Zeitpunkt erwartet werden (unsortiert).
		 */
		private ErwarteteMeldung[] erwarteteMeldungenZumZeitpunkt = null;

		/**
		 * Meldungszaehler.
		 */
		private int meldungsZaehler = 0;

		/**
		 * Standardkonstruktor.
		 * 
		 * @param meldungen
		 *            alle zu diesem Zeitpunkt erwarteten Meldungen
		 */
		public MeldungsZeitpunkt(ErwarteteMeldung[] meldungen) {
			this.erwarteteMeldungenZumZeitpunkt = meldungen;
		}

		/**
		 * Erfragt, ob alle fuer diesen Zeitpunkt erwarteten Meldungen bereits
		 * abgearbeitet wurden.
		 * 
		 * @return ob alle fuer diesen Zeitpunkt erwarteten Meldungen bereits
		 *         abgearbeitet wurden
		 */
		public boolean isNeuerZeitpunkt() {
			return this.meldungsZaehler >= this.erwarteteMeldungenZumZeitpunkt.length;
		}

		/**
		 * Erfragt, ob diese Meldung hier erwartet wurde.
		 * 
		 * @param meldung die empfangene Meldung
		 * @return ob diese Meldung hier erwartet wurde
		 */
		public final boolean isMeldungErwartet(ErwarteteMeldung meldung) {
			boolean erwartet = false;

			for (int i = 0; i < erwarteteMeldungenZumZeitpunkt.length; i++) {
				if (this.erwarteteMeldungenZumZeitpunkt[i].equals(meldung)) {
					erwartet = true;
					this.meldungsZaehler++;
					break;
				}
			}

			if (!erwartet) {
				System.out.println("**********\n\nErwartet: " + meldung); //$NON-NLS-1$
				for (int i = 0; i < erwarteteMeldungenZumZeitpunkt.length; i++) {
					System.out
							.println(this.erwarteteMeldungenZumZeitpunkt[i].text);
					if (this.erwarteteMeldungenZumZeitpunkt[i].equals(meldung)) {
						erwartet = true;
						this.meldungsZaehler++;
						break;
					}
				}
				System.out.println("\n\n**********" + meldung); //$NON-NLS-1$
			}

			return erwartet;
		}

	}

	/**
	 * Meldungstext mit Systemobjekt.
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 */
	private class ErwarteteMeldung {

		/**
		 * erwarteter Meldungstext.
		 */
		private String text = null;

		/**
		 * mit Meldung assoziiertes Systemobjekt.
		 */
		private SystemObject referenz = null;

		/**
		 * Standardkonstruktor.
		 * 
		 * @param referenzPid
		 *            PID des mit der Meldung assoziierten Systemobjekt
		 * @param text
		 *            erwarteter Meldungstext
		 */
		protected ErwarteteMeldung(String referenzPid, String text) {
			if (referenzPid != null) {
				try {
					this.referenz = DAVTest.getDav().getDataModel().getObject(
							referenzPid);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			this.text = text;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			boolean gleich = false;

			if (obj != null && obj instanceof ErwarteteMeldung) {
				ErwarteteMeldung that = (ErwarteteMeldung) obj;
				if (this.referenz != null && that.referenz != null) {
					gleich = this.referenz.equals(that.referenz);
				} else {
					gleich = true;
				}
				gleich &= this.text.equals(that.text);
			}

			return gleich;
		}

	}

}
