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
			return KEINE_DATEN;
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
				if(element.getQ(fahrzeugArt) >= 0){
					zaehler++;
					summe += element.getQ(fahrzeugArt);
				}
			}
			
			if(zaehler > 0.0){
				ergebnis.setQ(fahrzeugArt, summe / zaehler);
			}
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
			double summe = -1.0;
			if(summand1.getQ(fahrzeugArt) >= 0 && summand2.getQ(fahrzeugArt) >= 0){
				summe = summand1.getQ(fahrzeugArt) + summand2.getQ(fahrzeugArt); 
			}
			ergebnis.setQ(fahrzeugArt, summe);
		}
		
		return ergebnis;
	}

	
	/**
	 * Subtrahiert die Argumente in allen Attributen
	 * 
	 * @param minuend der Minuend
	 * @param subtrahend der Subtrahend
	 * @return die Differenz von Minuend und Subtrahend
	 */
	public static final IDELzFhDatum subtrahiere(IDELzFhDatum minuend, IDELzFhDatum subtrahend){
		if(minuend.isKeineDaten() || subtrahend.isKeineDaten()){
			return KEINE_DATEN;
		}
		
		RechenErgebnis ergebnis = new RechenErgebnis();
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			double summe = -1.0;
			if(minuend.getQ(fahrzeugArt) >= 0 && subtrahend.getQ(fahrzeugArt) >= 0){
				summe = minuend.getQ(fahrzeugArt) - subtrahend.getQ(fahrzeugArt); 
			}
			ergebnis.setQ(fahrzeugArt, summe);
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
			double summe = -1.0;
			if(minuend.getQ(fahrzeugArt) >= 0){
				summe = minuend.getQ(fahrzeugArt) - festerSubtrahend; 
			}
			ergebnis.setQ(fahrzeugArt, summe);
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
			double summe = -1.0;
			if(dividend.getQ(fahrzeugArt) >= 0 && divisor.getQ(fahrzeugArt) >= 0){
				summe = dividend.getQ(fahrzeugArt) / divisor.getQ(fahrzeugArt); 
			}
			ergebnis.setQ(fahrzeugArt, summe);
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
			double summe = -1.0;
			if(faktor.getQ(fahrzeugArt) >= 0){
				summe = faktor.getQ(fahrzeugArt) * festerFaktor; 
			}
			ergebnis.setQ(fahrzeugArt, summe);
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
		 * Standardkonstruktor
		 */
		private RechenErgebnis(){
			for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
				this.werte.put(fahrzeugArt, -1.0);
			}
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
