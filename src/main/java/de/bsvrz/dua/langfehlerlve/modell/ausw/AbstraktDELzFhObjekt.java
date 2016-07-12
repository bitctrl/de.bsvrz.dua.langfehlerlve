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

package de.bsvrz.dua.langfehlerlve.modell.ausw;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.langfehlerlve.parameter.AtgParameterMessStellenGruppe;
import de.bsvrz.dua.langfehlerlve.parameter.IAtgParameterMessStellenGruppeListener;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;

/**
 * Grundgeruest einer Klasse, die immer die aktuellen Parameter einer
 * Messstellengruppe in Bezug auf ein bestimmtes Vergleichsintervall (Lang- oder
 * Kurzzeit) benoetigt.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public abstract class AbstraktDELzFhObjekt implements
		IAtgParameterMessStellenGruppeListener {

	/**
	 * statische Datenverteiler-Verbindung.
	 */
	protected static ClientDavInterface dDav = null;

	/**
	 * die mit diesem Objekt assoziierte Messstellengruppe.
	 */
	protected DELzFhMessStellenGruppe messStellenGruppe = null;

	/**
	 * Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll.
	 */
	protected boolean langZeit = false;

	/**
	 * Initialisiert diese Klasse anstelle eines Konstruktors.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung
	 * @param messStellenGruppe1
	 *            die mit diesem Objekt zu assoziierende Messstellengruppe
	 * @param langZeit1
	 *            indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 */
	protected final void init(final ClientDavInterface dav,
			final DELzFhMessStellenGruppe messStellenGruppe1,
			final boolean langZeit1) {
		if (dDav == null) {
			dDav = dav;
		}
		this.messStellenGruppe = messStellenGruppe1;
		this.langZeit = langZeit1;
		AtgParameterMessStellenGruppe.getInstanz(dav,
				messStellenGruppe1.getObjekt()).addListener(this);
	}

	/**
	 * Aktualisiert die Parameter der assoziierten Messstellengruppe fuer dieses
	 * Objekt (und dieses Vergleichsintervall).
	 * 
	 * @param parameter
	 *            aktuelle Parameter fuer die Ueberwachung
	 */
	protected abstract void aktualisiereMsgParameter(
			IMsgDatenartParameter parameter);

	/**
	 * Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmert.
	 * 
	 * @return ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 *         kuemmert
	 */
	protected final boolean isLangZeit() {
		return this.langZeit;
	}

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereMsgParameter(IMsgDatenartParameter kzParameter,
			IMsgDatenartParameter lzParameter) {
		if (this.isLangZeit()) {
			this.aktualisiereMsgParameter(lzParameter);
		} else {
			this.aktualisiereMsgParameter(kzParameter);
		}
	}

}
