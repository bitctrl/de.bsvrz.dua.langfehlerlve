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

package de.bsvrz.dua.langfehlerlve.modell.ausw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStelle;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStellenGruppe;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * In dieser Klasse werden alle Systemobjekte unterhalb einer Messstellengruppe
 * (also Messstellen und Messquerschnitte) <b>fuer diese</b> Messstellengruppe
 * initialisiert und im Sinne der SWE 4.DELzFh DE Langzeit-Fehlererkennung in
 * Verbindung gesetzt.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DELzFhMessStellenGruppe {

	/**
	 * Langzeitauswertung.
	 */
	public static final boolean LANGZEIT_AUSWERTUNG = true;

	/**
	 * Kurzzeitauswertung.
	 */
	public static final boolean KURZZEIT_AUSWERTUNG = false;

	/**
	 * das mit diesem Objekt assoziierte Systemobjekt.
	 */
	private SystemObject objekt = null;

	/**
	 * alle an dieser Messstellengruppe (indirekt ueber Messstellen)
	 * konfigurierten Messquerschnitte.
	 */
	private Map<SystemObject, DELzFhMessQuerschnitt> messQuerschnitte = new HashMap<SystemObject, DELzFhMessQuerschnitt>();

	/**
	 * alle an dieser Messstellengruppe konfigurierten Messstellen.
	 */
	private Map<SystemObject, DELzFhMessStelle> messStellen = new HashMap<SystemObject, DELzFhMessStelle>();

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param msgObjekt
	 *            Systemobjekt vom Typ <code>typ.messStellenGruppe</code> das
	 *            innerhalb dieser SWE ueberwacht werden soll
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws Exception
	 *             wenn es Probleme bei der Initialisierung dieses Objektes gibt
	 */
	public DELzFhMessStellenGruppe(ClientDavInterface dav,
			SystemObject msgObjekt, boolean langZeit) throws Exception {
		this.objekt = msgObjekt;

		if (MessStellenGruppe.getInstanz(msgObjekt).getMessStellen().length > 1) {
			for (MessStelle ms : MessStellenGruppe.getInstanz(msgObjekt)
					.getMessStellen()) {
				if (ms.getPruefling() != null) {
					this.messQuerschnitte.put(ms.getPruefling()
							.getSystemObject(),
							new DELzFhMessQuerschnitt(dav, ms.getPruefling()
									.getSystemObject(), this, langZeit));
				} else {
					throw new DUAInitialisierungsException(
							"Messstelle " + ms + " hat keinen MQ-Pruefling.");
				}

				for (MessQuerschnittAllgemein mqAbfahrt : ms.getAbfahrten()) {
					this.messQuerschnitte.put(mqAbfahrt.getSystemObject(),
							new DELzFhMessQuerschnitt(dav, mqAbfahrt
									.getSystemObject(), this, langZeit));
				}
				for (MessQuerschnittAllgemein mqZufahrt : ms.getZufahrten()) {
					this.messQuerschnitte.put(mqZufahrt.getSystemObject(),
							new DELzFhMessQuerschnitt(dav, mqZufahrt
									.getSystemObject(), this, langZeit));
				}
			}

			/**
			 * Messstellen erst initialisieren, wenn deren Messquerschnitte
			 * bereits initialisiert sind
			 */
			for (MessStelle ms : MessStellenGruppe.getInstanz(msgObjekt)
					.getMessStellen()) {
				this.messStellen.put(ms.getSystemObject(),
						new DELzFhMessStelle(dav, ms.getSystemObject(), this,
								langZeit));
			}

			final int anzahlMessStellen = MessStellenGruppe.getInstanz(
					msgObjekt).getMessStellen().length;
			final MessStelle[] msFeld = MessStellenGruppe.getInstanz(msgObjekt)
					.getMessStellen();
			/**
			 * Ermittlung der Bilanzwerte anstossen
			 */
			if (anzahlMessStellen > 2) {

				for (int i = 1; i < anzahlMessStellen - 1; i++) {
					DELzFhMessStelle messStelle = this.messStellen
							.get(msFeld[i].getSystemObject());
					DELzFhMessStelle messStelleMinus1 = this.messStellen
							.get(msFeld[i - 1].getSystemObject());
					DELzFhMessQuerschnitt messQuerschnittPlus1 = this.messQuerschnitte
							.get(msFeld[i + 1].getPruefling().getSystemObject());
					DELzFhMessQuerschnitt messQuerschnitt = this.messQuerschnitte
							.get(msFeld[i].getPruefling().getSystemObject());
					if (messStelle != null && messStelleMinus1 != null
							&& messQuerschnittPlus1 != null
							&& messQuerschnitt != null) {
						new MessStellenBilanz(dav, messStelle,
								messStelleMinus1, messQuerschnittPlus1,
								messQuerschnitt, langZeit);
					} else {
						Debug.getLogger()
								.warning("Ermittlung der Bilanzwerte konnte nicht angestossen werden\nfuer Messstelle: " + //$NON-NLS-1$
										msFeld[i].getSystemObject()
										+ "\nan Messstellengruppe: " + this); //$NON-NLS-1$
					}
				}
			}

			if (MessStellenGruppe.getInstanz(msgObjekt)
					.isSystematischeDetektorfehler()) {
				/**
				 * Ermittlung der Abweichungen zu den Nachbarmessstellen
				 * anstossen
				 */
				if (anzahlMessStellen > 1) {
					for (int i = 0; i < anzahlMessStellen; i++) {
						DELzFhMessStelle messStelle = this.messStellen
								.get(msFeld[i].getSystemObject());
						DELzFhMessQuerschnitt messQuerschnitt = this.messQuerschnitte
								.get(msFeld[i].getPruefling().getSystemObject());
						Set<DELzFhMessStelle> restMessStellen = new HashSet<DELzFhMessStelle>();
						for (int j = 0; j < anzahlMessStellen; j++) {
							if (i != j) {
								restMessStellen.add(this.messStellen
										.get(msFeld[j].getSystemObject()));
							}
						}

						if (messStelle != null && messQuerschnitt != null
								&& !restMessStellen.isEmpty()) {
							new AbweichungNachbarn(dav, messStelle, this,
									restMessStellen
											.toArray(new DELzFhMessStelle[0]),
									messQuerschnitt, langZeit);
						} else {
							Debug.getLogger()
									.warning("Ermittlung der Abweichung zu den Nachbarn konnte nicht angestossen werden\nfuer Messstelle: " + //$NON-NLS-1$
											msFeld[i].getSystemObject()
											+ "\nan Messstellengruppe: " + this); //$NON-NLS-1$
						}
					}
				} 
			} else {
				/**
				 * Ermittlung der Abweichungen zum Vorgaenger anstossen
				 */
				if (anzahlMessStellen > 1) {
					for (int i = 1; i < anzahlMessStellen; i++) {
						DELzFhMessStelle messStelle = this.messStellen
								.get(msFeld[i].getSystemObject());
						DELzFhMessStelle messStelleMinus1 = this.messStellen
								.get(msFeld[i - 1].getSystemObject());
						DELzFhMessQuerschnitt messQuerschnitt = this.messQuerschnitte
								.get(msFeld[i].getPruefling().getSystemObject());

						if (messStelle != null && messQuerschnitt != null
								&& messStelleMinus1 != null) {
							new AbweichungVorgaenger(dav, messStelle, this,
									messStelleMinus1, messQuerschnitt, langZeit);
						} else {
							Debug.getLogger()
									.warning("Ermittlung der Abweichung zum Vorgaenger konnte nicht angestossen werden\nfuer Messstelle: " + //$NON-NLS-1$
											msFeld[i].getSystemObject()
											+ "\nan Messstellengruppe: " + this); //$NON-NLS-1$
						}
					}
				}				
			}
		} else {
			throw new DUAInitialisierungsException(
					"Messstellengruppe " + msgObjekt + //$NON-NLS-1$
							" hat weniger als 2 Messstellen"); //$NON-NLS-1$
		}
	}

	/**
	 * Erfragt das mit diesem Objekt assoziierte Systemobjekt.
	 * 
	 * @return das mit diesem Objekt assoziierte Systemobjekt
	 */
	public final SystemObject getObjekt() {
		return this.objekt;
	}

	/**
	 * Erfragt einen Messquerschnitt, der an dieser Messstellengruppe haengt.
	 * 
	 * @param mqObjekt
	 *            das mit dem gesuchten Messquerschnitt assoziierte Systemobjekt
	 * @return ein Messquerschnitt, der an dieser Messstellengruppe haengt
	 */
	public final DELzFhMessQuerschnitt getMq(final SystemObject mqObjekt) {
		return this.messQuerschnitte.get(mqObjekt);
	}

	/**
	 * Erfragt eine Messstelle, die an dieser Messstellengruppe haengt.
	 * 
	 * @param msObjekt
	 *            das mit der gesuchten Messstelle assoziierte Systemobjekt
	 * @return eine Messstelle, die an dieser Messstellengruppe haengt
	 */
	public final DELzFhMessStelle getMs(final SystemObject msObjekt) {
		return this.messStellen.get(msObjekt);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.objekt.toString();
	}

}
