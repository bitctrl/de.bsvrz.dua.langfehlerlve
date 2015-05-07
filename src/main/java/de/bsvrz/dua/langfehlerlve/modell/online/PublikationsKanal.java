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

package de.bsvrz.dua.langfehlerlve.modell.online;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Ausgabekanal fuer die Daten eines Systemobjektes und einer Datenbeschreibung.
 * Sorgt dafuer, dass <code>keine Daten</code> nicht unmittelbar zweimal
 * aufeinander folgt
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class PublikationsKanal {

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * statische Datenverteiler-Verbindung.
	 */
	protected static ClientDavInterface sDav = null;

	/**
	 * indiziert ob dieser Kanal z.Z. auf <code>keine Daten</code> steht.
	 */
	private boolean keineDaten = true;

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung
	 */
	public PublikationsKanal(final ClientDavInterface dav) {
		if (PublikationsKanal.sDav == null) {
			PublikationsKanal.sDav = dav;
		}
	}

	/**
	 * Publiziert ein Datum. Sorgt dafuer, dass <code>keine Daten</code> nicht
	 * unmittelbar zweimal aufeinander publiziert wird.
	 *
	 * @param resultat
	 *            ein Datum
	 */
	public final void publiziere(final ResultData resultat) {
		try {
			if (resultat.getData() != null) {
				this.keineDaten = false;
				PublikationsKanal.sDav.sendData(resultat);
			} else {
				if (!this.keineDaten) {
					this.keineDaten = true;

					PublikationsKanal.sDav.sendData(resultat);
				}
			}
		} catch (final DataNotSubscribedException e) {
			LOGGER.error(
					"Datum kann nicht publiziert werden:\n" + resultat, e); //$NON-NLS-1$
			e.printStackTrace();
		} catch (final SendSubscriptionNotConfirmed e) {
			LOGGER.error(
					"Datum kann nicht publiziert werden:\n" + resultat, e); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

}
