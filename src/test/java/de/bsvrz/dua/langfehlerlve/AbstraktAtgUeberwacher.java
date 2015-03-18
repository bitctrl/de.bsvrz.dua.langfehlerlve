/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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

import org.junit.Assert;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Ueberprueft den ersten echten (Nutz-)Wert einer Attributgruppe.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
abstract class AbstraktAtgUeberwacher implements ClientReceiverInterface {

	/**
	 * Der Wert mit dem der erste echte Nutzwert gegengeprueft werden soll.
	 */
	long wert = DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT;

	/**
	 * Erstes empfangenes Nutzdatum.
	 */
	ResultData data = null;

	/**
	 * Erfragt die PID der zu ueberpruefenden Atg.
	 * 
	 * @return die PID der zu ueberpruefenden Atg.
	 */
	abstract String getAtgPid();

	/**
	 * Erfragt die PID des zu ueberpruefenden Asp.
	 * 
	 * @return die PID des zu ueberpruefenden Asp.
	 */
	abstract String getAspPid();

	/**
	 * Initialisierungsmethode.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            das zu ueberwachende Objekt
	 * @param wert1
	 *            der Wert mit dem der erste echte Nutzwert gegengeprueft werden
	 *            soll
	 */
	void init(ClientDavInterface dav, SystemObject objekt, int wert1) {
		this.wert = wert1;
		dav.subscribeReceiver(this, objekt, new DataDescription(dav
				.getDataModel().getAttributeGroup(this.getAtgPid()), dav
				.getDataModel().getAspect(this.getAspPid())), ReceiveOptions
				.normal(), ReceiverRole.receiver());
	}

	/**
	 * Fuehrt die Ueberpruefung durch. Sollte erst aufgerufen werden, wenn man
	 * sich sicher ist, dass alle Berechnungen bereits durchgefuehrt wurden.
	 */
	void ueberpruefe() {
		if (data != null) {
			for (FahrzeugArt art : FahrzeugArt.getInstanzen()) {
				Assert.assertEquals("\n---\n" + this.getClass().getSimpleName()
						+ ":\nFehler in " + data.getObject() + "\n"
						+ art.toString() + "\n", wert, data.getData()
						.getUnscaledValue(art.getAttributName()).isState() ? -3
						: data.getData().getScaledValue(art.getAttributName())
								.longValue());
			}
		} else {
			Assert.assertTrue("\n---\n" + this.getClass().getSimpleName()
					+ ":\nKein Datum empfangen!\n", wert < 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] results) {
		if (results != null) {
			if (this.data == null) {
				for (ResultData r : results) {
					if (r != null && r.getData() != null) {
						this.data = r;
						break;
					}
				}
			}
		}
	}

}
