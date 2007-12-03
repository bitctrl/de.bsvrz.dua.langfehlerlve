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

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAUtensilien;


/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Erkennung systematischer Detektorfehler
 * fuer eine Messstelle vorgesehen sind (Afo DUA-BW-C1C2-11 - Vergleich mit allen anderen Messstellen).
 * Diese Daten werden hier auch publiziert
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class AbweichungNachbarn
extends AbstraktAbweichung{
		
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param messStelle Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStellenGruppe Messstellengruppe an der diese Berechnung erfolgt
	 * @param restMessStellen alle restlichen Messstellen, zu denen diese Messstelle ins
	 * Verhaeltnis gesetzt werden soll
	 * @param messQuerschnitt Verbindung zu den Onlinedaten des Hauptmessquerschnitts
	 * der Messstelle selbst
 	 * @param langZeit Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll
	 * @throws Exception wird weitergereicht
	 */
	protected AbweichungNachbarn(ClientDavInterface dav,
								 DELzFhMessStelle messStelle,
								 DELzFhMessStellenGruppe messStellenGruppe,
								 DELzFhMessStelle[] restMessStellen,
								 DELzFhMessQuerschnitt messQuerschnitt,
								 boolean langZeit)
	throws Exception{
		super(dav, messStelle, messStellenGruppe, restMessStellen, messQuerschnitt, langZeit);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aktualisiereMsgParameter(IMsgDatenartParameter parameter) {
		this.abweichungMax = parameter.getMaxAbweichungMessStellenGruppe();
		this.vergleichsIntervall = DUAUtensilien.getVergleichsIntervallInText(parameter.getVergleichsIntervall()); 
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getKzAspPid() {
		return "asp.messQuerschnittDerMessStellenGruppeKurzZeit"; //$NON-NLS-1$
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLzAspPid() {
		return "asp.messQuerschnittDerMessStellenGruppeLangZeit"; //$NON-NLS-1$
	}

}