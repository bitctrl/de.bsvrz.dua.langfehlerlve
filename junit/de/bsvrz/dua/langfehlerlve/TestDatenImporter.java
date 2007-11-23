/** 
 * Segment 4 Datenübernahme und Aufbereitung (DUA)
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

package de.bsvrz.dua.langfehlerlve;

import java.util.Collection;
import java.util.LinkedList;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.sys.funclib.bitctrl.dua.test.CSVImporter;

/**
 * Liest die Testdaten aus dem CSV format ein
 * Basiert auf das Code von TestFSImporter
 * 
 * 
 * @author BitCtrl Systems GmbH, Bachraty
 *	
 */

public class TestDatenImporter extends CSVImporter {
	
	/**
	 * Verbindung zum Datenverteiler
	 */
	protected static ClientDavInterface DAV = null;
	
	/**
	 * An dieser Spalte beginnen die wirklichen Daten
	 */
	protected static final int OFFSET = 1;
	
	/**
	 * Die Attributgruppe der Daten
	 */
	protected static AttributeGroup ATG;
	
	/**
	 * Die summarisierte werte
	 */
	protected static long [] ausgabe;

	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteier-Verbindung
	 * @param csvQuelle Quelle der Daten (CSV-Datei)
	 * @throws Exception falls dieses Objekt nicht vollständig initialisiert werden konnte
	 */
	public TestDatenImporter(final ClientDavInterface dav, final String csvQuelle)
	throws Exception{
		super(csvQuelle);
		if(DAV == null){
			DAV = dav;
		}
		ATG = DAV.getDataModel().getAttributeGroup("atg.verkehrsDatenKurzZeitMq");
		/**
		 * Tabellenkopf überspringen
		 */
		this.getNaechsteZeile();
	}
	
	/**
	 * Erfragt die nächste Zeile innerhalb der CSV-Datei als einen
	 * Datensatz der übergebenen Attributgruppe
	 * 
	 * @param atg eine Attributgruppe (KZD oder LZD)
	 * @return ein Datensatz der übergebenen Attributgruppe mit den Daten der nächsten Zeile
	 * oder <code>null</code>, wenn der Dateizeiger am Ende ist
	 */
	public final Collection<Data> getDatenSaetzeNaechstenIntervall(String testAttribut){
	
		Collection<Data> datenSaetze = new LinkedList<Data>();
			
		String[] zeile = this.getNaechsteZeile();
		if(zeile != null){
			try{
				
				for(int i = 0; i<10; i++) {
					long wert = Long.parseLong( zeile[OFFSET + i]);
					Data datensatz = DAV.createData(ATG);
					setDatenLeer(datensatz);
					setAttribut(testAttribut, wert, datensatz);
					datenSaetze.add(datensatz);
				}
				if(zeile.length > OFFSET + 10 && ! zeile[OFFSET + 11].equals("")) {
					ausgabe = new long[14]; 
					for(int i=0; i<14; i++) {
						long wert = Long.parseLong( zeile[OFFSET + 10 + i]);
						ausgabe[i] = wert;
					}
				}
				else ausgabe = null;
				
			} catch(NumberFormatException ex) {
				System.err.println("Fehler beim parsing von: " + zeile[0] );
				return null;
			}catch(ArrayIndexOutOfBoundsException ex){
				System.err.println("ArrayIndex Fehler " + ex.getMessage() );
				return null;
			} 
			return datenSaetze;
		} 
		else return null;
	}
	
	public static long [] getAusgabe() {
		return ausgabe;
	}
	
	/**
	 * Setzt einen Attribut in Datensatz 
	 * 
	 * @param attributName Name des Attributs
	 * @param wert Wert des Attributs
	 * @param datensatz der Datensatz
	 * @return der veränderte Datensatz
	 */
	private final static void  setAttribut(final String attributName, long wert, Data datensatz){

		datensatz.getItem(attributName).getItem("Wert").asUnscaledValue().set(wert);
		datensatz.getItem(attributName).getItem("Status").getItem("Erfassung").getUnscaledValue("NichtErfasst").set(0);
		datensatz.getItem(attributName).getItem("Status").getItem("PlFormal").getUnscaledValue("WertMax").set(0);
		datensatz.getItem(attributName).getItem("Status").getItem("PlFormal").getUnscaledValue("WertMin").set(0);			
		datensatz.getItem(attributName).getItem("Status").getItem("MessWertErsetzung").getUnscaledValue("Implausibel").set(0);
		datensatz.getItem(attributName).getItem("Status").getItem("MessWertErsetzung").getUnscaledValue("Interpoliert").set(0);
		datensatz.getItem(attributName).getItem("Güte").getUnscaledValue("Index").set(1000);
		datensatz.getItem(attributName).getItem("Güte").getUnscaledValue("Verfahren").set(0);
		
	}
	
	/**
	 * Setzt einen LZDatensatz als leer
	 * @param datensatz Der Datensatz
	 * @param geschwDaten true, wenn auch Geschwindigkeitsklassendaten erzeugt werden sollen
	 * @return der geaenderte Datensatz
	 */
	public final  static void setDatenLeer(Data datensatz) {
		String[] attribute = new String[] {"QKfz","VKfz","QLkw","VLkw","QPkw","VPkw", "B","BMax",
				   "SKfz","VgKfz","ALkw","KKfz","KLkw","KPkw", "QB", "KB", "VDelta" };

		for(int i=0;i<attribute.length;i++) {
			setAttribut(attribute[i], 0, datensatz);
		}
	}
}
