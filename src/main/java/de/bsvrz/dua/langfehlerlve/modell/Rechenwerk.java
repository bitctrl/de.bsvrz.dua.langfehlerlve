/*
 * Segment Datenübernahme und Aufbereitung (DUA), SWE Langzeit-Fehlererkennung LVE
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.
 * 
 * de.bsvrz.dua.langfehlerlve is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.modell;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;

/**
 * Einfaches Rechenwerk zum Verknuepfen von DELzFh-Werten mit der Schnittstelle
 * <code>IDELzFhDatum</code>. Es werden jeweils alle Attribute der Schnittstelle
 * einzeln miteinander verknuepft
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class Rechenwerk {

	/**
	 * <code>keine Daten</code>.
	 */
	private static final IDELzFhDatum KEINE_DATEN = new IDELzFhDatum() {

		@Override
		public double getQ(FahrzeugArt fahrzeugArt) {
			return -1.0;
		}

		@Override
		public boolean isKeineDaten() {
			return true;
		}

		@Override
		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return false;
		}

		@Override
		public SystemObject getObjekt() {
			return null;
		}

	};

	/**
	 * kein Element dieses Datums ist auswertbar.
	 */
	private static final IDELzFhDatum NICHT_AUSWERTBAR = new IDELzFhDatum() {

		@Override
		public double getQ(FahrzeugArt fahrzeugArt) {
			return -1.0;
		}

		@Override
		public boolean isKeineDaten() {
			return false;
		}

		@Override
		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return true;
		}

		@Override
		public SystemObject getObjekt() {
			return null;
		}

	};

	/**
	 * Standardkonstruktor.
	 */
	private Rechenwerk() {

	}

	/**
	 * Berechnet den Durchschnitt ueber allen Einzelattributen innerhalb der
	 * uebergebenen Elemente.
	 * 
	 * @param elemente
	 *            alle Elemente, ueber die der Durchschnitt ermittelt werden
	 *            soll
	 * @return der Durchschnitt ueber allen Einzelattributen innerhalb der
	 *         uebergebenen Elemente
	 */
	public static IDELzFhDatum durchschnitt(Collection<IDELzFhDatum> elemente) {
		if (elemente.isEmpty()) {
			return NICHT_AUSWERTBAR;
		}

		for (IDELzFhDatum element : elemente) {
			if (element.isKeineDaten()) {
				return KEINE_DATEN;
			}
		}

		if (elemente.size() == 1) {
			return elemente.iterator().next();
		}

		RechenErgebnis ergebnis = new RechenErgebnis(elemente.toArray(new IDELzFhDatum[0]));
		for (FahrzeugArt fahrzeugArt : FahrzeugArt.getInstanzen()) {
			double zaehler = 0.0;
			double summe = 0.0;
			for (IDELzFhDatum element : elemente) {
				if (element.isAuswertbar(fahrzeugArt)) {
					zaehler++;
					summe += element.getQ(fahrzeugArt);
				} else {
					zaehler = -1.0;
					break;
				}
			}

			if (zaehler > 0.0) {
				ergebnis.setQ(fahrzeugArt, summe / zaehler);
				ergebnis.setAuswertbar(fahrzeugArt, true);
			}
		}

		return ergebnis;
	}

	/**
	 * Summiert die Elemente in allen Attributen.
	 * 
	 * @param elemente
	 *            alle Elemente, ueber die der Durchschnitt ermittelt werden
	 *            soll
	 * @return die Summe ueber allen Einzelattributen innerhalb der uebergebenen
	 *         Elemente
	 */
	public static IDELzFhDatum addiere(Collection<IDELzFhDatum> elemente) {
		if (elemente == null || elemente.isEmpty()) {
			return NICHT_AUSWERTBAR;
		}

		for (IDELzFhDatum element : elemente) {
			if (element.isKeineDaten()) {
				return KEINE_DATEN;
			}
		}

		if (elemente.size() == 1) {
			return elemente.iterator().next();
		}

		RechenErgebnis ergebnis = new RechenErgebnis(elemente.toArray(new IDELzFhDatum[0]));
		for (FahrzeugArt fahrzeugArt : FahrzeugArt.getInstanzen()) {
			double summe = 0.0;
			boolean auswertbar = true;
			for (IDELzFhDatum element : elemente) {
				if (element.isAuswertbar(fahrzeugArt)) {
					summe += element.getQ(fahrzeugArt);
				} else {
					auswertbar = false;
					break;
				}
			}
			if (auswertbar) {
				ergebnis.setQ(fahrzeugArt, summe);
			}
			ergebnis.setAuswertbar(fahrzeugArt, auswertbar);
		}

		return ergebnis;
	}

	/**
	 * Summiert die Argumente in allen Attributen.
	 * 
	 * @param summand1
	 *            Summand Nr.1
	 * @param summand2
	 *            Summand Nr.1
	 * @return Summe von Summand Nr.1 und Summand Nr.2
	 */
	public static IDELzFhDatum addiere(IDELzFhDatum summand1, IDELzFhDatum summand2) {
		if (summand1 == null || summand2 == null || summand1.isKeineDaten() || summand2.isKeineDaten()) {
			return KEINE_DATEN;
		}

		RechenErgebnis ergebnis = new RechenErgebnis(summand1, summand2);
		FahrzeugArt.getInstanzen().stream()
				.filter((fahrzeugArt) -> summand1.isAuswertbar(fahrzeugArt) && summand2.isAuswertbar(fahrzeugArt))
				.forEach((fahrzeugArt) -> {
					ergebnis.setQ(fahrzeugArt, summand1.getQ(fahrzeugArt) + summand2.getQ(fahrzeugArt));
					ergebnis.setAuswertbar(fahrzeugArt, true);
				});

		return ergebnis;
	}

	/**
	 * Subtrahiert die Argumente in allen Attributen.
	 * 
	 * @param minuend
	 *            der Minuend
	 * @param subtrahend
	 *            der Subtrahend
	 * @return die Differenz von Minuend und Subtrahend
	 */
	public static IDELzFhDatum subtrahiere(IDELzFhDatum minuend, IDELzFhDatum subtrahend) {
		if (minuend == null || subtrahend == null || minuend.isKeineDaten() || subtrahend.isKeineDaten()) {
			return KEINE_DATEN;
		}

		RechenErgebnis ergebnis = new RechenErgebnis(minuend, subtrahend);
		FahrzeugArt.getInstanzen().stream()
				.filter((fahrzeugArt) -> minuend.isAuswertbar(fahrzeugArt) && subtrahend.isAuswertbar(fahrzeugArt))
				.forEach((fahrzeugArt) -> {
					ergebnis.setQ(fahrzeugArt, minuend.getQ(fahrzeugArt) - subtrahend.getQ(fahrzeugArt));
					ergebnis.setAuswertbar(fahrzeugArt, true);
				});

		return ergebnis;
	}

	/**
	 * Subtrahiert die Argumente in allen Attributen.
	 * 
	 * @param minuend
	 *            der Minuend
	 * @param festerSubtrahend
	 *            der Subtrahend
	 * @return die Differenz von Minuend und Subtrahend
	 */
	public static IDELzFhDatum subtrahiere(IDELzFhDatum minuend, double festerSubtrahend) {
		if (minuend.isKeineDaten()) {
			return KEINE_DATEN;
		}

		RechenErgebnis ergebnis = new RechenErgebnis(minuend);
		FahrzeugArt.getInstanzen().stream().filter(minuend::isAuswertbar).forEach((fahrzeugArt) -> {
			ergebnis.setQ(fahrzeugArt, minuend.getQ(fahrzeugArt) - festerSubtrahend);
			ergebnis.setAuswertbar(fahrzeugArt, true);
		});

		return ergebnis;
	}

	/**
	 * Dividiert die beiden Argumente in allen Attributen.
	 * 
	 * @param dividend
	 *            der Dividend
	 * @param divisor
	 *            der Divisor
	 * @return Dividend / Divisor
	 */
	public static IDELzFhDatum dividiere(IDELzFhDatum dividend, IDELzFhDatum divisor) {
		if (dividend.isKeineDaten() || divisor.isKeineDaten()) {
			return KEINE_DATEN;
		}

		RechenErgebnis ergebnis = new RechenErgebnis(dividend, divisor);
		FahrzeugArt.getInstanzen()
				.stream().filter((fahrzeugArt) -> dividend.isAuswertbar(fahrzeugArt)
						&& divisor.isAuswertbar(fahrzeugArt) && divisor.getQ(fahrzeugArt) != 0)
				.forEach((fahrzeugArt) -> {
					ergebnis.setQ(fahrzeugArt, dividend.getQ(fahrzeugArt) / divisor.getQ(fahrzeugArt));
					ergebnis.setAuswertbar(fahrzeugArt, true);
				});

		return ergebnis;
	}

	/**
	 * Multipliziert das erste Argumente in allen Attributen mit dem zweiten.
	 * Argument
	 * 
	 * @param faktor
	 *            Faktor Nr.1
	 * @param festerFaktor
	 *            Faktor Nr.1
	 * @return das Produkt
	 */
	public static IDELzFhDatum multipliziere(IDELzFhDatum faktor, final double festerFaktor) {
		if (faktor.isKeineDaten()) {
			return KEINE_DATEN;
		}

		RechenErgebnis ergebnis = new RechenErgebnis(faktor);
		FahrzeugArt.getInstanzen().stream().filter(faktor::isAuswertbar).forEach((fahrzeugArt) -> {
			ergebnis.setQ(fahrzeugArt, faktor.getQ(fahrzeugArt) * festerFaktor);
			ergebnis.setAuswertbar(fahrzeugArt, true);
		});

		return ergebnis;
	}

	/**
	 * Ein durch eine statische Methode dieser Klasse erzeugtes Rechenergebnis.
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 */
	private static final class RechenErgebnis implements IDELzFhDatum {

		/**
		 * das Systemobjekt, zu dem dieses Ergebnis gehoert, so sich das
		 * eindeutig bestimmen laesst.
		 */
		private SystemObject objekt = null;

		/**
		 * alle hier gespeicherten Werte.
		 */
		private Map<FahrzeugArt, Double> werte = new HashMap<>();

		/**
		 * ob (bzw. inwieweit) alle hier gespeicherten Werte auswertbar ist.
		 */
		private Map<FahrzeugArt, Boolean> auswertbar = new HashMap<>();

		/**
		 * Standardkonstruktor.
		 * 
		 * @param komponenten
		 *            die einzelnen Bestandteile, aus denen das Ergebnis
		 *            zusammengesetzt sein wird.
		 */
		private RechenErgebnis(IDELzFhDatum... komponenten) {
			if (komponenten != null) {
				Set<SystemObject> objekte = new HashSet<>();
				for (IDELzFhDatum komponente : komponenten) {
					if (komponente != null && komponente.getObjekt() != null) {
						objekte.add(komponente.getObjekt());
					}
				}
				if (objekte.size() == 1) {
					this.objekt = objekte.iterator().next();
				}
			}
			FahrzeugArt.getInstanzen().stream().forEach((fahrzeugArt) -> {
				this.auswertbar.put(fahrzeugArt, false);
				this.werte.put(fahrzeugArt, -1.0);
			});
		}

		/**
		 * Setzt, ob der ueber diese Schnittstelle erfragbare Wert fuer die
		 * uebergebene Fahrzeugart auswertbar ist.
		 * 
		 * @param fahrzeugArt
		 *            eine Fahrzeugart
		 * @param auswertbar1
		 *            ob (bzw. inwieweit) alle hier gespeicherten Werte
		 *            auswertbar ist
		 */
		private void setAuswertbar(FahrzeugArt fahrzeugArt, boolean auswertbar1) {
			this.auswertbar.put(fahrzeugArt, auswertbar1);
		}

		/**
		 * Setzt den Q-Wert einer Fahrzeugart.
		 * 
		 * @param fahrzeugArt
		 *            eine Fahrzeugart
		 * @param wert
		 *            der zu setzende Q-Wert
		 */
		private void setQ(FahrzeugArt fahrzeugArt, double wert) {
			this.werte.put(fahrzeugArt, wert);
		}

		@Override
		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			return this.auswertbar.get(fahrzeugArt);
		}

		@Override
		public double getQ(FahrzeugArt fahrzeugArt) {
			return this.werte.get(fahrzeugArt);
		}

		@Override
		public boolean isKeineDaten() {
			return false;
		}

		/**
		 * Erfragt das Systemobjekt, zu dem dieses Ergebnis gehoert, so sich das
		 * eindeutig bestimmen laesst.
		 * 
		 * @return das Systemobjekt, zu dem dieses Ergebnis gehoert
		 */
		@Override
		public SystemObject getObjekt() {
			return this.objekt;
		}

	}

}
