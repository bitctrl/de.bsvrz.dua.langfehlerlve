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
final class TestDatenImporterPrSpez {

	/**
	 * Alle Attribute der Attributgruppe
	 * <code>atg.verkehrsDatenKurzZeitMq</code>.
	 */
	private static final Attribut[] ATTRIBUTE = new Attribut[] {
			new Attribut("QKfz", true), new Attribut("QPkw", true),
			new Attribut("QLkw", true), new Attribut("VKfz", false),
			new Attribut("VPkw", false), new Attribut("VLkw", false),
			new Attribut("VgKfz", false), new Attribut("B", false),
			new Attribut("BMax", false),
			new Attribut("SKfz", false),
			new Attribut("ALkw", false),
			new Attribut("KKfz", false),
			new Attribut("KLkw", false),
			new Attribut("KPkw", false),			
			new Attribut("VDelta", false),
			new Attribut("KB", false),
			new Attribut("QB", false), };

	/**
	 * Daten fuer Knotenpunkte.
	 */
	private MSGDaten knotenpunkteTab = null;

	/**
	 * Daten fuer freie Strecke.
	 */
	private MSGDaten freieStreckeTab = null;

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
			knotenpunkteTab.add(importer.getNaechsteZeile());
		}

		importer.reset();
		for (int i = 0; i < 24; i++) {
			importer.getNaechsteZeile();
		}
		freieStreckeTab = new MSGDaten(importer.getNaechsteZeile());
		for (int i = 0; i < 5; i++) {
			freieStreckeTab.add(importer.getNaechsteZeile());
		}
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
		 * Standardkonstruktor.
		 * 
		 * @param qNamen die Namen der einzelnen Spalten
		 */
		private MSGDaten(final String[] qNamen) {
			this.spaltenMap = new HashMap<String, Integer>();
			this.zeilenMap = new ArrayList<Integer[]>();
			for (int i = 0; i < qNamen.length; i++) {
				this.spaltenMap.put(qNamen[i], i);
			}
		}

		/**
		 * Fuegt diesem Element eine Zeile hinzu.
		 * 
		 * @param zeile
		 *            eine neue Zeile.
		 */
		private void add(final String[] zeile) {
			Integer[] intZeile = new Integer[zeile.length];

			for (int i = 0; i < zeile.length; i++) {
				if (zeile[i].equals("fehlerhaft")) {
					intZeile[i] = DUAKonstanten.FEHLERHAFT;
				} else {
					intZeile[i] = Integer.parseInt(zeile[i]);
				}
			}

			this.zeilenMap.add(intZeile);
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
