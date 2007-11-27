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

package de.bsvrz.dua.langfehlerlve.modell;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.langfehlerlve.parameter.AtgParameterMessStellenGruppe;
import de.bsvrz.dua.langfehlerlve.parameter.IAtgParameterMessStellenGruppeListener;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.debug.Debug;


/**
 * TODO
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public abstract class AbstraktDELzFhObjekt 
implements IAtgParameterMessStellenGruppeListener{

	/**
	 * Debug-Logger
	 */
	protected static final Debug LOGGER = Debug.getLogger();
	
	/**
	 * statische Datenverteiler-Verbindung
	 */
	protected static ClientDavInterface DAV = null;
	
	/**
	 * die mit diesem Objekt assoziierte Messstellengruppe
	 */
	protected DELzFhMessStellenGruppe messStellenGruppe = null;
	
	/**
	 * Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll
	 */
	protected boolean langZeit = false;
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param messStellenGruppe die mit diesem Objekt assoziierte Messstellengruppe
	 * @param langZeit Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll
	 */
	protected AbstraktDELzFhObjekt(final ClientDavInterface dav, 
								   final DELzFhMessStellenGruppe messStellenGruppe,
								   final boolean langZeit){
		if(DAV == null){
			DAV = dav;
		}
		this.messStellenGruppe = messStellenGruppe;
		this.langZeit = langZeit;
		AtgParameterMessStellenGruppe.getInstanz(dav, messStellenGruppe.getObjekt()).addListener(this);
	}
	
	
	/**
	 * Aktualisiert die Parameter der assoziierten Messstellengruppe fuer dieses
	 * Objekt (und dieses Vergleichsintervall)
	 *  
	 * @param parameter aktuelle Parameter fuer die Ueberwachung
	 */
	protected abstract void aktualisiereMsgParameter(IMsgDatenartParameter parameter);
	
	
	/**
	 * Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmert
	 * 
	 * @return ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmert
	 */
	protected final boolean isLangZeit(){
		return this.langZeit;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereMsgParameter(IMsgDatenartParameter kzParameter,
										 IMsgDatenartParameter lzParameter) {
		if(this.isLangZeit()){
			this.aktualisiereMsgParameter(lzParameter);
		}else{
			this.aktualisiereMsgParameter(kzParameter);
		}
	}	
	
}
