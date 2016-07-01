/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bitctrl.Constants;

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
 * und kapselt dessen aktuelle Parameter (
 * <code>atg.parameterMessStellenGruppe</code>).
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
	private final Set<IAtgParameterMessStellenGruppeListener> listenerMenge = new HashSet<IAtgParameterMessStellenGruppeListener>();

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
	public static final AtgParameterMessStellenGruppe getInstanz(final ClientDavInterface dav,
			final SystemObject objekt) {
		AtgParameterMessStellenGruppe instanz = null;

		synchronized (AtgParameterMessStellenGruppe.instanzen) {
			instanz = AtgParameterMessStellenGruppe.instanzen.get(objekt);
		}

		if (instanz == null) {
			instanz = new AtgParameterMessStellenGruppe(dav, objekt);
			synchronized (AtgParameterMessStellenGruppe.instanzen) {
				AtgParameterMessStellenGruppe.instanzen.put(objekt, instanz);
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
	protected AtgParameterMessStellenGruppe(final ClientDavInterface dav, final SystemObject objekt) {
		dav.subscribeReceiver(this, objekt,
				new DataDescription(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"), //$NON-NLS-1$
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
			if (listenerMenge.add(listener) && (this.kzParameter != null)) {
				listener.aktualisiereMsgParameter(this.kzParameter, this.lzParameter);
			}
		}
	}

	@Override
	public void update(final ResultData[] resultate) {
		if (resultate != null) {
			for (final ResultData resultat : resultate) {
				if ((resultat != null) && (resultat.getData() != null)) {
					synchronized (this) {
						final int maxAbweichungMessStellenGruppeKZ = resultat.getData()
								.getUnscaledValue("maxAbweichungMessStellenGruppeKurzZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerKZ = resultat.getData()
								.getUnscaledValue("maxAbweichungVorgängerKurzZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallKZ = resultat.getData()
								.getUnscaledValue("VergleichsIntervallKurzZeit").longValue() //$NON-NLS-1$
								* Constants.MILLIS_PER_MINUTE;
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
								.getUnscaledValue("maxAbweichungMessStellenGruppeLangZeit").intValue(); //$NON-NLS-1$
						final int maxAbweichungVorgaengerLZ = resultat.getData()
								.getUnscaledValue("maxAbweichungVorgängerLangZeit").intValue(); //$NON-NLS-1$
						final long vergleichsIntervallLZ = resultat.getData()
								.getUnscaledValue("VergleichsIntervallLangZeit").longValue() //$NON-NLS-1$
								* Constants.MILLIS_PER_HOUR;
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

						for (final IAtgParameterMessStellenGruppeListener listener : this.listenerMenge) {
							listener.aktualisiereMsgParameter(kzParameter, lzParameter);
						}
					}
				}
			}
		}
	}

}
