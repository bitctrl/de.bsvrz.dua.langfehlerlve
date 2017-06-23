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

/**
 * Korrespondiert mit einem Objekt vom Typ <code>typ.messStellenGruppe</code>
 * und kapselt dessen aktuelle Parameter
 * (<code>atg.parameterMessStellenGruppe</code>).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class AtgParameterMessStellenGruppe implements ClientReceiverInterface {

	/**
	 * statische Instanzen dieser Klasse.
	 */
	protected final static Map<SystemObject, AtgParameterMessStellenGruppe> instanzen = new HashMap<>();

	/**
	 * Menge aller Beobachterobjekte.
	 */
	private Set<IAtgParameterMessStellenGruppeListener> listenerMenge = new HashSet<>();

	/**
	 * aktuelle Parameter fuer die KZD-Ueberwachung.
	 */
	private IMsgDatenartParameter kzParameter = null;

	/**
	 * aktuelle Parameter fuer die LZD-Ueberwachung.
	 */
	private IMsgDatenartParameter lzParameter = null;

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 */
	protected AtgParameterMessStellenGruppe(ClientDavInterface dav, SystemObject objekt) {
		dav.subscribeReceiver(this, objekt,
				new DataDescription(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"),
						dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_SOLL)),
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * Erfragt eine statische Instanz dieser Klasse.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param objekt
	 *            ein Objekt vom Typ <code>typ.messStellenGruppe</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static final AtgParameterMessStellenGruppe getInstanz(ClientDavInterface dav, SystemObject objekt) {
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
	 * Fuegt diesem Objekt einen Listener hinzu.
	 * 
	 * @param listener
	 *            eine neuer Listener
	 */
	public final void addListener(final IAtgParameterMessStellenGruppeListener listener) {
		synchronized (this) {
			if (listenerMenge.add(listener) && this.kzParameter != null) {
				listener.aktualisiereMsgParameter(this.kzParameter, this.lzParameter);
			}
		}
	}

	@Override
	public void update(ResultData[] resultate) {
		if (resultate != null) {
			for (ResultData resultat : resultate) {
				if (resultat != null && resultat.getData() != null) {
					synchronized (this) {
						final int maxAbweichungMessStellenGruppeKZ = resultat.getData()
								.getUnscaledValue("maxAbweichungMessStellenGruppeKurzZeit").intValue();
						final int maxAbweichungVorgaengerKZ = resultat.getData()
								.getUnscaledValue("maxAbweichungVorgängerKurzZeit").intValue();
						final long vergleichsIntervallKZ = resultat.getData()
								.getUnscaledValue("VergleichsIntervallKurzZeit").longValue() * (long) (60 * 1000);
						this.kzParameter = new IMsgDatenartParameter() {

							@Override
							public int getMaxAbweichungMessStellenGruppe() {
								return maxAbweichungMessStellenGruppeKZ;
							}

							@Override
							public int getMaxAbweichungVorgaenger() {
								return maxAbweichungVorgaengerKZ;
							}

							@Override
							public long getVergleichsIntervall() {
								return vergleichsIntervallKZ;
							}

						};

						final int maxAbweichungMessStellenGruppeLZ = resultat.getData()
								.getUnscaledValue("maxAbweichungMessStellenGruppeLangZeit").intValue();
						final int maxAbweichungVorgaengerLZ = resultat.getData()
								.getUnscaledValue("maxAbweichungVorgängerLangZeit").intValue();
						final long vergleichsIntervallLZ = resultat.getData()
								.getUnscaledValue("VergleichsIntervallLangZeit").longValue() * (long) (60 * 60 * 1000);
						this.lzParameter = new IMsgDatenartParameter() {

							@Override
							public int getMaxAbweichungMessStellenGruppe() {
								return maxAbweichungMessStellenGruppeLZ;
							}

							@Override
							public int getMaxAbweichungVorgaenger() {
								return maxAbweichungVorgaengerLZ;
							}

							@Override
							public long getVergleichsIntervall() {
								return vergleichsIntervallLZ;
							}

						};

						this.listenerMenge.stream()
								.forEach((listener) -> listener.aktualisiereMsgParameter(kzParameter, lzParameter));
					}
				}
			}
		}
	}

}
