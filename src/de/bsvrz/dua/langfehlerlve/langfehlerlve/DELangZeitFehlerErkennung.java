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
import java.util.HashSet;
import java.util.List;

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
 * Die SWE DE Langzeit-Fehlererkennung dient zur Erkennung von systematischen Fehlern bei 
 * der Verkehrdatenerfassung einzelner Messquerschnitte, die durch die Plausibilisierungsstufen 
 * nicht ermittelbar sind. Mit diesen Fehlern sind z. B. dauerhaft zu niedrige oder zu hohe 
 * Fahrzeugmengenwerte (QPkw, QLkw, QKfz) gemeint. Diese lassen sich nur durch einen längerfristigen 
 * Vergleich von verkehrlich ähnlichen Messquerschnitten nachweisen. Um solche Fehler erkennen zu 
 * können, werden hier Funktionen realisiert, die sowohl laufend Indikatoren für systematische 
 * Abweichungen im Rahmen einer permanent durchgeführten kurzfristigen Bilanzierung (i. d. R. 5 
 * Minuten-Intervalle) als auch relative Fehlerangaben für einen längeren Bilanzierungszeitraum 
 * (i. d. R. ein Tag) ermitteln. Diese Daten werden permanent publiziert. Bei der Überschreitung 
 * gewisser pro Messstellengruppe (topographisch sinnvolle Zusammenfassung mehrerer Messstellen 
 * bzw. Messquerschnitte) definierter Grenzwerte werden Betriebsmeldungen ausgegeben.
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DELangZeitFehlerErkennung 
implements StandardApplication{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * die Argumente der Kommandozeile
	 */
	private ArrayList<String> komArgumente = new ArrayList<String>();
	
	
	/**
	 * Erfragt den Namen dieser Applikation
	 * 
	 * @return der Name dieser Applikation
	 */
	public static final String getName(){
		return "DE Langzeit-Fehlererkennung"; //$NON-NLS-1$
	}
	

	/**
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface dav)
	throws Exception {
		DuaVerkehrsNetz.initialisiere(dav);
		
		Collection<ConfigurationArea> kbFilter = this.getKonfigurationsBereicheAlsObjekte(
				dav, 
				DUAUtensilien.getArgument(DUAKonstanten.ARG_KONFIGURATIONS_BEREICHS_PID,
				this.komArgumente));
				
		Collection<SystemObject> msgObjekte = 
			DUAUtensilien.getBasisInstanzen(
					dav.getDataModel().getType(DUAKonstanten.TYP_MESS_STELLEN_GRUPPE),
					dav,
					kbFilter);
		
		String config = "Betrachtete Messstellengruppen:\n"; //$NON-NLS-1$
		for(SystemObject msgObjekt:msgObjekte){
			config += msgObjekt + "\n";  //$NON-NLS-1$
		}
		LOGGER.config(config);
		
		for(SystemObject msgObjekt:msgObjekte){
			new DELzFhMessStellenGruppe(dav, msgObjekt, DELzFhMessStellenGruppe.LANGZEIT_AUSWERTUNG);
			new DELzFhMessStellenGruppe(dav, msgObjekt, DELzFhMessStellenGruppe.KURZZEIT_AUSWERTUNG);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void parseArguments(ArgumentList argumente)
	throws Exception {
		Debug.init("DELzFh DE Langzeit-Fehlererkennung", argumente); //$NON-NLS-1$

		for(String s:argumente.getArgumentStrings()){
			if(s != null)this.komArgumente.add(s);
		}

		argumente.fetchUnusedArguments();
	}

	
	/**
	 * Extrahiert aus einer Zeichenkette alle über Kommata getrennten
	 * Konfigurationsbereiche und gibt deren Systemobjekte zurück.
	 *
	 * @param dav Verbindung zum Datenverteiler
	 * @param kbString Zeichenkette mit den Konfigurationsbereichen
	 * @return (ggf. leere) <code>ConfigurationArea-Collection</code>
	 * mit allen extrahierten Konfigurationsbereichen.
	 */
	private final Collection<ConfigurationArea>
				getKonfigurationsBereicheAlsObjekte(final ClientDavInterface dav,
													final String kbString){
		List<String> resultListe = new ArrayList<String>();

		if(kbString != null){
			String[] s = kbString.split(","); //$NON-NLS-1$
			for(String dummy:s){
				if(dummy != null && dummy.length() > 0){
					resultListe.add(dummy);
				}
			}
		}
		Collection<ConfigurationArea> kbListe = new HashSet<ConfigurationArea>();

		for(String kb:resultListe){
			try{
				ConfigurationArea area = dav.getDataModel().
											getConfigurationArea(kb);
				if(area != null)kbListe.add(area);
			}catch(UnsupportedOperationException ex){
				LOGGER.warning("Konfigurationsbereich " + kb +  //$NON-NLS-1$
						" konnte nicht identifiziert werden.", ex); //$NON-NLS-1$
			}
		}

		return kbListe;
	}
	
	
	/**
	 * Startet diese Applikation
	 * 
	 * @param argumente Argumente der Kommandozeile
	 */
	public static void main(String argumente[]){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.
        				UncaughtExceptionHandler(){
            public void uncaughtException(@SuppressWarnings("unused")
			Thread t, Throwable e) {
                LOGGER.error("Applikation wird wegen" +  //$NON-NLS-1$
                		" unerwartetem Fehler beendet", e);  //$NON-NLS-1$
            	e.printStackTrace();
                Runtime.getRuntime().exit(0);
            }
        });
		StandardApplicationRunner.run(new DELangZeitFehlerErkennung(), argumente);
	}
	
}
