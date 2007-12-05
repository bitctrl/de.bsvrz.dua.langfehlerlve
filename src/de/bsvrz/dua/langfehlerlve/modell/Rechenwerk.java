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

package de.bsvrz.dua.langfehlerlve.modell;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;

/**
 * Einfaches Rechenwerk zum Verknuepfen von DELzFh-Werten mit der Schnittstelle
 * <code>IDELzFhDatum</code>. Es werden jeweils alle Attribute der Schnittstelle
 * einzeln miteinander verknuepft
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class Rechenwerk {
	
	/**
	 * <code>keine Daten</code>
	 */
	private static final IDELzFhDatum KEINE_DATEN = new IDELzFhDatum(){

		public double getQ(FahrzeugArt fahrzeugArt) {
			return -1.0;
		}

		public boolean isKeineDaten() {
			return true;
		}

		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return false;
		}		
		
	};

	/**
	 * kein Element dieses Datums ist auswertbar
	 */
	private static final IDELzFhDatum NICHT_AUSWERTBAR = new IDELzFhDatum(){

		public double getQ(FahrzeugArt fahrzeugArt) {
			return -1.0;
		}

		public boolean isKeineDaten() {
			return false;
		}

		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return true;
		}		
		
	};
	
	
	/**
	 * Berechnet den Durchschnitt ueber allen Einzelattributen innerhalb
	 * der uebergebenen Elemente
	 * 
	 * @param elemente alle Elemente, ueber die der Durchschnitt ermittelt werden soll
	 * @return der Durchschnitt ueber allen Einzelattributen innerhalb
	 * der uebergebenen Elemente
	 */
	public static final IDELzFhDatum durchschnitt(Collection<IDELzFhDatum> elemente){
		if(elemente.isEmpty()){
			return NICHT_AUSWERTBAR;
		}
		
		for(IDELzFhDatum element:elemente){
			if(element.isKeineDaten()){
				return KEINE_DATEN;
			}
		}
		
		if(elemente.size() == 1){
			return elemente.iterator().next();
		}

		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			double zaehler = 0.0;
			double summe = 0.0;
			for(IDELzFhDatum element:elemente){
				if(element.isAuswertbar(fahrzeugArt)){
					zaehler++;
					summe += element.getQ(fahrzeugArt);
				}
			}
			
			if(zaehler > 0.0){
				ergebnis.setQ(fahrzeugArt, summe / zaehler);
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}
		}
		
		return ergebnis;
	}
	
	/**
	 * Summiert die Elemente in allen Attributen
	 * 
	 * @param elemente alle Elemente, ueber die der Durchschnitt ermittelt werden soll
	 * @return die Summe ueber allen Einzelattributen innerhalb
	 * der uebergebenen Elemente
	 */
	public static final IDELzFhDatum addiere(Collection<IDELzFhDatum> elemente){
		if(elemente.isEmpty()){
			return NICHT_AUSWERTBAR;
		}
		
		for(IDELzFhDatum element:elemente){
			if(element.isKeineDaten()){
				return KEINE_DATEN;
			}
		}
		
		if(elemente.size() == 1){
			return elemente.iterator().next();
		}

		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			double summe = 0.0;
			boolean  auswertbar = true;
			for(IDELzFhDatum element:elemente){
				if(element.isAuswertbar(fahrzeugArt)) {
					summe += element.getQ(fahrzeugArt);
				}
				else {
					auswertbar = false;
					break;
				}
			}
			if(auswertbar) ergebnis.setQ(fahrzeugArt, summe );
			ergebnis.setAuswertbar(fahrzeugArt, auswertbar);
		}
		
		return ergebnis;
	}
	
	
	/**
	 * Summiert die Argumente in allen Attributen
	 * 
	 * @param summand1 Summand Nr.1
	 * @param summand2 Summand Nr.1
	 * @return Summe von Summand Nr.1 und Summand Nr.2
	 */
	public static final IDELzFhDatum addiere(IDELzFhDatum summand1, IDELzFhDatum summand2){
		if(summand1.isKeineDaten() || summand2.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(summand1.isAuswertbar(fahrzeugArt) && summand2.isAuswertbar(fahrzeugArt)){
				ergebnis.setQ(fahrzeugArt, summand1.getQ(fahrzeugArt) + summand2.getQ(fahrzeugArt));
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}			
		}
		
		return ergebnis;
	}

	
	/**
	 * Subtrahiert die Argumente in allen Attributen
	 * 
	 * @param minuend der Minuend
	 * @param subtrahend der Subtrahend
	 * @param negativErlaubt ob Negative Ausgangswerte erlaubt sind
	 * @return die Differenz von Minuend und Subtrahend
	 */
	public static final IDELzFhDatum subtrahiere(IDELzFhDatum minuend, IDELzFhDatum subtrahend){
		if(minuend.isKeineDaten() || subtrahend.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(minuend.isAuswertbar(fahrzeugArt) && subtrahend.isAuswertbar(fahrzeugArt)){
				ergebnis.setQ(fahrzeugArt, minuend.getQ(fahrzeugArt) - subtrahend.getQ(fahrzeugArt));
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}
		}
		
		return ergebnis;
	}

	
	/**
	 * Subtrahiert die Argumente in allen Attributen
	 * 
	 * @param minuend der Minuend
	 * @param festerSubtrahend der Subtrahend
	 * @return die Differenz von Minuend und Subtrahend
	 */
	public static final IDELzFhDatum subtrahiere(IDELzFhDatum minuend, double festerSubtrahend){
		if(minuend.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(minuend.isAuswertbar(fahrzeugArt)){
				ergebnis.setQ(fahrzeugArt, minuend.getQ(fahrzeugArt) - festerSubtrahend);
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}
		}
		
		return ergebnis;
	}

	
	/**
	 * Dividiert die beiden Argumente in allen Attributen
	 * 
	 * @param dividend der Dividend
	 * @param divisor der Divisor
	 * @return Dividend / Divisor
	 */
	public static final IDELzFhDatum dividiere(IDELzFhDatum dividend, IDELzFhDatum divisor){
		if(dividend.isKeineDaten() || divisor.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(dividend.isAuswertbar(fahrzeugArt) &&
				divisor.isAuswertbar(fahrzeugArt) && divisor.getQ(fahrzeugArt) != 0){
				ergebnis.setQ(fahrzeugArt, dividend.getQ(fahrzeugArt) / divisor.getQ(fahrzeugArt));
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}			
		}
		
		return ergebnis;
	}
	
	
	/**
	 * Multipliziert das erste Argumente in allen Attributen mit dem zweiten Argument
	 * 
	 * @param faktor Faktor Nr.1
	 * @param festerFaktor Faktor Nr.1
	 * @return das Produkt
	 */
	public static final IDELzFhDatum multipliziere(IDELzFhDatum faktor, final double festerFaktor){
		if(faktor.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(faktor.isAuswertbar(fahrzeugArt)){
				ergebnis.setQ(fahrzeugArt, faktor.getQ(fahrzeugArt) * festerFaktor);
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}
		}
		
		return ergebnis;
	}

	
	/**
	 * Ein durch eine statische Methode dieser Klasse erzeugtes Rechenergebnis
	 *  
	 * @author BitCtrl Systems GmbH, Thierfelder
	 *
	 */
	private static class RechenErgebnis
	implements IDELzFhDatum{
		
		/**
		 * alle hier gespeicherten Werte
		 */
		private Map<FahrzeugArt, Double> werte = new HashMap<FahrzeugArt, Double>();
		
		/**
		 * ob (bzw. inwieweit) alle hier gespeicherten Werte auswertbar ist
		 */
		private Map<FahrzeugArt, Boolean> auswertbar = new HashMap<FahrzeugArt, Boolean>();
		
		
		/**
		 * Standardkonstruktor
		 */
		private RechenErgebnis(){
			for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
				this.auswertbar.put(fahrzeugArt, false);
				this.werte.put(fahrzeugArt, -1.0);
			}
		}
		
		
		/**
		 * Setzt, ob der ueber diese Schnittstelle erfragbare Wert fuer die
		 * uebergebene Fahrzeugart auswertbar ist
		 * 
		 * @param fahrzeugArt eine Fahrzeugart
		 * @param auswertbar ob (bzw. inwieweit) alle hier gespeicherten Werte auswertbar ist
		 */
		private final void setAuswertbar(FahrzeugArt fahrzeugArt, boolean auswertbar){
			this.auswertbar.put(fahrzeugArt, auswertbar);
		}
		
		
		/**
		 * Setzt den Q-Wert einer Fahrzeugart
		 * 
		 * @param fahrzeugArt eine Fahrzeugart
		 * @param wert der zu setzende Q-Wert
		 */
		private final void setQ(FahrzeugArt fahrzeugArt, double wert) {
			this.werte.put(fahrzeugArt, wert);
		}

		
		/**
		 * {@inheritDoc}
		 */
		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return this.auswertbar.get(fahrzeugArt);
		}
		

		/**
		 * {@inheritDoc}
		 */
		public double getQ(FahrzeugArt fahrzeugArt) {
			return this.werte.get(fahrzeugArt);
		}

		
		/**
		 * {@inheritDoc}
		 */
		public boolean isKeineDaten() {
			return false;
		}
		
	}
	
}
