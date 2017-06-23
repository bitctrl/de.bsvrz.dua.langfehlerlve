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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;
import de.bsvrz.dua.langfehlerlve.modell.Rechenwerk;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatenListener;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;
import de.bsvrz.dua.langfehlerlve.modell.online.Intervall;
import de.bsvrz.dua.langfehlerlve.modell.online.PublikationsKanal;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStelle;

/**
 * Diese Klasse korrespondiert mit einem DAV-Objekt vom Typ
 * <code>typ.messStelle</code> und kapselt direkt oder indirekt saemtliche
 * Funktionalitäten, die innerhalb der SWE DE Langzeit-Fehlererkennung in Bezug
 * auf Objekte dieses Typs benoetigt werden. Ggf. (bei Intervallende) werden die
 * hier aggregierten DELzFh-Werte an die Messstellengruppe zur Analyse
 * weitergereicht
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class DELzFhMessStelle extends AbstraktDELzFhObjekt implements IDELzFhDatenListener, ClientSenderInterface {

	/**
	 * diese Messstelle.
	 */
	private MessStelle messStelle = null;

	/**
	 * Datenbeschreibung zur Veroeffentlichung von MQ-Daten fuer die
	 * Intervallverkehrsstaerke.
	 */
	private DataDescription mqDb = null;

	/**
	 * Datenbeschreibung zur Veroeffentlichung von MS-Daten fuer die
	 * Intervallverkehrsstaerke.
	 */
	private DataDescription msDb = null;

	/**
	 * Aktuelle Daten aller mit der Messstelle assoziierten Messquerschnitte.
	 */
	private Map<SystemObject, Intervall> mqPuffer = new HashMap<>();

	/**
	 * Menge von Beobachtern der Online-Daten dieses Objektes.
	 */
	private Set<IDELzFhDatenListener> listenerMenge = new HashSet<>();

	/**
	 * wenn dieser Wert auf <code>!= null</code> steht, bedeutet das, dass das
	 * letzte eingetroffene Datum fuer dieses Objekt das erste des (neuen)
	 * Intervalls I + 1 ist und das mindestens ein Datum fuer das Intervall I
	 * vorhanden ist. In diesem Objekt stehen die Intervallgrenzen.
	 */
	private Intervall fertigesIntervall = null;

	/**
	 * Publikationskanal fuer MQ-Daten.
	 */
	private PublikationsKanal mqKanal = null;

	/**
	 * Publikationskanal fuer MS-Daten.
	 */
	private PublikationsKanal msKanal = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param msObjekt
	 *            Systemobjekt vom Typ <code>typ.messStelle</code>
	 * @param messStellenGruppe
	 *            die mit diesem Objekt assoziierte Messstellengruppe
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws Exception
	 *             wenn das Objekt nicht sinnvoll initialisiert werden konnte
	 */
	protected DELzFhMessStelle(ClientDavInterface dav, SystemObject msObjekt, DELzFhMessStellenGruppe messStellenGruppe,
			final boolean langZeit) throws Exception {
		this.messStelle = MessStelle.getInstanz(msObjekt);

		this.mqKanal = new PublikationsKanal(dav);
		this.msKanal = new PublikationsKanal(dav);

		this.mqDb = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit"));
		this.msDb = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messStelleKurzZeit"));
		if (langZeit) {
			this.mqDb = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsStärke"),
					dav.getDataModel().getAspect("asp.messQuerschnittLangZeit"));
			this.msDb = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsStärke"),
					dav.getDataModel().getAspect("asp.messStelleLangZeit"));
		}

		super.init(dav, messStellenGruppe, langZeit);

		if (this.messStelle != null) {

			dav.subscribeSender(this, this.messStelle.getSystemObject(), this.mqDb, SenderRole.source());
			dav.subscribeSender(this, this.messStelle.getSystemObject(), this.msDb, SenderRole.source());

			if (this.messStelle.getPruefling() != null) {
				messStellenGruppe.getMq(this.messStelle.getPruefling().getSystemObject()).addListener(this);

				this.messStelle.getAbfahrten().stream()
						.forEach((abfahrt) -> messStellenGruppe.getMq(abfahrt.getSystemObject()).addListener(this));
				this.messStelle.getZufahrten().stream()
						.forEach((zufahrt) -> messStellenGruppe.getMq(zufahrt.getSystemObject()).addListener(this));
				this.initMQPuffer();
			} else {
				System.out.println(this.messStelle.getPruefling());
				throw new DUAInitialisierungsException("Messstelle " + msObjekt + " besitzt keinen Pruefling (MQ)");
			}
		} else {
			throw new DUAInitialisierungsException("Messstelle " + msObjekt + " konnte nicht ausgelesen werden");
		}
	}

	/**
	 * Erfragt das Strukturobjekt dieser Messstelle.
	 * 
	 * @return das Strukturobjekt dieser Messstelle
	 */
	public final MessStelle getMessStelle() {
		return this.messStelle;
	}

	/**
	 * Initialisiert bzw. loescht den MQ-Puffer
	 */
	private void initMQPuffer() {
		synchronized (this.mqPuffer) {
			this.mqPuffer.put(this.messStelle.getPruefling().getSystemObject(), null);

			this.messStelle.getAbfahrten().stream()
					.forEach((abfahrt) -> this.mqPuffer.put(abfahrt.getSystemObject(), null));
			this.messStelle.getZufahrten().stream()
					.forEach((zufahrt) -> this.mqPuffer.put(zufahrt.getSystemObject(), null));
		}
	}

	/**
	 * Fuegt diesem Objekt einen neuen Listener hinzu und informiert diesen ggf.
	 * ueber aktuelle Daten
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public final void addListener(final IDELzFhDatenListener listener) {
		synchronized (this.listenerMenge) {
			if (this.listenerMenge.add(listener) && this.fertigesIntervall != null) {
				listener.aktualisiereDatum(this.messStelle.getSystemObject(), this.fertigesIntervall);
			}
		}
	}

	@Override
	public void dataRequest(SystemObject object, DataDescription dataDescription, byte state) {
		// Quellenanmeldung
	}

	@Override
	public boolean isRequestSupported(SystemObject object, DataDescription dataDescription) {
		return false;
	}

	/**
	 * Untersucht, ob eine Berechnung des Q-Wertes fuer Messstellen eingeleitet
	 * werden kann (durch das empfangene Datum getriggert). Dies ist der Fall,
	 * wenn ein Datum
	 * 
	 * @param mqObjekt
	 *            ein Systemobjekt des MQ, fuer den Daten empfangen wurden
	 * @param neuesDatum
	 *            ein MQ-Datum mit Nutzdaten
	 */
	private synchronized void versucheMessStellenBerechnung(SystemObject mqObjekt, Intervall neuesDatum) {
		synchronized (this.mqPuffer) {
			if (this.mqPuffer.size() > 0) {
				if (this.mqPuffer.size() > 1) {
					int schonEingetroffeneIntervalle = 0;

					for (SystemObject mq : this.mqPuffer.keySet()) {
						Intervall aktuellesIntervall = this.mqPuffer.get(mq);
						if (aktuellesIntervall != null) {
							if (neuesDatum.getStart() > aktuellesIntervall.getStart()) {
								veroeffentlicheAktuellenMsWert();
								this.mqPuffer.put(mqObjekt, neuesDatum);
								return;
							} else {
								if (neuesDatum.getStart() == aktuellesIntervall.getStart()) {
									schonEingetroffeneIntervalle++;
									// } else {
									// throw new RuntimeException(
									// "Veralteten Zeitstempel empfangen: " +
									// mqObjekt);
								}
							}
						}
					}
					this.mqPuffer.put(mqObjekt, neuesDatum);

					if (this.mqPuffer.size() - 1 == schonEingetroffeneIntervalle) {
						veroeffentlicheAktuellenMsWert();
					}
				} else {
					this.mqPuffer.put(mqObjekt, neuesDatum);
					veroeffentlicheAktuellenMsWert();
				}
			}
		}
	}

	/**
	 * Berechnet und veroeffentlicht ein MS-Datum auf Basis der im MQ-Puffer
	 * gespeicherten Daten. Danach werde alle gepufferten Daten wieder gelöscht
	 */
	private void veroeffentlicheAktuellenMsWert() {
		IDELzFhDatum pDatum = null;
		Set<IDELzFhDatum> abfahrtsDaten = new HashSet<>();
		Set<IDELzFhDatum> zufahrtsDaten = new HashSet<>();

		Intervall intervall = null;

		for (SystemObject mq : this.mqPuffer.keySet()) {
			Intervall aktuellesIntervall = this.mqPuffer.get(mq);
			if (aktuellesIntervall != null) {
				intervall = aktuellesIntervall;

				boolean brk = false;
				if (this.messStelle.getPruefling().getSystemObject().equals(mq)) {
					pDatum = aktuellesIntervall.getDatum();
					brk = true;
				}

				if (!brk) {
					for (MessQuerschnittAllgemein zufahrt : this.messStelle.getZufahrten()) {
						if (zufahrt.getSystemObject().equals(mq)) {
							zufahrtsDaten.add(aktuellesIntervall.getDatum());
							brk = true;
							break;
						}
					}
				}

				if (!brk) {
					for (MessQuerschnittAllgemein abfahrt : this.messStelle.getAbfahrten()) {
						if (abfahrt.getSystemObject().equals(mq)) {
							abfahrtsDaten.add(aktuellesIntervall.getDatum());
							break;
						}
					}
				}
			}
		}

		IDELzFhDatum ergebnis = Rechenwerk.subtrahiere(Rechenwerk.addiere(pDatum, Rechenwerk.addiere(zufahrtsDaten)),
				Rechenwerk.addiere(abfahrtsDaten));

		this.fertigesIntervall = new Intervall(intervall.getStart(), intervall.getEnde(), ergebnis);

		Data nutzDaten = dDav.createData(this.msDb.getAttributeGroup());
		FahrzeugArt.getInstanzen().stream().forEach((fahrzeugArt) -> {
			if (this.fertigesIntervall.getDatum().getQ(fahrzeugArt) < 0.0) {
				nutzDaten.getUnscaledValue(fahrzeugArt.getAttributName())
						.set(DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			} else {
				nutzDaten.getUnscaledValue(fahrzeugArt.getAttributName())
						.set(Math.round(this.fertigesIntervall.getDatum().getQ(fahrzeugArt)));
			}
		});

		ResultData msIntervallVSResultat = new ResultData(this.messStelle.getSystemObject(), this.msDb,
				this.fertigesIntervall.getStart(), nutzDaten);
		this.msKanal.publiziere(msIntervallVSResultat);

		synchronized (this.listenerMenge) {
			this.listenerMenge.stream().forEach((listener) -> listener
					.aktualisiereDatum(this.messStelle.getSystemObject(), this.fertigesIntervall));
		}

		/**
		 * Datenpuffer initialisieren
		 */
		this.initMQPuffer();
	}

	@Override
	public void aktualisiereDatum(SystemObject mqObjekt, Intervall intervallDatum) {
		if (intervallDatum.getDatum().isKeineDaten()) {
			ResultData msIntervallVSResultat = new ResultData(this.messStelle.getSystemObject(), this.msDb,
					intervallDatum.getStart(), null);
			this.fertigesIntervall = intervallDatum;
			synchronized (this.listenerMenge) {
				this.listenerMenge.stream().forEach((listener) -> listener
						.aktualisiereDatum(this.messStelle.getSystemObject(), this.fertigesIntervall));
			}
			this.msKanal.publiziere(msIntervallVSResultat);
		} else {
			this.versucheMessStellenBerechnung(mqObjekt, intervallDatum);
		}

		if (this.messStelle.getPruefling().getSystemObject().equals(mqObjekt)) {
			Data nutzDaten = null;
			if (!intervallDatum.getDatum().isKeineDaten()) {
				nutzDaten = dDav.createData(this.mqDb.getAttributeGroup());
				for (FahrzeugArt fahrzeugArt : FahrzeugArt.getInstanzen()) {
					if (intervallDatum.getDatum().getQ(fahrzeugArt) < 0.0) {
						nutzDaten.getUnscaledValue(fahrzeugArt.getAttributName())
								.set(DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
					} else {
						nutzDaten.getUnscaledValue(fahrzeugArt.getAttributName())
								.set(Math.round(intervallDatum.getDatum().getQ(fahrzeugArt)));
					}
				}
			}

			ResultData mqIntervallVSResultat = new ResultData(this.messStelle.getSystemObject(), this.mqDb,
					intervallDatum.getStart(), nutzDaten);
			this.mqKanal.publiziere(mqIntervallVSResultat);
		}
	}

	@Override
	protected void aktualisiereMsgParameter(IMsgDatenartParameter parameter) {
		this.initMQPuffer();
	}

}
