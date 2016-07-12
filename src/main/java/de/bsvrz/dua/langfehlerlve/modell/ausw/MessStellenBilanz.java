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

package de.bsvrz.dua.langfehlerlve.modell.ausw;

import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;
import de.bsvrz.dua.langfehlerlve.modell.Rechenwerk;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatenListener;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;
import de.bsvrz.dua.langfehlerlve.modell.online.Intervall;
import de.bsvrz.dua.langfehlerlve.modell.online.PublikationsKanal;
import de.bsvrz.sys.funclib.debug.Debug;

import java.util.*;

/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Fehlererkennung ueber
 * Differenzbildung fuer eine Messstelle vorgesehen sind. Diese Daten werden
 * hier auch publiziert
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class MessStellenBilanz implements ClientSenderInterface,
		IDELzFhDatenListener {

	/**
	 * Untere Grenze des Attributtyps
	 * <code>att.verkehrsStärkeStundeBilanz</code>.
	 */
	private static final double BILANZ_MIN = -100000000.0;

	/**
	 * Obere Grenze des Attributtyps <code>att.verkehrsStärkeStundeBilanz</code>.
	 */
	private static final double BILANZ_MAX = 100000000.0;

	/**
	 * Zustand des <code>nicht ermittelbar/fehlerhaft</code> des Attributtyps
	 * <code>att.verkehrsStärkeStundeBilanz</code>.
	 */
	private static final long NICHT_ERMITTELBAR_BZW_FEHLERHAFT = -100000003;

	/**
	 * Stellt einen <code>Comperator</code> zur Verfuegung, der mit Hilfe von
	 * <code>TreeSets</code> die Gleichheit aller Intervalle bestimmten soll.
	 */
	private static final Comparator<Intervall> INTERVALL_SORTIERER = new Comparator<Intervall>() {

		public int compare(Intervall int1, Intervall int2) {
			int res = new Long(int1.getStart()).compareTo(int2.getStart());

			if (res == 0) {
				res = new Long(int1.getEnde()).compareTo(int2.getEnde());
			}

			return res;
		}

	};

	/**
	 * statische Verbindung zum Datenverteiler.
	 */
	private static ClientDavInterface dDav = null;

	/**
	 * Datenbeschreibung der zu publizierenden Daten
	 * (Langzeitvergleichsintervall).
	 */
	private static DataDescription pubBeschreibungLz = null;

	/**
	 * Datenbeschreibung der zu publizierenden Daten
	 * (Kurzzeitvergleichsintervall).
	 */
	private static DataDescription pubBeschreibungKz = null;

	/**
	 * Publikationskanal.
	 */
	private PublikationsKanal kanal = null;

	/**
	 * Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll.
	 */
	private boolean langZeit = false;

	/**
	 * Verbindung zu den Onlinedaten der Messstelle selbst.
	 */
	private DELzFhMessStelle messStelle = null;

	/**
	 * Verbindung zu den Onlinedaten des Vorgaengers der Messstelle.
	 */
	private DELzFhMessStelle messStelleMinus1 = null;

	/**
	 * Verbindung zu den Onlinedaten des Hauptmessquerschnitts des Nachfolgers.
	 * der Messstelle.
	 */
	private DELzFhMessQuerschnitt messQuerschnittPlus1 = null;

	/**
	 * Verbindung zu den Onlinedaten des Hauptmessquerschnitts der Messstelle
	 * selbst.
	 */
	private DELzFhMessQuerschnitt messQuerschnitt = null;

	/**
	 * puffert alle aktuellen hier benoetigten Onlinedaten zur Berechnung der
	 * (Zwischen-)Bilanzen.
	 */
	private Map<SystemObject, Intervall> puffer = new HashMap<SystemObject, Intervall>();

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param messStelle
	 *            Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStelleMinus1
	 *            Verbindung zu den Onlinedaten des Vorgaengers der Messstelle
	 * @param messQuerschnittPlus1
	 *            Verbindung zu den Onlinedaten des Hauptmessquerschnitts des
	 *            Nachfolgers der Messstelle
	 * @param messQuerschnitt
	 *            Verbindung zu den Onlinedaten des Hauptmessquerschnitts der
	 *            Messstelle selbst
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws Exception
	 *             wird weitergereicht
	 */
	protected MessStellenBilanz(ClientDavInterface dav,
			DELzFhMessStelle messStelle, DELzFhMessStelle messStelleMinus1,
			DELzFhMessQuerschnitt messQuerschnittPlus1,
			DELzFhMessQuerschnitt messQuerschnitt, boolean langZeit)
			throws Exception {
		if (dDav == null) {
			dDav = dav;
			pubBeschreibungLz = new DataDescription(dav.getDataModel()
					.getAttributeGroup("atg.bilanzVerkehrsStärke"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.messQuerschnittLangZeit")); //$NON-NLS-1$
			pubBeschreibungKz = new DataDescription(dav.getDataModel()
					.getAttributeGroup("atg.bilanzVerkehrsStärke"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit")); //$NON-NLS-1$
		}
		this.kanal = new PublikationsKanal(dav);
		this.langZeit = langZeit;
		this.messStelle = messStelle;
		this.messStelleMinus1 = messStelleMinus1;
		this.messQuerschnittPlus1 = messQuerschnittPlus1;
		this.messQuerschnitt = messQuerschnitt;
		this.initPuffer();

		dav.subscribeSender(this, messStelle.getMessStelle().getSystemObject(),
				langZeit ? pubBeschreibungLz : pubBeschreibungKz, SenderRole
						.source());

		messStelle.addListener(this);
		messStelleMinus1.addListener(this);
		messQuerschnitt.addListener(this);
		messQuerschnittPlus1.addListener(this);
	}

	/**
	 * Initialisiert (loescht) den Online-Puffer dieser Klasse.
	 */
	private void initPuffer() {
		Debug.getLogger().fine(
				getInfo()
						+ "Initialisiere Puffer fuer Messstelle ("
						+ this.messStelle.getMessStelle().getSystemObject()
								.getPid()
						+ "), Messquerschnitt ("
						+ this.messQuerschnitt.getObjekt().getPid()
						+ "), Vorgaenger-Messstelle ("
						+ this.messStelleMinus1.getMessStelle()
								.getSystemObject().getPid()
						+ "), Nachfolger-Messquerschnitt ("
						+ this.messQuerschnittPlus1.getObjekt().getPid() + ")");
		synchronized (this.puffer) {
			Intervall msIntervall = this.puffer.get(this.messStelle
					.getMessStelle().getSystemObject());
			if (msIntervall != null) {
				if (msIntervall.getDatum() == null
						|| (msIntervall.getDatum() != null && !msIntervall
								.getDatum().isKeineDaten())) {
					this.puffer.put(this.messStelle.getMessStelle()
							.getSystemObject(), null);
				}
			} else {
				this.puffer.put(this.messStelle.getMessStelle()
						.getSystemObject(), null);
			}

			Intervall msMinus1Intervall = this.puffer.get(this.messStelleMinus1
					.getMessStelle().getSystemObject());
			if (msMinus1Intervall != null) {
				if (msMinus1Intervall.getDatum() == null
						|| (msMinus1Intervall.getDatum() != null && !msMinus1Intervall
								.getDatum().isKeineDaten())) {
					this.puffer.put(this.messStelleMinus1.getMessStelle()
							.getSystemObject(), null);
				}
			} else {
				this.puffer.put(this.messStelleMinus1.getMessStelle()
						.getSystemObject(), null);
			}

			Intervall mqPlus1Intervall = this.puffer
					.get(this.messQuerschnittPlus1.getObjekt());
			if (mqPlus1Intervall != null) {
				if (mqPlus1Intervall.getDatum() == null
						|| (mqPlus1Intervall.getDatum() != null && !mqPlus1Intervall
								.getDatum().isKeineDaten())) {
					this.puffer
							.put(this.messQuerschnittPlus1.getObjekt(), null);
				}
			} else {
				this.puffer.put(this.messQuerschnittPlus1.getObjekt(), null);
			}

			Intervall mqIntervall = this.puffer.get(this.messQuerschnitt
					.getObjekt());
			if (mqIntervall != null) {
				if (mqIntervall.getDatum() == null
						|| (mqIntervall.getDatum() != null && !mqIntervall
								.getDatum().isKeineDaten())) {
					this.puffer.put(this.messQuerschnitt.getObjekt(), null);
				}
			} else {
				this.puffer.put(this.messQuerschnitt.getObjekt(), null);
			}
		}
	}

	/**
	 * Versucht die Berechnung der Bilanzverkehrsstaerke.
	 * 
	 * @param objekt
	 *            das Systemobjekt, zu dem das gerade empfangene Datum gehoert
	 * @param intervallDatum
	 *            ein gerade empfangenes Intervalldatum != null
	 */
	private void versucheBerechnung(SystemObject objekt,
			Intervall intervallDatum) {

		if (intervallDatum.getDatum().isKeineDaten()) {
			ResultData resultat = new ResultData(messStelle.getMessStelle()
					.getSystemObject(), this.langZeit ? pubBeschreibungLz
					: pubBeschreibungKz, intervallDatum.getStart(), null);
			this.fillPuffer(objekt, intervallDatum);
			this.kanal.publiziere(resultat);
			this.initPuffer();
		} else {
			synchronized (this.puffer) {
				try {
					Intervall protoTyp = this.getPrototypischesPufferElement();

					if (protoTyp == null) {
						Debug
								.getLogger()
								.fine(
										getInfo()
												+ "Puffer (noch vollstaendig leer) wird fuer "
												+ objekt.getPid()
												+ " beschrieben mit:\n"
												+ intervallDatum);
						this.fillPuffer(objekt, intervallDatum);
					} else {
						Debug.getLogger().fine(
								this.getInfo() + "Prototyp: " + protoTyp);
						if (protoTyp.getStart() == intervallDatum.getStart()) {
							Debug.getLogger().fine(
									getInfo() + "Fuege neues Element ein:\n"
											+ intervallDatum);
							this.fillPuffer(objekt, intervallDatum);
						} else {
							this.initPuffer();
							if (protoTyp.getStart() > intervallDatum.getStart()) {
								Debug
										.getLogger()
										.warning(
												getInfo()
														+ "Veralteten Datensatz fuer " + //$NON-NLS-1$
														objekt
														+ " empfangen:\n" + intervallDatum + "\n" + this.toString()); //$NON-NLS-1$
							} else {
								MessStellenBilanz.this.puffer.put(objekt,
										intervallDatum);
							}
						}
					}

					if (isAlleDatenVollstaendig()) {
						this.erzeugeErgebnis();
						this.initPuffer();
					}
				} catch (PufferException e) {
					Debug
							.getLogger()
							.error(
									getInfo()
											+ "Intervallanfang kann nicht betimmt werden.",
									e);
					this.initPuffer();
				}
			}
		}
	}

	/**
	 * Speichert ein Intervalldatum.
	 * 
	 * @param objekt
	 *            das Systemobjekt, zu dem das zu speichernde Datum gehoert
	 * @param intervallDatum
	 *            ein zu speicherndes Intervalldatum != null
	 */
	public void fillPuffer(SystemObject objekt, Intervall intervallDatum) {
		String debug = "Fuege Element ein fuer " + objekt.getPid() + ":\n"
				+ intervallDatum;

		synchronized (this.puffer) {
			this.puffer.put(objekt, intervallDatum);
		}

		Debug.getLogger().fine(getInfo() + debug + "\n" + this.toString());
	}

	/**
	 * Erfragt ob alle Daten vollstaendig sind.
	 * 
	 * @return ob alle Daten vollstaendig sind.
	 */
	private boolean isAlleDatenVollstaendig() {
		boolean datenVollstaendig = true;

		synchronized (this.puffer) {
			for (SystemObject obj : this.puffer.keySet()) {
				if (this.puffer.get(obj) == null
						|| this.puffer.get(obj).getDatum().isKeineDaten()) {
					datenVollstaendig = false;
					break;
				}
			}
		}

		return datenVollstaendig;
	}

	/**
	 * Erfragt ein in Bezug auf Intervallanfang und -ende prototypisches
	 * Pufferelement mit Nutzdaten. Das Element hat die Eigenschaft, den
	 * gleichen Start- und Endezeitstempel zu besitzen wie sämtliche anderen
	 * Elemente (mit Nutzdaten) im Puffer auch.
	 * 
	 * @return ein in Bezug auf Intervallanfang und -ende prototypisches
	 *         Pufferelement mit Nutzdaten oder <code>null</code>, wenn keine
	 *         (Nutz-)Daten im Puffer stehen.
	 * @throws PufferException
	 *             wenn sich die Intervalle der gespeicherten Objekte
	 *             unterscheiden
	 */
	public Intervall getPrototypischesPufferElement() throws PufferException {
		Intervall ergebnis = null;

		SortedMap<Intervall, SystemObject> intervalleSortiert = new TreeMap<Intervall, SystemObject>(
				INTERVALL_SORTIERER);
		synchronized (this.puffer) {
			for (SystemObject obj : this.puffer.keySet()) {
				Intervall intervall = this.puffer.get(obj);
				if (intervall != null && intervall.getDatum() != null
						&& !intervall.getDatum().isKeineDaten()) {
					intervalleSortiert.put(intervall, obj);
				}
			}
			if (intervalleSortiert.size() > 0) {
				if (intervalleSortiert.size() > 1) {
					String fehlerMeldung = "\nInkompatible Intervalle im Puffer:\n";
					for (Intervall intervall : intervalleSortiert.keySet()) {
						fehlerMeldung += intervalleSortiert.get(intervall)
								.getPid()
								+ ": " + intervall + "\n";
					}
					throw new PufferException(fehlerMeldung);
				}
				ergebnis = intervalleSortiert.firstKey();
			}
		}

		return ergebnis;
	}

	/**
	 * Berechnet und publiziert den Bilanzwert im Vergleichsintervall analog Afo
	 * DUA-BW-C1C2-7 fuer alle zur Zeit im lokalen Puffer stehenden Daten.
	 */
	private void erzeugeErgebnis() {
		Debug.getLogger().fine(
				getInfo() + "Erzeuge VerkehrsStaerkeStundeBilanz fuer "
						+ this.messStelle.getMessStelle().getPid());

		IDELzFhDatum bilanz = null;
		long datenZeit = -1;
		synchronized (this.puffer) {
			datenZeit = this.puffer.get(this.messQuerschnitt.getObjekt())
					.getStart();

			IDELzFhDatum zwischenBilanzI = Rechenwerk.subtrahiere(this.puffer
					.get(this.messQuerschnitt.getObjekt()).getDatum(),
					this.puffer.get(
							this.messStelleMinus1.getMessStelle()
									.getSystemObject()).getDatum());
			IDELzFhDatum zwischenBilanzIPlus1 = Rechenwerk.subtrahiere(
					this.puffer.get(this.messQuerschnittPlus1.getObjekt())
							.getDatum(), this.puffer.get(
							this.messStelle.getMessStelle().getSystemObject())
							.getDatum());

			bilanz = Rechenwerk.subtrahiere(zwischenBilanzI,
					zwischenBilanzIPlus1);
		}

		DataDescription datenBeschreibung = this.langZeit ? pubBeschreibungLz
				: pubBeschreibungKz;
		Data nutzDatum = dDav.createData(datenBeschreibung.getAttributeGroup());

		for (FahrzeugArt fahrzeugArt : FahrzeugArt.getInstanzen()) {
			if (bilanz.isAuswertbar(fahrzeugArt)
					&& bilanz.getQ(fahrzeugArt) >= BILANZ_MIN
					&& bilanz.getQ(fahrzeugArt) <= BILANZ_MAX) {
				nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(
						bilanz.getQ(fahrzeugArt));
			} else {
				nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(
						NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}
		}

		ResultData publikationsDatum = new ResultData(this.messStelle
				.getMessStelle().getSystemObject(), datenBeschreibung,
				datenZeit, nutzDatum);

		this.kanal.publiziere(publikationsDatum);
	}

	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// Quellenanmeldung
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereDatum(SystemObject mqObjekt,
			Intervall intervallDatum) {
		Debug.getLogger().fine(
				getInfo() + "Datum fuer " + mqObjekt + " empfangen:\n"
						+ intervallDatum);
		MessStellenBilanz.this.versucheBerechnung(mqObjekt, intervallDatum);
	}

	/**
	 * Erfragt eine Debug-Information ueber die Instanz dieser Klasse.
	 * 
	 * @return eine Debug-Information ueber die Instanz dieser Klasse.
	 */
	private String getInfo() {
		return "-->"
				+ this.messStelle.getMessStelle().getSystemObject().getPid()
				+ "<--\n";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String text = "Pufferinhalt:\n";
		synchronized (this.puffer) {
			for (SystemObject obj : this.puffer.keySet()) {
				Intervall intervall = this.puffer.get(obj);
				text += "  " + obj.getPid() + ": "
						+ (intervall == null ? "<<null>>\n" : intervall + "\n");
			}
		}

		return text;
	}

	/**
	 * Sollte geworfen werden, wenn beim Einfuegen eines Intervalls in diesen
	 * Puffer die Intervalle (in Bezug auf ihre Intervallgrenzen) inkompatibel
	 * werden.
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 * @version $Id$
	 */
	private class PufferException extends Exception {

		/**
		 * Standardkonstruktor.
		 * 
		 * @param meldung
		 *            eine Fehlermeldung.
		 */
		public PufferException(String meldung) {
			super(meldung);
		}

	}

}
