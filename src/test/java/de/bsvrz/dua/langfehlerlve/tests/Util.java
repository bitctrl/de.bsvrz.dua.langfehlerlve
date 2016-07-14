/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.tests.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.tests;

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
 * 
 * @version $Id$
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
	public static void parametriere(ClientDavInterface dav, SystemObject mg1,
			long kurzZeitAgg, long langZeitAgg, long maxAbwVorKurz,
			long maxAbwGrpKurz, long maxAbwVorLang, long maxAbwGrpLang)
			throws Exception {

		DataDescription ddParam = new DataDescription(dav.getDataModel()
				.getAttributeGroup("atg.parameterMessStellenGruppe"), dav
				.getDataModel().getAspect("asp.parameterSoll"));
		ClientSenderInterface sender = new ClientSenderInterface() {

			public void dataRequest(SystemObject object,
					DataDescription dataDescription, byte state) {
				//
			}

			public boolean isRequestSupported(SystemObject object,
					DataDescription dataDescription) {
				return false;
			}

		};
		dav.subscribeSender(sender, mg1, ddParam, SenderRole.source());
		
		Data data;
		ResultData resultat;
		data = dav.createData(dav.getDataModel().getAttributeGroup(
				"atg.parameterMessStellenGruppe")); //$NON-NLS-1$

		data
				.getItem("VergleichsIntervallKurzZeit").asUnscaledValue().set(kurzZeitAgg); //$NON-NLS-1$
		data
				.getItem("maxAbweichungVorgängerKurzZeit").asUnscaledValue().set(maxAbwVorKurz); //$NON-NLS-1$
		data
				.getItem("maxAbweichungMessStellenGruppeKurzZeit").asUnscaledValue().set(maxAbwGrpKurz); //$NON-NLS-1$

		data
				.getItem("VergleichsIntervallLangZeit").asUnscaledValue().set(langZeitAgg); //$NON-NLS-1$
		data
				.getItem("maxAbweichungVorgängerLangZeit").asUnscaledValue().set(maxAbwVorLang); //$NON-NLS-1$
		data
				.getItem("maxAbweichungMessStellenGruppeLangZeit").asUnscaledValue().set(maxAbwGrpLang); //$NON-NLS-1$

		resultat = new ResultData(mg1, ddParam, System.currentTimeMillis(),
				data);
		
		Thread.sleep(2000L);
		dav.sendData(resultat);
		
		dav.unsubscribeSender(sender, mg1, ddParam);
	}

}
