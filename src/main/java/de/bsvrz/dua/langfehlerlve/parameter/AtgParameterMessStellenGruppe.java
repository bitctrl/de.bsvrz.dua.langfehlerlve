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

package de.bsvrz.dua.langfehlerlve.parameter;

import de.bsvrz.dav.daf.main.*;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Korrespondiert mit einem Objekt vom Typ <code>typ.messStellenGruppe</code>
 * und kapselt dessen aktuelle Parameter (<code>atg.parameterMessStellenGruppe</code>).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class AtgParameterMessStellenGruppe implements ClientReceiverInterface {

	/**
	 * statische Instanzen dieser Klasse.
	 */
	protected static Map<SystemObject, AtgParameterMessStellenGruppe> instanzen = new HashMap<SystemObject, AtgParameterMessStellenGruppe>();

	/**
	 * Menge aller Beobachterobjekte.
	 */
	private Set<IAtgParameterMessStellenGruppeListener> listenerMenge = new HashSet<IAtgParameterMessStellenGruppeListener>();

	/**
	 * aktuelle Parameter fuer die KZD-Ueberwachung.
	 */
	private IMsgDatenartParameter kzParameter = null;

	/**
	 * aktuelle Parameter fuer die LZD-Ueberwachung.
	 */
	private IMsgDatenartParameter lzParameter = null;

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static final AtgParameterMessStellenGruppe getInstanz(
			ClientDavInterface dav, SystemObject objekt) {
		AtgParameterMessStellenGruppe instanz = null;

		synchronized (instanzen) {
			instanz = instanzen.get(objekt);
		}

		if (instanz == null) {
			instanz = new AtgParameterMessStellenGruppe(dav, objekt);
			synchronized (instanzen) {
				instanzen.put(objekt, instanz);
			}
		}

		return instanz;
	}

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 */
	protected AtgParameterMessStellenGruppe(ClientDavInterface dav,
			SystemObject objekt) {
		dav.subscribeReceiver(this, objekt, new DataDescription(
				dav.getDataModel().getAttributeGroup(
						"atg.parameterMessStellenGruppe"), //$NON-NLS-1$
				dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_SOLL)),
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * Fuegt diesem Objekt einen Listener hinzu.
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public final void addListener(final IAtgParameterMessStellenGruppeListener listener) {
		synchronized (this) {
			if (listenerMenge.add(listener) && this.kzParameter != null) {
				listener.aktualisiereMsgParameter(this.kzParameter,
						this.lzParameter);
			}			
		}
	}

	public void update(ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if (resultat != null && resultat.getData() != null) {
					synchronized (this) {
						final int maxAbweichungMessStellenGruppeKZ = resultat
								.getData()
								.getUnscaledValue(
										"maxAbweichungMessStellenGruppeKurzZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerKZ = resultat
								.getData()
								.getUnscaledValue(
										"maxAbweichungVorgängerKurzZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallKZ = resultat
								.getData()
								.getUnscaledValue("VergleichsIntervallKurzZeit").longValue() * (long) (60 * 1000); //$NON-NLS-1$					
						this.kzParameter = new IMsgDatenartParameter() {

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

						final int maxAbweichungMessStellenGruppeLZ = resultat
								.getData()
								.getUnscaledValue(
										"maxAbweichungMessStellenGruppeLangZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerLZ = resultat
								.getData()
								.getUnscaledValue(
										"maxAbweichungVorgängerLangZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallLZ = resultat
								.getData()
								.getUnscaledValue("VergleichsIntervallLangZeit").longValue() * (long) (60 * 60 * 1000); //$NON-NLS-1$
						this.lzParameter = new IMsgDatenartParameter() {

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

						for (IAtgParameterMessStellenGruppeListener listener : this.listenerMenge) {
							listener.aktualisiereMsgParameter(kzParameter,
									lzParameter);
						}							
					}
				}
			}
		}
	}

}
