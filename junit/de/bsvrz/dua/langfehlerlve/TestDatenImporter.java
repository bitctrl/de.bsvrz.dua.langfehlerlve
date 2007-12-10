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

package de.bsvrz.dua.langfehlerlve;

import java.util.Collection;
import java.util.LinkedList;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.test.CSVImporter;

/**
 * Liest die Testdaten aus dem CSV format ein
 * Basiert auf das Code von TestFSImporter
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
	 * Wo die Ausgabedaten beginnen
	 */
	protected static final int AUSGABE_OFFSET = 15;
	
	/**
	 * Die Attributgruppe der Daten
	 */
	protected AttributeGroup ATG;
	
	/**
	 * Die summarisierte werte
	 */
	protected long [] ausgabe;
	
	/**
	 * Bestimmt, ob die gerade geparste linine auch eine Ausgabe enthaelt
	 */
	protected boolean hatAusgabe = false;

	
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
		ATG = DAV.getDataModel().getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ);
		/**
		 * Tabellenkopf überspringen
		 */
		this.getNaechsteZeile();
	}
	
	
	/**
	 * Erfragt die nächste Zeile innerhalb der CSV-Datei als eine Liste von 
	 * Datensätzen der übergebenen Attributgruppe
	 * 
	 * @param testAttribut eine Attributgruppe (KZD oder LZD)
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
				if(zeile.length > AUSGABE_OFFSET  && ! zeile[AUSGABE_OFFSET].equals("")) { //$NON-NLS-1$
					ausgabe = new long[14]; 
					for(int i=0; i<14; i++) {
						long wert = Long.parseLong( zeile[AUSGABE_OFFSET + i]);
						ausgabe[i] = wert;
					}
					hatAusgabe = true;
				}
				else {
					ausgabe = null;
					hatAusgabe = false;
				}
				
			} catch(NumberFormatException ex) {
				System.err.println("Fehler beim parsing von: " + zeile[0] ); //$NON-NLS-1$
				return null;
			}catch(ArrayIndexOutOfBoundsException ex){
				System.err.println("ArrayIndex Fehler " + ex.getMessage() ); //$NON-NLS-1$
				return null;
			} 
			return datenSaetze;
		} 
		else return null;
	}
	
	
	/**
	 * Erfragt ob die letzte bearbeitete Zeile auch Ausgabedaten enthält
	 *  
	 * @return <code>true</code> wenn Ausgabedaten aus der CSV-Datei gelesen wurden 
	 */
	public  boolean hatAusgabe() {
		 return this.hatAusgabe;
	}
	
	
	/**
	 * Leifert die Ausgabedaten wenn sie ausgelesen worden sind 
	 * 
	 * @return die Ausgabedaten wenn sie ausgelesen worden sind
	 */
	public long [] getAusgabe() {
		return ausgabe;
	}
	
	
	/**
	 * Setzt einen Attribut in Datensatz 
	 * 
	 * @param attributName Name des Attributs
	 * @param wert Wert des Attributs
	 * @param datensatz der Datensatz
	 */
	private final static void  setAttribut(final String attributName, long wert, Data datensatz){

		datensatz.getItem(attributName).getItem("Wert").asUnscaledValue().set(wert); //$NON-NLS-1$
		datensatz.getItem(attributName).getItem("Status").getItem("Erfassung").getUnscaledValue("NichtErfasst").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("PlFormal").getUnscaledValue("WertMax").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("PlFormal").getUnscaledValue("WertMin").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("PlLogisch").getUnscaledValue("WertMaxLogisch").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("PlLogisch").getUnscaledValue("WertMinLogisch").set(0);	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("MessWertErsetzung").getUnscaledValue("Implausibel").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Status").getItem("MessWertErsetzung").getUnscaledValue("Interpoliert").set(0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		datensatz.getItem(attributName).getItem("Güte").getUnscaledValue("Index").set(1000); //$NON-NLS-1$ //$NON-NLS-2$
		datensatz.getItem(attributName).getItem("Güte").getUnscaledValue("Verfahren").set(0); //$NON-NLS-1$ //$NON-NLS-2$
		
	}
	
	
	/**
	 * Setzt einen LZDatensatz als leer
	 * 
	 * @param datensatz Der Datensatz
	 */
	public final  static void setDatenLeer(Data datensatz) {
		String[] attribute = new String[] {"QKfz", //$NON-NLS-1$
				"VKfz", //$NON-NLS-1$
				"QLkw", //$NON-NLS-1$
				"VLkw", //$NON-NLS-1$
				"QPkw", //$NON-NLS-1$
				"VPkw", //$NON-NLS-1$
				"B", //$NON-NLS-1$
				"BMax", //$NON-NLS-1$
				"SKfz", //$NON-NLS-1$
				"VgKfz", //$NON-NLS-1$
				"ALkw", //$NON-NLS-1$
				"KKfz", //$NON-NLS-1$
				"KLkw", //$NON-NLS-1$
				"KPkw", //$NON-NLS-1$
				"QB", //$NON-NLS-1$
				"KB", //$NON-NLS-1$
				"VDelta" }; //$NON-NLS-1$

		for(int i=0;i<attribute.length;i++) {
			setAttribut(attribute[i], 0, datensatz);
		}
	}
}
