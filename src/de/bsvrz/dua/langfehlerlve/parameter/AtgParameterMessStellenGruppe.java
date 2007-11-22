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

package de.bsvrz.dua.langfehlerlve.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;

/**
 * Korrespondiert mit einem Objekt vom Typ <code>typ.messStellenGruppe</code>
 * und kapselt dessen aktuelle Parameter (<code>atg.parameterMessStellenGruppe</code>) 
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class AtgParameterMessStellenGruppe
implements ClientReceiverInterface{

	/**
	 * statische Instanzen dieser Klasse
	 */
	private static Map<SystemObject, AtgParameterMessStellenGruppe> INSTANZEN =
								Collections.synchronizedMap(new HashMap<SystemObject, AtgParameterMessStellenGruppe>());
	
	/**
	 * Menge aller Beobachterobjekte
	 */
	private Set<IAtgParameterMessStellenGruppeListener> listenerMenge = Collections.synchronizedSet(
			new HashSet<IAtgParameterMessStellenGruppeListener>());
	
	/**
	 * aktuelle Parameter fuer die KZD-Ueberwachung
	 */
	private IMsgDatenartParameter kzParameter = null;
	
	/**
	 * aktuelle Parameter fuer die LZD-Ueberwachung
	 */
	private IMsgDatenartParameter lzParameter = null;

	
	/**
	 * Erfragt eine statische Instanz dieser Klasse
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param objekt ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static final AtgParameterMessStellenGruppe getInstanz(ClientDavInterface dav,
															 	 SystemObject objekt){
		AtgParameterMessStellenGruppe instanz = null;
		
		synchronized (INSTANZEN) {
			instanz= INSTANZEN.get(objekt);	
		}		
		
		if(instanz == null){
			instanz = new AtgParameterMessStellenGruppe(dav, objekt);
			synchronized (INSTANZEN) {
				INSTANZEN.put(objekt, instanz);	
			}			
		}
		
		return instanz;
	}
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param objekt ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 */
	private AtgParameterMessStellenGruppe(ClientDavInterface dav,
										  SystemObject objekt){
		dav.subscribeReceiver(this, 
							  objekt,
							  new DataDescription(
									  dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"), //$NON-NLS-1$
									  dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_SOLL),
									  (short)0),
							  ReceiveOptions.normal(),
							  ReceiverRole.receiver());
	}

	
	/**
	 * Fuegt diesem Objekt einen Listener hinzu
	 * 
	 * @param listener eine neuer Listener
	 */
	public final synchronized void addListener(final IAtgParameterMessStellenGruppeListener listener){
		if(listenerMenge.add(listener) && this.kzParameter != null){
			listener.aktualisiereMsgParameter(this.kzParameter, this.lzParameter);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] resultate) {
		if(resultate != null){
			for(ResultData resultat:resultate){
				if(resultat != null && resultat.getData() != null){
					synchronized (this) {
						final int maxAbweichungMessStellenGruppeKZ =
							resultat.getData().getUnscaledValue("maxAbweichungMessStellenGruppeKurzZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerKZ =
							resultat.getData().getUnscaledValue("maxAbweichungVorgängerKurzZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallKZ =
							resultat.getData().getUnscaledValue("VergleichsIntervallKurzZeit").longValue() * Konstante.MINUTE_IN_MS; //$NON-NLS-1$					
						this.kzParameter = new IMsgDatenartParameter(){

							public int getMaxAbweichungMessStellenGruppe() {
								return maxAbweichungMessStellenGruppeKZ;
							}

							public int getMaxAbweichungVorgaenger() {
								return maxAbweichungVorgaengerKZ;
							}

							public long getVergleichsIntervall() {
								return vergleichsIntervallKZ;
							}
							
						};

						final int maxAbweichungMessStellenGruppeLZ =
							resultat.getData().getUnscaledValue("maxAbweichungMessStellenGruppeLangZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerLZ =
							resultat.getData().getUnscaledValue("maxAbweichungVorgängerLangZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallLZ =
							resultat.getData().getUnscaledValue("VergleichsIntervallLangZeit").longValue() * Konstante.STUNDE_IN_MS; //$NON-NLS-1$
						this.lzParameter = new IMsgDatenartParameter(){

							public int getMaxAbweichungMessStellenGruppe() {
								return maxAbweichungMessStellenGruppeLZ;
							}

							public int getMaxAbweichungVorgaenger() {
								return maxAbweichungVorgaengerLZ;
							}

							public long getVergleichsIntervall() {
								return vergleichsIntervallLZ;
							}
							
						};
						
						for(IAtgParameterMessStellenGruppeListener listener:this.listenerMenge){
							listener.aktualisiereMsgParameter(kzParameter, lzParameter);
						}
					}					
				}
			}
		}		
	}
	
}
