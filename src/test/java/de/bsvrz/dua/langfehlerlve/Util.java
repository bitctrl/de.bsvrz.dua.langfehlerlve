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

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Utensilien.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class Util {

	/**
	 * Standardkonstruktor.
	 */
	private Util() {
		//
	}

	/**
	 * Parametriert die Messstellengruppe.
	 *
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param mg1
	 *            MessStellengruppe
	 * @param kurzZeitAgg
	 *            Aggregationsintervall der Kurzzeitdaten in Minuten
	 * @param langZeitAgg
	 *            Aggregationsintervall der Langzeitdaten in Stunden
	 * @param maxAbwVorKurz
	 *            Maximale Abweichung fuer KZD zum Vorgaenger
	 * @param maxAbwGrpKurz
	 *            Maximale Abweichung fuer KZD zur Gruppe
	 * @param maxAbwVorLang
	 *            Maximale Abweichung fuer LZD zum Vorgaenger
	 * @param maxAbwGrpLang
	 *            Maximale Abweichung fuer LZD zur Gruppe
	 * @throws Exception
	 *             Wird beim Sende-fehler geworfen
	 */
	public static void parametriere(final ClientDavInterface dav,
			final SystemObject mg1, final long kurzZeitAgg,
			final long langZeitAgg, final long maxAbwVorKurz,
			final long maxAbwGrpKurz, final long maxAbwVorLang,
			final long maxAbwGrpLang) throws Exception {

		final DataDescription ddParam = new DataDescription(dav.getDataModel()
				.getAttributeGroup("atg.parameterMessStellenGruppe"), dav
				.getDataModel().getAspect("asp.parameterVorgabe"));
		final ClientSenderInterface sender = new ClientSenderInterface() {

			@Override
			public void dataRequest(final SystemObject object,
					final DataDescription dataDescription, final byte state) {
				//
			}

			@Override
			public boolean isRequestSupported(final SystemObject object,
					final DataDescription dataDescription) {
				return false;
			}

		};
		dav.subscribeSender(sender, mg1, ddParam, SenderRole.sender());

		Data data;
		ResultData resultat;
		data = dav.createData(dav.getDataModel().getAttributeGroup(
				"atg.parameterMessStellenGruppe")); //$NON-NLS-1$

		data.getItem("VergleichsIntervallKurzZeit").asUnscaledValue().set(kurzZeitAgg); //$NON-NLS-1$
		data.getItem("maxAbweichungVorgängerKurzZeit").asUnscaledValue().set(maxAbwVorKurz); //$NON-NLS-1$
		data.getItem("maxAbweichungMessStellenGruppeKurzZeit").asUnscaledValue().set(maxAbwGrpKurz); //$NON-NLS-1$

		data.getItem("VergleichsIntervallLangZeit").asUnscaledValue().set(langZeitAgg); //$NON-NLS-1$
		data.getItem("maxAbweichungVorgängerLangZeit").asUnscaledValue().set(maxAbwVorLang); //$NON-NLS-1$
		data.getItem("maxAbweichungMessStellenGruppeLangZeit").asUnscaledValue().set(maxAbwGrpLang); //$NON-NLS-1$

		resultat = new ResultData(mg1, ddParam, System.currentTimeMillis(),
				data);

		Thread.sleep(2000L);
		dav.sendData(resultat);

		dav.unsubscribeSender(sender, mg1, ddParam);
	}

}
