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

package de.bsvrz.dua.langfehlerlve;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Diese Klasse korrespondiert mit einem DAV-Objekt vom Typ <code>typ.messQuerschnittAllgemein</code>
 * und kapselt direkt oder indirekt saemtliche Funktionalitäten, die innerhalb der SWE
 * DE Langzeit-Fehlererkennung in Bezug auf Objekte dieses Typs benoetigt werden. Insbesondere
 * werden hier alle MQ-Werte des letzten Intervalls vorgehalten und in korrespondierende 
 * DELzFh-Werte uebersetzt. Diese Werte werden dann an die Messstelle bzw. die Messstellengruppe
 * weitergeleitet
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DELzFhMessQuerschnitt
implements ClientReceiverInterface{

	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();


	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param mqObjekt Systemobjekt vom Typ <code>typ.messQuerschnittAllgemein</code>
	 * @throws DUAInitialisierungsException wenn das Objekt nicht sinnvoll initialisiert
	 * werden konnte
	 */
	protected DELzFhMessQuerschnitt(ClientDavInterface dav,
									SystemObject mqObjekt)
	throws DUAInitialisierungsException{
		DataDescription fsAnalyseDatenBeschreibung = new DataDescription(
				dav.getDataModel().getAttributeGroup(DUAKonstanten.ATG_KURZZEIT_FS),
				dav.getDataModel().getAspect(DUAKonstanten.ASP_ANALYSE));

		dav.subscribeReceiver(this,
				mqObjekt,
				fsAnalyseDatenBeschreibung,
				ReceiveOptions.normal(),
				ReceiverRole.receiver());				
	}


	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] resultate) {
		if(resultate != null){
			for(ResultData resultat:resultate){
				if(resultat != null && resultat.getData() != null){

				}
			}
		}
	}
}