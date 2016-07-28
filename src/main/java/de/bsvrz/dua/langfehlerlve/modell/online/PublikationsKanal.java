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
	public PublikationsKanal(ClientDavInterface dav) {
		if (sDav == null) {
			sDav = dav;
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
				sDav.sendData(resultat);
			} else {
				if (!this.keineDaten) {
					this.keineDaten = true;
					
					sDav.sendData(resultat);
				}
			}
		} catch (DataNotSubscribedException e) {
			Debug.getLogger().error("Datum kann nicht publiziert werden:\n" + resultat, e); //$NON-NLS-1$
			e.printStackTrace();
		} catch (SendSubscriptionNotConfirmed e) {
			Debug.getLogger().error("Datum kann nicht publiziert werden:\n" + resultat, e); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

}
