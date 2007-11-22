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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAInitialisierungsException;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStelle;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MessStellenGruppe;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Diese Klasse korrespondiert mit einem DAV-Objekt vom Typ <code>typ.messStellenGruppe</code>
 * und kapselt direkt oder indirekt saemtliche Funktionalitäten, die innerhalb der SWE
 * DE Langzeit-Fehlererkennung in Bezug auf Objekte dieses Typs benoetigt werden
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DELzFhMessStellenGruppe {

	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * Datenverteiler-Verbindung
	 */
	private static ClientDavInterface DAV = null;
	
	/**
	 * Messstellen dieser Gruppe
	 */
	private Set<DELzFhMessStelle> messStellen = new HashSet<DELzFhMessStelle>();
	
	
	/**
	 * Initialisiert alle uebergebenen Messstellengruppen fuer diese SWE
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param msgObjekte alle Systemobjekte vom Typ <code>typ.messStellenGruppe</code>
	 * die innerhalb dieser SWE ueberwacht werden sollen
	 * @throws DUAInitialisierungsException wenn die Initialisierung eines Objektes nicht 
	 * moeglich ist
	 */
	public static final synchronized void initialisiere(ClientDavInterface dav,
														Collection<SystemObject> msgObjekte)
	throws DUAInitialisierungsException{
		DAV = dav;
		for(SystemObject msgObjekt:msgObjekte){
			new DELzFhMessStellenGruppe(msgObjekt);
		}
	}
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param msgObjekt Systemobjekt vom Typ <code>typ.messStellenGruppe</code>
	 * das innerhalb dieser SWE ueberwacht werden soll
	 * @throws DUAInitialisierungsException wenn es Probleme bei der Initialisierung
	 * dieses Objektes gibt
	 */
	private DELzFhMessStellenGruppe(SystemObject msgObjekt)
	throws DUAInitialisierungsException{
		if(MessStellenGruppe.getInstanz(msgObjekt).getMessStellen().size() > 1){
			for(MessStelle ms:MessStellenGruppe.getInstanz(msgObjekt).getMessStellen()){
				this.messStellen.add(new DELzFhMessStelle(DAV, ms.getSystemObject()));
			}			
		}else{
			throw new DUAInitialisierungsException("Messstellengruppe " + msgObjekt + //$NON-NLS-1$
					" hat weniger als 2 Messstellen"); //$NON-NLS-1$
		}
	}
	
}
