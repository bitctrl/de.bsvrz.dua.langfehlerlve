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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.MesswertUnskaliert;
import de.bsvrz.sys.funclib.bitctrl.dua.test.CSVImporter;

/**
 * Liest die Testdaten ein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
class TestDatenImporterPrSpezKurz {

	/**
	 * Debug?
	 */
	private static final boolean DEBUG = true;

	/**
	 * Alle Attribute der Attributgruppe
	 * <code>atg.verkehrsDatenKurzZeitMq</code>.
	 */
	private static final Attribut[] ATTRIBUTE = new Attribut[] {
			new Attribut("QKfz", true), new Attribut("QPkw", true),
			new Attribut("QLkw", true), new Attribut("VKfz", false),
			new Attribut("VPkw", false), new Attribut("VLkw", false),
			new Attribut("VgKfz", false), new Attribut("B", false),
			new Attribut("BMax", false), new Attribut("SKfz", false),
			new Attribut("ALkw", false), new Attribut("KKfz", false),
			new Attribut("KLkw", false), new Attribut("KPkw", false),
			new Attribut("VDelta", false), new Attribut("KB", false),
			new Attribut("QB", false), };

	/**
	 * Daten fuer Knotenpunkte.
	 */
	protected MSGDaten knotenpunkteTab = null;

	/**
	 * Daten fuer freie Strecke.
	 */
	protected MSGDaten freieStreckeTab = null;

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
		for (int i = 0; i < 5; i++) {
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
		for (int i = 0; i < 24; i++) {
			importer.getNaechsteZeile();
		}
		freieStreckeTab = new MSGDaten(importer.getNaechsteZeile());
		for (int i = 0; i < 5; i++) {
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

	/**
	 * Erfragt die Daten fuer Knotenpunkte.
	 * 
	 * @return Daten fuer Knotenpunkte.
	 */
	MSGDaten getKnotenpunkteTab() {
		return knotenpunkteTab;
	}

	/**
	 * Erfragt die Daten fuer freie Strecke.
	 * 
	 * @return Daten fuer freie Strecke.
	 */
	MSGDaten getFreieStreckeTab() {
		return freieStreckeTab;
	}

	/**
	 * Uebersetzt in DAV-Daten der Attributgruppe
	 * <code>atg.verkehrsDatenKurzZeitMq</code>.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param wert
	 *            ein Wert
	 * @return DAV-Datum der Attributgruppe
	 *         <code>atg.verkehrsDatenKurzZeitMq</code>
	 */
	static Data getDatensatz(ClientDavInterface dav, long wert) {
		Data data = dav.createData(dav.getDataModel().getAttributeGroup(
				DUAKonstanten.ATG_KURZZEIT_MQ));

		for (Attribut attr : ATTRIBUTE) {
			MesswertUnskaliert mw = new MesswertUnskaliert(attr.getName());

			if (attr.istQAttribut) {
				mw.setWertUnskaliert(wert);
			} else {
				mw.setWertUnskaliert(1);
			}

			mw.kopiereInhaltNach(data);
		}

		return data;
	}

	/**
	 * Speichert alle Daten einer Messstellengruppe.
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 * @version $Id$
	 */
	final class MSGDaten {

		/**
		 * Mappt die Namen auf die Spalten in denen sie stehen.
		 */
		private Map<String, Integer> spaltenMap = null;

		/**
		 * Mappt die Zeilennummer auf den Zeileninhalt.
		 */
		private List<Integer[]> zeilenMap = null;

		/**
		 * Intervallverkehrsstaerke fuer MQ.
		 */
		private Map<String, Integer> intervallMq = null;

		/**
		 * Intervallverkehrsstaerke fuer Messstellen.
		 */
		private Map<String, Integer> intervallMs = null;

		/**
		 * Bilanzverkehrsstaerke fuer Messstellen.
		 */
		private Map<String, Integer> bilanzMs = null;

		/**
		 * Abweichungsverkehrsstaerke.
		 */
		private Map<String, Integer> abweichung = null;

		/**
		 * Standardkonstruktor.
		 * 
		 * @param qNamen die Namen der einzelnen Spalten
		 */
		protected MSGDaten(final String[] qNamen) {
			this.spaltenMap = new HashMap<String, Integer>();
			this.intervallMq = new HashMap<String, Integer>();
			this.intervallMs = new HashMap<String, Integer>();
			this.bilanzMs = new HashMap<String, Integer>();
			this.abweichung = new HashMap<String, Integer>();
			this.zeilenMap = new ArrayList<Integer[]>();
			for (int i = 0; i < qNamen.length; i++) {
				this.spaltenMap.put(qNamen[i], i);
			}
		}

		/**
		 * Erfragt einen Bilanzwert einer Messstelle.
		 * 
		 * @param ms eine Messstelle.
		 * @return ein Bilanzwert einer Messstelle.
		 */
		protected int getAusgabeBilanz(final String ms) {
			return this.bilanzMs.get(ms);
		}
		
		/**
		 * Erfragt einen Intervallwert eines MQ an einer Messstelle.
		 * 
		 * @param ms eine Messstelle.
		 * @return ein Intervallwert einer MQ an einer Messstelle.
		 */
		protected int getAusgabeIntervallMq(final String ms) {
			return this.intervallMq.get(ms);
		}
		
		/**
		 * Erfragt einen Intervallwert eines MQ an einer Messstelle.
		 * 
		 * @param ms eine Messstelle.
		 * @return ein Intervallwert einer MQ an einer Messstelle.
		 */
		protected int getAusgabeIntervallMs(final String ms) {
			return this.intervallMs.get(ms);
		}
		
		/**
		 * Erfragt einen Abweichungswert einer Messstelle.
		 * 
		 * @param ms eine Messstelle.
		 * @return ein Abweichungswert einer Messstelle.
		 */
		protected int getAusgabeAbweichungMs(final String ms) {
			return this.abweichung.get(ms);
		}
		
		/**
		 * Fuegt diesem Element eine Zeile hinzu (Eingabedatum).
		 * 
		 * @param zeile
		 *            eine neue Zeile.
		 */
		protected void addEingabe(final String[] zeile) {
			Integer[] intZeile = new Integer[zeile.length];

			for (int i = 0; i < zeile.length; i++) {
				if (zeile[i].equals("fehlerhaft")) {
					intZeile[i] = DUAKonstanten.FEHLERHAFT;
				} else {
					intZeile[i] = Integer.parseInt(zeile[i]);
				}
				if (DEBUG) {
					System.out.print(intZeile[i] + ", ");
				}
			}
			if (DEBUG) {
				System.out.println();
			}

			this.zeilenMap.add(intZeile);
		}

		/**
		 * Fuegt diesem Element eine Zeile hinzu (Erwartetes Ergebnis).
		 * 
		 * @param mqId
		 *            die ID des MQ bzw. der Messstelle
		 * @param zeile
		 *            eine neue Zeile.
		 */
		protected void addAusgabe(final String mqId, final String[] zeile) {

			if (DEBUG) {
				System.out.print(mqId + ": ");
			}

			if (zeile[0].matches("[-]?[0-9]+([,][0-9]+)?")) {
				this.intervallMq.put(mqId, (int) Math.round(Double
						.parseDouble(zeile[0].replaceAll("[,]", "."))));
			} else {
				this.intervallMq.put(mqId,
						DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}
			if (DEBUG) {
				System.out.print("IMQ=" + this.intervallMq.get(mqId));
			}

			if (zeile[2].matches("[-]?[0-9]+([,][0-9]+)?")) {
				this.intervallMs.put(mqId, (int) Math.round(Double
						.parseDouble(zeile[2].replaceAll("[,]", "."))));
			} else {
				this.intervallMs.put(mqId,
						DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}
			if (DEBUG) {
				System.out.print(", IMS=" + this.intervallMs.get(mqId));
			}

			if (zeile[5].matches("[-]?[0-9]+([,][0-9]+)?")) {
				this.bilanzMs.put(mqId, (int) Math.round(Double
						.parseDouble(zeile[5].replaceAll("[,]", "."))));
			} else {
				this.bilanzMs.put(mqId,
						DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}
			if (DEBUG) {
				System.out.print(", BIL=" + this.bilanzMs.get(mqId));
			}

			if (zeile[8].matches("[-]?[0-9]+([,][0-9]+)?")) {
				this.abweichung.put(mqId, (int) Math.round(Double
						.parseDouble(zeile[8].replaceAll("[,]", "."))));
			} else {
				this.abweichung.put(mqId,
						DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}
			if (DEBUG) {
				System.out.println(", ABW=" + this.abweichung.get(mqId));
			}
		}

		/**
		 * Erfragt die Anzahl der Zeilen.
		 * 
		 * @return die Anzahl der Zeilen.
		 */
		int getAnzahlZeilen() {
			return this.zeilenMap.size();
		}

		/**
		 * Erfragt den Wert eines Querschnittes in einer bestimmten Zeile.
		 * 
		 * @param zeile die Zeile.
		 * @param qName der Name des Querschnitts (Name in der Tabelle)
		 * @return den Wert eines Querschnittes in einer bestimmten Zeile.
		 */
		long get(int zeile, String qName) {
			return this.zeilenMap.get(zeile)[this.spaltenMap.get(qName)];
		}

	}

	/**
	 * Information zu einem Attribut innerhalb der Attributgruppe
	 * <code>atg.verkehrsDatenKurzZeitMq</code>. 
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 * @version $Id$
	 */
	private static final class Attribut {

		/**
		 * Der Name des Attributs.
		 */
		private String name;

		/**
		 * Ob es sich um ein Q...-Attribut handelt.
		 */
		private boolean istQAttribut = false;

		/**
		 * Standardkonstruktor.
		 * 
		 * @param name
		 *            der Name des Attributs
		 * @param istQAttribut
		 *            ob es sich um ein Q...-Attribut handelt
		 */
		Attribut(String name, boolean istQAttribut) {
			this.name = name;
			this.istQAttribut = istQAttribut;
		}

		/**
		 * Erfragt den Namen des Attributs.
		 * 
		 * @return der Name des Attributs
		 */
		public String getName() {
			return name;
		}

		/**
		 * Erfragt, ob es sich um ein Q...-Attribut handelt.
		 * 
		 * @return ob es sich um ein Q...-Attribut handelt
		 */
		public boolean isQAttribut() {
			return istQAttribut;
		}
	}
}
