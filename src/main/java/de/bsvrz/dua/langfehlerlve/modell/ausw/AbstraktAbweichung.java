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
import de.bsvrz.sys.funclib.bitctrl.daf.BetriebsmeldungIdKonverter;
import de.bsvrz.sys.funclib.bitctrl.daf.DefaultBetriebsMeldungsIdKonverter;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageTemplate;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Erkennung systematischer
 * Detektorfehler fuer eine Messstelle vorgesehen sind (Afo DUA-BW-C1C2-11 bis
 * Afo DUA-BW-C1C2-17: Vergleich mit allen anderen Messstellen bzw. der
 * Vorgaengermessstelle und ggf. Ausgabe einer Betriebsmeldung). Diese Daten
 * werden hier auch publiziert
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public abstract class AbstraktAbweichung extends AbstraktDELzFhObjekt implements
		ClientSenderInterface, IDELzFhDatenListener {

	private static final BetriebsmeldungIdKonverter KONVERTER = new DefaultBetriebsMeldungsIdKonverter();

	/**
	 * <code>atg.abweichungVerkehrsStärke</code>.
	 */
	static final String ATG_PID = "atg.abweichungVerkehrsStärke"; //$NON-NLS-1$

	/**
	 * Untere Grenze des Attributtyps <code>att.prozentPlusMinus</code>.
	 */
	private static final long PROZENT_MIN = -100000;

	/**
	 * Obere Grenze des Attributtyps <code>att.prozentPlusMinus</code>.
	 */
	private static final long PROZENT_MAX = 100000;

	/**
	 * Zustand des <code>nicht ermittelbar/fehlerhaft</code> des Attributtyps
	 * <code>att.prozentPlusMinus</code>.
	 */
	private static final long NICHT_ERMITTELBAR_BZW_FEHLERHAFT = -100003;

	/**
	 * Zeitausgabeformat fuer Betriebsmeldungen.
	 */
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm"); //$NON-NLS-1$

	/**
	 * Publikationskanal.
	 */
	private PublikationsKanal kanal = null;

	/**
	 * Verbindung zu den Onlinedaten der Messstelle selbst.
	 */
	private DELzFhMessStelle messStelle = null;

	/**
	 * Verbindung zu den Onlinedaten des Hauptmessquerschnitts der Messstelle
	 * selbst.
	 */
	private DELzFhMessQuerschnitt messQuerschnitt = null;

	/**
	 * alle restlichen Messstellen, zu denen diese Messstelle ins Verhaeltnis
	 * gesetzt werden soll.
	 */
	protected Set<SystemObject> restMessStellen = new HashSet<SystemObject>();

	/**
	 * puffert alle aktuellen hier benoetigten Onlinedaten zur Berechnung der
	 * (Zwischen-)Bilanzen.
	 */
	protected Map<SystemObject, Intervall> puffer = Collections
			.synchronizedMap(new HashMap<SystemObject, Intervall>());

	/**
	 * die maximal zulässige Toleranz für die Abweichung von Messwerten beim
	 * Vergleich mit dem Vorgänger beim Kurzzeitintervall für die
	 * Langzeitfehlererkennung von Verkehrsdaten.
	 */
	protected int abweichungMax = -1;

	/**
	 * Die Laenge des Vergleichsintervalls als Text.
	 */
	protected String vergleichsIntervall = "";

	/**
	 * Erfragt die PID des Aspektes, unter dem hier die Daten des Kurzzeit-
	 * Vergleichsintervalls veroeffentlicht werden.
	 * 
	 * @return die PID des Aspektes, unter dem hier die Daten des Kurzzeit-
	 *         Vergleichsintervalls veroeffentlicht werden
	 */
	protected abstract String getKzAspPid();

	/**
	 * Erfragt die PID des Aspektes, unter dem hier die Daten des Langzeit-
	 * Vergleichsintervalls veroeffentlicht werden.
	 * 
	 * @return die PID des Aspektes, unter dem hier die Daten des Langzeit-
	 *         Vergleichsintervalls veroeffentlicht werden
	 */
	protected abstract String getLzAspPid();

	/**
	 * Erfragt eine Identifikation der Vergleichsmethode.
	 * 
	 * @return eine Identifikation der Vergleichsmethode
	 */
	protected abstract String getVergleichsIdentifikation();
	
	/** Text der Betriebsmeldung */
	private static final MessageTemplate MESSAGE_TEMPLATE = new MessageTemplate(
			MessageGrade.WARNING, 
			MessageType.APPLICATION_DOMAIN,
	        MessageTemplate.fixed("Langzeitmessfehler: Der Wert "),
	        MessageTemplate.variable("attr"),
	        MessageTemplate.fixed(" weicht um mehr als "),
			MessageTemplate.variable("abw"),
	        MessageTemplate.fixed(" % vom erwarteten Wert im Intervall "),
			MessageTemplate.variable("von"),
			MessageTemplate.fixed(" - "),
			MessageTemplate.variable("bis"),
			MessageTemplate.fixed(" ("),
			MessageTemplate.variable("dauer"),
			MessageTemplate.fixed(") ab am "),
			MessageTemplate.object(),
			MessageTemplate.fixed(". "),
	        MessageTemplate.ids()
	).withIdFactory(message -> message.getObject().getPidOrId() + " [DUA-PP-MA]");
	
	/**
	 * Format der Zeitangabe innerhalb der Betriebsmeldung. 
	 */
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.GERMAN);

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param messStelle
	 *            Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStellenGruppe
	 *            Messstellengruppe an der diese Berechnung erfolgt
	 * @param restMessStellen
	 *            alle restlichen Messstellen, zu denen diese Messstelle ins
	 *            Verhaeltnis gesetzt werden soll
	 * @param messQuerschnitt
	 *            Verbindung zu den Onlinedaten des Hauptmessquerschnitts der
	 *            Messstelle selbst
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws Exception
	 *             wird weitergereicht
	 */
	protected AbstraktAbweichung(ClientDavInterface dav,
			DELzFhMessStelle messStelle,
			DELzFhMessStellenGruppe messStellenGruppe,
			DELzFhMessStelle[] restMessStellen,
			DELzFhMessQuerschnitt messQuerschnitt, boolean langZeit)
			throws Exception {
		super.init(dav, messStellenGruppe, langZeit);

		this.kanal = new PublikationsKanal(dav);

		this.messStelle = messStelle;
		this.messQuerschnitt = messQuerschnitt;
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
					.getSystemObject(), langZeit ? new DataDescription(dDav
					.getDataModel().getAttributeGroup(ATG_PID), dDav
					.getDataModel().getAspect(this.getLzAspPid()))
					: new DataDescription(dDav.getDataModel()
							.getAttributeGroup(ATG_PID), dDav.getDataModel()
							.getAspect(this.getKzAspPid())),
					intervallDatum.getStart(), null);
			this.kanal.publiziere(resultat);
			this.initPuffer();
		} else {
			synchronized (this.puffer) {
				long pufferZeit = -1;
				for (SystemObject obj : this.puffer.keySet()) {
					if (this.puffer.get(obj) != null) {
						pufferZeit = this.puffer.get(obj).getStart();
						break;
					}
				}
				if (pufferZeit == -1) {
					this.puffer.put(objekt, intervallDatum);
				} else {
					if (pufferZeit == intervallDatum.getStart()) {
						this.puffer.put(objekt, intervallDatum);
					} else {
						this.initPuffer();
						if (pufferZeit > intervallDatum.getStart()) {
							Debug.getLogger().warning(
									"Veralteten Datensatz fuer " + //$NON-NLS-1$
											objekt
											+ " empfangen:\n" + intervallDatum); //$NON-NLS-1$
						} else {
							this.puffer.put(objekt, intervallDatum);
						}
					}
				}

				boolean datenVollstaendig = true;
				for (SystemObject obj : this.puffer.keySet()) {
					if (this.puffer.get(obj) == null) {
						datenVollstaendig = false;
						break;
					}
				}

				if (datenVollstaendig) {
					this.erzeugeErgebnis();
					this.initPuffer();
				}
			}
		}
	}

	/**
	 * Initialisiert (loescht) den Online-Puffer dieser Klasse.
	 */
	synchronized void initPuffer() {
		this.puffer.put(this.messQuerschnitt.getObjekt(), null);
		for (SystemObject rms : this.restMessStellen) {
			this.puffer.put(rms, null);
		}
	}

	/**
	 * Berechnet und publiziert die Abweichung im Vergleichsintervall analog Afo
	 * DUA-BW-C1C2-11 fuer alle zur Zeit im lokalen Puffer stehenden Daten.
	 */
	private void erzeugeErgebnis() {
		IDELzFhDatum abweichung = null;
		long datenZeit = -1;
		long intervallEnde = -1;

		synchronized (this.puffer) {
			datenZeit = this.puffer.get(this.messQuerschnitt.getObjekt())
					.getStart();
			intervallEnde = this.puffer.get(this.messQuerschnitt.getObjekt())
					.getEnde();

			Collection<IDELzFhDatum> restDaten = new HashSet<IDELzFhDatum>();
			for (SystemObject rms : this.restMessStellen) {
				restDaten.add(this.puffer.get(rms).getDatum());
			}

			abweichung = Rechenwerk.multipliziere(Rechenwerk.dividiere(
					this.puffer.get(this.messQuerschnitt.getObjekt())
							.getDatum(), Rechenwerk.durchschnitt(restDaten)),
					100.0);
		}

		DataDescription datenBeschreibung = langZeit ? new DataDescription(dDav
				.getDataModel().getAttributeGroup(ATG_PID), dDav.getDataModel()
				.getAspect(this.getLzAspPid())) : new DataDescription(dDav
				.getDataModel().getAttributeGroup(ATG_PID), dDav.getDataModel()
				.getAspect(this.getKzAspPid()));
		Data nutzDatum = dDav.createData(datenBeschreibung.getAttributeGroup());

		for (FahrzeugArt fahrzeugArt : FahrzeugArt.getInstanzen()) {

			if (abweichung.isAuswertbar(fahrzeugArt)) {
				long abweichungMinus100 = Math.round(abweichung
						.getQ(fahrzeugArt) - 100.0);

				if (abweichungMinus100 >= PROZENT_MIN
						&& abweichungMinus100 <= PROZENT_MAX) {
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName())
							.set(abweichungMinus100);
				} else {
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName())
							.set(NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
				}

				synchronized (this) {
					if (this.abweichungMax > 0) {
						if (Math.abs(abweichungMinus100) > this.abweichungMax) {
							OperatingMessage message = MESSAGE_TEMPLATE.newMessage(this.messStelle.getMessStelle().getPruefling().getSystemObject());
							message.put("attr", fahrzeugArt.getAttributName());
							message.put("von", formatDate(Instant.ofEpochMilli(datenZeit)));
							message.put("bis", formatDate(Instant.ofEpochMilli(intervallEnde)));
							message.put("dauer", formatDuration(intervallEnde-datenZeit));
							message.put("abw", abweichungMax);
							message.addId("[DUA-LF-MA01]");
							message.send();
						}
					}
				}
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
	 * Formatiert ein Zeitbereich
	 * @param tmp Dauer in Millisekunden
	 * @return Ein Text wie "1 Stunde 13 Minuten"
	 */
	public static String formatDuration(long tmp) {
		long ms = tmp % 1000;
		tmp /= 1000;
		long sec = tmp % 60;
		tmp /= 60;
		long min = tmp % 60;
		tmp /= 60;
		long h = tmp;
		StringBuilder stringBuilder = new StringBuilder();
		if(h >= 1){
			if(h == 1){
				stringBuilder.append("1 Stunde ");
			}
			else {
				stringBuilder.append(h).append(" Stunden ");
			}
		}
		if(min >= 1){
			if(min == 1){
				stringBuilder.append("1 Minute ");
			}
			else {
				stringBuilder.append(min).append(" Minuten ");
			}
		}
		if(sec >= 1){
			if(sec == 1){
				stringBuilder.append("1 Sekunde ");
			}
			else {
				stringBuilder.append(sec).append(" Sekunden ");
			}
		}
		if(ms >= 1){
			if(ms == 1){
				stringBuilder.append("1 Millisekunde ");
			}
			else {
				stringBuilder.append(ms).append(" Millisekunden ");
			}
		}
		stringBuilder.setLength(stringBuilder.length()-1);
		return stringBuilder.toString();
	}

	/**
	 * Formatiert ein Datum
	 * @param dateTime Zeitpunkt
	 * @return String-Wert
	 */
	public static String formatDate(final Instant dateTime) {
		return DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()));
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
	public void aktualisiereDatum(SystemObject objekt, Intervall intervallDatum) {
		this.versucheBerechnung(objekt, intervallDatum);
	}

}
