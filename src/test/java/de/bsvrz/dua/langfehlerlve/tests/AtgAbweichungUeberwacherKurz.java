/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.tests.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.tests;



/**
 * Ueberprueft den ersten echten (Nutz-)Wert von
 * <code>atg.abweichungVerkehrsStärke</code>,
 * <code>asp.messQuerschnittZumVorgängerKurzZeit</code>.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
class AtgAbweichungUeberwacherKurz extends AbstraktAtgUeberwacher {

	@Override
	String getAspPid() {
		return "asp.messQuerschnittZumVorgängerKurzZeit";
	}

	@Override
	String getAtgPid() {
		return "atg.abweichungVerkehrsStärke";
	}
	
}
