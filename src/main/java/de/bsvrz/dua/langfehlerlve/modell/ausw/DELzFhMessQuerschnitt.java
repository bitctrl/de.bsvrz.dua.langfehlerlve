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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;
import de.bsvrz.dua.langfehlerlve.modell.Rechenwerk;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatenListener;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;
import de.bsvrz.dua.langfehlerlve.modell.online.Intervall;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.schnittstellen.IZeitStempel;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Diese Klasse korrespondiert mit einem DAV-Objekt vom Typ
 * <code>typ.messQuerschnittAllgemein</code> und kapselt direkt oder indirekt
 * saemtliche Funktionalitaeten, die innerhalb der SWE DE
 * Langzeit-Fehlererkennung in Bezug auf Objekte dieses Typs benoetigt werden.
 * Insbesondere werden hier alle MQ-Werte des letzten Intervalls vorgehalten und
 * in korrespondierende DELzFh-Werte uebersetzt. Diese Werte werden dann an die
 * Messstelle bzw. die Messstellengruppe weitergeleitet
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class DELzFhMessQuerschnitt extends AbstraktDELzFhObjekt implements ClientReceiverInterface {

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * nach Zeitstempeln sortierter Datenpuffer.
	 */
	private SortedSet<MQDatum> puffer = new TreeSet<>();

	/**
	 * aktuelle Maximallaenge des Pufferintervalls.
	 */
	private long intervallLaenge = (long) (24 * 60 * 60 * 1000);

	/**
	 * zeigt an, ob die Intervalllaenge bereits initialisiert wurde.
	 */
	private boolean intervallLaengeInitialisiert = false;

	/**
	 * das mit diesem Messquerschnitt assoziierte Systemobjekt.
	 */
	private SystemObject mqObjekt = null;

	/**
	 * Menge von Beobachtern der Online-Daten dieses Objektes.
	 */
	private Set<IDELzFhDatenListener> listenerMenge = new HashSet<>();

	/**
	 * wenn dieser Wert auf <code>!= null</code> steht, bedeutet das, dass das
	 * letzte eingetroffene Datum fuer dieses Objekt das erste des (neuen)
	 * Intervalls I + 1 ist und das mindestens ein Datum fuer das Intervall I
	 * vorhanden ist. In diesem Objekt stehen die Intervallgrenzen
	 */
	private Intervall fertigesIntervall = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param mqObjekt
	 *            Systemobjekt vom Typ <code>typ.messQuerschnittAllgemein</code>
	 * @param messStellenGruppe
	 *            die mit diesem Objekt assoziierte Messstellengruppe
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws DUAInitialisierungsException
	 *             wenn das Objekt nicht sinnvoll initialisiert werden konnte
	 */
	protected DELzFhMessQuerschnitt(ClientDavInterface dav, SystemObject mqObjekt,
			DELzFhMessStellenGruppe messStellenGruppe, final boolean langZeit) throws DUAInitialisierungsException {
		this.intervallLaengeInitialisiert = false;
		this.mqObjekt = mqObjekt;

		DataDescription fsAnalyseDatenBeschreibung = new DataDescription(
				dav.getDataModel().getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_MQ),
				dav.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		super.init(dav, messStellenGruppe, langZeit);

		dav.subscribeReceiver(this, mqObjekt, fsAnalyseDatenBeschreibung, ReceiveOptions.normal(),
				ReceiverRole.receiver());
	}

	/**
	 * Erfragt das mit diesem Messquerschnitt assoziierte Systemobjekt.
	 * 
	 * @return das mit diesem Messquerschnitt assoziierte Systemobjekt
	 */
	public final SystemObject getObjekt() {
		return this.mqObjekt;
	}

	/**
	 * Fuegt diesem Objekt einen neuen Listener hinzu und informiert diesen ggf.
	 * ueber aktuelle Daten
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public final void addListener(final IDELzFhDatenListener listener) {
		synchronized (this) {
			if (this.listenerMenge.add(listener) && this.fertigesIntervall != null) {
				listener.aktualisiereDatum(this.mqObjekt, this.fertigesIntervall);
			}
		}
	}

	@Override
	public void update(ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if (resultat != null) {
					if (resultat.getData() == null) {
						synchronized (this) {
							this.fertigesIntervall = new Intervall(resultat.getDataTime(), resultat.getDataTime(),
									new IDELzFhDatum() {

										@Override
										public double getQ(FahrzeugArt fahrzeugArt) {
											return -1;
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
											return mqObjekt;
										}

									});

							this.listenerMenge.stream().forEach(
									(listener) -> listener.aktualisiereDatum(this.mqObjekt, this.fertigesIntervall));
							LOGGER.finer("Habe \"keine Daten\" empfangen. Puffer wird geloescht.", this);
							this.puffer.clear();
						}
					} else {
						MQDatum dummy = new MQDatum(resultat);
						this.addDatum(dummy);
						LOGGER.finer("Habe Datum empfangen.", new Object[] { dummy, this });
					}
				}
			}
		}
	}

	@Override
	protected void aktualisiereMsgParameter(IMsgDatenartParameter parameter) {
		this.intervallLaengeInitialisiert = true;
		if (this.intervallLaenge != parameter.getVergleichsIntervall()) {
			this.intervallLaenge = parameter.getVergleichsIntervall();
			this.aufraeumen();
		}
	}

	/**
	 * Fuegt ein Datum in diesen Puffer ein und loescht gleichzeitig alle
	 * Elemente aus dem Puffer, die nicht mehr im Intervall liegen.
	 * 
	 * @param datum
	 *            ein MQDatum
	 */
	private void addDatum(final MQDatum datum) {
		synchronized (this) {
			this.puffer.add(datum);
		}
		this.aufraeumen();
	}

	/**
	 * Loescht alle Elemente aus dem Puffer, die nicht mehr im Intervall liegen.
	 */
	private synchronized void aufraeumen() {
		if (!this.puffer.isEmpty()) {
			/**
			 * Jungster Zeitstempel
			 */
			final long aktuelleDatenZeit = this.puffer.first().getZeitStempel();
			this.setJetzt(aktuelleDatenZeit);

			/**
			 * Es ist nur dann ein Intervall fertig, wenn die Intervalllaenge
			 * bereits aktiv beschrieben wurde
			 */
			if (this.intervallLaengeInitialisiert && this.puffer.size() > 1) {
				int i = 0;
				MQDatum zweitLetztesDatum = null;
				for (MQDatum datum : this.puffer) {
					if (++i == 2) {
						zweitLetztesDatum = datum;
						break;
					}
				}

				if (zweitLetztesDatum == null) {
					return;
				}

				final long intervallEnde = this.getEndeLetztesIntervallVor(aktuelleDatenZeit);
				final long intervallAnfang = intervallEnde - this.intervallLaenge;

				SimpleDateFormat dateFormat = new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR);
				LOGGER.fine("Intervallende: " + dateFormat.format(new Date(intervallEnde)) + " Intervallanfang: "
						+ dateFormat.format(new Date(intervallAnfang)) + ", i: " + this.intervallLaenge);

				if (intervallAnfang <= zweitLetztesDatum.getZeitStempel()
						&& zweitLetztesDatum.getZeitStempel() < intervallEnde
						&& !(intervallAnfang <= aktuelleDatenZeit && aktuelleDatenZeit < intervallEnde)) {
					/**
					 * Das heißt, es existieren wenigstens zwei Werte, von denen
					 * die letzten beiden jeweils innerhalb und außerhalb des
					 * vergangenen Intervalls liegen
					 */

					this.berechneFertigesIntervall(intervallAnfang, intervallEnde);
				}
			}
		}
	}

	/**
	 * Kumuliert die Q-Werte eines Intervalls.
	 * 
	 * @param start
	 *            Intervallbegin (absolute Zeit in ms)
	 * @param ende
	 *            Intervallende (absolute Zeit in ms)
	 */
	private void berechneFertigesIntervall(final long start, final long ende) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR);

		LOGGER.fine("Start: " + dateFormat.format(new Date(start)) + " (" + start + "), Ende: "
				+ dateFormat.format(new Date(ende)) + " (" + ende + ")");

		Set<IDELzFhDatum> datenImIntervall = new HashSet<>();
		this.puffer.stream().filter((mQDatum) -> start <= mQDatum.getZeitStempel() && mQDatum.getZeitStempel() < ende)
				.forEach((mQDatum) -> {
					datenImIntervall.add(mQDatum);
					LOGGER.fine("Bewerte Datum", mQDatum);
				});

		this.fertigesIntervall = new Intervall(start, ende, Rechenwerk.durchschnitt(datenImIntervall));

		this.listenerMenge.stream()
				.forEach((listener) -> listener.aktualisiereDatum(this.mqObjekt, this.fertigesIntervall));
	}

	/**
	 * Erfragt den Zeitstempel des Endes des Intervalls, das in bezug auf den
	 * uebergebenen Zeitstempel gerade vergangen ist.
	 * 
	 * @param zeitStempel
	 *            ein Zeitpunkt
	 * @return den Zeitstempel des Endes des Intervalls, das in bezug auf den
	 *         uebergebenen Zeitstempel gerade vergangen ist
	 */
	private long getEndeLetztesIntervallVor(final long zeitStempel) {
		GregorianCalendar nullElement = new GregorianCalendar();
		nullElement.setTimeInMillis(zeitStempel);
		nullElement.set(Calendar.MINUTE, 0);
		nullElement.set(Calendar.SECOND, 0);
		nullElement.set(Calendar.MILLISECOND, 0);

		if (this.intervallLaenge > (long) (60 * 60 * 1000)) {
			nullElement.set(Calendar.HOUR_OF_DAY, 0);
		}

		final long jetztMinus0Element = zeitStempel - nullElement.getTimeInMillis();
		final long vollstaendigVergangeneIntervalleBisJetzt = jetztMinus0Element / this.intervallLaenge;
		final long intervallEnde = nullElement.getTimeInMillis()
				+ vollstaendigVergangeneIntervalleBisJetzt * this.intervallLaenge;

		return intervallEnde;
	}

	/**
	 * Setzt den Jetzt-Zeitpunkt und bereinigt danach den Puffer.
	 * 
	 * @param jetzt
	 *            der Jetzt-Zeitpunkt
	 */
	private synchronized void setJetzt(final long jetzt) {
		LOGGER.fine("Jetzt: " + new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR).format(new Date(jetzt)) + " ("
				+ jetzt + ")");

		if (this.puffer.size() > 1) {
			int i = 0;
			MQDatum zweitLetztesDatum = null;
			for (MQDatum datum : this.puffer) {
				if (++i == 2) {
					zweitLetztesDatum = datum;
					break;
				}
			}

			if (zweitLetztesDatum == null) {
				return;
			}

			long aeltesterErlaubterZeitStempel = this.getEndeLetztesIntervallVor(zweitLetztesDatum.getZeitStempel());

			Collection<MQDatum> zuLoeschendeElemente = new ArrayList<>();
			this.puffer.stream()
					.filter((pufferElement) -> pufferElement.getZeitStempel() < aeltesterErlaubterZeitStempel)
					.forEach((pufferElement) -> {
						zuLoeschendeElemente.add(pufferElement);
						LOGGER.fine("Loesche Element", pufferElement);
					});

			this.puffer.removeAll(zuLoeschendeElemente);
		}
	}

	@Override
	public String toString() {
		String s = "Datenerfassung (" + (this.langZeit ? "langzeit" : "kurzzeit") + "): " + this.mqObjekt.getPid()
				+ "\n";
		s += "  Aktuelle Intervalllaenge: " + (this.intervallLaenge / 1000L) + "s\n";

		synchronized (this) {
			s += "  Pufferdaten: ";
			if (this.puffer.isEmpty()) {
				s += "leer\n";
			} else {
				s += "\n";
				SimpleDateFormat dateFormat = new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR);
				for (MQDatum element : this.puffer) {
					s += "    " + dateFormat.format(new Date(element.getZeitStempel())) + ": ";
					if (element.isKeineDaten()) {
						s += "keine Daten\n";
					} else {
						s += "QKfz: " + (element.getQ(FahrzeugArt.KFZ) < 0 ? "/" : element.getQ(FahrzeugArt.KFZ));
						s += ", QLkw: " + (element.getQ(FahrzeugArt.LKW) < 0 ? "/" : element.getQ(FahrzeugArt.LKW));
						s += ", QPkw: " + (element.getQ(FahrzeugArt.PKW) < 0 ? "/" : element.getQ(FahrzeugArt.PKW))
								+ "\n";
					}
				}
			}
		}

		return s;
	}

	/**
	 * Enthaelt alle Werte eines Datums der Attributgruppe
	 * <code>atg.verkehrsDatenKurzZeitMq</code>, die fuer die SWE 4.DELzFh DE
	 * Langzeit-Fehlererkennung benoetigt werden (sortierbar nach Zeitstempel).
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 * 
	 */
	private static class MQDatum implements IZeitStempel, IDELzFhDatum {

		/**
		 * Das Systemobjekt des Datums.
		 */
		private SystemObject objekt = null;

		/**
		 * Datenzeit dieses Wertes.
		 */
		private final long datenZeit;

		/**
		 * der Wert <code>QKfz</code>.
		 */
		private double qKfz = -1.0;

		/**
		 * der Wert <code>QLkw</code>.
		 */
		private double qLkw = -1.0;

		/**
		 * der Wert <code>QPkw</code>.
		 */
		private double qPkw = -1.0;

		/**
		 * indiziert <code>keine Daten</code>.
		 */
		private boolean keineDaten = false;

		/**
		 * Standardkonstruktor.
		 * 
		 * @param resultat
		 *            ein Datum der Attributgruppe
		 *            <code>atg.verkehrsDatenKurzZeitMq</code>
		 */
		public MQDatum(ResultData resultat) {
			if (resultat != null) {
				this.objekt = resultat.getObject();
				this.datenZeit = resultat.getDataTime();

				if (resultat.getData() != null) {
					Data data = resultat.getData();
					this.qKfz = data.getItem("QKfz").getUnscaledValue("Wert").doubleValue();
					this.qLkw = data.getItem("QLkw").getUnscaledValue("Wert").doubleValue();
					this.qPkw = data.getItem("QPkw").getUnscaledValue("Wert").doubleValue();
				} else {
					this.keineDaten = true;
				}
			} else {
				this.datenZeit = -1;
				throw new NullPointerException("<<null>> ist kein gueltiges Argument");
			}
		}

		@Override
		public final boolean isKeineDaten() {
			return this.keineDaten;
		}

		@Override
		public long getZeitStempel() {
			return this.datenZeit;
		}

		@Override
		public int compareTo(IZeitStempel that) {
			return -Long.compare(this.getZeitStempel(), that.getZeitStempel());
		}

		@Override
		public boolean equals(Object obj) {
			boolean ergebnis = false;

			if (obj != null && obj instanceof IZeitStempel) {
				IZeitStempel that = (IZeitStempel) obj;
				ergebnis = this.getZeitStempel() == that.getZeitStempel();
			}

			return ergebnis;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(datenZeit);
		}

		@Override
		public String toString() {
			String s = (this.objekt == null ? "kein Objekt" : this.objekt) + " --> "
					+ new SimpleDateFormat(DUAKonstanten.ZEIT_FORMAT_GENAU_STR).format(new Date(this.datenZeit)) + " ("
					+ this.datenZeit + ")\n";

			if (this.keineDaten) {
				s += "keine Daten";
			} else {
				s += "QKfz: " + (this.qKfz < 0 ? "/" : this.qKfz);
				s += ", QLkw: " + (this.qLkw < 0 ? "/" : this.qLkw);
				s += ", QPkw: " + (this.qPkw < 0 ? "/" : this.qPkw);
			}

			return s;
		}

		@Override
		public double getQ(FahrzeugArt fahrzeugArt) {
			double ergebnis = -1.0;

			if (fahrzeugArt.equals(FahrzeugArt.KFZ)) {
				ergebnis = qKfz;
			} else if (fahrzeugArt.equals(FahrzeugArt.LKW)) {
				ergebnis = qLkw;
			} else if (fahrzeugArt.equals(FahrzeugArt.PKW)) {
				ergebnis = qPkw;
			}

			return ergebnis;
		}

		@Override
		public boolean isAuswertbar(FahrzeugArt fahrzeugArt) {
			boolean ergebnis = true;

			if (fahrzeugArt.equals(FahrzeugArt.KFZ)) {
				ergebnis = qKfz >= 0.0;
			} else if (fahrzeugArt.equals(FahrzeugArt.LKW)) {
				ergebnis = qLkw >= 0.0;
			} else if (fahrzeugArt.equals(FahrzeugArt.PKW)) {
				ergebnis = qPkw >= 0.0;
			}

			return ergebnis;
		}

		@Override
		public SystemObject getObjekt() {
			return this.objekt;
		}
	}
}
