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

package de.bsvrz.dua.langfehlerlve.langfehlerlve;

import java.util.ArrayList;
import java.util.Collection;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.ConfigurationArea;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.modell.ausw.DELzFhMessStellenGruppe;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAUtensilien;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.DuaVerkehrsNetz;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Die SWE DE Langzeit-Fehlererkennung dient zur Erkennung von systematischen
 * Fehlern bei der Verkehrdatenerfassung einzelner Messquerschnitte, die durch
 * die Plausibilisierungsstufen nicht ermittelbar sind. Mit diesen Fehlern sind
 * z. B. dauerhaft zu niedrige oder zu hohe Fahrzeugmengenwerte (QPkw, QLkw,
 * QKfz) gemeint. Diese lassen sich nur durch einen längerfristigen Vergleich
 * von verkehrlich ähnlichen Messquerschnitten nachweisen. Um solche Fehler
 * erkennen zu können, werden hier Funktionen realisiert, die sowohl laufend
 * Indikatoren für systematische Abweichungen im Rahmen einer permanent
 * durchgeführten kurzfristigen Bilanzierung (i. d. R. 5 Minuten-Intervalle) als
 * auch relative Fehlerangaben für einen längeren Bilanzierungszeitraum (i. d.
 * R. ein Tag) ermitteln. Diese Daten werden permanent publiziert. Bei der
 * Überschreitung gewisser pro Messstellengruppe (topographisch sinnvolle
 * Zusammenfassung mehrerer Messstellen bzw. Messquerschnitte) definierter
 * Grenzwerte werden Betriebsmeldungen ausgegeben.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class DELangZeitFehlerErkennung implements StandardApplication {

	/**
	 * die Argumente der Kommandozeile.
	 */
	private ArrayList<String> komArgumente = new ArrayList<String>();

	/**
	 * Erfragt den Namen dieser Applikation.
	 * 
	 * @return der Name dieser Applikation
	 */
	public static final String getName() {
		return "DE Langzeit-Fehlererkennung";
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface dav) throws Exception {
		Collection<ConfigurationArea> kbFilter = DUAUtensilien
				.getKonfigurationsBereicheAlsObjekte(dav, DUAUtensilien
						.getArgument(
								DUAKonstanten.ARG_KONFIGURATIONS_BEREICHS_PID,
								this.komArgumente));

		DuaVerkehrsNetz.initialisiere(dav);

		Collection<SystemObject> msgObjekte = DUAUtensilien.getBasisInstanzen(
				dav.getDataModel().getType(
						DUAKonstanten.TYP_MESS_STELLEN_GRUPPE), dav, kbFilter);

		String config = "Ueberwachte Messstellengruppen:\n";
		for (SystemObject msgObjekt : msgObjekte) {
			config += msgObjekt + "\n";
		}
		Debug.getLogger().config(config);

		for (SystemObject msgObjekt : msgObjekte) {
			new DELzFhMessStellenGruppe(dav, msgObjekt,
					DELzFhMessStellenGruppe.LANGZEIT_AUSWERTUNG);
			new DELzFhMessStellenGruppe(dav, msgObjekt,
					DELzFhMessStellenGruppe.KURZZEIT_AUSWERTUNG);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void parseArguments(ArgumentList argumente) throws Exception {

		Thread
				.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					public void uncaughtException(@SuppressWarnings("unused")
					Thread t, Throwable e) {
						Debug.getLogger().error(
								"Applikation wird wegen"
										+ " unerwartetem Fehler beendet", e);
						e.printStackTrace();
						Runtime.getRuntime().exit(-1);
					}
				});

		for (String s : argumente.getArgumentStrings()) {
			if (s != null) {
				this.komArgumente.add(s);
			}
		}

		argumente.fetchUnusedArguments();
	}

	/**
	 * Startet diese Applikation.
	 * 
	 * @param argumente
	 *            Argumente der Kommandozeile
	 */
	public static void main(String[] argumente) {
		StandardApplicationRunner.run(new DELangZeitFehlerErkennung(),
				argumente);
	}

}
