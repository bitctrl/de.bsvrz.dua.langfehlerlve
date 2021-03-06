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
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAUtensilien;

/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Erkennung systematischer
 * Detektorfehler fuer eine Messstelle vorgesehen sind (Afo DUA-BW-C1C2-13 -
 * Vergleich mit Vorgaenger). Diese Daten werden hier auch publiziert
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class AbweichungVorgaenger extends AbstraktAbweichung {

	/**
	 * Standardkonstruktor.
	 * 
	 * @param dav
	 *            Verbindung zum Datenverteiler
	 * @param messStelle
	 *            Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStellenGruppe
	 *            Messstellengruppe an der diese Berechnung erfolgt
	 * @param messStelleMinus1
	 *            Verbindung zu den Onlinedaten des Vorgaengers der Messstelle
	 * @param messQuerschnitt
	 *            Verbindung zu den Onlinedaten des Hauptmessquerschnitts der
	 *            Messstelle selbst
	 * @param langZeit
	 *            Indiziert, ob sich dieses Objekt um das
	 *            Langzeit-Vergleichsintervall kuemmern soll
	 * @throws Exception
	 *             wird weitergereicht
	 */
	protected AbweichungVorgaenger(ClientDavInterface dav, DELzFhMessStelle messStelle,
			DELzFhMessStellenGruppe messStellenGruppe, DELzFhMessStelle messStelleMinus1,
			DELzFhMessQuerschnitt messQuerschnitt, boolean langZeit) throws Exception {
		super(dav, messStelle, messStellenGruppe, new DELzFhMessStelle[] { messStelleMinus1 }, messQuerschnitt,
				langZeit);

		this.restMessStellen.add(messStelleMinus1.getMessStelle().getSystemObject());
		this.initPuffer();

		dav.subscribeSender(this, messStelle.getMessStelle().getSystemObject(),
				langZeit ? new DataDescription(dav.getDataModel().getAttributeGroup(ATG_PID),
						dav.getDataModel().getAspect(this.getLzAspPid()))
						: new DataDescription(dav.getDataModel().getAttributeGroup(ATG_PID),
								dav.getDataModel().getAspect(this.getKzAspPid())),
				SenderRole.source());

		messQuerschnitt.addListener(this);
		messStelleMinus1.addListener(this);
	}

	@Override
	protected void aktualisiereMsgParameter(IMsgDatenartParameter parameter) {
		this.abweichungMax = parameter.getMaxAbweichungVorgaenger();
		this.vergleichsIntervall = DUAUtensilien.getVergleichsIntervallInText(parameter.getVergleichsIntervall());
	}

	@Override
	protected String getKzAspPid() {
		return "asp.messQuerschnittZumVorgängerKurzZeit";
	}

	@Override
	protected String getLzAspPid() {
		return "asp.messQuerschnittZumVorgängerLangZeit";
	}

	@Override
	protected String getVergleichsIdentifikation() {
		return "Vergleich mit Vorgaenger";
	}

}
