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

/**
 * Hoert auf Veraenderungen der Attributgruppe
 * (<code>atg.parameterMessStellenGruppe</code>).
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public interface IAtgParameterMessStellenGruppeListener {

	/**
	 * Aktualisiert die Parameter der Attributgruppe
	 * <code>atg.parameterMessStellenGruppe</code> getrennt fuer die KZD- und
	 * LZD-Ueberwachung.
	 * 
	 * @param kzParameter
	 *            aktuelle Parameter fuer die KZD-Ueberwachung
	 * @param lzParameter
	 *            aktuelle Parameter fuer die LZD-Ueberwachung
	 */
	void aktualisiereMsgParameter(IMsgDatenartParameter kzParameter, IMsgDatenartParameter lzParameter);

}
